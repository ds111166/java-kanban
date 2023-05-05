import entities.Epic;
import manager.Managers;
import manager.TaskManager;
import manager.InMemoryTaskManager;
import entities.Status;
import entities.Subtask;
import entities.Task;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Integer taskId1 = taskManager.createTask(new Task("Задача 1", "Это \"Задача 1\""));
        Integer taskId2 = taskManager.createTask(new Task("Задача 2", "Это \"Задача 2\""));

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

        taskManager.deleteTask(taskId2);
        System.out.println("Задача 2, удалена");

        System.out.println(epic1);
        taskManager.deleteSubtask(subtaskId2);
        System.out.println("Подзадача 2, удалена");

        System.out.println(epic1);
    }

}
