package tasks;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, Status status) {
        super(title, description ,status);
    }

    public Subtask(String title, String description, Status status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
        this.taskTypeList = TaskTypeList.SUBTASK;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }


    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", id, taskTypeList, title, status, description, epic.getId());
    }
}