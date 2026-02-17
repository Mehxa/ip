package purpleguy.parser;

import purpleguy.CommandRunner;
import purpleguy.CommandValidator;
import purpleguy.exception.AftonException;
import purpleguy.tasklist.TaskList;

/**
 * Parses the commands inputted by the user
 */
public class Parser {
    private static TaskList tL;
    private final CommandValidator validator;
    private final CommandRunner runner;

    /**
     * Creates a new Parser object
     * @param ui AftonUI for printing out success and error messages
     * @param taskList ArrayList of Tasks, the current taskList
     */
    public Parser(TaskList taskList) {
        tL = taskList;
        validator = new CommandValidator();
        runner = new CommandRunner(taskList);
    }

    /**
     * Formats the input into an array of strings which will be used to validate and execute the command
     * @param input String input from user, to be parsed
     * @throws AftonException If error is occured during validateCommand
     */
    public String parse(String input) throws AftonException {
        String[] caseVars = input.split("\\s+", 2); // To extract command
        String command = caseVars[0].toLowerCase();
        String[] details = (caseVars.length < 2 || caseVars[1].trim().isEmpty())
            ? new String[0]
            : caseVars[1].trim().split("\s+(?=/)| ^\s+ | \s+");
        validator.validate(command, details, tL);
        return runner.run(command, details);
    }
}
