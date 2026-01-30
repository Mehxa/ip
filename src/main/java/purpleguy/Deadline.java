package purpleguy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains the details of a task with a completion status and a deadline.
 */
public class Deadline extends Task {
    private LocalDateTime deadline;

    /**
     * Creates a new Deadline task using the given name and deadline
     * @param name Name of the task
     * @param deadline LocalDateTime object representing task deadline
     */
    public Deadline(String name, LocalDateTime deadline) {
        super(name);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
            + deadline.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")) + ")";
    }

    @Override
    public String toData() {
        return String.format("D | %s | %s", super.toData(), deadline.format(
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")));
    }
}
