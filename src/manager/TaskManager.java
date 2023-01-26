package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
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

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epics.remove(id);
            for (Integer subtaskId : epic.getEpicSubtasks()) {
                subtasks.remove(subtaskId);
            }
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
    public void getEpicSubtasks(int epicId) {
        if (epics.containsKey(epicId)) {
            if (!epics.isEmpty()) {
                List<Integer> test = epics.get(epicId).getEpicSubtasks(); // список id субтасков;
                for (Integer t : test) {
                    System.out.println("Selected Epic`s subtask(s): " + subtasks.get(t));
                }
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getEpic());
    }

    public Subtask getSubtask(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = subtasks.get(id).getEpic();
            epic.getEpicSubtasks().remove((Integer) id);
            checkEpicStatus(epic);
            subtasks.remove(id);
        }
    }


    /*Попробовал сделать по твоей ремарке замену статусов в Эпиках,
    * если проходить по циклу:
    * subtacks.clear();
    * for (Epic epic : epics.values()) {
            checkEpicStatus(epic);
        }
        * выбивает ошибку NullPointer, проверил через дебагер, ошибка выскакивает из-за того,
        * что при выполнении метода subtasks.clear() идет удаление всех сабтасков, но если вызвать метод
        * epic.getEpicSubtasks().size() - выдаст не нулевое значение, напишет количество сабтасков до удаления.
        * Подскажи пожалуйста, как с этим бороться, или я не так понял твое задание?
        * */

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