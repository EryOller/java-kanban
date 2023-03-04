package model;

public class SubTask extends Task{
    Epic epic;

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
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epic=" + epic +
                '}';
    }
}
