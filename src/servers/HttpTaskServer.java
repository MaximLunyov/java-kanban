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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault("http://localhost:8078"));
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handle);
    }

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
                if (subtasks == null || manager.getEpic(id) == null) {
                    exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                    response = "Эпик не найден.";
                } else {
                    response = gson.toJson(subtasks);
                    exchange.sendResponseHeaders(HTTP_OK, 0);
                }
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks/history" -> {
                response = gson.toJson(manager.getHistory());
                exchange.sendResponseHeaders(HTTP_OK, 0);
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks" -> {
                response = gson.toJson(manager.getTaskByPriority());
                exchange.sendResponseHeaders(HTTP_OK, 0);
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
        String response;
        int id = 0;
        if (data == null) {
            response = gson.toJson(manager.getTasks());
            h.sendResponseHeaders(HTTP_OK, 0);
        } else {
            id = Integer.parseInt(data.split("=")[1]);
            Task task = manager.getTask(id);
            if (task != null) {
                response = gson.toJson(task);
                h.sendResponseHeaders(HTTP_OK, 0);
            } else {
                response = "Задача не найдена.";
                h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
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
        if (body.isBlank()) {
            h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
            response = "Задача отсутствует в теле запроса.";
        } else {
            Task task = gson.fromJson(body, Task.class);
            if (data == null) {
                manager.addTask(task);
                int taskId = task.getId();
                if (taskId < 0) {
                    h.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    response = "Задача не была добавлена.";
                } else {
                    h.sendResponseHeaders(HTTP_CREATED, 0);
                    response = "Задача добавлена успешно.";
                }
            } else {
                task.setId(id);
                if (task.getId() < 0) {
                    h.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    response = "Не удалось обновить задачу.";
                } else {
                    h.sendResponseHeaders(HTTP_CREATED, 0);
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
            h.sendResponseHeaders(HTTP_OK, 0);
            response = "Задачи удалены.";
        } else {
            if (manager.getTask(id) == null) {
                h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                response = "Задача не найдена.";
            } else {
                manager.deleteTask(id);
                h.sendResponseHeaders(HTTP_OK, 0);
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
        if (data == null) {

            response = gson.toJson(manager.getEpics());
            h.sendResponseHeaders(HTTP_OK, 0);
        } else {
            id = Integer.parseInt(data.split("=")[1]);
            Epic epic = manager.getEpic(id);
            if (epic != null) {
                response = gson.toJson(epic);
                h.sendResponseHeaders(HTTP_OK, 0);
            } else {
                response = "Эпик не найден.";
                h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
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
            h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
            response = "Эпик отсутствует в теле запроса.";
        } else {
            Epic epic = gson.fromJson(body, Epic.class);
            if (data == null) {
                manager.addEpic(epic);
                if (epic.getId() < 0) {
                    h.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    response = "Эпик не добавлен.";
                } else {
                    h.sendResponseHeaders(HTTP_CREATED, 0);
                    response = "Эпик добавлен.";
                }
            } else {
                epic.setId(id);
                manager.updateEpic(epic);
                if (epic.getId() < 0) {
                    h.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    response = "Не удалось обновить Эпик.";
                } else {
                    h.sendResponseHeaders(HTTP_CREATED, 0);
                    response = "Эпик " + id + " обновлен.";
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
            h.sendResponseHeaders(HTTP_OK, 0);
            response = "Все эпики удалены.";
        } else {
            if (manager.getEpic(id) == null) {
                h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                response = "Эпик не найден.";
            } else {
                manager.deleteEpic(id);
                h.sendResponseHeaders(HTTP_OK, 0);
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
            response = gson.toJson(manager.getSubtasks());
            h.sendResponseHeaders(HTTP_OK, 0);
        } else {
            Subtask subtask = manager.getSubtask(id);
            if (subtask == null) {
                h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                response = "Сабтаск не найден.";
            } else {
                response = gson.toJson(subtask);
                h.sendResponseHeaders(HTTP_OK, 0);
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
            h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
            response = "Сабтаск отсутствует в теле запроса.";
        } else {
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (data == null) {
                manager.addSubtask(subtask);

                if (subtask.getId() < 0) {
                    h.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    response = "Сабтаск не добавлен.";
                } else {
                    h.sendResponseHeaders(HTTP_CREATED, 0);
                    response = "Сабстаск добавлен.";
                }
            } else {
                subtask.setId(id);
                manager.updateSubtask(subtask);
                if (subtask.getId() < 0) {
                    h.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    response = "Не удалось обновить Сабтаск.";
                } else {
                    h.sendResponseHeaders(HTTP_CREATED, 0);
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
            h.sendResponseHeaders(HTTP_OK, 0);
            response = "Все Сабтаски удалены.";
        } else {
            if (manager.getSubtask(id) == null) {
                h.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                response = "Сабтаск не найден.";
            } else {
                manager.deleteSubtask(id);
                h.sendResponseHeaders(HTTP_OK, 0);
                response = "Сабтаск " + id + " удален.";
            }
        }
        return response;
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Ссылка в браузере http://localhost:" + PORT + "/");
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

