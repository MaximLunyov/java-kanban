package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> epicSubtasks;

    public Epic(String title) {
        super(title, "");
        epicSubtasks = new ArrayList<>();
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