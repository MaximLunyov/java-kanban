/*
package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> taskHistory;

    public InMemoryHistoryManager() {
        this.taskHistory = new ArrayList<>();
    }


    Привет, Патимат!
    У меня возникла проблема с переносом истории из Класса InMemoryTaskManager
    в отдельный класс с интерфейсом HistoryManager - InMemoryHistoryManager.
    Я не понимаю как правильно передать в список taskHistory данные о запросах истории по id из класса InMemoryTaskManager.
    Пробовал разные варианты, максимум, что получил по вызову метода Managers.getDefaultHistory():
    название списка InMemoryHistoryManager и какие-то символы.
    Подскажи пожалуйста, как правильно перенести истории вызывов по id в отдельный интерфейс и менеджер.

    @Override
    public void add(Task task) {
        if (taskHistory.size() > 10) {
                taskHistory.remove(0);
            }
            taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
*/
