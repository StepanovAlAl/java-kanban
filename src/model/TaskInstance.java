package model;

public class TaskInstance extends Task {
    public TaskInstance(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getType() {
        return TaskType.TASK;
    }
}