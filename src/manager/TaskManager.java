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

     /*     Возникла новая проблема: когда я вызываю return в конце метода epicSubtasks,
            метод ничего не возвращает, выходит пустая строка в итоге.
            Проверял через дебагер, список epicSubtasks заполняется корректно,
            но при вызове return данного списка ничего не происходит.
            Попробовал сделать по аналогии с другими public List<...> getSmth:
            Завел HashMap<Integer, Subtack> epicSubtasks = new HashMap<>();
            заполнил мапу субтасками по epicId,
            возврат списка return new ArrayList<>(epicSubtasks.values());
            но return все так же ничего не возвращает.
            Не выходит разобраться самостоятельно в этом моменте, ты не могла бы подсказать как правильно
            вернуть значение из списка? */

    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            if (!epics.isEmpty()) {
                List<Integer> test = epics.get(epicId).getEpicSubtasks(); // список id субтасков;
                for (Integer t : test) {
                    epicSubtasks.add(subtasks.get(t));
                }
            }
        }
        return epicSubtasks;
    }
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getEpic());
    }

    public Subtask getSubtask(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = subtasks.get(id).getEpic();
            epic.getEpicSubtasks().remove((Integer) id);
            checkEpicStatus(epic);
            subtasks.remove(id);
        }
    }

    public void deleteAllSubtask() { //Спасибо за подсказку!
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicSubtasks().clear();
            checkEpicStatus(epic);
        }
    }


    //epicsStatusChecking
    private void checkEpicStatus(Epic epic) {

        if (epic.getEpicSubtasks().isEmpty()) {
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