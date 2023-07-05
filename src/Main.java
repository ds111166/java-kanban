import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;
import manager.TaskManager;
import manager.utilities.Managers;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Integer taskId1 = taskManager.createTask(new Task("Задача 1", "Это \"Задача 1\""));
        Integer taskId2 = taskManager.createTask(new Task("Задача 2", "Это \"Задача 2\""));
        Integer taskId3 = taskManager.createTask(new Task("Задача 3", "Это \"Задача 3\""));
        Integer taskId4 = taskManager.createTask(new Task("Задача 4", "Это \"Задача 4\""));
        Integer taskId5 = taskManager.createTask(new Task("Задача 5", "Это \"Задача 5\""));
        Integer taskId6 = taskManager.createTask(new Task("Задача 6", "Это \"Задача 6\""));
        Integer taskId7 = taskManager.createTask(new Task("Задача 7", "Это \"Задача 7\""));
        Integer taskId8 = taskManager.createTask(new Task("Задача 8", "Это \"Задача 8\""));
        Integer taskId9 = taskManager.createTask(new Task("Задача 9", "Это \"Задача 9\""));

        Integer epicId1 = taskManager.createEpic(new Epic("Эпик 1", "Это Эпик 1!"));
        Integer subtaskId1 = taskManager.createSubtask(new Subtask(epicId1, "Подзадача 1", "Подзадача 1 эпика 1"));
        Integer subtaskId2 = taskManager.createSubtask(new Subtask(epicId1, "Подзадача 2", "Подзадача 2 эпика 1"));

        Integer epicId2 = taskManager.createEpic(new Epic("Эпик 2", "Это Эпик 2!"));
        Integer subtaskId3 = taskManager.createSubtask(new Subtask(epicId2, "Подзадача 3", "Подзадача 1 эпика 2"));

        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("Список задач:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Список подзадач:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }
        Task task1 = taskManager.getTask(taskId1);
        System.out.println("Задача 1, статус = " + task1.getStatus());
        System.out.println("над Задача 1 начинается работа");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println("Задача 1, статус = " + task1.getStatus());

        Task task2 = taskManager.getTask(taskId1);
        System.out.println("Задача 2, статус = " + task2.getStatus());
        System.out.println("Задача 2 выполнена");
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);
        System.out.println("Задача 2, статус = " + task2.getStatus());

        Epic epic1 = taskManager.getEpic(epicId1);
        System.out.println("Эпик 1, статус = " + epic1.getStatus());
        System.out.println("Подзадача 1 эпика 1 выполнена");
        Subtask subtask1 = taskManager.getSubtask(subtaskId1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println("Эпик 1, статус = " + epic1.getStatus());


        System.out.println("\n1 Просмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();

        taskManager.deleteTask(taskId2);
        System.out.println("\nЗадача 2, удалена");

        System.out.println(epic1);

        taskManager.deleteSubtask(subtaskId2);
        System.out.println("\nПодзадача 2, удалена\n");

        System.out.println(epic1);

        System.out.println("\n2 Просмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        taskManager.deleteEpic(epicId1);
        System.out.println("\nЭпик 1, удален");
        System.out.println("\n3 Просмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);
        taskManager.getTask(taskId3);
        taskManager.getTask(taskId4);
        taskManager.getTask(taskId5);
        taskManager.getTask(taskId6);
        taskManager.getTask(taskId7);
        taskManager.getTask(taskId8);
        taskManager.getTask(taskId9);
        taskManager.getTask(taskId1);
        taskManager.getTask(taskId1);
        System.out.println("\n4 Просмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        taskManager.deleteEpic(epicId2);
        taskManager.deleteTask(taskId1);
        taskManager.deleteTask(taskId2);
        taskManager.deleteTask(taskId3);
        taskManager.deleteTask(taskId4);
        taskManager.deleteTask(taskId5);
        taskManager.deleteTask(taskId6);
        taskManager.deleteTask(taskId7);
        taskManager.deleteTask(taskId8);
        taskManager.deleteTask(taskId9);
        System.out.println("\n5 Просмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskId1 = taskManager.createTask(new Task("Задача 1", "Это \"Задача 1\""));
        taskManager.getTask(taskId1);
        System.out.println("\n6 Просмотр истории просмотров задач: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

}
