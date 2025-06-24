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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File filename;

    public FileBackedTaskManager(File filename) {
        this.filename = filename;
    }

    public void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic,duration,startTime,endTime");
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
        String taskDuration = "";
        String taskStartTime = "";
        String taskEndTime = "";
        String comma = "";
        String commaForStartTime = "";
        String commaForEndTime = "";
        String commaForDuration = "";

        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getIdEpic());
            comma = ",";
        }

        if (task.getDuration() != null) {
            taskDuration = String.valueOf(task.getDuration().toMinutes());
            commaForDuration = ",";
        }

        if (task.getStartTime() != null) {
            taskStartTime = String.valueOf(task.getStartTime());
            commaForStartTime = ",";
        }

        if (task.getEndTime() != null) {
            taskEndTime = String.valueOf(task.getEndTime());
            commaForEndTime = ",";
        }

        return String.format("%d,%s,%s,%s,%s" + comma + "%s" + commaForDuration + "%s" + commaForStartTime + "%s" + commaForEndTime + "%s",
                task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription(), epicId,
                taskDuration, taskStartTime, taskEndTime);
    }

    public Task fromString(String value) {
        Task task = new Task();
        String[] items = value.split(",");
        int id = Integer.parseInt(items[0]);
        Type type = Type.valueOf(items[1]);
        String name = items[2];
        Status status = Status.NEW;
        String description = items[4];
        Duration duration = null;
        LocalDateTime startTime = null;

        if (!Objects.equals(items[3], "null")) {
            status = Status.valueOf(items[3]);
        }

        if (Objects.equals(type.toString(), "SUBTASK")) {
            if (items.length > 6 && items[6] != null) {
                if (!items[6].contains("T")) {
                    duration = Duration.ofMinutes(Integer.parseInt(items[6]));
                } else {
                    startTime = LocalDateTime.parse(items[6]);
                }
            }

            if (items.length > 7 && items[7] != null) {
                startTime = LocalDateTime.parse(items[7]);
            }
        } else {
            if (items.length > 5 && items[5] != null) {
                if (!items[5].contains("T")) {
                    duration = Duration.ofMinutes(Integer.parseInt(items[5]));
                } else {
                    startTime = LocalDateTime.parse(items[5]);
                }
            }

            if (items.length > 6 && items[6] != null) {
                startTime = LocalDateTime.parse(items[6]);
            }
        }

        switch (type) {
            case TASK -> task = new Task(id, name, description, status, duration, startTime);
            case EPIC -> task = new Epic(id, name, description, status, duration, startTime);
            case SUBTASK -> {
                if (items.length > 5 && items[5] != null) {
                    task = new Subtask(id, name, description, status, Integer.parseInt(items[5]), duration, startTime);
                }
            }
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

                if (!line.isEmpty() && !line.equals("id,type,name,status,description,epic") && !line.equals("id,type,name,status,description,epic,duration,startTime,endTime")) {
                    Task task = fileBackedTasksManager.fromString(line);
                    int taskId = task.getId();

                    switch (task.getType()) {
                        case TASK:
                            tasks.put(taskId, task);
                            break;
                        case EPIC:
                            epics.put(taskId, (Epic) task);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) task;
                            int epicId = subtask.getIdEpic();

                            subtasks.put(taskId, subtask);

                            if (epics.get(epicId) != null && epics.get(epicId).getSubtasks() != null) {
                                epics.get(epicId).getSubtasks().put(taskId, subtask);
                            }

                            break;
                        default:
                        {
                            throw new IllegalStateException("Неизвестный тип задачи: " + task.getType());
                        }
                    }

                    if (taskId >= count) {
                        count = taskId;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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

        fileBackedTaskManager.updateTask(task1, "", "", Status.DONE, null, null);
        fileBackedTaskManager.updateTask(task2, "", "", Status.IN_PROGRESS, Duration.ofMinutes(11), LocalDateTime.parse("01.04.2025 17:00", formatter));
        fileBackedTaskManager.updateSubtask(subtask1, "", "", Status.IN_PROGRESS, null, null);
        fileBackedTaskManager.updateSubtask(subtask2, "", "", Status.DONE, Duration.ofMinutes(111), LocalDateTime.parse("01.01.2025 10:00", formatter));

        System.out.println("Задачи:");

        for (int task : fileBackedTaskManager.getTasks().keySet()) {
            System.out.print(fileBackedTaskManager.getTasks().get(task));
        }

        System.out.println();
        System.out.println("Эпики:");

        for (int task : fileBackedTaskManager.getEpics().keySet()) {
            System.out.print(fileBackedTaskManager.getEpics().get(task));
        }

        System.out.println();
        System.out.println("Подзадачи:");

        for (int task : fileBackedTaskManager.getSubtasks().keySet()) {
            System.out.print(fileBackedTaskManager.getSubtasks().get(task));
        }

        FileBackedTaskManager fileBackedTaskManagerLoaded = FileBackedTaskManager.loadFromFile(tasksFile);

        System.out.println();
        System.out.println("Проверка загрузки задач из файла _______________________________________________________");
        System.out.println("Задачи:");

        for (int task : fileBackedTaskManagerLoaded.getTasks().keySet()) {
            System.out.print(fileBackedTaskManagerLoaded.getTasks().get(task));
        }

        System.out.println();
        System.out.println("Эпики:");

        for (int task : fileBackedTaskManagerLoaded.getEpics().keySet()) {
            System.out.print(fileBackedTaskManagerLoaded.getEpics().get(task));
        }

        System.out.println();
        System.out.println("Подзадачи:");

        for (int task : fileBackedTaskManagerLoaded.getSubtasks().keySet()) {
            System.out.print(fileBackedTaskManagerLoaded.getSubtasks().get(task));
        }

//        Files.deleteIfExists(pathFile);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super.updateTask(task, name, description, status, duration, startTime);
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
    public void updateSubtask(Subtask subtask, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super.updateSubtask(subtask, name, description, status, duration, startTime);
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
