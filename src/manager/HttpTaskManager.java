package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
import servers.KVTaskClient;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(String url) {
        this(url, false);
    }

    public HttpTaskManager(String url, boolean load) {
        super(new File("/java-kanban/workHistory.csv"));
        gson = Managers.getGson();
        client = new KVTaskClient(url);
        if (load) {
            load();
        }
    }

    @Override
    public void save() {

        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);


        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }

    private void load() {
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        System.out.println(epics.values());
        ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        int generatorId = 0;

        for (Task task : tasks) {
            final int id = task.getId();

            if (id > generatorId) {
                generatorId = id;
            }
            this.tasks.put(id, task);

            sortedTasks.add(task);
        }

        for (Epic epic : epics) {
            final int id = epic.getId();

            if (id > generatorId) {
                generatorId = id;
            }
            this.epics.put(id, epic);

            sortedTasks.add(epic);
        }

        for (Subtask subtask : subtasks) {
            final int id = subtask.getId();

            if (id > generatorId) {
                generatorId = id;
            }
            this.tasks.put(id, subtask);

            sortedTasks.add(subtask);
        }
    }
}


