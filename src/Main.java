import manager.*;
import tasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Помыть машину", "описание1",Status.NEW,
                60, LocalDateTime.of(2023, 5, 2, 16, 20));
        taskManager.addTask(task1);
        Task task2 = new Task("Купить продукты", "описание2",Status.IN_PROGRESS,
                30, LocalDateTime.of(2023, 5, 2, 17, 20));
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Охота", "Поиск дичи");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Купить патроны","Выбрать калибр", Status.NEW,
                epic1, 60, LocalDateTime.of(2023, 6, 3, 16, 0));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить ружье", "Посмотреть обзоры на YouTube", Status.DONE,
                epic1, 60, LocalDateTime.of(2023, 5, 4, 5, 0));
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Купить корм для собаки","Посмотреть состав", Status.IN_PROGRESS,
                epic1, 60, LocalDateTime.of(2023, 5, 1, 6, 0));
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Работа", "описание3");
        taskManager.addEpic(epic2);
        taskManager.getTask(2); //история
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(3); //история

        taskManager.getEpicSubtasks(3);

        taskManager.getTask(1); //история

        Task task3 = new Task("ЧТо-то", "desk", Status.IN_PROGRESS,
                5, LocalDateTime.of(2023, 5, 1, 16, 20));
        taskManager.addTask(task3);

        System.out.println(taskManager.getTaskByPriority());
        taskManager.deleteTask(task3.getId());
        System.out.println(taskManager.getTaskByPriority());
    }
}
