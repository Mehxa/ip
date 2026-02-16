package purpleguy;
/**
 * Provides the blueprint for all Task-based children classes
 * Contains the details of a Task
 */
public class Task {
    private String name;
    private Boolean isMarked;
    private int priority;

    protected Task(String name) {
        this.name = name;
        this.isMarked = false;
        this.priority = 3;
    }

    protected Task(String name, int priority) {
        this.name = name;
        this.isMarked = false;
        this.priority = priority;
    }

    /**
     * Changes a task's status to be complete/marked
     */
    public void mark() {
        this.isMarked = true;
    }

    /**
     * Changes a task's status to be incomplete/unmarked
     */
    public void unmark() {
        this.isMarked = false;
    }

    /**
     * Returns an X if this task is marked, or space if the task is unmarked
     * @return Marked status represented by X or " "
     */
    public String getStatusString() {
        return (isMarked ? "X" : " ");
    }

    /**
     * Checks if the task's name contains a certain substring
     * @param s Substring to check for
     * @return T/F based on if the substring is in the name
     */
    public boolean containsString(String s) {
        return this.name.contains(s);
    }

    public int getPriority() {
        return this.priority;
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
