package tests;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void shouldGetHistory() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(firstTask);
        taskManager.addEpic(secondEpic);
        taskManager.getTask(firstTask.getId());
        taskManager.getEpic(secondEpic.getId());

        assertNotNull(taskManager.getHistory());
        assertEquals(2, taskManager.getHistory().size());

        taskManager.deleteTask(firstTask.getId());
        assertEquals(1, taskManager.getHistory().size());

        taskManager.addTask(firstTask);
        assertEquals(1, taskManager.getHistory().size());
    }
}
