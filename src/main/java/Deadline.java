/**
 * Contains the details of a task with a completion status and a deadline.
 */
public class Deadline extends Task {
    private String deadline;
    Deadline(String name, String deadline) {
        super(name);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline + ")";
    }
}
