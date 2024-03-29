package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    HashMap<Integer, Task> getTasksMap();
    HashMap<Integer, Epic> getEpicsMap();
    HashMap<Integer, Subtask> getSubtasksMap();
    List<Task> getHistory();

    List<Task> getTaskByPriority();

    //Tasks
    void addTask(Task task);

    void updateTask(Task task);

    Task getTask(int id);

    List<Task> getTasks();

    void deleteTask(int id);

    void deleteAllTasks();

    //Epics
    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    Epic getEpic(int id);

    List<Epic> getEpics();

    void deleteEpic(int id);

    void deleteAllEpics();

    //Subtasks
    void addSubtask(Subtask subtask);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Subtask> getSubtasks();

    void updateSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void deleteSubtask(int id);

    void deleteAllSubtask();


}