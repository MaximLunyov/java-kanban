package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> epicSubtasks;

    public Epic(String title, String description) {
        super(title, description,Status.NEW);
        epicSubtasks = new ArrayList<>();
        this.taskTypeList = TaskTypeList.EPIC;
    }

    public Epic(String title, String description, int id) {
        super(title, description,Status.NEW);
        epicSubtasks = new ArrayList<>();
        this.taskTypeList = TaskTypeList.EPIC;
        this.id = id;
    }

    public Epic(String title, String description, int id, Status status) {
        super(title, description,Status.NEW);
        epicSubtasks = new ArrayList<>();
        this.taskTypeList = TaskTypeList.EPIC;
        this.id = id;
        this.status = status;
    }

    public int getEpicId() {
        return id;
    }

    public void addSubtaskId() {

    }

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }


}