package highdeger.highdownloader;

public class HighQueue {
    int id;
    String name;
    int speed_limit;

    public HighQueue() {
    }

    public HighQueue(int id, String name, int speed_limit) {
        this.id = id;
        this.name = name;
        this.speed_limit = speed_limit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed_limit() {
        return speed_limit;
    }

    public void setSpeed_limit(int speed_limit) {
        this.speed_limit = speed_limit;
    }
}
