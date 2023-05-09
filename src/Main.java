import manager.*;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kVServer = new KVServer();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        kVServer.start();
        httpTaskServer.start();
        /*InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addTask(new Task("Помыть машину", "описание1",Status.NEW,
                60, LocalDateTime.of(2023, 5, 2, 16, 20)));*/
    }
}
