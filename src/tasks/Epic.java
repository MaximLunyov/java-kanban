package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> epicSubtasks;

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String title, String description) {
        super(title, description,Status.NEW, 0, LocalDateTime.of(2023, 1, 1,0,1));
        endTime = LocalDateTime.of(2023, 1, 1,0,1);
        epicSubtasks = new ArrayList<>();
        this.taskTypeList = TaskTypeList.EPIC;
    }

    public Epic(String title, String description, int id, Status status, int duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(title, description,Status.NEW, duration, startTime);
        epicSubtasks = new ArrayList<>();
        this.taskTypeList = TaskTypeList.EPIC;
        this.id = id;
        this.duration = duration;
        this.status = status;
        this.endTime = endTime;
    }

    public int getEpicId() {
        return id;
    }

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    public void addSubtask(int subtaskId) {
        epicSubtasks.add(subtaskId);
    }
}