public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();

        int task1 = manager.createTask(new Task(0, "Task 1", "Description 1", Status.NEW));
        int task2 = manager.createTask(new Task(0, "Task 2", "Description 2", Status.NEW));

        int epic1 = manager.createEpic(new Epic(0, "Epic 1", "Description Epic 1", Status.NEW));
        int subtask1 = manager.createSubtask(new Subtask(0, "Subtask 1", "Description Subtask 1", Status.NEW, epic1));
        int subtask2 = manager.createSubtask(new Subtask(0, "Subtask 2", "Description Subtask 2", Status.NEW, epic1));

        System.out.println("Tasks: " + manager.getAllTasks());
        System.out.println("Subtasks: " + manager.getAllSubtasks());
        System.out.println("Epics: " + manager.getAllEpics());

        manager.deleteTask(task1);
        manager.deleteEpic(epic1);

        System.out.println("After deletion:");
        System.out.println("Tasks: " + manager.getAllTasks());
        System.out.println("Subtasks: " + manager.getAllSubtasks());
        System.out.println("Epics: " + manager.getAllEpics());
    }
}
