import entities.Epic;
import entities.Manager;
import entities.Status;
import entities.Subtask;
import entities.Task;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = manager.createTask(new Task("Задача 1", "Это \"Задача 1\""));
        Task task2 = manager.createTask(new Task("Задача 2", "Это \"Задача 2\""));

        Epic epic1 = new Epic("Эпик 1", "Это Эпик 1!");
        epic1 = manager.createEpic(epic1);
        Subtask subtask1 = manager.createSubtask(new Subtask(epic1.getId(), "Подзадача 1", "Подзадача 1 эпика 1"));
        Subtask subtask2 = manager.createSubtask(new Subtask(epic1.getId(), "Подзадача 2", "Подзадача 2 эпика 1"));
        epic1 = manager.updateEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Это Эпик 2!");
        epic2 = manager.createEpic(epic2);
        Subtask subtask3 = manager.createSubtask(new Subtask(epic2.getId(), "Подзадача 3", "Подзадача 1 эпика 2"));
        epic2 = manager.updateEpic(epic2);

        System.out.println("Список эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Список подзадач:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("Задача 1, статус = " + task1.getStatus());
        System.out.println("над Задача 1 начинается работа");
        task1.setStatus(Status.IN_PROGRESS);
        task1 = manager.updateTask(task1);
        System.out.println("Задача 1, статус = " + task1.getStatus());

        System.out.println("Задача 2, статус = " + task2.getStatus());
        System.out.println("Задача 2 выполнена");
        task2.setStatus(Status.DONE);
        task2 = manager.updateTask(task2);
        System.out.println("Задача 2, статус = " + task2.getStatus());

        System.out.println("Эпик 1, статус = " + epic1.getStatus());
        System.out.println("Подзадача 1 эпика 1 выполнена");
        subtask1.setStatus(Status.DONE);
        subtask1 = manager.updateSubtask(subtask1);
        System.out.println("Эпик 1, статус = " + epic1.getStatus());

        if (manager.deleteTaskById(task2.getId())) {
            System.out.println("Задача 2, удалена");
        }

        System.out.println(epic1);
        if (manager.deleteSubtaskById(subtask2.getId())) {
            System.out.println("Подзадача 2, удалена");
        }
        System.out.println(epic1);
    }

}
