package manager;

import model.*;

import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected int nextId = 1;
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
    );

    @Override
    public int createTask(Task task) {
        if (task == null) return -1;
        if (hasAnyTimeOverlap(task)) {
            throw new ManagerSaveException("Задача пересекается с другой!");
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask == null) return -1;
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new ManagerSaveException("Эпик для подзадачи не существует");
        }

        if (hasAnyTimeOverlap(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается с другой!");
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTime(epic);

        return subtask.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic == null) return -1;

        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    protected void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) return;

        Task oldTask = tasks.get(task.getId());
        if (hasTimeConflict(task, oldTask)) {
            throw new ManagerSaveException("Новое время задачи пересекается с другими");
        }

        removeFromPrioritizedTasks(oldTask);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return;

        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (hasTimeConflict(subtask, oldSubtask)) {
            throw new ManagerSaveException("Новое время подзадачи пересекается с другими");
        }

        removeFromPrioritizedTasks(oldSubtask);
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;

        Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritizedTasks(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritizedTasks(subtask);
            historyManager.remove(id);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove((Integer) id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            historyManager.remove(id);
            List<Subtask> epicSubtasks = new ArrayList<>();
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    epicSubtasks.add(subtask);
                }
            }

            for (Subtask subtask : epicSubtasks) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                removeFromPrioritizedTasks(subtask);
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            removeFromPrioritizedTasks(tasks.get(taskId));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
            removeFromPrioritizedTasks(subtasks.get(subtaskId));
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();//Переиспользуем

        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    public void updateEpicTime(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epic.getId());

        if (epicSubtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration duration = Duration.ZERO;

        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStartTime() != null) {
                if (start == null || subtask.getStartTime().isBefore(start)) {
                    start = subtask.getStartTime();
                }

                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (end == null || subtaskEnd.isAfter(end)) {
                    end = subtaskEnd;
                }

                duration = duration.plus(subtask.getDuration());
            }
        }

        epic.setStartTime(start);
        epic.setDuration(duration);
        epic.setEndTime(end);
    }

    private boolean hasTimeConflict(Task newTask, Task existingTask) {
        if (newTask.getStartTime() == null || existingTask.getStartTime() == null) {
            return false;
        }

        // Если время не изменилось - нет конфликта
        if (newTask.getStartTime().equals(existingTask.getStartTime()) &&
                newTask.getDuration().equals(existingTask.getDuration())) {
            return false;
        }

        return hasTimeOverlap(newTask, existingTask);
    }

    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1 == task2) return false;
        if (task1.getStartTime() == null || task2.getStartTime() == null) return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private boolean hasAnyTimeOverlap(Task task) {
        if (task.getStartTime() == null) return false;

        return prioritizedTasks.stream()
                .anyMatch(existingTask -> hasTimeOverlap(task, existingTask));
    }

}