package purpleguy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains a task with a completion status, a start time and an end time.
 */
public class Event extends Task {
    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Creates an Event task using the given name, start and end times
     * @param name Name of the task
     * @param start Start time of the event
     * @param end End time of the event
     */
    public Event(String name, LocalDateTime start, LocalDateTime end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
        start.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")),
        end.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")));
    }

    @Override
    public String toData() {
        return String.format("E | %s | %s - %s", super.toData(),
        start.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")),
        end.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")));
    }
}
