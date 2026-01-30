package purpleguy.tasklist;
import java.util.ArrayList;
import java.util.List;

import purpleguy.Task;

/**
 * Stores and manages all tasks inputted into the program
 */
public class TaskList {
    private ArrayList<Task> tL;

    public TaskList() {
        tL = new ArrayList<Task>();
    }

    public void addTask(Task task) {
        tL.add(task);
    }

    public boolean isEmpty() {
        return tL.isEmpty();
    }

    public int size() {
        return tL.size();
    }

    public Task get(int index) {
        return tL.get(index);
    }

    public void remove(int index) {
        tL.remove(index);
    }

    public List<String> toData() {
        return tL.stream().map(x->x.toData()).toList();
    }
}
