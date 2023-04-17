package manager;

import exeptions.ManagerCreateException;
import exeptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;
    private String fileName;

    public FileBackedTasksManager(File file) {
        this.file = file;

        fileName = "C:\\Users\\Laptop\\IdeaProjects\\java-kanban\\workHistory.csv";

        file = new File(fileName);
        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerCreateException("Ошибка! Файл не может быть создан." + e);
            }
        } else {
            String content = readFileContentsOrNull(fileName);
            String[] lines = content.split("\r?\n");
            boolean isEmptyLine = false;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                String[] parts = line.split(",");

                if (parts[0] != null) {
                    if (parts[0].equals("")) {
                    isEmptyLine = true;
                    }
                    String title;
                    String status;
                    String description;
                    String subtaskEpic;

                    if (!isEmptyLine && parts[1].equals("TASK")) {
                        title = parts[2];
                        status = parts[3];
                        description = parts[4];
                        Task task = new Task(title, description, checkStatus(status));
                        addTask(task);
                    } else if (!isEmptyLine && parts[1].equals("EPIC")) {
                        title = parts[2];
                        description = parts[4];
                        Epic epic = new Epic(title, description);
                        addEpic(epic);
                    } else if (!isEmptyLine && parts[1].equals("SUBTASK")) {
                        title = parts[2];
                        status = parts[3];
                        description = parts[4];
                        subtaskEpic = parts[5];
                        Subtask subtask = new Subtask(title, description, checkStatus(status), super.getEpic(Integer.parseInt(subtaskEpic)));
                        addSubtask(subtask);
                    }
                }
            }
        }
    }

    public Status checkStatus(String status) {
        Status status1 = Status.NEW;
        if (status.equals("NEW")) {
            status1 = Status.NEW;
        }
        if (status.equals("NEW")) {
            status1 = Status.IN_PROGRESS;
        }
        if (status.equals("NEW")) {
            status1 = Status.DONE;
        }
        return status1;
    }

    public String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл. Возможно файл не находится в нужной директории.");
            return null;
        }
    }


    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public List<String> historyForFile() {
        return super.historyForFile();
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {

            writer.write("id,type,name,status,description,epic\n");
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getTasksMap();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, tasks.get(id).toStringFromFile());
            }

            HashMap<Integer, Epic> epics = super.getEpicsMap();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, epics.get(id).toStringFromFile());
            }

            HashMap<Integer, Subtask> subtasks = super.getSubtasksMap();
            for (Integer id : subtasks.keySet()) {
                allTasks.put(id, subtasks.get(id).toStringFromFile());
            }


            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }
            writer.write("\n");

            for (int i = 0; i < historyForFile().size(); i++) {
                writer.write(historyForFile().get(i) + ",");
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
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }


}
