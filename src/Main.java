import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Testing + Source Tasks for work:");
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

        //--------

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Создать задачу");
            System.out.println("2. Создать подзадачу");
            System.out.println("3. Создать эпик");
            System.out.println("4. Показать все задачи");
            System.out.println("5. Показать все подзадачи");
            System.out.println("6. Показать все эпики");
            System.out.println("7. Обновить статус задачи");
            System.out.println("8. Удалить задачу");
            System.out.println("9. Удалить подзадачу");
            System.out.println("10. Удалить эпик");
            System.out.println("0. Выйти");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1://1. Создать задачу
                    System.out.println("Введите название задачи:");
                    String taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String taskDescription = scanner.nextLine();
                    manager.createTask(new Task(0, taskName, taskDescription, Status.NEW));
                    System.out.println("Задача создана.");
                    break;
                case 2://2. Создать подзадачу
                    System.out.println("Введите название подзадачи:");
                    String subtaskName = scanner.nextLine();
                    System.out.println("Введите описание подзадачи:");
                    String subtaskDescription = scanner.nextLine();
                    System.out.println("Введите ID эпика:");
                    int epicId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    manager.createSubtask(new Subtask(0, subtaskName, subtaskDescription, Status.NEW, epicId));
                    System.out.println("Подзадача создана.");
                    break;
                case 3://3. Создать эпик
                    System.out.println("Введите название эпика:");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание эпика:");
                    String epicDescription = scanner.nextLine();
                    manager.createEpic(new Epic(0, epicName, epicDescription, Status.NEW));
                    System.out.println("Эпик создан.");
                    break;
                case 4://4. Показать все задачи
                    System.out.println("Все задачи:");
                    for (Task task : manager.getAllTasks()) {
                        System.out.println(task);
                    }
                    break;
                case 5://5. Показать все подзадачи
                    System.out.println("Все подзадачи:");
                    for (Subtask subtask : manager.getAllSubtasks()) {
                        System.out.println(subtask);
                    }
                    break;
                case 6://6. Показать все эпики
                    System.out.println("Все эпики:");
                    for (Epic epic : manager.getAllEpics()) {
                        System.out.println(epic);
                    }
                    break;
                case 7://7. Обновить статус задачи
                    System.out.println("Введите ID задачи для обновления статуса:");
                    int taskId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.println("Введите новый статус (NEW, IN_PROGRESS, DONE):");
                    String statusStr = scanner.nextLine();
                    Status status = Status.valueOf(statusStr);
                    Task task = manager.getAllTasks().stream()
                            .filter(t -> t.getId() == taskId)
                            .findFirst()
                            .orElse(null);
                    if (task != null) {
                        task.setStatus(status);
                        System.out.println("Статус задачи обновлен.");
                    } else {
                        System.out.println("Задача не найдена.");
                    }
                    break;
                case 8://8. Удалить задачу
                    System.out.println("Введите ID задачи для удаления:");
                    int deleteTaskId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    manager.deleteTask(deleteTaskId);
                    System.out.println("Задача удалена.");
                    break;
                case 9://9. Удалить подзадачу
                    System.out.println("Введите ID подзадачи для удаления:");
                    int deleteSubtaskId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    manager.deleteSubtask(deleteSubtaskId);
                    System.out.println("Подзадача удалена.");
                    break;
                case 10://10. Удалить эпик
                    System.out.println("Введите ID эпика для удаления:");
                    int deleteEpicId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    manager.deleteEpic(deleteEpicId);
                    System.out.println("Эпик удален.");
                    break;
                case 0:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }

    }
}
