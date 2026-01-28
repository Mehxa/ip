/**
 * Contains a task with a completion status, a start time and an end time.
 */
public class Event extends Task {
    private String start;
    private String end;

    Event(String name, String start, String end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), start, end);
    }

    @Override
    public String toData() {
        return String.format("E | %s | %s - %s", super.toData(), start, end);
    }
}
