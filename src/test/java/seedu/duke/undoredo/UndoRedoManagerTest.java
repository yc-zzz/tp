package seedu.duke.undoredo;

import seedu.duke.MoneyBagProMaxException;
import seedu.duke.transaction.Expense;
import seedu.duke.transaction.Transaction;
import seedu.duke.undoredo.UndoRedoManager.ActionPair;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UndoRedoManagerTest {

    private Transaction sampleTransaction() {
        return new Expense("food", 10.00, "lunch", LocalDate.of(2026, 3, 20));
    }

    @Test
    public void recordAdd_thenUndo_returnsAddAction() throws MoneyBagProMaxException {
        UndoRedoManager manager = new UndoRedoManager();
        Transaction t = sampleTransaction();
        manager.recordAdd(t, 0);

        ActionPair action = manager.getUndoAction();
        assertEquals(UndoRedoManager.ActionType.ADD, action.getType());
        assertEquals(t, action.getTransaction());
        assertEquals(0, action.getIndex());
    }

    @Test
    public void recordDelete_thenUndo_returnsDeleteAction() throws MoneyBagProMaxException {
        UndoRedoManager manager = new UndoRedoManager();
        Transaction t = sampleTransaction();
        manager.recordDelete(t, 2);

        ActionPair action = manager.getUndoAction();
        assertEquals(UndoRedoManager.ActionType.DELETE, action.getType());
        assertEquals(t, action.getTransaction());
        assertEquals(2, action.getIndex());
    }

    @Test
    public void undo_thenRedo_roundTripsCorrectly() throws MoneyBagProMaxException {
        UndoRedoManager manager = new UndoRedoManager();
        Transaction t = sampleTransaction();
        manager.recordAdd(t, 0);

        manager.getUndoAction();
        assertTrue(manager.canRedo());

        ActionPair redone = manager.getRedoAction();
        assertEquals(UndoRedoManager.ActionType.ADD, redone.getType());
        assertEquals(t, redone.getTransaction());
    }

    @Test
    public void newAction_clearsRedoStack() throws MoneyBagProMaxException {
        UndoRedoManager manager = new UndoRedoManager();
        Transaction t = sampleTransaction();
        manager.recordAdd(t, 0);
        manager.getUndoAction();
        assertTrue(manager.canRedo());

        manager.recordAdd(sampleTransaction(), 0);
        assertFalse(manager.canRedo());
    }

    @Test
    public void undoOnEmptyStack_throwsException() {
        UndoRedoManager manager = new UndoRedoManager();
        MoneyBagProMaxException e = assertThrows(MoneyBagProMaxException.class, manager::getUndoAction);
        assertEquals("[ERROR!] Nothing to undo.", e.getMessage());
    }

    @Test
    public void redoOnEmptyStack_throwsException() {
        UndoRedoManager manager = new UndoRedoManager();
        MoneyBagProMaxException e = assertThrows(MoneyBagProMaxException.class, manager::getRedoAction);
        assertEquals("[ERROR!] Nothing to redo.", e.getMessage());
    }
}
