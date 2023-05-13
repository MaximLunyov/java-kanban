import manager.*;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {

        KVServer kVServer = new KVServer();
        kVServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

    }
}
