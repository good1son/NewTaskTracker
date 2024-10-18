public class Task extends Tasks {
    private Task() {
        super();
        this.type = typeTask.TASK;
    }

    public Task(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, statusTask status, String description) {
        super(id, name, status, description);
        this.type = typeTask.TASK;
    }

    @Override
    public String toString() {
        return "Задача" + super.toString();
    }
}


