import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    //Tasks
    public void addTask(Task task) {
        task.setId(id++);
        tasks.put(id, task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTask(int id) {
        return tasks.getOrDefault(id, null);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    //Epics
    public void addEpic(Epic epic) {
        epic.setId(id++);
        epic.setStatus("NEW");
        epics.put(id, epic);
    }

    public void updateEpic(Epic epic) {
        epic.setEpicSubtasks(epics.get(epic.getId()).getEpicSubtasks());
        epics.put(epic.getId(), epic);
        checkEpicStatus(epic);
    }

    public Epic getEpic(int id) {
        return epics.getOrDefault(id, null);
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epics.remove(id);
            for (Integer subtaskId : epic.getEpicSubtasks()) {
                subtasks.remove(subtaskId);
            }
            epic.setEpicSubtasks(new ArrayList<>());
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Subtasks
    public void addSubtask(Subtask subtask) {
        subtask.setId(id++);
        subtasks.put(id, subtask);
        subtask.getEpic().getEpicSubtasks().add(id);
        checkEpicStatus(subtask.getEpic());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getEpic());
    }

    public Subtask getSubtask(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = subtasks.get(id).getEpic();
            epic.getEpicSubtasks().remove((Integer) id);
            checkEpicStatus(epic);
            subtasks.remove(id);
        }
    }

    public void deleteAllSubtask() {
        ArrayList<Epic> epicsForStatusUpdate = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtask.getEpic().setEpicSubtasks(new ArrayList<>());
            if (!epicsForStatusUpdate.contains(subtask.getEpic())) {
                epicsForStatusUpdate.add(subtask.getEpic());
            }
        }
        subtasks.clear();
        for (Epic epic : epicsForStatusUpdate) {
            epic.setStatus("NEW");
        }
    }

    //epicsStatusChecking
    private void checkEpicStatus(Epic epic) {

        if (epic.getEpicSubtasks().size() == 0) {
            epic.setStatus("NEW");
            return;
        }

        boolean isAllNew = true;
        boolean isAllDone = true;

        for (Integer epicSubtaskId : epic.getEpicSubtasks()) {
            String status = subtasks.get(epicSubtaskId).getStatus();
            if (!status.equals("NEW")) {
                isAllNew = false;
            }
            if (!status.equals("DONE")) {
                isAllDone = false;
            }
        }

        if (isAllDone) {
            epic.setStatus("DONE");
        } else if (isAllNew) {
            epic.setStatus("NEW");
        } else {
            epic.setStatus("IN_PROGRESS");
        }

    }

}