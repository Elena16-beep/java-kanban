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

        taskManager.getTasks();
        taskManager.getEpics();

        taskManager.updateTask(task1, "", "", Status.DONE);
        taskManager.updateTask(task2, "", "", Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1, "", "", Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2, "", "", Status.NEW);
        taskManager.updateSubtask(subtask3, "", "", Status.DONE);
        taskManager.updateSubtask(subtask4, "", "", Status.IN_PROGRESS);

        taskManager.getTasks();
        taskManager.getEpics();

        taskManager.updateSubtask(subtask1, "", "", Status.DONE);
        taskManager.updateSubtask(subtask2, "", "", Status.DONE);
        taskManager.updateSubtask(subtask3, "", "", Status.DONE);

        taskManager.getTasks();
        taskManager.getEpics();

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(7);

        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(1);

        System.out.println("История:________________________________________________");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
