package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected int id;
    protected String title;
    protected Status status;
    protected String description;
    protected TaskTypeList taskTypeList;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    protected int duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;


    public TaskTypeList getTaskTypeList() {
        return taskTypeList;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task(String title, String description, Status status, int duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskTypeList = TaskTypeList.TASK;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = getEndTime();
    }

    public Task(String title, String description, Status status, int id, int duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskTypeList = TaskTypeList.TASK;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",id, duration, startTime, getEndTime(),
                taskTypeList, title, status, description, "");
    }

    public TaskTypeList getType() {
        return taskTypeList;
    }
}