package tests;

import manager.FileBackedTasksManager;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(new File("/java-kanban/src/tests/workHistoryForTest.csv"));
    }

    @Test
    public void shouldLoadFromFile() {
        FileBackedTasksManager f1 = new FileBackedTasksManager(new File
                ("/java-kanban/src/tests/workHistoryForTest.csv"));
        assertEquals(3, f1.getTasks().size());
        assertEquals(5, f1.getEpics().size());
        assertEquals(1, f1.getSubtasks().size());
    }

    @Test
    public void shouldLoadFromFileWithoutTasks() {
        FileBackedTasksManager f1 = new FileBackedTasksManager(new File
                ("/java-kanban/src/tests/workHistoryForTest1.csv"));
        assertEquals(0, f1.getTasks().size());
        assertEquals(2, f1.getEpics().size());
        assertEquals(3, f1.getSubtasks().size());
    }

    @Test
    public void shouldLoadFromFileWithoutEpicsSubtasks() {
        FileBackedTasksManager f1 = new FileBackedTasksManager(new File
                ("/java-kanban/src/tests/workHistoryForTest2.csv"));
        assertEquals(3, f1.getTasks().size());
        assertEquals(2, f1.getEpics().size());
        assertEquals(0, f1.getSubtasks().size());
    }

    @Test
    public void shouldLoadFromFileWithoutHistory() {
        FileBackedTasksManager f1 = new FileBackedTasksManager(new File
                ("/java-kanban/src/tests/workHistoryForTest3.csv"));
        assertEquals(3, f1.getTasks().size());
        assertEquals(2, f1.getEpics().size());
        assertEquals(3, f1.getSubtasks().size());
        assertEquals(0, f1.getHistory().size());
    }


}
