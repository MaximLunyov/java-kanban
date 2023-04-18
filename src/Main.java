import manager.*;
import tasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Помыть машину", "описание1",Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Купить продукты", "описание2",Status.IN_PROGRESS);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Охота", "Поиск дичи");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Купить патроны","Выбрать калибр", Status.NEW, epic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить ружье", "Посмотреть обзоры на YouTube", Status.DONE, epic1);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Купить корм для собаки","Посмотреть состав", Status.IN_PROGRESS, epic1);
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Работа", "описание3");
        taskManager.addEpic(epic2);
        taskManager.getTask(2);

        System.out.println(taskManager.getHistory());

        taskManager.getEpic(3);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicSubtasks(3);
        System.out.println(taskManager.getHistory());

        System.out.println(taskManager.getHistory());


        taskManager.getTask(1);
        System.out.println(taskManager.getHistory());
        Task task3 = new Task("ЧТо-то", "desk", Status.IN_PROGRESS);
        taskManager.addTask(task3);

    }
}
