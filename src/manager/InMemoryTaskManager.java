package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
//import manager.InMemoryHistoryManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
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
        }
    }
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Epics
    @Override
    public void addEpic(Epic epic) {
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
            for (Integer subtaskId : epic.getEpicSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
    }
    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Subtasks
    @Override
    public void addSubtask(Subtask subtask) {
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
        }
    }
    @Override
    public void deleteAllSubtask() { //Спасибо за подсказку!
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