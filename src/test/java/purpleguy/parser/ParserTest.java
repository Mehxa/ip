package purpleguy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import purpleguy.exception.AftonException;
import purpleguy.tasklist.TaskList;
import purpleguy.ui.AftonUI;

public class ParserTest {
    private AftonUI testUi = new AftonUI();
    private TaskList taskList = new TaskList();

    @Test
    public void missing_info_test() {
        Parser testParser = new Parser(testUi, taskList);
        // Test missing task name
        Exception exception = assertThrows(AftonException.class, () -> testParser.parse("todo"));
        assertEquals("A hollow entry? Much like those empty suits, "
                    + "it's useless without...something inside. Give it a name."
                    + "\n[HINT]: Try: todo [description] ...", exception.getMessage());

        // Test missing index
        exception = assertThrows(AftonException.class, () -> testParser.parse("mark"));
        assertEquals("You're pointing at thin air. Give me a number, or step away. "
                    + "\n[HINT]: Usage: mark [task index number]", exception.getMessage());

        // Test missing /by tag for deadline
        exception = assertThrows(AftonException.class, () -> testParser.parse("deadline code 2026-01-27 23:59"));
        assertEquals("How disappointing. You forgot the '/by' tag."
                    + " Precision is the difference between a masterpiece and a... mess."
                    + "\n[HINT]: A deadline requires a '/by [time]' marker.", exception.getMessage());
    }
}
