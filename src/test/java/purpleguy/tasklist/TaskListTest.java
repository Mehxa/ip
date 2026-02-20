package purpleguy.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import purpleguy.Deadline;
import purpleguy.Event;
import purpleguy.ToDo;

public class TaskListTest {
    @Test
    public void task_conversion_test() {
        TaskList testTL = new TaskList();
        ToDo testToDo = new ToDo("homework");
        Deadline testDeadline = new Deadline("household chores", LocalDateTime.parse("2026-01-28 13:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        Event testEvent = new Event("Sports Day", LocalDateTime.parse("2026-01-29 13:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), LocalDateTime.parse("2026-01-30 13:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ArrayList<String> testList = new ArrayList<String>();
        testList.add(testToDo.toData());
        testList.add(testDeadline.toData());
        testList.add(testEvent.toData());
        testTL.addTask(testToDo);
        testTL.addTask(testDeadline);
        testTL.addTask(testEvent);
        assertEquals(testTL.toData(), testList.stream().toList());
    }
}
