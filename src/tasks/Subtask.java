package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    protected Epic epic;
    protected int epicIdH;

    public Subtask(String title, String description, Status status) {
        super(title, description ,status, 0, LocalDateTime.MAX);
    }

    public Subtask(String title, String description, Status status,
                   Epic epic, int duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.epic = epic;
        this.taskTypeList = TaskTypeList.SUBTASK;
        this.id = id;
    }

    public Subtask(int id, String title, String description, Status status,
                   int epicId, int duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(title, description, status, duration, startTime);
        this.id = id;
        this.taskTypeList = TaskTypeList.SUBTASK;
        this.epicIdH = epicId;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public String toStringHistory() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, duration,startTime, endTime, taskTypeList, title, status, description, epicIdH);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, duration,startTime, endTime, taskTypeList, title, status, description, epic.getId());
    }
}