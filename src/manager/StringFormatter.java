package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public final class StringFormatter {
    private StringFormatter() {
    }

    public static String toString(Task task) {
        TaskType type = task.getType();

        String epicId = "";
        if (type == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        String startTimeStr = "";
        if (task.getStartTime() != null) {
            startTimeStr = task.getStartTime().toString();
        }

        String durationStr = "";
        if (task.getDuration() != null) {
            durationStr = String.valueOf(task.getDuration().toMinutes());
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                startTimeStr,
                durationStr);
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            default: //SUBTASK
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(name, description, epicId);
                break;
        }

        task.setId(id);
        task.setStatus(status);

        if (parts.length > 6 && !parts[6].isEmpty()) {
            LocalDateTime startTime = LocalDateTime.parse(parts[6]);
            task.setStartTime(startTime);
        }

        if (parts.length > 7 && !parts[7].isEmpty()) {
            Duration duration = Duration.ofMinutes(Long.parseLong(parts[7]));
            task.setDuration(duration);
        }

        return task;
    }
}
