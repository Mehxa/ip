/**
 * Contains the details of a task with only a completion status.
 */
public class ToDo extends Task {
    ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
