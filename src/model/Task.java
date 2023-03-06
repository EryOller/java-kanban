package model;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected String status;
    private final String NEW_STATUS = "NEW";
    public final String IN_PROGRESS_STATUS = "IN_PROGRESS";
    public final String DONE_STATUS = "DONE";

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = NEW_STATUS;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
