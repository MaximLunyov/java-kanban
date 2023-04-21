package tasks;

public class Subtask extends Task {
    protected Epic epic;
    protected int epicIdH;

    public Subtask(String title, String description, Status status) {
        super(title, description ,status);
    }

    public Subtask(String title, String description, Status status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
        this.taskTypeList = TaskTypeList.SUBTASK;
        this.id = id;
    }

    public Subtask(int id, String title, String description, Status status,  int epicId) {
        super(title, description, status);
        this.id = id;
        this.taskTypeList = TaskTypeList.SUBTASK;
        this.epicIdH = epicId;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public String toStringHistory() {
        return String.format("%s,%s,%s,%s,%s,%s,", id, taskTypeList, title, status, description, epicIdH);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,", id, taskTypeList, title, status, description, epic.getId());
    }
}