package tests;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Test1", "description1");
    }

    @Test
    public void shouldHaveStatusNew() {
        taskManager.addEpic(epic);
        assertEquals("NEW", epic.getStatus().name());
    }

    @Test
    public void shouldHaveSubtasksStatusNew() {
        Subtask subtask1 = new Subtask("TestSubtask1", "desc1", Status.NEW, epic, 10, LocalDateTime.MAX);
        Subtask subtask2 = new Subtask("TestSubtask2", "desc2", Status.NEW, epic, 10, LocalDateTime.MAX);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals("NEW", epic.getStatus().name());
    }

    @Test
    public void shouldHaveSubtasksStatusDone() {
        Subtask subtask1 = new Subtask("TestSubtask1", "desc1", Status.DONE, epic,10, LocalDateTime.MAX);
        Subtask subtask2 = new Subtask("TestSubtask2", "desc2", Status.DONE, epic,10, LocalDateTime.MAX);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals("DONE", epic.getStatus().name());
    }

    @Test
    public void shouldHaveSubtasksStatusNewAndDone() {
        Subtask subtask1 = new Subtask("TestSubtask1", "desc1", Status.NEW, epic,10, LocalDateTime.MAX);
        Subtask subtask2 = new Subtask("TestSubtask2", "desc2", Status.DONE, epic,10, LocalDateTime.MAX);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals("IN_PROGRESS", epic.getStatus().name());
    }

    @Test
    public void shouldHaveSubtasksStatusInProgress() {
        Subtask subtask1 = new Subtask("TestSubtask1", "desc1", Status.IN_PROGRESS, epic,10, LocalDateTime.MAX);
        Subtask subtask2 = new Subtask("TestSubtask2", "desc2", Status.IN_PROGRESS, epic,10, LocalDateTime.MAX);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals("IN_PROGRESS", epic.getStatus().name());
    }

}