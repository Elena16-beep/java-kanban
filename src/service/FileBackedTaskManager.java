package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Type;
import model.Status;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File filename;

    public FileBackedTaskManager(File filename) {
        this.filename = filename;
    }

    public void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic");
            writer.write("\n");

            for (Task task : getTasks().values()) {
                writer.write(toString(task));
                writer.write("\n");
            }

            for (Epic epic : getEpics().values()) {
                writer.write(toString(epic));
                writer.write("\n");
            }

            for (Subtask subtask : getSubtasks().values()) {
                writer.write(toString(subtask));
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время сохранения файла: " + e.getMessage());
        }
    }

    public String toString(Task task) {
        String epicId = "";
        String comma = "";

        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getIdEpic());
            comma = ",";
        }

        return String.format("%d,%s,%s,%s,%s" + comma + "%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription(), epicId);
    }

    public Task fromString(String value) {
        Task task = new Task();
        String[] items = value.split(",");
        int id = Integer.parseInt(items[0]);
        Type type = Type.valueOf(items[1]);
        String name = items[2];
        Status status = Status.valueOf(items[3]);
        String description = items[4];

        switch (type) {
            case TASK -> task = new Task(id, name, description, status);
            case EPIC -> task = new Epic(id, name, description, status);
            case SUBTASK -> task = new Subtask(id, name, description, status, Integer.parseInt(items[5]));
        }

        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;

            while (reader.ready()) {
                line = reader.readLine();
                line = line.trim();

                if (!line.isEmpty() && !line.equals("id,type,name,status,description,epic")) {
                    Task task = fileBackedTasksManager.fromString(line);

                    switch (task.getType()) {
                        case TASK:
                            tasks.put(task.getId(), task);
                            break;
                        case EPIC:
                            epics.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) task;

                            subtasks.put(subtask.getId(), subtask);

                            if (epics.get(subtask.getIdEpic()) != null) {
                                epics.get(subtask.getIdEpic()).getSubtasks().put(subtask.getId(), subtask);
                            }

                            break;
                    }

                    if (task.getId() >= count) {
                        count = task.getId();
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время загрузки из файла: " + e.getMessage());
        }

        return fileBackedTasksManager;
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager;
        Path pathFile = Paths.get("src/files/tasksFile.csv");
        File tasksFile;

        if (!Files.exists(pathFile)) {
            try {
                tasksFile = Files.createFile(pathFile).toFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка во время создания файла tasksFile.csv: " + e.getMessage());
            }
        } else {
            tasksFile = new File(String.valueOf(pathFile));
        }

        if (Files.exists(pathFile) && tasksFile.length() == 0) {
            fileBackedTaskManager = new FileBackedTaskManager(tasksFile);
        } else {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tasksFile);
        }

        Task task1 = new Task("Test1 addNewTask", "Test1 addNewTask description");
        fileBackedTaskManager.addTask(task1);
        Task task2 = new Task("Test2 addNewTask", "Test2 addNewTask description");
        fileBackedTaskManager.addTask(task2);

        Epic epic1 = new Epic("Test3 addNewEpic", "Test3 addNewEpic description");
        fileBackedTaskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Test4 addNewSubtask", "Test4 addNewSubtask description", 3);
        fileBackedTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test5 addNewSubtask", "Test5 addNewSubtask description", 3);
        fileBackedTaskManager.addSubtask(subtask2);

        fileBackedTaskManager.updateTask(task1, "", "", Status.DONE);
        fileBackedTaskManager.updateTask(task2, "", "", Status.IN_PROGRESS);
        fileBackedTaskManager.updateSubtask(subtask1, "", "", Status.IN_PROGRESS);
        fileBackedTaskManager.updateSubtask(subtask2, "", "", Status.DONE);

        System.out.println("Задачи:");

        for (int task : fileBackedTaskManager.getTasks().keySet()) {
            System.out.println(fileBackedTaskManager.getTasks().get(task));
        }

        System.out.println("Эпики:");

        for (int task : fileBackedTaskManager.getEpics().keySet()) {
            System.out.println(fileBackedTaskManager.getEpics().get(task));
        }

        System.out.println("Подзадачи:");

        for (int task : fileBackedTaskManager.getSubtasks().keySet()) {
            System.out.println(fileBackedTaskManager.getSubtasks().get(task));
        }

        FileBackedTaskManager fileBackedTaskManagerLoaded = FileBackedTaskManager.loadFromFile(tasksFile);

        System.out.println("Задачи:");

        for (int task : fileBackedTaskManagerLoaded.getTasks().keySet()) {
            System.out.println(fileBackedTaskManagerLoaded.getTasks().get(task));
        }

        System.out.println("Эпики:");

        for (int task : fileBackedTaskManagerLoaded.getEpics().keySet()) {
            System.out.println(fileBackedTaskManagerLoaded.getEpics().get(task));
        }

        System.out.println("Подзадачи:");

        for (int task : fileBackedTaskManagerLoaded.getSubtasks().keySet()) {
            System.out.println(fileBackedTaskManagerLoaded.getSubtasks().get(task));
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task, String name, String description, Status status) {
        super.updateTask(task, name, description, status);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic, String name, String description) {
        super.updateEpic(epic, name, description);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, String name, String description, Status status) {
        super.updateSubtask(subtask, name, description, status);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}
