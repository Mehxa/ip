package purpleguy;
/**
 * Provides the blueprint for all Task-based children classes
 * Contains the details of a Task
 */
public class Task {
    private String name;
    private Boolean marked;

    Task(String name) {
        this.name = name;
        this.marked = false;
    }

    /**
     * Changes a task's status to be complete/marked
     */
    public void mark() {
        this.marked = true;
    }

    /**
     * Changes a task's status to be incomplete/unmarked
     */
    public void unmark() {
        this.marked = false;
    }

    /**
     * Returns an X if this task is marked, or space if the task is unmarked
     * @return Marked status represented by X or " "
     */
    public String getStatusString() {
        return (marked ? "X" : " ");
    }

    /**
     * Returns a formatted string containing the details of the task
     */
    public String toString() {
        return String.format("[%s] %s", this.getStatusString(), this.name);
    }

    public String toData() {
        return String.format("%s | %s", this.getStatusString(), this.name);
    }
}
