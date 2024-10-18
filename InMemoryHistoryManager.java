import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {
    static TaskLinkedList history;

    public InMemoryHistoryManager() {
        history = new TaskLinkedList();
    }

    @Override
    public void add(Tasks task) {
        if (history.historyMap.containsKey(task.getId()))
            replace(task.getId());
        else {
            history.linkFirst(task); // добавляется в начало последняя запись !!! для смены в конец - linkLast
        }
    }

    public void replace(int id) {
        history.replaceNode(history.historyMap.get(id));
    }

    @Override
    public List<Tasks> getHistory() {
        if (history.isEmpty())
            System.out.println("История просмотренных задач пустая");
        return history.getTasks();
    }

    //Первый стрим в проекте !!! .map() Tasks::getId получаем ID шки объектов класса Tasks и упаковываем в List !!!
    @Override
    public List<Integer> getHistoryIdList() {
        return history.getTasks().stream().map(Tasks::getId).toList();
    }

    // сделал метод Static, убрал @override
    @Override
    public void clear(int id) {
        if (history.historyMap.containsKey(id))  // добавил проверку при удалении
            history.removeNode(history.historyMap.get(id));
    }

    @Override
    public void clearAll() {
        history.clear();
    }

    static class TaskLinkedList {
        Node head;
        Node tail;
        int size;
        private final Map<Integer, Node> historyMap;


        public TaskLinkedList() {
            head = null;
            tail = null;
            size = 0;
            historyMap = new HashMap<>();
        }

        public boolean isEmpty() {
            return (size == 0);
        }

        public boolean isLast() {
            return (size == 1);
        }

        public void initNewNode(Tasks task) {
            Node newNode = new Node(null, task, null);
            head = newNode;
            tail = newNode;
            historyMap.put(task.getId(), newNode);
        }

        public void linkFirst(Tasks task) {
            if (isEmpty())
                initNewNode(task);
            else
            {
                Node newNode = new Node(null, task, head);
                head.prev = newNode;
                head = newNode;
                historyMap.put(task.getId(), newNode);
            }
            size++;
        }

        public void linkLast(Tasks task) {
            if (isEmpty())
                initNewNode(task);
            else
            {
                Node newNode = new Node(tail, task, null);
                tail.next = newNode;
                tail = newNode;
                historyMap.put(task.getId(), newNode);
            }
            size++;
        }

        public void removeFirst() {
            if (!isLast() && !isEmpty()) {
                head.next.prev = null;
                head = head.next;
                size--;
            }
            else
                clear();

        }

        public void removeLast() {
            if (!isLast() && !isEmpty()) {
                tail.prev.next = null;
                tail = tail.prev;
                size--;
            }
            else
                clear();
        }

        public void clear() {
            head = null;
            tail = null;
            size = 0;
            historyMap.clear();
        }

        public void removeNode(Node node) {
            if (isLast())
                clear();
            else if (node == head)
                removeFirst();
            else if (node == tail)
                removeLast();
            else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                size--;
            }
        }

        public void replaceNode(Node node) {
            linkFirst(node.data);
            removeNode(node);
        }

        public List<Tasks> getTasks() {
            List<Tasks> tasksList = new ArrayList<>();
            if (!isEmpty()) {
                Node currentHead = head;
                while (currentHead != null) {
                    tasksList.add(currentHead.data);
                    currentHead = currentHead.next;
                }
            }
            return tasksList;
        }
    }
}
