package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> epicSubtasks;

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

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }


}