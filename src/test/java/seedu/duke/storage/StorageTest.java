package seedu.duke.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import seedu.duke.MoneyBagProMaxException;
import seedu.duke.budget.Budget;
import seedu.duke.transaction.Expense;
import seedu.duke.transaction.Frequency;
import seedu.duke.transaction.Income;
import seedu.duke.transaction.RecurringTransaction;
import seedu.duke.transactionlist.RecurringTransactionList;
import seedu.duke.transactionlist.TransactionList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageTest {

    private static final String DATA_FILE = "data/transactions.txt";
    private static final String TMP_FILE  = "data/transactions.txt.tmp";
    private static final String REC_FILE  = "data/recurring.txt";
    private static final String REC_TMP   = "data/recurring.txt.tmp";
    
    private Storage storage;
    private TransactionList list;
    private Budget budget;

    @BeforeEach
    void setUp() {
        storage = new Storage();
        list = new TransactionList();
        budget = new Budget();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(DATA_FILE));
        Files.deleteIfExists(Paths.get(TMP_FILE));
        Files.deleteIfExists(Paths.get(REC_FILE));
        Files.deleteIfExists(Paths.get(REC_TMP));
    }

    @Test
    void load_noFileExists_createsEmptyFile() throws MoneyBagProMaxException {
        storage.load(list, budget);
        assertTrue(Files.exists(Paths.get(DATA_FILE)));
        assertEquals(0, list.size());
    }

    @Test
    void load_emptyFile_listRemainsEmpty() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.createFile(Paths.get(DATA_FILE));
        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_validExpense_restoredCorrectly() throws MoneyBagProMaxException {
        list.add(new Expense("food", 10.0, "lunch",
                             LocalDate.of(2026, 3, 23)));
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);

        assertEquals(1, loaded.size());
        assertEquals("food", loaded.get(0).getCategory());
        assertEquals(10.0, loaded.get(0).getAmount());
        assertEquals("lunch", loaded.get(0).getDescription());
        assertEquals(LocalDate.of(2026, 3, 23), loaded.get(0).getDate());
        assertEquals("expense", loaded.get(0).getType());
    }

    @Test
    void load_validIncome_restoredCorrectly() throws MoneyBagProMaxException {
        list.add(new Income("salary", 3000.0, "march pay",
                            LocalDate.of(2026, 3, 1)));
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);

        assertEquals(1, loaded.size());
        assertEquals("salary", loaded.get(0).getCategory());
        assertEquals(3000.0, loaded.get(0).getAmount());
        assertEquals("income", loaded.get(0).getType());
    }

    @Test
    void load_multipleTransactions_allRestored() throws MoneyBagProMaxException {
        list.add(new Expense("food", 10.0, "lunch",
                             LocalDate.of(2026, 3, 23)));
        list.add(new Income("salary", 3000.0, "march pay",
                            LocalDate.of(2026, 3, 1)));
        list.add(new Expense("transport", 2.50, "bus",
                             LocalDate.of(2026, 3, 22)));
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);

        assertEquals(3, loaded.size());
    }

    @Test
    void load_malformedLine_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=INVALID | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_unknownType_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=transfer | category=misc | amount=50.0 | description= | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_missingDescriptionField_defaultsToEmpty() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=10.0 | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(1, list.size());
        assertEquals("", list.get(0).getDescription());
    }

    @Test
    void save_emptyList_writesEmptyFile() throws MoneyBagProMaxException {
        storage.save(list, budget);
        assertTrue(Files.exists(Paths.get(DATA_FILE)));
    }

    @Test
    void save_afterDelete_fileReflectsDeletion() throws MoneyBagProMaxException {
        list.add(new Expense("food", 10.0, "lunch",
                             LocalDate.of(2026, 3, 23)));
        list.add(new Expense("transport", 2.50, "bus",
                             LocalDate.of(2026, 3, 22)));
        storage.save(list, budget);

        list.remove(0);
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);
        assertEquals(1, loaded.size());
        assertEquals("transport", loaded.get(0).getCategory());
    }

    @Test
    void save_overwritesPreviousSave() throws MoneyBagProMaxException {
        list.add(new Expense("food", 10.0, "lunch",
                             LocalDate.of(2026, 3, 23)));
        storage.save(list, budget);

        TransactionList updated = new TransactionList();
        updated.add(new Income("salary", 500.0, "allowance",
                               LocalDate.of(2026, 3, 1)));
        storage.save(updated, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);
        assertEquals(1, loaded.size());
        assertEquals("income", loaded.get(0).getType());
    }

    @Test
    void load_expenseWithEmptyDescription_preservedCorrectly() throws MoneyBagProMaxException {
        list.add(new Expense("misc", 5.0, "", 
                             LocalDate.of(2026, 1, 15)));
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);

        assertEquals("", loaded.get(0).getDescription());
        assertEquals(5.0, loaded.get(0).getAmount());
        assertEquals("misc", loaded.get(0).getCategory());
    }

    @Test
    void load_nonPrefixedLines_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "# this is a comment\n\n"
                                  + "[TXN] | type=expense | category=food | amount=10.0"
                                  + " | description=lunch | date=2026-03-23\n"
                                  + "some random line\n");
        storage.load(list, budget);
        assertEquals(1, list.size());
    }

    @Test
    void load_clearsExistingListBeforeLoading() throws MoneyBagProMaxException {
        list.add(new Expense("food", 10.0, "lunch", 
                             LocalDate.of(2026, 3, 23)));
        list.add(new Expense("transport", 2.50, "bus", 
                             LocalDate.of(2026, 3, 22)));
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        loaded.add(new Expense("misc", 99.0, "stale", 
                               LocalDate.of(2026, 1, 1)));
        storage.load(loaded, newBudget);

        assertEquals(2, loaded.size());
    }

    @Test
    void save_andLoad_preservesTransactionOrder() throws MoneyBagProMaxException {
        list.add(new Expense("food", 10.0, "lunch", 
                             LocalDate.of(2026, 3, 23)));
        list.add(new Income("salary", 3000.0, "pay",
                            LocalDate.of(2026, 3, 1)));
        list.add(new Expense("transport", 2.50, "bus",
                             LocalDate.of(2026, 3, 22)));
        storage.save(list, budget);

        TransactionList loaded = new TransactionList();
        Budget newBudget = new Budget();
        storage.load(loaded, newBudget);

        assertEquals("food", loaded.get(0).getCategory());
        assertEquals("salary", loaded.get(1).getCategory());
        assertEquals("transport", loaded.get(2).getCategory());
    }

    // loadRecurring() / saveRecurring() round-trip tests

    @Test
    void saveRecurring_andLoad_restoresTemplate() throws MoneyBagProMaxException {
        RecurringTransactionList recList = new RecurringTransactionList();
        LocalDate start = LocalDate.of(2026, 3, 1);
        recList.add(new RecurringTransaction("food", 10.0, "lunch", Frequency.WEEKLY, start));
        storage.saveRecurring(recList);

        RecurringTransactionList loaded = new RecurringTransactionList();
        storage.loadRecurring(loaded);

        assertEquals(1, loaded.size());
        assertEquals("food", loaded.get(0).getCategory());
        assertEquals(10.0, loaded.get(0).getAmount());
        assertEquals("lunch", loaded.get(0).getDescription());
        assertEquals(Frequency.WEEKLY, loaded.get(0).getFrequency());
        assertEquals(start, loaded.get(0).getStartDate());
        assertNull(loaded.get(0).getLastGeneratedDate());
    }

    @Test
    void saveRecurring_withLastGeneratedDate_roundTripsCorrectly() throws MoneyBagProMaxException {
        RecurringTransactionList recList = new RecurringTransactionList();
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate lastGen = LocalDate.of(2026, 3, 24);
        RecurringTransaction rt = new RecurringTransaction("salary", 500.0, "", Frequency.MONTHLY, start);
        rt.setLastGeneratedDate(lastGen);
        recList.add(rt);
        storage.saveRecurring(recList);

        RecurringTransactionList loaded = new RecurringTransactionList();
        storage.loadRecurring(loaded);

        assertEquals(1, loaded.size());
        assertEquals(lastGen, loaded.get(0).getLastGeneratedDate());
    }

    @Test
    void saveRecurring_nullLastGeneratedDate_roundTripsAsNull() throws MoneyBagProMaxException {
        RecurringTransactionList recList = new RecurringTransactionList();
        recList.add(new RecurringTransaction("food", 10.0, "", Frequency.DAILY, LocalDate.of(2026, 3, 1)));
        storage.saveRecurring(recList);

        RecurringTransactionList loaded = new RecurringTransactionList();
        storage.loadRecurring(loaded);

        assertNull(loaded.get(0).getLastGeneratedDate());
    }

    @Test
    void loadRecurring_noFile_createsEmptyList() throws MoneyBagProMaxException {
        RecurringTransactionList recList = new RecurringTransactionList();
        storage.loadRecurring(recList);
        assertTrue(recList.isEmpty());
    }

    @Test
    void saveRecurring_noDataDirectory_createsItAndSaves() throws MoneyBagProMaxException, IOException {
        Files.deleteIfExists(Paths.get(DATA_FILE));
        Files.deleteIfExists(Paths.get(REC_FILE));
        Files.deleteIfExists(Paths.get("data/categories.txt"));
        Files.deleteIfExists(Paths.get("data"));

        RecurringTransactionList recList = new RecurringTransactionList();
        recList.add(new RecurringTransaction("food", 10.0, "", Frequency.DAILY, LocalDate.of(2026, 3, 1)));
        storage.saveRecurring(recList);

        assertTrue(Files.exists(Paths.get(REC_FILE)));
    }

    @Test
    void save_noDataDirectory_createsItAndSaves() throws MoneyBagProMaxException, IOException {
        Files.deleteIfExists(Paths.get(DATA_FILE));
        Files.deleteIfExists(Paths.get("data/categories.txt"));
        Files.deleteIfExists(Paths.get("data"));

        list.add(new Expense("food", 10.0, "lunch",
                             LocalDate.of(2026, 3, 23)));
        storage.save(list, budget);

        assertTrue(Files.exists(Paths.get(DATA_FILE)));
    }

    @Test
    void load_negativeAmount_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=-10.0"
                                  + " | description=lunch | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_zeroAmount_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=0.0 | description=lunch | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_invalidDateFormat_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=10.0 | description=lunch | date=23-03-2026\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_missingAmountField_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | description=lunch | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_missingDateField_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=10.0 | description=lunch\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_missingCategoryField_skipped() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | amount=10.0 | description=lunch | date=2026-03-23\n");

        storage.load(list, budget);
        assertEquals(0, list.size());
    }

    @Test
    void load_corruptedAndValidMixed_onlyValidLoaded() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE),
                          "[TXN] | type=expense | category=food | amount=10.0"
                                  + " | description=lunch | date=2026-03-23\n"
                                  + "[TXN] | type=expense | category=food | amount=-5.0"
                                  + " | description=bad | date=2026-03-23\n"
                                  + "[TXN] | type=income | category=salary | amount=3000.0"
                                  + " | description=pay | date=2026-03-01\n");

        storage.load(list, budget);
        assertEquals(2, list.size());
        assertEquals("food", list.get(0).getCategory());
        assertEquals("salary", list.get(1).getCategory());
    }

    @Test
    void saveAndLoad_budgetPersists() throws Exception {
        Storage storage = new Storage();
        TransactionList list = new TransactionList();
        Budget budget = new Budget();

        budget.setMonthlyBudget(500);
        storage.save(list, budget);

        // simulate restart
        TransactionList loadedList = new TransactionList();
        Budget loadedBudget = new Budget();

        storage.load(loadedList, loadedBudget);

        assertEquals(500, loadedBudget.getMonthlyBudget());
    }
}
