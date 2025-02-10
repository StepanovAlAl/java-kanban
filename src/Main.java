public class Main {

    public static void main(String[] args) {
        //Тестирование через объекты
        TaskManager manager = new TaskManager();

        // Создаем две задачи
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        int taskId1 = manager.createTask(task1);
        int taskId2 = manager.createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        int epicId1 = manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", epicId1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", epicId1);
        int subtaskId1 = manager.createSubtask(subtask1);
        int subtaskId2 = manager.createSubtask(subtask2);

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");
        int epicId2 = manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", epicId2);
        int subtaskId3 = manager.createSubtask(subtask3);

        // Распечатываем списки задач, подзадач и эпиков
        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        // Изменяем статусы задач и подзадач
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask3.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask3);

        // Распечатываем обновленные статусы
        System.out.println("\nОбновленные задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nОбновленные подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nОбновленные эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        // Удаляем одну задачу и один эпик
        manager.deleteTask(taskId1);
        manager.deleteEpic(epicId1);

        // Распечатываем списки после удаления
        System.out.println("\nСписки после удаления задачи и эпика:");
        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

    }
}
