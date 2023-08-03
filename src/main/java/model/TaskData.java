package model;

import java.util.List;

public class TaskData {
    private final List<Task> tasks;
    private final List<Integer> history;

    public TaskData(List<Task> tasks, List<Integer> history) {
        this.tasks = tasks;
        this.history = history;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Integer> getHistory() {
        return history;
    }
}