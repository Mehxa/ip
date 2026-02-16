package purpleguy.tasklist;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Task> findTasks(String s) {
        return tL.stream().filter(x -> x.containsString(s)).toList();
    }

    public List<String> toData() {
        return tL.stream().map(x -> x.toData()).toList();
    }

    public List<Task> getTasksSortedByPriority() {
        return tL.stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .collect(Collectors.toList());
    }
}
