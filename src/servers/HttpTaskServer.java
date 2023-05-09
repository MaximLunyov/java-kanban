package servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import servers.adapters.FileAdapter;
import servers.adapters.HistoryManagerAdapter;
import servers.adapters.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager = Managers.getDefault("http://localhost:8078", "key");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handle);
    }
    /*Привет, Патимат!
    * С Майскими праздниками тебя!
    * Написал код для сервера, но возникла проблема с выполнением запросов:
    * manager вызывает "зависание" ответа сервера, код не выполняется корректно.
    * Не могу понять почему вызов методов manager, который наследуется от ранее рабочих классов,
    * вызывает зависание, не добавляя в список задач новые.
    * Через "костыль" с получением списка задач и отдельным прогоном по его id, смог давать ответ о наличии задач
    * в корректном виде.
    * Подскажи пожалуйста, как правильно реализовать вызов manager, чтобы он не "ломал" сервер и работали его методы?
    * */


    public void handle(HttpExchange exchange) throws IOException {
        String response;
        String path = exchange.getRequestURI().getPath();
        String data = exchange.getRequestURI().getQuery();
        switch (path) {
            case "/tasks/task" -> handleTask(exchange);
            case "/tasks/subtask" -> handleSubtask(exchange);
            case "/tasks/epic" -> handleEpic(exchange);
            case "/tasks/subtask/epic" -> {
                int id = Integer.parseInt(data.split("=")[1]);
                List<Subtask> subtasks = manager.getEpicSubtasks(id);
                if (subtasks == null) {
                    exchange.sendResponseHeaders(404, 0);
                    response = "Эпик не найден.";
                } else {
                    response = GSON.toJson(subtasks);
                    exchange.sendResponseHeaders(200, 0);
                }
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks/history" -> {
                response = GSON.toJson(manager.getHistory());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks" -> {
                response = GSON.toJson(manager.getTaskByPriority());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }
    //Tasks
    private void handleTask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleTaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleTaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleTaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private String handleTaskGet(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response = "";
        int id = 0;
        if (data == null) {
            response = GSON.toJson(manager.getTasks());
            h.sendResponseHeaders(200, 0);
        } else {
            id = Integer.parseInt(data.split("=")[1]);
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < manager.getTasks().size(); i++) {
                ids.add(manager.getTasks().get(i).getId());
            }
            if (ids.contains(id)) {
                Task task = manager.getTasks().get(id);
                response = GSON.toJson(task);
                h.sendResponseHeaders(200, 0);
            } else {
                response = "Задача не найдена.";
                h.sendResponseHeaders(404, 0);
            }
        }
        return response;
    }

    private String handleTaskPost(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;

        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        String body = readText(h);
        System.out.println(body);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "Задача отсутствует в теле запроса.";
        } else {
            Task task = GSON.fromJson(body, Task.class);
            System.out.println(task);
            if (data == null) {
                manager.addTask(task);
                System.out.println(manager.getTasks());
                System.out.println("Ya tut 2");
                int taskId = task.getId();
                if (taskId < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Задача не была добавлена.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Задача добавлена успешно.";
                }
            } else {
                task.setId(id);
                if (task.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить задачу.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleTaskDelete(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        if (data == null) {
            manager.deleteAllTasks();
            h.sendResponseHeaders(200, 0);
            response = "Задачи удалены.";
        } else {
            if (manager.getTask(id) == null) {
                h.sendResponseHeaders(404, 0);
                response = "Задача не найдена.";
            } else {
                manager.deleteTask(id);
                h.sendResponseHeaders(200, 0);
                response = "Задача " + id + " удалена.";
            }
        }
        return response;
    }

    //Epics

    private void handleEpic(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleEpicGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleEpicPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleEpicDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private String handleEpicGet(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        if (data == null) {
            response = GSON.toJson(manager.getEpics());
            h.sendResponseHeaders(200, 0);
        } else {
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                h.sendResponseHeaders(404, 0);
                response = "Epic задача не найдена.";
            } else {
                response = GSON.toJson(epic);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleEpicPost(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "Epic задача отсутствует в теле запроса.";
        } else {
            Epic epic = GSON.fromJson(body, Epic.class);
            if (data == null) {
                manager.addEpic(epic);
                if (epic.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Epic задача не добавлена.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Epic задача добавлена.";
                }
            } else {
                epic.setId(id);
                manager.updateEpic(epic);
                if (epic.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить Epic задачу.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Epic задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleEpicDelete(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        if (data == null) {
            manager.deleteAllEpics();
            h.sendResponseHeaders(200, 0);
            response = "Все эпики удалены.";
        } else {
            if (manager.getEpic(id) == null) {
                h.sendResponseHeaders(404, 0);
                response = "Эпик не найден.";
            } else {
                manager.deleteEpic(id);
                h.sendResponseHeaders(200, 0);
                response = "Эпик " + id + " удален.";
            }
        }
        return response;
    }

    //Subtasks

    private void handleSubtask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleSubtaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleSubtaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleSubtaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private String handleSubtaskGet(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        if (data == null) {
            response = GSON.toJson(manager.getSubtasks());
            h.sendResponseHeaders(200, 0);
        } else {
            Subtask subtask = manager.getSubtask(id);
            if (subtask == null) {
                h.sendResponseHeaders(404, 0);
                response = "Сабтаск не найден.";
            } else {
                response = GSON.toJson(subtask);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleSubtaskPost(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "Сабтаск отсутствует в теле запроса.";
        } else {
            Subtask subtask = GSON.fromJson(body, Subtask.class);
            if (data == null) {
                manager.addSubtask(subtask);
                if (subtask.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Сабтаск не добавлен.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Сабстаск добавлен.";
                }
            } else {
                subtask.setId(id);
                manager.updateSubtask(subtask);
                if (subtask.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить Сабтаск.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "Сабтаск " + id + " обновлен.";
                }
            }
        }
        return response;
    }
    private String handleSubtaskDelete(HttpExchange h) throws IOException {
        String data = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (data != null) {
            id = Integer.parseInt(data.split("=")[1]);
        }
        if (data == null) {
            manager.deleteAllSubtask();
            h.sendResponseHeaders(200, 0);
            response = "Все Сабтаски удалены.";
        } else {
            if (manager.getSubtask(id) == null) {
                h.sendResponseHeaders(404, 0);
                response = "Сабтаск не найден.";
            } else {
                manager.deleteSubtask(id);
                h.sendResponseHeaders(200, 0);
                response = "Сабтаск " + id + " удален.";
            }
        }
        return response;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseBody().write(resp);
    }
}

