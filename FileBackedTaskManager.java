import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

// ВTASKS ЧЕРЕЗ ГЕТТЕРЫ И СЕТТЕРЫ РЕАЛИЩОВАТЬ ДОСЬУП К ID TYPE И ТД ??????????????!!!!!!!!!!!!!!!!!!!!!!!!!\\
// РЕАЛИЗОВАТЬ МЕТОД ДОБАВЛЕНИЯ ПОДЗАДАЧИ В ЭПИК -  epics.get(subTask.epicId).addSubTask(subTask); \\\
// РАЗОБРАТЬСЯ С EXCEPTIONS РЕАЛИЗОВАТЬ СВОЙ EXCEPTION РЕШЕНО!!!!!!!!!!!!!!!!!!!!!!! \\
///////////////////////// РЕАЛИЗАЦИЯ БЕЗ КЭШ ФАЙЛОВ !!!!! \\\\\\\\\\\\\\\\\\\\\\\\\\\\
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    // при создании объекта FileBackedTaskManager в конструктор передается файл для авто сохранения данных
    FileBackedTaskManager(File file) {
        this.file = file;
    }

    // загружает данные по указанному файлу, и возвращает объект типа FileBackedTaskManager
    static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            while (bufferedReader.ready()) {
                String data = bufferedReader.readLine();
                if (!data.isEmpty()) {  // если не пустая строка ? - подгружаем задачки в ОП
                    loadData(data);
                    id = getId(data);  // для отслеживания занятых ID шек
                } else {  // если строка пустая(отступ между задачи и историей), считываем следующую - ID шки истории
                    data = bufferedReader.readLine();
                    if (data != null) // смотрим следующую строку, где должна находиться история просмотров
                        loadHistory(data);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return fileBackedTaskManager;
    }

    // метод подгружает историю задач в ОП (InMemoryHistoryManager)
    private static void loadTaskByIdInHistoryMap(int id) {
        switch (getTypeFromId(id)) {
            case typeTask.TASK -> Managers.getDefaultHistory().add(tasks.get(id));
            case typeTask.EPIC -> Managers.getDefaultHistory().add(epics.get(id));
            case typeTask.SUBTASK -> Managers.getDefaultHistory().add(subTasks.get(id));
            case null -> {
            }
        }
    }

    // метод загружает историю задач из строки и записывает загруженную историю в локальный файл history.txt
    private static void loadHistory(String data) throws ManagerSaveException {
        if (!data.isEmpty()) {
            List<Integer> historyIdList = getHistoryIdList(data);
            for (int id : historyIdList)
                loadTaskByIdInHistoryMap(id);
        }
    }

    // метод преобразовывает историю просмотра в строку формата .csv с ID просмотренных задач {1, 2, 3....}
    private static String historyToCsv(HistoryManager historyManager) {
        List<Integer> historyIdList = historyManager.getHistoryIdList();
        if (!historyIdList.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (int id : historyIdList.reversed()) //reverse тк сохраняем в порядке последней просмотренной
                result.append(id).append(",");
            return result.deleteCharAt(result.length() - 1).toString();
        }
        return "";
    }

    // метод формирует список ID из указанной строки формата .csv
    private static List<Integer> getHistoryIdList(String data) {
        return Arrays.stream(data.split(",")).map(Integer::parseInt).toList();
    }

    private void saveTasks() throws ManagerSaveException {
        HashMap<Integer, Tasks> allTasks = getAllTypeTasks();
        try {
            FileWriter fileWriter = new FileWriter(file);
            String data = "";
            if (!allTasks.isEmpty()) {
                for (int id : allTasks.keySet()) {
                    data = taskToCsv(allTasks.get(id));
                    fileWriter.write(data + "\n");
                }
            } else
                fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }

    }

    private void saveHistory() throws ManagerSaveException {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write("\n" + historyToCsv(Managers.getDefaultHistory())); // \n для отделения задач и истории
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    // TRY WITH RESOURCE НАДО
    private void save() throws ManagerSaveException {
        saveTasks();
        saveHistory();
    }

    // метод получает строку формата .csv и возвращает ID объекта {id}
    private static int getId(String data) {
        String[] taskData = parseData(data);
        return Integer.parseInt(taskData[0]);
    }

    // метод получает строку формата .csv и возвращает объект typeTask - тип задачи {type}
    private static typeTask getType(String data) {
        String[] taskData = parseData(data);
        return switch (taskData[1]) {
            case "TASK" -> typeTask.TASK;
            case "EPIC" -> typeTask.EPIC;
            case "SUBTASK" -> typeTask.SUBTASK;
            default -> throw new IllegalStateException("Unexpected value: " + taskData[1]);
        };
    }

    // метод получает строку формата .csv и возвращает объект Имя объекта {name}
    private static String getName(String data) {
        String[] taskData = parseData(data);
        return taskData[2];
    }

    // метод получает строку формата .csv и возвращает объект Описание объекта {description}
    private static String getDescription(String data) {
        String[] taskData = parseData(data);
        return taskData[4];
    }

    // метод получает строку формата .csv и возвращает объект statusTask статус задачи {status}
    private static statusTask getStatus(String data) {
        String[] taskData = parseData(data);
        return switch (taskData[3]) {
            case "NEW" -> statusTask.NEW;
            case "IN_PROGRESS" -> statusTask.IN_PROGRESS;
            case "DONE" -> statusTask.DONE;
            default -> statusTask.NONE;
        };
    }

    // метод получает строку формата .csv и возвращает ID ЭпикЗадачи для ПодЗадачи {epicId}
    private static int getEpicId(String data) {
        String[] taskData = parseData(data);
        return Integer.parseInt(taskData[5]);
    }

    // метод определяет тип объекта {typeTask} по ID {id} задачи
    private static typeTask getTypeFromId(int id) {
        if (tasks.containsKey(id))
            return typeTask.TASK;
        else if (epics.containsKey(id))
            return typeTask.EPIC;
        else if (subTasks.containsKey(id))
            return typeTask.SUBTASK;
        return null;
    }

    // метод преобразовывает задачу с общим типом Tasks в строку формата .csv
    private String taskToCsv(Tasks task) {
        String result = task.getId() + "," + task.type + "," + task.name + "," + task.status + ","
                + task.description + ",";
        if (task instanceof Epic.SubTask) {
            result += ((Epic.SubTask) task).epicId;
        }
        return result;
    }

    // метод преобразовывает данные из строки формата .csv в массив строк {id, type, name, status, description, epic}
    private static String[] parseData(String data) {
        return data.split(",");
    }

    // метод получает строку .csv формата, и на основании указанного типа задачи передает в конструктор определенного типа
    private static void loadData(String data) {
        switch (getType(data)) {
            case typeTask.TASK -> loadTask(data);
            case typeTask.EPIC -> loadEpic(data);
            case typeTask.SUBTASK -> loadSubtask(data);
        }
    }

    // метод воссоздает объект типа Task из строки формата .csv
    private static void loadTask(String csvData) {
        int id = getId(csvData);
        tasks.put(id, new Task(id, getName(csvData), getStatus(csvData), getDescription(csvData)));
        System.out.println(tasks.get(id) + " была успешно загружена из файла");
    }

    // метод воссоздает объект типа Epic из строки формата .csv
    private static void loadEpic(String csvData) {
        int id = getId(csvData);
        epics.put(id, new Epic(id, getName(csvData), getStatus(csvData), getDescription(csvData)));
        System.out.println(epics.get(id) + " была успешно загружена из файла");
    }

    // метод воссоздает объект типа SubTask из строки формата .csv
    private static void loadSubtask(String csvData) {
        int id = getId(csvData);
        int epicId = getEpicId(csvData);
        Epic.SubTask subTask = new Epic.SubTask(id, getName(csvData), getStatus(csvData),
                getDescription(csvData), epicId);
        subTasks.put(id, subTask);
        epics.get(epicId).addSubTask(subTask);
        System.out.println(subTask + " была успешно загружена из файла");
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        save();
    }

    @Override
    public void addTask(Epic.SubTask subTask) {
        super.addTask(subTask);
        save();
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> taskList = super.getAllTasks();
        if (!taskList.isEmpty())
            save();
        return taskList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicList = super.getAllEpics();
        if (!epicList.isEmpty())
            save();
        return epicList;
    }

    @Override
    public List<Epic.SubTask> getAllSubTasks() {
        List<Epic.SubTask> subTaskList = super.getAllSubTasks();
        if (!subTaskList.isEmpty())
            save();
        return subTaskList;
    }

    @Override
    public List<Epic.SubTask> getAllSubTaskFromEpic(int id) {
        List<Epic.SubTask> subTaskList = super.getAllSubTaskFromEpic(id);
        if (!subTaskList.isEmpty())
            save();
        return subTaskList;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Epic.SubTask getSubTask(int id) {
        Epic.SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllSubTasksFromEpicId(int id) {
        super.deleteAllSubTasksFromEpicId(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Epic.SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }


    public static void main(String[] args) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("data.txt"));
    }
}
