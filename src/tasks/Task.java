package tasks;

public class Task {
    protected int id;
    protected String title;
    protected Status status;
    protected String description;
    protected TaskTypeList taskTypeList;


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

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskTypeList = TaskTypeList.TASK;
        this.id = id;
    }

    public Task(String title, String description, Status status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskTypeList = TaskTypeList.TASK;
        this.id = id;
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


    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", id, taskTypeList, title, status, description, "");
    }
}