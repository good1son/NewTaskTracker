import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager extends InMemoryHistoryManager implements TaskManager {
    protected static int id = 0;
    static HashMap<Integer, Task> tasks;
    static HashMap<Integer, Epic.SubTask> subTasks;
    static HashMap<Integer, Epic> epics;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    protected static int setId() {
        return ++id;
    }

    protected static int getId() {
        return id;
    }

    // метод возвращает true, если есть хотя бы одна любая задача
    protected static boolean isEmptyAllTasks() {
        return (tasks.isEmpty() & epics.isEmpty() & subTasks.isEmpty());
    }

    public int getIdByName(String name) {
        for (int id : tasks.keySet()) {
            if (Objects.equals(tasks.get(id).name, name))
                return id;
        }
        for (int id : subTasks.keySet()) {
            if (Objects.equals(subTasks.get(id).name, name))
                return id;
        }
        for (int id : epics.keySet()) {
            if (Objects.equals(epics.get(id).name, name))
                return id;
        }
        return 0;
    }

    @Override
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println(task + " была успешно создана");
    }

    @Override
    public void addTask(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println(epic + " была успешно создана");
    }

    @Override
    public void addTask(Epic.SubTask subTask) {
        if (epics.containsKey(subTask.epicId)) {
            subTasks.put(subTask.getId(), subTask);
            epics.get(subTask.epicId).addSubTask(subTask);
            updateEpic(epics.get(subTask.epicId));
            System.out.println(subTask + " была успешно добавлена в Эпик");
        } else System.out.printf("Невозможно добавить подЗадачу. Эпик задача с ID: %d не найдена.\n", subTask.epicId);
    }

   /* @Override
    public void addTask(Tasks task) {
        if (task instanceof Task)
            addTask(task);
        else if (task instanceof Epic)
            addTask(task);
    }*/

    protected HashMap<Integer, Tasks> getAllTypeTasks() {
        HashMap<Integer, Tasks> allTasks = new HashMap<>();
        allTasks.putAll(tasks);
        allTasks.putAll(epics);
        allTasks.putAll(subTasks);
        return allTasks;

        // вроде как уже отсортированна
        /*return allTasks.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));*/
    }

    //Придумать как реализовать то, чтобы не создавался пустой список, при отсутствии
    @Override
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        if (tasks.isEmpty())
            System.out.println("Список Задач пуст");
        else {
            for (Task task : tasks.values())
                Managers.getDefaultHistory().add(task);
            taskList.addAll(tasks.values());
        }
        return taskList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicList = new ArrayList<>();
        if (epics.isEmpty())
            System.out.println("Список ЭпикЗадач пуст");
        else {
            for (Epic epic : epics.values())
                Managers.getDefaultHistory().add(epic);
            epicList.addAll(epics.values());
        }
        return epicList;
    }

    @Override
    public List<Epic.SubTask> getAllSubTasks() {
        List<Epic.SubTask> subTaskList = new ArrayList<>();
        if (subTasks.isEmpty())
            System.out.println("Список подЗадач пуст");
        else {
            for (Epic.SubTask subTask : subTasks.values())
                Managers.getDefaultHistory().add(subTask);
            subTaskList.addAll(subTasks.values());
        }
        return subTaskList;
    }

    @Override
    public List<Epic.SubTask> getAllSubTaskFromEpic(int id) {
        List<Epic.SubTask> subTaskList = epics.get(id).getSubTasks();
        if (subTaskList.isEmpty())
            System.out.println("У данной ЭпикЗадачи нет ПодЗадач");
        else {
            for (Epic.SubTask subTask : subTaskList)
                Managers.getDefaultHistory().add(subTask);
        }
        return subTaskList;
    }

    @Override
    public void deleteAllTasks() {
        if (tasks.isEmpty())
            System.out.println("Список Задач пуст");
        else {
            for (int id : tasks.keySet())
                Managers.getDefaultHistory().clear(id);
            tasks.clear();
            System.out.println("Вы удалили все имеющиеся Задачи");
        }
    }

    @Override
    public void deleteAllEpics() {
        if (epics.isEmpty())
            System.out.println("Список ЭпикЗадач пуст");
        else {
            for (int id : subTasks.keySet())
                Managers.getDefaultHistory().clear(id);
            for (int id : epics.keySet())
                Managers.getDefaultHistory().clear(id);
            epics.clear();
            subTasks.clear();
            System.out.println("Вы удалили все имеющиеся ЭпикЗадачи");
        }

    }

    @Override
    public void deleteAllSubTasks() {
        if (subTasks.isEmpty())
            System.out.println("Список подЗадач пуст");
        else {
            for (int id : subTasks.keySet())
                Managers.getDefaultHistory().clear(id);
            for (Epic epic : epics.values())
                epic.clearSubTasks();
            subTasks.clear();
            System.out.println("Вы удалили все имеющиеся подЗадачи");
        }
    }

    @Override
    public void deleteAllSubTasksFromEpicId(int id) {
        List<Epic.SubTask> subTaskList = getAllSubTaskFromEpic(id);
        for (Epic.SubTask subTask : subTaskList) {
            subTasks.remove(subTask.getId());
            Managers.getDefaultHistory().clear(subTask.getId());
        }
        epics.get(id).clearSubTasks();
        System.out.println("Все задачи у ЭпикЗадачи с ID: " + id + " были удалены");
    }

    //РЕАЛИЗАЦИЯ БЕЗ проверки!!! корректности ввода ID !!! во всех методах, где запрашивается ввод ID...
    @Override
    public Task getTask(int id) {
        Managers.getDefaultHistory().add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        Managers.getDefaultHistory().add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic.SubTask getSubTask(int id) {
        Managers.getDefaultHistory().add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        task.updateTasks();
        tasks.put(task.getId(), task);
    }

    //ПОДУМАТЬ НАД БОЛЕЕ КРАТКОЙ РЕАЛИЗАЦИЕЙ ПРОВЕРКИ ЭПИКА БЕЗ СОЗДАНИЯ НОВОЙ ПЕРЕМЕННОЙ OldStatus
    @Override
    public void updateEpic(Epic epic) {
        statusTask oldStatus = epic.status;
        if (epic.isNew())
            epic.status = statusTask.NEW;
        else if (epic.isDone())
            epic.status = statusTask.DONE;
        else epic.status = statusTask.IN_PROGRESS;
        if (oldStatus != epic.status)
            System.out.println("Новый статус ЭпикЗадачи \"" + epic.name + "\": " + epic.status);
    }

    @Override
    public void updateSubTask(Epic.SubTask subTask) {
        subTask.updateTasks();
        subTasks.put(subTask.getId(), subTask);
        updateEpic(epics.get(subTask.epicId));
    }

    //РЕАЛИЗАЦИЯ УДАЛЕНИЯ БЕЗ проверки!!! корректности ввода ID !!!
    @Override
    public void deleteTask(int id) {
        System.out.println("Удаление задачи: " + tasks.get(id));
        Managers.getDefaultHistory().clear(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        System.out.println("Удаление ЭпикЗадачи: " + epic.getId());
        for (Epic.SubTask subTask : epic.getSubTasks()) {
            Managers.getDefaultHistory().clear(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
        Managers.getDefaultHistory().clear(id);
    }

    @Override
    public void deleteSubTask(int id) {
        Epic.SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.epicId);
        System.out.println("Удаление ПодЗадачи: " + subTask);
        epic.deleteSubTask(subTask);
        subTasks.remove(id);
        updateEpic(epic);
        Managers.getDefaultHistory().clear(id);
    }

}

//BUILDER
    /*public void createTask(String name, String descriptionTask) {
        Task task = new Task.TaskBuilder(typeTask.TASK, name).description(descriptionTask).build();
        tasks.put(task.id, task);
    }

    public void createTask(String name) {
        Task task = new Task.TaskBuilder(typeTask.TASK, name).build();
        tasks.put(task.id, task);
    }

    public void createEpic(String name, String descriptionEpic) {
        Task epic = new Epic.TaskBuilder(typeTask.EPIC, name).description(descriptionEpic).build();
        epics.put(epic.id, epic);
    }

    public void createEpic(String name) {
        Task epic = new Epic.TaskBuilder(typeTask.EPIC, name).build();
        epics.put(epic.id, epic);
    }

    public void createSubTask(String name, String descriptionSubTask, int epicId) {
        if (checkEpicId(epicId)) {
            Task subTask = new SubTask.TaskBuilder(typeTask.SUBTASK, name).description(descriptionSubTask)
                    .epicId(epicId).build();
            subTasks.put(subTask.id, subTask);
        } else System.out.printf("Эпик задачи по данному ID: %d не существует\n", epicId);
    }

    public void createSubTask(String name, int epicId) {
        if (checkEpicId(epicId)) {
            Task subTask = new SubTask.TaskBuilder(typeTask.SUBTASK, name).epicId(epicId).build();
            subTasks.put(subTask.id, subTask);

            //ОШИБКА ТАЙПКАСТА ВНОВЬ ХОТЕЛ В ЭПИК СПИСОК ПОЛОЖИТЬ ПОДЗАДАЧУ !!! ВРОДЕ РАБОТАЕТ???
            ((Epic)epics.get(epicId)).subTasks.add(subTask);
        } else System.out.printf("Эпик задачи по данному ID: %d не существует\n", epicId);

    }*/
//OLD VERSION
    /*public void createTask(Epic epic) {
        epic.id = this.getId();
        epics.put(id, epic);
    }

    public void createTask(Epic epic, SubTask... subTasks) {
        epic.id = this.getId();
        epics.put(id, epic);

        for (SubTask subTask : subTasks) {
            subTask.id = this.getId();
            subTask.setEpic(epic);
            epic.subTasks.add(subTask);
            this.subTasks.put(id, subTask);
        }
    }*/
//Старый метод update передаем ту же задачу, и статус меняем на указанный в конструкторе
    /*@Override
    public void updateTask(Task task, statusTask newStatus) {
        task.status = newStatus;
        System.out.println("Новый статус Задачи \"" + task.name + "\": " + task.status);
    }

    @Override
    public void updateSubTask(Epic.SubTask subTask, statusTask newStatus) {
        subTask.status = newStatus;
        System.out.println("Новый статус ПодЗадачи \"" + subTask.name + "\": " + subTask.status);
        updateEpic(epics.get(subTask.epicId));
    }*/

