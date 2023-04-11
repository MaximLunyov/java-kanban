import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import tasks.Status;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Дела на завтра", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Дела на пятницу", Status.IN_PROGRESS);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Охота");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Купить патроны", Status.NEW, epic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить ружье", Status.DONE, epic1);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Купить корм для собаки", Status.IN_PROGRESS, epic1);
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Работа");
        taskManager.addEpic(epic2);
        taskManager.getTask(2);
        taskManager.getTask(1);

        taskManager.getTask(2);
        System.out.println(taskManager.getHistory());

        taskManager.getEpic(3);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicSubtasks(3);
        System.out.println(taskManager.getHistory());

        taskManager.getEpic(7);
        System.out.println(taskManager.getHistory());

        taskManager.deleteEpic(3);
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getHistory());

    }
}
