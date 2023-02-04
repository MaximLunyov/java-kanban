package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getHistory(); // тут

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

    //epicsStatusChecking
    void checkEpicStatus(Epic epic);

}