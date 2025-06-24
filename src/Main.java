import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager;
        File test1 = new File("src/files/tasks.csv");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (test1.length() == 0) {
            fileBackedTaskManager = new FileBackedTaskManager(test1);
        } else {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(test1);
        }

        Task task1 = new Task("Помыть посуду", "Пойти на кухню и загрузить посудомойку", Duration.ofMinutes(10), LocalDateTime.parse("16.06.2025 12:00", formatter));
        fileBackedTaskManager.addTask(task1);
        Task task2 = new Task("Купить продукты", "Сходить в магазин и купить продукты по списку");
        fileBackedTaskManager.addTask(task2);

        Epic epic1 = new Epic("Переезд", "Поиск грузчиков и упаковка вещей и сборка мебели");
        fileBackedTaskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Собрать коробки", "Упаковать вещи", 3, null, LocalDateTime.parse("17.06.2025 10:00", formatter));
        fileBackedTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сказать слова прощания", "До свидания и наилучшие пожелания всем", 3, Duration.ofMinutes(30), null);
        fileBackedTaskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Упаковать кошку", "Посадить кошку в переноску", 3, Duration.ofMinutes(45), LocalDateTime.parse("19.06.2025 16:00", formatter));
        fileBackedTaskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Организовать праздник", "Купить свечи и продукты и позвать гостей");
        fileBackedTaskManager.addEpic(epic2);
        Subtask subtask4 = new Subtask("Приготовить еду", "Купить продукты и найти рецепты", 7);
        fileBackedTaskManager.addSubtask(subtask4);

        System.out.println("Задачи:");

        for (int task : fileBackedTaskManager.getTasks().keySet()) {
            System.out.print(fileBackedTaskManager.getTasks().get(task));
        }

        System.out.println();
        System.out.println("Эпики:");

        for (int task : fileBackedTaskManager.getEpics().keySet()) {
            System.out.print(fileBackedTaskManager.getEpics().get(task));
        }

        fileBackedTaskManager.updateTask(task1, "", "", Status.DONE, Duration.ofMinutes(12) , LocalDateTime.parse("01.04.2025 17:12", formatter));
        fileBackedTaskManager.updateTask(task2, "", "", Status.IN_PROGRESS, Duration.ofMinutes(11), LocalDateTime.parse("01.04.2025 17:00", formatter));
//        fileBackedTaskManager.updateSubtask(subtask1, "", "", Status.IN_PROGRESS);
//        fileBackedTaskManager.updateSubtask(subtask2, "", "", Status.NEW);
//        fileBackedTaskManager.updateSubtask(subtask3, "", "", Status.DONE);
        fileBackedTaskManager.updateSubtask(subtask4, "", "", Status.IN_PROGRESS, Duration.ofMinutes(16), LocalDateTime.parse("02.02.2023 01:30", formatter));
        fileBackedTaskManager.updateSubtask(subtask1, "", "", Status.DONE, null, LocalDateTime.parse("03.03.2025 03:00", formatter));
        fileBackedTaskManager.updateSubtask(subtask3, "", "", Status.DONE, Duration.ofMinutes(15), LocalDateTime.parse("04.04.2025 04:00", formatter));

        System.out.println();
        System.out.println("Задачи после обновления:");

        for (int task : fileBackedTaskManager.getTasks().keySet()) {
            System.out.print(fileBackedTaskManager.getTasks().get(task));
        }

        System.out.println();
        System.out.println("Эпики после обновления:");

        for (int task : fileBackedTaskManager.getEpics().keySet()) {
            System.out.print(fileBackedTaskManager.getEpics().get(task));
        }

        System.out.println();
        System.out.println("Подзадачи после обновления:");

        for (int task : fileBackedTaskManager.getSubtasks().keySet()) {
            System.out.print(fileBackedTaskManager.getSubtasks().get(task));
        }

        fileBackedTaskManager.getTaskById(1);
        fileBackedTaskManager.getEpicById(3);
        fileBackedTaskManager.getSubtaskById(5);
        fileBackedTaskManager.getSubtaskById(6);
        fileBackedTaskManager.getTaskById(2);
        fileBackedTaskManager.getEpicById(3);
        fileBackedTaskManager.getTaskById(1);
        fileBackedTaskManager.getSubtaskById(5);
        fileBackedTaskManager.getSubtaskById(8);
        fileBackedTaskManager.getEpicById(3);
        fileBackedTaskManager.getEpicById(7);

//        System.out.println("История:________________________________________________");
//
//        for (Task task : fileBackedTaskManager.getHistory()) {
//            System.out.println(task);
//        }

//        fileBackedTaskManager.deleteTaskById(1);
//        fileBackedTaskManager.deleteEpicById(3);
//        fileBackedTaskManager.deleteSubtaskById(8);

//        System.out.println("История:________________________________________________");
//
//        for (Task task : fileBackedTaskManager.getHistory()) {
//            System.out.print(task);
//        }

        System.out.println();
        System.out.println("Задачи по приоритету:________________________________________________");
        for (Task task : fileBackedTaskManager.getPrioritizedTasks()) {
            System.out.print(task);
        }
    }
}
