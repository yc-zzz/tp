package seedu.duke.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import seedu.duke.MoneyBagProMaxException;
import seedu.duke.transaction.Expense;
import seedu.duke.transaction.Income;
import seedu.duke.transactionlist.TransactionList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvExporterTest {

    private static final String OUTPUT_FILE = "data/test_export.csv";

    private CsvExporter exporter;
    private TransactionList list;

    @BeforeEach
    void setUp() {
        exporter = new CsvExporter();
        list = new TransactionList();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(OUTPUT_FILE));
    }

    @Test
    void export_emptyList_writesHeaderOnly() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(1, lines.size());
        assertEquals("date,type,category,description,amount", lines.get(0));
    }

    @Test
    void export_singleExpense_writesHeaderAndOneRow() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertEquals("date,type,category,description,amount", lines.get(0));
        assertTrue(lines.get(1).contains("food"));
        assertTrue(lines.get(1).contains("10.0"));
        assertTrue(lines.get(1).contains("lunch"));
        assertTrue(lines.get(1).contains("expense"));
        assertTrue(lines.get(1).contains("2026-03-23"));
    }

    @Test
    void export_singleIncome_writesCorrectType() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Income("salary", 3000.0, "march pay", LocalDate.of(2026, 3, 1)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("income"));
        assertTrue(lines.get(1).contains("salary"));
        assertTrue(lines.get(1).contains("3000.0"));
    }

    @Test
    void export_multipleTransactions_writesAllRows() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));
        list.add(new Income("salary", 3000.0, "march pay", LocalDate.of(2026, 3, 1)));
        list.add(new Expense("transport", 2.50, "bus", LocalDate.of(2026, 3, 22)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(4, lines.size());
    }

    @Test
    void export_preservesTransactionOrder() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));
        list.add(new Income("salary", 3000.0, "pay", LocalDate.of(2026, 3, 1)));
        list.add(new Expense("transport", 2.50, "bus", LocalDate.of(2026, 3, 22)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(lines.get(1).contains("food"));
        assertTrue(lines.get(2).contains("salary"));
        assertTrue(lines.get(3).contains("transport"));
    }

    @Test
    void export_descriptionWithComma_escapedWithQuotes() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 12.0, "rice, noodles", LocalDate.of(2026, 3, 23)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(lines.get(1).contains("\"rice, noodles\""));
    }

    @Test
    void export_descriptionWithQuote_escapedCorrectly() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 5.0, "Bob's \"special\"", LocalDate.of(2026, 3, 23)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(lines.get(1).contains("\"\""));
    }

    @Test
    void export_emptyDescription_writesEmptyField() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("misc", 5.0, "", LocalDate.of(2026, 1, 15)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains(",,"));
    }

    @Test
    void export_invalidOutputPath_throwsMoneyBagProMaxException() {
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));

        assertThrows(MoneyBagProMaxException.class,
                     () -> exporter.export(list, "/nonexistent_directory/output.csv"));
    }

    @Test
    void export_overwritesExistingFile() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));
        exporter.export(list, OUTPUT_FILE);

        TransactionList updated = new TransactionList();
        updated.add(new Income("salary", 500.0, "allowance", LocalDate.of(2026, 3, 1)));
        exporter.export(updated, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("income"));
    }

    @Test
    void export_headerColumnsAreCorrect() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        exporter.export(list, OUTPUT_FILE);

        String header = Files.readAllLines(Paths.get(OUTPUT_FILE)).get(0);
        assertEquals("date,type,category,description,amount", header);
    }

    @Test
    void export_rowFieldOrderMatchesHeader() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));

        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        String[] fields = lines.get(1).split(",");
        assertEquals("2026-03-23", fields[0]);
        assertEquals("expense", fields[1]);
        assertEquals("food", fields[2]);
        assertEquals("lunch", fields[3]);
        assertEquals("10.0", fields[4]);
    }

    @Test
    void export_returnsPathMatchingOutputPath() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));

        Path returned = exporter.export(list, OUTPUT_FILE);

        assertNotNull(returned);
        assertEquals(Paths.get(OUTPUT_FILE).toAbsolutePath(), returned.toAbsolutePath());
    }

    @Test
    void export_returnedPath_fileExists() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "lunch", LocalDate.of(2026, 3, 23)));

        Path returned = exporter.export(list, OUTPUT_FILE);

        assertNotNull(returned);
        assertTrue(Files.exists(returned));
    }

    @Test
    void export_veryLargeList_writesAllRows() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        for (int i = 0; i < 1000; i++) {
            list.add(new Expense("food", 10.0, "item" + i, LocalDate.of(2026, 3, 23)));
        }
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(1001, lines.size()); // 1000 rows + header
    }

    @Test
    void export_descriptionWithNewline_newlineReplacedWithSpace() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "line1\nline2", LocalDate.of(2026, 3, 23)));
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("line1 line2"));
    }

    @Test
    void export_veryLargeAmount_writesCorrectly() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", Double.MAX_VALUE, "test", LocalDate.of(2026, 3, 23)));
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains(String.valueOf(Double.MAX_VALUE)));
    }

    @Test
    void export_descriptionWithCarriageReturn_handledGracefully() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "line1\rline2", LocalDate.of(2026, 3, 23)));
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("line1 line2"));
    }

    @Test
    void export_descriptionWithBothQuoteAndComma_escapedCorrectly() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.0, "rice, \"special\"", LocalDate.of(2026, 3, 23)));
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("\"rice, \"\"special\"\"\""));
    }

    @Test
    void export_amountWithManyDecimalPlaces_writesCorrectly() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        list.add(new Expense("food", 10.123456789, "test", LocalDate.of(2026, 3, 23)));
        exporter.export(list, OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("10.123456789"));
    }
}
