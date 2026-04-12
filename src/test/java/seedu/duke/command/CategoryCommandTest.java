package seedu.duke.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.budget.Budget;
import seedu.duke.category.CategoryManager;
import seedu.duke.transactionlist.RecurringTransactionList;
import seedu.duke.transactionlist.TransactionList;
import seedu.duke.ui.Ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryCommandTest {

    private static class CapturingUi extends Ui {
        final List<String> messages = new ArrayList<>();
        @Override
        public void showMessage(String message) {
            messages.add(message);
        }
    }

    private CapturingUi ui;
    private TransactionList list;
    private RecurringTransactionList recurringList;
    private Budget budget;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = CategoryManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        ui = new CapturingUi();
        list = new TransactionList();
        recurringList = new RecurringTransactionList();
        budget = new Budget();
    }

    @Test
    void execute_addNewCategory_showsSuccess() {
        new CategoryCommand("add", "groceries", recurringList).execute(list, budget, ui);
        assertTrue(ui.messages.get(0).contains("added"));
        assertTrue(CategoryManager.getInstance().isValidExpenseCategory("groceries"));
    }

    @Test
    void execute_addBuiltInCategory_showsAlreadyBuiltIn() {
        new CategoryCommand("add", "food", recurringList).execute(list, budget, ui);
        assertTrue(ui.messages.get(0).contains("built-in"));
        //food cannot be added as a custom category
        assertFalse(CategoryManager.getInstance().getCustomCategories().contains("food"));
    }

    @Test
    void execute_addDuplicateCustom_showsAlreadyExists() {
        CategoryManager.getInstance().addCustomCategory("hobbies");
        new CategoryCommand("add", "hobbies", recurringList).execute(list, budget, ui);
        assertTrue(ui.messages.get(0).contains("already exists"));
    }
    
    @Test
    void execute_removeExistingCustom_showsSuccess() {
        CategoryManager.getInstance().addCustomCategory("hobbies");
        new CategoryCommand("remove", "hobbies", recurringList).execute(list, budget, ui);
        assertTrue(ui.messages.get(0).contains("removed"));
        assertFalse(CategoryManager.getInstance().isValidExpenseCategory("hobbies"));
    }

    @Test
    void execute_removeBuiltIn_showsCannotRemove() {
        new CategoryCommand("remove", "food", recurringList).execute(list, budget, ui);
        assertTrue(ui.messages.get(0).contains("Cannot remove"));
    }

    @Test
    void execute_removeNonExistent_showsNotFound() {
        new CategoryCommand("remove", "xyz", recurringList).execute(list, budget, ui);
        assertTrue(ui.messages.get(0).contains("not found"));
    }
    
    @Test
    void execute_list_showsBuiltInsAndCustom() {
        CategoryManager.getInstance().addCustomCategory("petcare");
        new CategoryCommand("list", "", recurringList).execute(list, budget, ui);
        String output = ui.messages.get(0);
        assertTrue(output.contains("food"));    // built-in
        assertTrue(output.contains("petcare")); // custom
    }

    @Test
    void isMutating_alwaysFalse() {
        assertFalse(new CategoryCommand("add", "x", recurringList).isMutating());
        assertFalse(new CategoryCommand("remove", "x", recurringList).isMutating());
        assertFalse(new CategoryCommand("list", "", recurringList).isMutating());
    }
}
