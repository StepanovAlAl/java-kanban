package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int createTask(Task task);

    int createSubtask(Subtask subtask);

    int createEpic(Epic epic);

    List<Task> getAllTasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    List<Task> getPrioritizedTasks();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void updateEpicTime(Epic epic);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    ArrayList<Task> getHistory();
}
