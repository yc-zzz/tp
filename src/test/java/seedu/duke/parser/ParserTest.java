package seedu.duke.parser;

import seedu.duke.MoneyBagProMaxException;
import seedu.duke.command.Command;
import seedu.duke.command.SortCommand;
import seedu.duke.command.UndoCommand;
import seedu.duke.command.RedoCommand;
import seedu.duke.undoredo.UndoRedoManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// structure of test names: methodToTest_input_expectedOutput
class   ParserTest {

    @Test
    public void parse_sortByDate_returnsSortCommand() throws MoneyBagProMaxException {
        Parser parser = new Parser(new UndoRedoManager());
        Command command = parser.parse("sort by/date");
        assertInstanceOf(SortCommand.class, command);
    }

    @Test
    public void parse_sortByAmount_returnsSortCommand() throws MoneyBagProMaxException {
        Parser parser = new Parser(new UndoRedoManager());
        Command command = parser.parse("sort by/amount");
        assertInstanceOf(SortCommand.class, command);
    }

    @Test
    public void parse_sortByCategory_returnsSortCommand() throws MoneyBagProMaxException {
        Parser parser = new Parser(new UndoRedoManager());
        Command command = parser.parse("sort by/category");
        assertInstanceOf(SortCommand.class, command);
    }

    @Test
    public void parse_sortNoArguments_throwsException() {
        Parser parser = new Parser(new UndoRedoManager());
        assertThrows(MoneyBagProMaxException.class, () -> parser.parse("sort"));
    }

    @Test
    public void parse_sortInvalidCriteria_throwsException() {
        Parser parser = new Parser(new UndoRedoManager());
        assertThrows(MoneyBagProMaxException.class, () -> parser.parse("sort by/invalid"));
    }

    @Test
    public void parse_sortMissingByPrefix_throwsException() {
        Parser parser = new Parser(new UndoRedoManager());
        assertThrows(MoneyBagProMaxException.class, () -> parser.parse("sort date"));
    }

    @Test
    public void parse_undo_returnsUndoCommand() throws MoneyBagProMaxException {
        Parser parser = new Parser(new UndoRedoManager());
        Command command = parser.parse("undo");
        assertInstanceOf(UndoCommand.class, command);
    }

    @Test
    public void parse_redo_returnsRedoCommand() throws MoneyBagProMaxException {
        Parser parser = new Parser(new UndoRedoManager());
        Command command = parser.parse("redo");
        assertInstanceOf(RedoCommand.class, command);
    }

    @Test
    public void parseFilterCommand_missingDates_throwsException() {
        Parser parser = new Parser(new UndoRedoManager());

        String input = "filter from/ to/";
        MoneyBagProMaxException exception = assertThrows(MoneyBagProMaxException.class, () -> {
            parser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Missing 'from'"));
    }

    @Test
    public void parseFilterCommand_invalidDateFormat_throwsException() {
        Parser parser = new Parser(new UndoRedoManager());
        String input = "filter from/31-12-2026 to/2026-12-31";

        MoneyBagProMaxException exception = assertThrows(MoneyBagProMaxException.class, () -> {
            parser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Invalid date format — expected YYYY-MM-DD."));
    }

    @Test
    public void parseFilterCommand_fromAfterTo_throwsException() {
        Parser parser = new Parser(new UndoRedoManager());

        String input = "filter from/2026-12-31 to/2026-01-01";
        MoneyBagProMaxException exception = assertThrows(MoneyBagProMaxException.class, () -> {
            parser.parse(input);
        });
        assertTrue(exception.getMessage().contains("The 'from' date cannot be after the 'to' date!"));
    }
}
