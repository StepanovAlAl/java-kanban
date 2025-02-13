package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_MAX_SIZE = 10;
    private ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return; // Игнорируем null и не добавляем его в историю
        }

        if (history.size() >= HISTORY_MAX_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}