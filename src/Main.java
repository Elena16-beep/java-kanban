public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Помыть посуду", "Пойти на кухню, загрузить посудомойку");
        manager.addTask(task1);
        Task task2 = new Task("Купить продукты", "Сходить в магазин, купить продукты по списку");
        manager.addTask(task2);

        Epic epic1 = new Epic("Переезд", "Поиск грузчиков, упаковка вещей, сборка мебели");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Собрать коробки", "Упаковать вещи", 3);
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сказать слова прощания", "До свидания и наилучшие пожелания всем", 3);
        manager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Упаковать кошку", "Посадить кошку в переноску", 3);
        manager.addSubtask(subtask3);

        Epic epic2 = new Epic("Организовать праздник", "Купить свечи, продукты, позвать гостей");
        manager.addEpic(epic2);
        Subtask subtask4 = new Subtask("Приготовить еду", "Купить продукты, найти рецепты", 7);
        manager.addSubtask(subtask4);

        manager.getTasks();
        manager.getEpics();

        manager.updateTask(task1, "", "", Status.DONE);
        manager.updateTask(task2, "", "", Status.IN_PROGRESS);
        manager.updateSubtask(subtask1, "", "", Status.IN_PROGRESS);
        manager.updateSubtask(subtask2, "", "", Status.NEW);
        manager.updateSubtask(subtask3, "", "", Status.DONE);
        manager.updateSubtask(subtask4, "", "", Status.IN_PROGRESS);

        manager.getTasks();
        manager.getEpics();

        manager.updateSubtask(subtask1, "", "", Status.DONE);
        manager.updateSubtask(subtask2, "", "", Status.DONE);
        manager.updateSubtask(subtask3, "", "", Status.DONE);

        manager.getTasks();
        manager.getEpics();

        manager.deleteTaskById(2);
        manager.deleteEpicById(7);

        manager.getTasks();
        manager.getEpics();
        manager.getSubtasks();
    }
}
