package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_NUMBER_OF_RECORDS = 10;
    private static LinkedList<Task> listHistory =  new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return listHistory;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (listHistory.size() == MAX_NUMBER_OF_RECORDS) {
                listHistory.remove(0);
            }
            listHistory.add(task);
        }
    }
}
