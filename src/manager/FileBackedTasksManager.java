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

        fileName = "workHistory.csv";

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
                        Task task = new Task(title, description, Status.valueOf(status));
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
                        Subtask subtask = new Subtask(title, description, Status.valueOf(status),
                                super.getEpic(Integer.parseInt(subtaskEpic)));
                        addSubtask(subtask);
                    }
                }
            }
        }
    }

    Task fromString(String value) {
        return null;
    }


    static List<Integer> historyFromString(String value) {
        return null;
    }
    /*Привет, Патимат!
    Хотел бы поблагодарить тебя за ответы на вопросы и помощь с кодом в ревью в прошлых Спринтах.

    Исправил все недочеты, указанные тобой в прошлом ревью, кроме пункта с методом loadFromFile(File file).
    Уже довольно долго сижу над кодом, но не могу понять как правильно реализовать загрузку файла методом loadfFromFile,
    а не автоматически при запуске менеджера во время старта программы, как я сделал изначально.
    Как я понимаю, я должен вызвать статический метод, который мне вернет ранее созданный файл и дальше я его должен
    его прогнать через методы Task fromString(String value) и historyFromString(String value),
    чтобы заполнить менеджер данными из истории. Или же я понимаю не правильно?

    Пробовал сделать подобным образом:
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        String data;
        try {

            data = Files.readString(Path.of(Path.of(file.getAbsolutePath()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }
        String[] lines = data.split("\n");

        return fileBackedTasksManager;
    }

    Но данный код выбивает ошибку чтения файла.
    И не могу понять логику вызова метода loadFromFile - он должен вызываться автоматически при старте программы вместе
    с taskManager или же нужно вызвать отдельно класс FileBackedTasksManager fileBackedTasksManager
     = new FileBackedTasksManager(file), но в данном случае файл читается, но в него не идет запись из taskManager.
    Получился замкнутый круг, подскажи пожалуйста, как правильно из него выйти?
    */

    static FileBackedTasksManager loadFromFile(File file) {
        return null;
    }

    public String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл. Возможно файл не находится в нужной директории.");
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
