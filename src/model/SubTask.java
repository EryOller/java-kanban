package model;

public class SubTask extends Task{
    protected Epic epic;
    private final TaskType type = TaskType.SUBTASK;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic != null) {
            this.epic = epic;
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", description='" + this.description + '\'' +
                ", status='" + this.status + '\'' +
                ", epic=" + epic +
                '}';
    }

    @Override
    public TaskType getType() {
        return type;
    }
}
