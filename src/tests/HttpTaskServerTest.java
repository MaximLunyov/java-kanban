package tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HttpTaskServerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    Task firstTask;
    Task secondTask;
    Epic firstEpic;
    Epic secondEpic;
    Subtask firstSubtask;
    Subtask secondSubtask;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    Gson gson = Managers.getGson();

    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @BeforeEach
    public void putTaskForTesting() {
        firstTask = new Task("Тест 1", "описание1", Status.NEW, 10, LocalDateTime.of(2024, 5, 2, 23, 20));
        secondTask = new Task("Тест 2", "описание2", Status.DONE, 32, LocalDateTime.of(2024, 9, 2, 16, 20));
        firstEpic = new Epic("Epic 1", "desc3", 20, Status.NEW, 32,LocalDateTime.of(2023, 8, 2, 15, 20),LocalDateTime.of(2024, 9, 2, 16, 20));
        secondEpic = new Epic("Epic2", "desc4",21, Status.NEW, 32,LocalDateTime.of(2023, 8, 2, 16, 20),LocalDateTime.of(2024, 9, 2, 16, 20));
        firstSubtask = new Subtask("Subtask1", "desc5", Status.IN_PROGRESS, firstEpic,32, LocalDateTime.of(2023, 6, 2, 16, 20));
        secondSubtask = new Subtask("Subtask2", "desc6", Status.DONE, firstEpic,32, LocalDateTime.of(2023, 5, 2, 16, 20));
    }

    @AfterEach
    public void stopAllServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }



//Tasks
    @Test
    public void shouldSaveTask() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(firstTask, "/task");
        assertEquals(201, response.statusCode());
        assertEquals("Задача добавлена успешно.", response.body());
    }

    @Test
    public void shouldNotSaveEmptyTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        addTaskToServer(firstTask, "/task");
        secondTask.setId(0);
        HttpResponse<String> response = addTaskToServer(secondTask, "/task?id=0");

        assertEquals(201, response.statusCode());
        assertEquals("Задача 0 обновлена.", response.body());
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        addTaskToServer(firstTask, "/task");
        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=1"), handler);
        assertEquals(200, response.statusCode());
        assertEquals("Задача 1 удалена.", response.body());
    }

    @Test
    public void shouldSendErrorByTaskId() throws IOException, InterruptedException {
        addTaskToServer(firstTask, "/task");
        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");
        HttpResponse<String> response = client.send(createDeleteRequest("/task"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Задачи удалены.", response.body());
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        client.send(createDeleteRequest("/task"), handler);
        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");
        HttpResponse<String> response = client.send(createGetRequest("/task"), handler);
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        addTaskToServer(firstTask, "/task");
        HttpResponse<String> response = client.send(createGetRequest("/task?id=12"), handler);
        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task.getTitle(), firstTask.getTitle());
    }

    @Test
    public void shouldNotGetTaskById() throws IOException, InterruptedException {
        addTaskToServer(firstTask, "/task");
        HttpResponse<String> response = client.send(createGetRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    //Epics
    @Test
    public void shouldSaveEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(firstEpic, "/epic");

        assertEquals(201, response.statusCode());
        assertEquals("Эпик добавлен.", response.body());
    }

    @Test
    public void shouldNotSaveEmptyEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Эпик отсутствует в теле запроса.", response.body());
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        firstEpic.setId(3);
        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=3"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Эпик 3 удален.", response.body());
    }

    @Test
    public void shouldNotDeleteEpicById() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Эпик не найден.", response.body());
    }

    @Test
    public void shouldDeleteAllEpics() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(secondEpic, "/epic");
        HttpResponse<String> response = client.send(createDeleteRequest("/epic"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все эпики удалены.", response.body());
    }

    @Test
    public void shouldGetAllEpics() throws IOException, InterruptedException {
        client.send(createDeleteRequest("/epic"), handler);
        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(secondEpic, "/epic");

        HttpResponse<String> response = client.send(createGetRequest("/epic"), handler);
        List<Epic> taskList = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(2, taskList.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        HttpResponse<String> response = client.send(createGetRequest("/epic?id=12"), handler);
        Epic epic = gson.fromJson(response.body(), Epic.class);

        assertEquals(firstEpic.getTitle(), epic.getTitle());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetEpicById() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        HttpResponse<String> response = client.send(createGetRequest("/epic?id=10"), handler);

        assertEquals("Эпик не найден.", response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldSaveSubtask() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskToServer(firstSubtask, "/subtask");

        assertEquals(201, response.statusCode());
        assertEquals("Сабстаск добавлен.", response.body());
    }

    @Test
    public void shouldNotSaveEmptySubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Сабтаск отсутствует в теле запроса.", response.body());
    }

    @Test
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        firstSubtask.setDescription("for Test");
        addTaskToServer(firstSubtask, "/subtask");

        HttpResponse<String> response = addTaskToServer(secondSubtask, "/subtask?id=13");

        assertEquals(201, response.statusCode());
        assertEquals("Сабтаск 13 обновлен.", response.body());
    }

    @Test
    public void shouldNotDeleteSubtaskById() throws IOException, InterruptedException {
        addTaskToServer(firstSubtask, "/subtask");
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask?id=2"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Сабтаск не найден.", response.body());
    }

    @Test
    public void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        addTaskToServer(firstSubtask, "/subtask");
        HttpResponse<String> response = client.send(createDeleteRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все Сабтаски удалены.", response.body());
    }

    @Test
    public void shouldGetAllSubtasks() throws IOException, InterruptedException {
        addTaskToServer(firstSubtask, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetSubtaskById() throws IOException, InterruptedException {
        addTaskToServer(firstSubtask, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=222"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Сабтаск не найден.", response.body());
    }

    @Test
    public void shouldGetSubtasksInEpicByEpicId() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=12"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotGetSubtasksInEpicByEpicId() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=541"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Эпик не найден.", response.body());
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");
        client.send(createGetRequest("/task?id=3"), handler);
        client.send(createGetRequest("/task?id=4"), handler);

        HttpResponse<String> history = client.send(createGetRequest("/history"), handler);

        assertEquals(200, history.statusCode());
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        client.send(createDeleteRequest("/task"), handler);
        client.send(createDeleteRequest("/epic"), handler);
        client.send(createDeleteRequest("/subtask"), handler);

        firstTask.setStartTime(LocalDateTime.of(2023,1,1,10,0));
        secondTask.setStartTime(LocalDateTime.of(2023, 1, 2, 6,0));
        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");

        HttpResponse<String> prioritized = client.send(createGetRequest(""), handler);
        List<Task> tasks = gson.fromJson(prioritized.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(secondTask.getTitle(), tasks.get(1).getTitle());

        assertEquals(200, prioritized.statusCode());
    }

    public HttpRequest createGetRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().GET().uri(uri)
            .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
            .build();
}

    public HttpRequest createDeleteRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().DELETE().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpResponse<String> addTaskToServer(Task task, String path) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        String body = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        return client.send(request, handler);
    }
}





