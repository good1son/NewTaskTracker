import java.util.ArrayList;
import java.util.List;

public class Epic extends Tasks {
    private final List<SubTask> subTasks = new ArrayList<>();


    protected List<SubTask> getSubTasks() {
        return subTasks;
    }

    protected void clearSubTasks() {
        subTasks.clear();
    }

    protected void addSubTask (Epic.SubTask subTask) {
        subTasks.add(subTask);
    }

    protected void deleteSubTask (Epic.SubTask subTask) {
        subTasks.remove(subTask);
    }

    private Epic() {
        super();
        this.type = typeTask.EPIC;

    }

    public Epic(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public Epic(int id, String name, statusTask status, String description) {
        super(id, name, status, description);
        this.type = typeTask.EPIC;
    }

    /*public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }*/

    protected boolean isNew() {
        for (SubTask subTask : subTasks)
            if (subTask.status != statusTask.NEW)
                return false;
        System.out.println();
        return true;
    }

    protected boolean isDone() {
        for (SubTask subTask : subTasks)
            if (subTask.status != statusTask.DONE)
                return false;
        return true;
    }

    @Override
    public String toString() {
        return "ЭпикЗадача" + super.toString();
    }

    public static class SubTask extends Epic {
        protected final int epicId;

        private SubTask(int epicId) {
            super();
            this.type = typeTask.SUBTASK;
            this.epicId = epicId;
        }

        public SubTask(String name, String description, int epicId) {
            this(epicId);
            this.name = name;
            this.description = description;
        }

        public SubTask(int id, String name, statusTask status, String description, int epicId) {
            super(id, name, status, description);
            this.type = typeTask.SUBTASK;
            this.epicId = epicId;
        }

        @Override
        public String toString() {
            return "ПодЗадача {" +
                    "ID: " + getId() +
                    ", Тип: " + type +
                    ", Название: '" + name + '\'' +
                    ", Статус: " + status +
                    ", Описание: '" + description + '\'' +
                    ", (ЭпикЗадачи с ID: " + epicId + ") }";
        }
    }
}

