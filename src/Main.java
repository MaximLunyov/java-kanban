import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Дела на завтра", "NEW");
        taskManager.addTask(task1);

        Task task2 = new Task("Дела на пятницу", "NEW");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Охота");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Купить патроны", "NEW", epic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить ружье", "DONE", epic1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Сделать ДЗ");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Отправить ТЗ спринт 3 на первое ревью", "IN_PROGRESS", epic2);
        taskManager.addSubtask(subtask3);

        System.out.println("Tasks = " + taskManager.getTasks());
        System.out.println("Epics = " + taskManager.getEpics());
        System.out.println("Subtasks = " + taskManager.getSubtasks());

        System.out.println("");

        taskManager.getEpicSubtasks(3);

    }
}
