/*import manager.HistoryManager;
import manager.InMemoryHistoryManager;*/
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
        taskManager.getTask(1);
        taskManager.getTask(1);
        System.out.println(taskManager.getHistory());


        System.out.println("");
        Task task2 = new Task("Дела на пятницу", Status.IN_PROGRESS);
        taskManager.addTask(task2);
        taskManager.getTask(2);
        System.out.println(taskManager.getHistory());

        Epic epic1 = new Epic("Охота");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Купить патроны", Status.NEW, epic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить ружье", Status.DONE, epic1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Сделать ДЗ");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Отправить ТЗ спринт 3 на первое ревью", Status.IN_PROGRESS, epic2);
        taskManager.addSubtask(subtask3);

        System.out.println("Tasks = " + taskManager.getTasks());
        System.out.println("Epics = " + taskManager.getEpics());
        System.out.println("Subtasks = " + taskManager.getSubtasks());



    }
}
