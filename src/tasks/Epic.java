package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> epicSubtasks;

    public Epic(String title, String description) {
        super(title, description,Status.NEW);
        epicSubtasks = new ArrayList<>();
        this.taskTypeList = TaskTypeList.EPIC;
    }

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "№=" + id +
                ", Название='" + title + '\'' +
                ", Статус='" + status + '\'' +
                '}';
    }


}