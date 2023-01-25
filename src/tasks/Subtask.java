package tasks;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String status) {
        super(title, status);
    }

    public Subtask(String title,  String status, Epic epic) {
        super(title, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "№=" + id +
                ", Название='" + title + '\'' +
                ", Статус='" + status + '\'' +
                '}';
    }
}