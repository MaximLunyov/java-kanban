package tasks;

public class Task {
    protected int id;
    protected String title;
    protected String status;

    public Task(String title, String status) {
        this.title = title;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ЗАДАЧА{" +
                "№=" + id +
                ", Название='" + title + '\'' +
                ", Статус='" + status + '\'' +
                '}';
    }
}