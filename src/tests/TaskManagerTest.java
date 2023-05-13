package tests;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    private T taskManager;
    Task firstTask;
    Task secondTask;
    Epic firstEpic;
    Epic secondEpic;
    Subtask firstSubtask;
    Subtask secondSubtask;

    abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
        firstTask = new Task("Тест 1", "описание1", Status.NEW, 10, LocalDateTime.of(2024, 5, 2, 23, 20));
        secondTask = new Task("Тест 2", "описание2", Status.DONE, 32, LocalDateTime.of(2024, 9, 2, 16, 20));
        firstEpic = new Epic("Epic 1", "desc3");
        secondEpic = new Epic("Epic2", "desc4");
        firstSubtask = new Subtask("Subtask1", "desc5", Status.IN_PROGRESS, firstEpic,32, LocalDateTime.of(2023, 6, 2, 16, 20));
        secondSubtask = new Subtask("Subtask2", "desc6", Status.DONE, firstEpic,32, LocalDateTime.of(2023, 5, 2, 16, 20));
    }
//Tasks
    @Test
    void shouldAddTask() {
        taskManager.addTask(firstTask);
        assertEquals(firstTask, taskManager.getTask(firstTask.getId()));
    }

    @Test
    public void shouldUpdateTask() {
        taskManager.addTask(firstTask);
        firstTask.setDescription("new_Description");
        taskManager.updateTask(firstTask);
        assertEquals("new_Description", firstTask.getDescription());
    }

    @Test
    public void shouldGetTask() {
        taskManager.addTask(firstTask);
        assertNotNull(firstTask, "Заданный Task не найден!");
        assertEquals(firstTask, taskManager.getTask(firstTask.getId()));
    }

    @Test
    public void shouldNotGetTask() {
        taskManager.deleteAllTasks();
        taskManager.addTask(firstTask);
        assertNotNull(firstTask, "Заданный Task не найден!");
        assertNull(taskManager.getTask(999999));
    }

    @Test
    public void shouldGetTasks() {
        taskManager.deleteAllTasks();
        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);
        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void shouldDeleteTask() {
        taskManager.addTask(secondTask);
        int test = secondTask.getId();
        taskManager.deleteTask(test);
        assertNull(taskManager.getTask(test));
    }

    @Test
    public void shouldNotDeleteTask() {
        taskManager.addTask(secondTask);
        int test = secondTask.getId();
        taskManager.deleteTask(99999);
        assertEquals(secondTask, taskManager.getTask(test));
    }

    @Test
    public void shouldDeleteAllTasks() {
        taskManager.deleteAllTasks();
        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);
        List<Task> testList = new ArrayList<>();
        taskManager.deleteAllTasks();
        assertEquals(testList ,taskManager.getTasks());
    }
//Epics
    @Test
    public void shouldAddEpic() {
        taskManager.addEpic(firstEpic);
        assertEquals(firstEpic, taskManager.getEpic(firstEpic.getId()));
    }

    @Test
    public void shouldUpdateEpic() {
        taskManager.addEpic(firstEpic);
        firstEpic.setDescription("new_Description");
        taskManager.updateEpic(firstEpic);
        assertEquals("new_Description", firstEpic.getDescription());
    }

    @Test
    public void shouldGetEpic() {
        taskManager.addEpic(firstEpic);
        assertNotNull(firstEpic, "Заданный Task не найден!");
        assertEquals(firstEpic, taskManager.getEpic(firstEpic.getId()));
    }

    @Test
    public void shouldNotGetEpic() {
        taskManager.addEpic(firstEpic);
        assertNotNull(firstEpic, "Заданный Task не найден!");
        assertNull(taskManager.getEpic(999999));
    }

    @Test
    public void shouldGetEpics() {
        taskManager.deleteAllEpics();
        taskManager.addEpic(firstEpic);
        taskManager.addEpic(secondEpic);
        assertEquals(2, taskManager.getEpics().size());
    }

    @Test
    public void shouldDeleteEpic() {
        taskManager.addEpic(secondEpic);
        int test = secondEpic.getId();
        taskManager.deleteEpic(test);
        assertNull(taskManager.getEpic(test));
    }

    @Test
    public void shouldNotDeleteEpic() {
        taskManager.addEpic(secondEpic);
        int test = secondEpic.getId();
        taskManager.deleteEpic(99999);
        assertEquals(secondEpic, taskManager.getEpic(test));
    }

    @Test
    public void shouldDeleteAllEpics() {
        taskManager.addEpic(firstEpic);
        taskManager.addEpic(secondEpic);
        List<Epic> testList = new ArrayList<>();
        taskManager.deleteAllEpics();
        assertEquals(testList ,taskManager.getEpics());
    }
//Subtasks
    @Test
    public void shouldAddSubtask() {
    taskManager.addSubtask(firstSubtask);
    assertEquals(firstSubtask, taskManager.getSubtask(firstSubtask.getId()));
}

    @Test
    public void shouldUpdateSubtask() {
        taskManager.addSubtask(firstSubtask);
        firstSubtask.setDescription("new_Description");
        taskManager.updateSubtask(firstSubtask);
        assertEquals("new_Description", firstSubtask.getDescription());
    }

    @Test
    public void shouldGetSubtask() {
        taskManager.addSubtask(firstSubtask);
        assertNotNull(firstSubtask, "Заданный Task не найден!");
        assertEquals(firstSubtask, taskManager.getSubtask(firstSubtask.getId()));
    }

    @Test
    public void shouldNotGetSubtask() {
        taskManager.addSubtask(firstSubtask);
        assertNotNull(firstSubtask, "Заданный Task не найден!");
        assertNull(taskManager.getSubtask(999999));
    }

    @Test
    public void shouldGetSubtasks() {
        taskManager.deleteAllSubtask();
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        assertEquals(2, taskManager.getSubtasks().size());
    }

    @Test
    public void shouldDeleteSubtask() {
        taskManager.addEpic(firstEpic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        int test = firstSubtask.getId();
        taskManager.deleteSubtask(test);
        assertEquals(Status.DONE, firstEpic.getStatus());
        assertNull(taskManager.getSubtask(test));
    }

    @Test
    public void shouldNotDeleteSubtask() {
        taskManager.addSubtask(firstSubtask);
        int test = firstSubtask.getId();
        taskManager.deleteSubtask(99999);
        assertEquals(firstSubtask, taskManager.getSubtask(test));
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        taskManager.addEpic(firstEpic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        assertEquals(Status.IN_PROGRESS, firstEpic.getStatus());
        List<Epic> testList = new ArrayList<>();
        taskManager.deleteAllSubtask();
        assertEquals(Status.NEW, firstEpic.getStatus());
        assertArrayEquals(testList.toArray() ,taskManager.getSubtasks().toArray());
    }

    @Test
    public void shouldGetEpicSubtasks() {
        List<Subtask> epicSubtasksTest = new ArrayList<>();
        taskManager.addEpic(firstEpic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        epicSubtasksTest.add(firstSubtask);
        epicSubtasksTest.add(secondSubtask);
        assertArrayEquals(epicSubtasksTest.toArray(), taskManager.getEpicSubtasks(firstEpic.getId()).toArray());
    }

    @Test
    public void shouldNotGetEpicSubtasks() {
        List<Subtask> epicSubtasksTest = new ArrayList<>();
        taskManager.addEpic(secondEpic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        assertArrayEquals(epicSubtasksTest.toArray(), taskManager.getEpicSubtasks(secondEpic.getId()).toArray());
    }

    @Test
    public void shouldCheckEpicStatus() {
        taskManager.addEpic(firstEpic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        firstSubtask.setStatus(Status.DONE);
        taskManager.updateEpic(firstEpic);
        assertEquals(Status.DONE, firstEpic.getStatus());
        firstSubtask.setStatus(Status.NEW);
        secondSubtask.setStatus(Status.NEW);
        taskManager.updateEpic(firstEpic);
        assertEquals(Status.NEW, firstEpic.getStatus());
    }
    //History
    @Test
    public void shouldAddHistoryToFile() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        taskManager.addTask(firstTask);
        taskManager.addEpic(secondEpic);

        taskManager.getTask(firstTask.getId());
        taskManager.getEpic(secondEpic.getId());
        assertNotNull(taskManager.getHistory());
        assertEquals(2, taskManager.getHistory().size());

        taskManager.deleteTask(firstTask.getId());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldGetTaskByPriority() {
        Set<Task> expected = new TreeSet<>((o1, o2) -> {
            if ((o1.getStartTime() != null) && (o2.getStartTime() != null)) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            } else if (o1.getStartTime() == null) {
                return 1;
            } else if (o2.getStartTime() == null) {
                return -1;
            } else {
                return 0;
            }
        });
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtask();

        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());

        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);
        expected.addAll(taskManager.getTasks());
        expected.addAll(taskManager.getSubtasks());

        List<Task> actual = taskManager.getTaskByPriority();

        assertEquals(expected.stream().collect(Collectors.toList()), actual);
    }
}