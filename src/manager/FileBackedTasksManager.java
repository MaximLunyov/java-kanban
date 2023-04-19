package manager;

import exeptions.ManagerCreateException;
import exeptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

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
    //Привет, Патимат! Спасибо большое за ответ на мои вопросы в ревью. Стало намного понятнее:)
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
        List<Integer> listOfIds = new ArrayList<>();

        for (int i = 1; i < work.length; i++) {
            String line = work[i];
            if (!line.isEmpty() && line.contains("TASK")) {
                final Task task = taskFromString(line, taskManager);
                if (task.getTaskTypeList().equals(TaskTypeList.TASK)) {
                    listOfIds.add(task.getId());
                    taskManager.addTask(task);
                }
            }

            if (!line.isEmpty() && line.contains("EPIC")) {
                final Epic epic = (Epic) taskFromString(line, taskManager);
                if (epic.getTaskTypeList().equals(TaskTypeList.EPIC)) {
                    listOfIds.add(epic.getId());
                    taskManager.addEpic(epic);
                }
            }

            if (!line.isEmpty() && line.contains("SUBTASK")) {

                final Subtask subtask = (Subtask) taskFromString(line, taskManager);
                if (subtask.getTaskTypeList().equals(TaskTypeList.SUBTASK)) {
                    listOfIds.add(subtask.getId());
                    taskManager.addSubtask(subtask);
                }
            }

            if (!line.isEmpty() && (!line.contains("TASK") && !line.contains("EPIC") && !line.contains("SUBTASK"))) {
                List<Integer> history = historyFromString(line);
                for (Integer ids : history) {
                    taskManager.getTask(ids);
                    taskManager.getEpic(ids);
                    taskManager.getSubtask(ids);
                }
            }
        }
        return taskManager;
    }


    static Task taskFromString(String value, FileBackedTasksManager taskManager) {
        String[] parts = value.split(","); //id,type,name,status,description,epic
        int id;
        String title;
        String status;
        String description;
        int subtaskEpic;
        if (parts[1].equals("TASK")) {
            id = Integer.parseInt(parts[0]);
            title = parts[2];
            status = parts[3];
            description = parts[4];
            return new Task(title, description, Status.valueOf(status),id);
        } else if (parts[1].equals("EPIC")) {
            id = Integer.parseInt(parts[0]);
            title = parts[2];
            description = parts[4];
            return new Epic(title, description, id);
        } else if (parts[1].equals("SUBTASK")) {
            id = Integer.parseInt(parts[0]);
            title = parts[2];
            status = parts[3];
            description = parts[4];
            subtaskEpic = Integer.parseInt(parts[5]);
            return new Subtask(title, description, Status.valueOf(status),
                    taskManager.getEpic(subtaskEpic), id);
        } else {
            return null;
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
                allTasks.put(id, subtasks.get(id).toString());
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
