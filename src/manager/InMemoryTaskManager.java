package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    protected Set<Task> sortedTasks = new TreeSet<>((task1, task2) -> {
        if ((task1.getStartTime() != null) && (task2.getStartTime() != null)) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        } else {
            return 0;
        }
    });

    @Override
    public HashMap<Integer, Task> getTasksMap() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpicsMap() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksMap() {
        return subtasks;
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTaskByPriority() {
        return sortedTasks.stream().collect(Collectors.toList());
    }

    @Override
    public void addTask(Task task) {
        checkTaskSameTime(task);
        task.setId(++id);
        tasks.put(id, task);
        sortedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        sortedTasks.remove(task);
        checkTaskSameTime(task);
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return tasks.getOrDefault(id, null);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            sortedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);

        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            sortedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    //Epics
    @Override
    public void addEpic(Epic epic)  {
        epic.setId(++id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setEpicSubtasks(epics.get(epic.getId()).getEpicSubtasks());
        epics.put(epic.getId(), epic);
        checkEpicStatus(epic);
    }

    @Override
    public Epic getEpic(int id) {
        final Task task = epics.get(id);
        historyManager.add(task);

        return epics.getOrDefault(id, null);
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epics.remove(id);
            historyManager.remove(id);

            for (Integer subtaskId : epic.getEpicSubtasks()) {
                sortedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();

        for (Subtask subtask : subtasks.values()) {
            sortedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    //Subtasks
    @Override
    public void addSubtask(Subtask subtask)  {
        checkTaskSameTime(subtask);
        subtask.setId(++id);
        subtasks.put(id, subtask);
        subtask.getEpic().getEpicSubtasks().add(id);
        epicSetDateTime(subtask.getEpic());
        checkEpicStatus(subtask.getEpic());
        sortedTasks.add(subtask);
    }

    protected void epicSetDateTime(Epic epic) {
        setEpicStartTime(epic.getEpicSubtasks(),epic);
        setEpicDuration(epic.getEpicSubtasks(),epic);
        setEpicFinishTime(epic.getEpicSubtasks(),epic);
    }

    protected void setEpicDuration(List<Integer> epicSubtasksId, Epic epic) {
        int duration = 0;
        for (int id : epicSubtasksId) {
            Subtask subtask = subtasks.get(id);
            duration += subtask.getDuration();
        }
        epic.setDuration(duration);
    }

    protected void setEpicStartTime(List<Integer> epicSubtasksId, Epic epic) {
        LocalDateTime epicStartTime = null;
        if (epicSubtasksId.size() != 0) {
            Subtask first = subtasks.get(epicSubtasksId.get(0));
            epicStartTime = first.getStartTime();
        }

        for (int i = 1; i < epicSubtasksId.size(); i++) {
            Subtask checked = subtasks.get(epicSubtasksId.get(i));
            if (checked.getStartTime().isBefore(epicStartTime)) {
                epicStartTime = checked.getStartTime();
            }
            epic.setStartTime(epicStartTime);
        }
    }

    protected void setEpicFinishTime(List<Integer> subtasksID, Epic epic) {
        LocalDateTime epicFinishTime = null;
        if (subtasksID.size() != 0) {
            Subtask last = subtasks.get(subtasksID.get(0));
            epicFinishTime = last.getEndTime();
        }

        for (int i = 1; i < subtasksID.size(); i++) {
            Subtask checked = subtasks.get(subtasksID.get(i));
            if (checked.getEndTime().isAfter(epicFinishTime)) {
                epicFinishTime = checked.getEndTime();
            }
            epic.setEndTime(epicFinishTime);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            List<Integer> epicSubtaskIds  = epics.get(epicId).getEpicSubtasks();
            for (Integer id : epicSubtaskIds) {
                epicSubtasks.add(subtasks.get(id));
            }
        }
        return epicSubtasks;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        sortedTasks.remove(subtask);
        checkTaskSameTime(subtask);
        subtasks.put(subtask.getId(), subtask);
        epicSetDateTime(subtask.getEpic());
        checkEpicStatus(subtask.getEpic());
        sortedTasks.add(subtask);
    }

    @Override
    public Subtask getSubtask(int id) {
        final Task task = subtasks.get(id);
        historyManager.add(task);

        return subtasks.getOrDefault(id, null);
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = subtasks.get(id).getEpic();
            sortedTasks.remove(subtasks.get(id));
            epic.getEpicSubtasks().remove((Integer) id);
            checkEpicStatus(epic);
            epicSetDateTime(epic);
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            sortedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicSubtasks().clear();
            epicSetDateTime(epic);
            checkEpicStatus(epic);
        }
    }

    //epicsStatusChecking
    public void checkEpicStatus(Epic epic) {

        if (epic.getEpicSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isAllNew = true;
        boolean isAllDone = true;

        for (Integer epicSubtaskId : epic.getEpicSubtasks()) {
            Status status = subtasks.get(epicSubtaskId).getStatus();
            if (!status.equals(Status.NEW)) {
                isAllNew = false;
            }
            if (!status.equals(Status.DONE)) {
                isAllDone = false;
            }
        }

        if (isAllDone) {
            epic.setStatus(Status.DONE);
        } else if (isAllNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void checkTaskSameTime(Task task) {
        LocalDateTime startTime = task.getStartTime(); //5:00
        LocalDateTime finishTime = task.getEndTime(); //6:00
        List<Task> listOfTasks = getTaskByPriority();

        for (Task tasks : listOfTasks) {
            LocalDateTime checkedStart = tasks.getStartTime(); //6:00
            LocalDateTime checkedFinish = tasks.getEndTime();  //7:00

            if ((startTime.isAfter(checkedStart) && startTime.isBefore(checkedFinish))
                    || (finishTime.isAfter(checkedStart) && finishTime.isBefore(checkedFinish)) || (startTime.isEqual(checkedStart) && finishTime.isEqual(checkedFinish))) {
                throw new RuntimeException("Задачи не могут выполнятся одновременно! (Если вы не Гай Юлий Цезарь:))");
            }
        }
    }
}