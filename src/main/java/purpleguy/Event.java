package purpleguy;
/**
 * Contains a task with a completion status, a start time and an end time.
 */
public class Event extends Task {
    private String start;
    private String end;

    /**
     * Creates an Event task using the given name, start and end times
     * @param name Name of the task
     * @param start Start time of the event
     * @param end End time of the event
     */
    public Event(String name, String start, String end) {
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
