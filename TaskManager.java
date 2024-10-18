import java.io.IOException;
import java.util.List;

public interface TaskManager {

    void addTask(Task task) throws IOException;
    void addTask(Epic epic) throws IOException;
    void addTask(Epic.SubTask subTask) throws IOException;

    List<Task> getAllTasks() throws IOException;
    List<Epic> getAllEpics() throws IOException;
    List<Epic.SubTask> getAllSubTasks() throws IOException;
    List<Epic.SubTask> getAllSubTaskFromEpic(int id) throws IOException;

    void deleteAllTasks() throws IOException;
    void deleteAllEpics() throws IOException;
    void deleteAllSubTasks() throws IOException;
    void deleteAllSubTasksFromEpicId(int id) throws IOException;

    Task getTask(int id) throws IOException;
    Epic getEpic(int id) throws IOException;
    Epic.SubTask getSubTask(int id) throws IOException;

    void updateTask(Task task) throws IOException;
    void updateEpic(Epic epic) throws IOException;
    void updateSubTask(Epic.SubTask subTask) throws IOException;

    void deleteTask(int id) throws IOException;
    void deleteEpic(int id) throws IOException;
    void deleteSubTask(int id) throws IOException;



    /*void updateTask(Task task, statusTask newStatus);
    void updateSubTask(Epic.SubTask subTask, statusTask newStatus);*/

}
