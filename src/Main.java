import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Помыть посуду", "Пойти на кухню, загрузить посудомойку");
        taskManager.addTask(task1);
        Task task2 = new Task("Купить продукты", "Сходить в магазин, купить продукты по списку");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Переезд", "Поиск грузчиков, упаковка вещей, сборка мебели");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Собрать коробки", "Упаковать вещи", 3);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сказать слова прощания", "До свидания и наилучшие пожелания всем", 3);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Упаковать кошку", "Посадить кошку в переноску", 3);
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Организовать праздник", "Купить свечи, продукты, позвать гостей");
        taskManager.addEpic(epic2);
        Subtask subtask4 = new Subtask("Приготовить еду", "Купить продукты, найти рецепты", 7);
        taskManager.addSubtask(subtask4);

        System.out.println("Задачи:");

        for (int task : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(task));
        }

        System.out.println("Эпики:");

        for (int task : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(task));
        }

        taskManager.updateTask(task1, "", "", Status.DONE);
        taskManager.updateTask(task2, "", "", Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1, "", "", Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2, "", "", Status.NEW);
        taskManager.updateSubtask(subtask3, "", "", Status.DONE);
        taskManager.updateSubtask(subtask4, "", "", Status.IN_PROGRESS);

        System.out.println("Задачи:");

        for (int task : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(task));
        }

        System.out.println("Эпики:");

        for (int task : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(task));
        }

        taskManager.updateSubtask(subtask1, "", "", Status.DONE);
        taskManager.updateSubtask(subtask2, "", "", Status.DONE);
        taskManager.updateSubtask(subtask3, "", "", Status.DONE);

        System.out.println("Задачи:");

        for (int task : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(task));
        }

        System.out.println("Эпики:");

        for (int task : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(task));
        }

        System.out.println("Подзадачи:");

        for (int task : taskManager.getSubtasks().keySet()) {
            System.out.println(taskManager.getSubtasks().get(task));
        }

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(8);
        taskManager.getEpicById(3);
        taskManager.getEpicById(7);

        System.out.println("История:________________________________________________");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        taskManager.deleteSubtaskById(8);

        System.out.println("История:________________________________________________");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
