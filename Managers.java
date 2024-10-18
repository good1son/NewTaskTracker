public class Managers {
    TaskManager manager;
    static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();



    public TaskManager getDefault() {
        return manager;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManager;
    }
}
