package manager;

import exeptions.ManagerCreateException;
import exeptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;
    private String fileName;

    public FileBackedTasksManager(File file) {
        this.file = file;
        fileName = file.getName();
        file = new File(fileName);
            if (!file.isFile()) {
                try {
                    Files.createFile(Path.of(fileName));
                } catch (IOException e) {
                    throw new ManagerCreateException("Ошибка! Файл не может быть создан." + e);
                }
            } else {
                loadFromFile(this, file);
            }
    }

    static List<Integer> historyFromString(String value) {
        ArrayList<Integer> ids = new ArrayList<>();
        String[] work = value.split(",");
        for (String t : work) {
            ids.add(Integer.parseInt(t));
        }
        return ids;
    }

    public static FileBackedTasksManager loadFromFile(FileBackedTasksManager taskManager, File file) {

        String[] lines;
        try {
            final String csv = Files.readString(file.toPath());
            lines = csv.split(System.lineSeparator());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }

        String li = lines[0];
        String[] work = li.split("\r?\n");
       /* Привет, Патимат!
       * Я изначально и хотел заполнять хранилища, вместо вызова метода, потому что, если я правильно понимаю,
       * программа так будет быстрее компилироваться и работать, но забыл поинтересоваться как это сделать.
       * Еще раз спасибо за помощь!
       * */
        List<Integer> history = Collections.emptyList();
        int generatorId = 0;
        for (int i = 1; i < work.length; i++) {
            String line = work[i];
            if (line.isEmpty()) {
                history = historyFromString(work[i + 1]);
                break;
            }
            final Task task = taskFromString(line);
            final int id = task.getId();

            if (id > generatorId) {
                generatorId = id;
            }
            taskManager.addTaskToHistory(task);
        }
        for (Integer taskId : history) {
            taskManager.historyManager.add(taskManager.findTask(taskId));
        }

        taskManager.id = generatorId;
        return taskManager;
    }

    public Task findTask (int taskId) {
        if (tasks.get(taskId) != null) {
            return tasks.get(taskId);
        } else if (epics.get(taskId) != null) {
            return epics.get(taskId);
        } else {
            return subtasks.get(taskId);
        }
    }

    public static Task taskFromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskTypeList type = TaskTypeList.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4];
        if (type == TaskTypeList.TASK) {
            return new Task(name, description, status, id);
        } else if ((type == TaskTypeList.SUBTASK)) {
            final int epicId = Integer.parseInt(values[5]);
            return new Subtask(id, name, description, status, epicId); //(id, name, description, status, epicId)
        } else {
            return new Epic(name, description, id, status);
        }
    }

    public void addTaskToHistory (Task task) {
        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
        }
    }

    public void save() {
        try (Writer writer = new FileWriter("workHistory.csv")) {

            writer.write("id,type,name,status,description,epic\n");
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getTasksMap();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, tasks.get(id).toString());
            }

            HashMap<Integer, Epic> epics = super.getEpicsMap();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, epics.get(id).toString());
            }

            HashMap<Integer, Subtask> subtasks = super.getSubtasksMap();
            for (Integer id : subtasks.keySet()) {
                if (subtasks.get(id).getEpic() == null) {
                    allTasks.put(id, subtasks.get(id).toStringHistory());
                } else {
                    allTasks.put(id, subtasks.get(id).toString());
                }
            }

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }
            writer.write("\n");

            List<Task> history = super.getHistory();
            for (int i = 0; i < history.size(); i++) {
                writer.write(history.get(i).getId() + ",");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }




    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTask(int id) {
        save();
        return super.getTask(id);
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        save();
        return super.getEpic(id);
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        save();
        return super.getSubtask(id);
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }
}
