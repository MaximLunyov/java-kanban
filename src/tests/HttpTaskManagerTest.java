package tests;
import manager.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import servers.KVTaskClient;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    HttpTaskManager httpTaskManager;

    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void shouldLoadFromServer() {
        httpTaskManager = new HttpTaskManager("http://localhost:8078");
        assertEquals(2, httpTaskManager.getTasks().size());
        assertEquals(2, httpTaskManager.getEpics().size());
        assertEquals(6, httpTaskManager.getSubtasks().size());
    }
}



