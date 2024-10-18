public abstract class Tasks {
    private final int id;
    protected typeTask type;
    protected String name;
    protected statusTask status;
    protected String description = " ";

    protected Tasks() {
        this.status = statusTask.NEW;
        this.id = InMemoryTaskManager.setId();
    }

    protected Tasks(int id, String name, statusTask status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void updateTasks() {
        if (status == statusTask.DONE)
            System.out.println("Задача уже была выполнена!");
        else {
            if (status == statusTask.NEW)
                status = statusTask.IN_PROGRESS;
            else if (status == statusTask.IN_PROGRESS)
                status = statusTask.DONE;
            System.out.println("Новый статус Задачи \"" + name + "\": " + status);
        }
    }

    @Override
    public String toString() {
        return " {" +
                "ID: " + id +
                ", Тип: " + type +
                ", Название: '" + name + '\'' +
                ", Статус: " + status +
                ", Описание: '" + description + '\'' +
                '}';
    }
}
