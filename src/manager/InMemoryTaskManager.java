package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(id, task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
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
            tasks.remove(id);
            historyManager.remove(id);
        }
    }
    @Override
    public void showTasks() {
        System.out.println(tasks);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
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
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    //Subtasks
    @Override
    public void addSubtask(Subtask subtask)  {
        subtask.setId(++id);
        subtasks.put(id, subtask);
        subtask.getEpic().getEpicSubtasks().add(id);
        checkEpicStatus(subtask.getEpic());

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
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getEpic());
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
            epic.getEpicSubtasks().remove((Integer) id);
            checkEpicStatus(epic);
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicSubtasks().clear();
            checkEpicStatus(epic);
        }
    }

    //epicsStatusChecking
    @Override
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



}