import java.util.List;

public interface HistoryManager {
    void add(Tasks task);
    List<Tasks> getHistory();
    List<Integer> getHistoryIdList();
    void clear (int id);
    void clearAll ();

}