package seedu.duke.storage;

import seedu.duke.MoneyBagProMaxException;
import seedu.duke.budget.Budget;
import seedu.duke.category.CategoryManager;
import seedu.duke.transaction.Expense;
import seedu.duke.transaction.Frequency;
import seedu.duke.transaction.Income;
import seedu.duke.transaction.RecurringTransaction;
import seedu.duke.transaction.Transaction;
import seedu.duke.transactionlist.RecurringTransactionList;
import seedu.duke.transactionlist.TransactionList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Flat-file persistent storage for MoneyBagProMax.
 * Reads and writes transactions to {@code data/transactions.txt}.
 */
public class Storage {

    private static final Logger logger = Logger.getLogger(Storage.class.getName());
    static {
        logger.setLevel(Level.WARNING);
    }

    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "data/transactions.txt";
    private static final String REC_FILE = "data/recurring.txt";

    private static final String TXN_PREFIX = "[TXN]";
    private static final String REC_PREFIX = "[REC]";
    private static final String FIELD_SEP = " | ";
    private static final String KV_SEP = "=";

    private static final List<String> VALID_TYPES = List.of("income", "expense");

    private static final String BUDGET_PREFIX = "[BUDGET]";


    /**
     * Populates the transaction list from disk. Call once on app startup.
     *
     * @param list TransactionList, required — the list to load transactions into.
     * @throws MoneyBagProMaxException if the file cannot be read.
     */
    public void load(TransactionList list, Budget budget) throws MoneyBagProMaxException {
        assert list != null : "TransactionList should not be null";
        assert budget != null : "Budget should not be null";

        try {
            if (!initialiseFile()) {
                return;
            }
            clearList(list);
            parseLines(list, budget, Files.readAllLines(Paths.get(DATA_FILE)));
        } catch (IOException e) {
            throw new MoneyBagProMaxException("Failed to load data: " + e.getMessage());
        }
    }

    /**
     * Creates the data directory and file if they do not exist.
     *
     * @return true if the file already existed, false if it was newly created.
     * @throws IOException if the directory or file cannot be created.
     */
    private boolean initialiseFile() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        Path p = Paths.get(DATA_FILE);
        if (!Files.exists(p)) {
            Files.createFile(p);
            return false;
        }
        return true;
    }

    /**
     * Removes all transactions from the list.
     *
     * @param list TransactionList, required — the list to clear.
     */
    private void clearList(TransactionList list) {
        while (!list.isEmpty()) {
            list.remove(0);
        }
    }

    /**
     * Parses each line from the file and adds valid transactions to the list.
     *
     * @param list   TransactionList, required — the list to populate.
     * @param budget
     * @param lines  List of raw lines read from the storage file.
     */
    private void parseLines(TransactionList list, Budget budget, List<String> lines) {
        for (String line : lines) {
            if (line.startsWith(BUDGET_PREFIX)) {
                Map<String, String> f = parseLine(line.replace(BUDGET_PREFIX, TXN_PREFIX));
                if (f != null && f.containsKey("amount")) {
                    try {
                        double amount = Double.parseDouble(f.get("amount"));
                        budget.setMonthlyBudget(amount);
                    } catch (NumberFormatException e) {
                        logger.warning("Invalid budget amount: " + line);
                    }
                }
                continue;
            }
            if (!line.startsWith(TXN_PREFIX)) {
                continue;
            }
            Map<String, String> f = parseLine(line);
            if (f == null) {
                logger.warning("Skipping malformed line (could not parse): " + line);
                continue;
            }
            try {
                Transaction t = validateAndBuild(f, line);
                if (t != null) {
                    list.add(t);
                }
            } catch (CorruptedEntryException e) {
                logger.warning("Skipping corrupted entry: " + e.getMessage());
            }
        }
    }

    /**
     * Validates the fields of a parsed transaction and builds the Transaction object.
     *
     * @param f Map of field names to values.
     * @param line The original raw line, used for error context.
     * @return A valid Transaction, or null if type is unrecognised.
     * @throws CorruptedEntryException if any field is missing or invalid.
     */
    private Transaction validateAndBuild(Map<String, String> f, String line)
            throws CorruptedEntryException {
        validateFields(f, line);
        return buildTransaction(f, line);
    }

    /**
     * Extracts fields from the map and builds the Transaction object.
     *
     * @param f Map of field names to values.
     * @param line The original raw line, used for error context.
     * @return A valid Transaction, or null if type is unrecognised.
     * @throws CorruptedEntryException if any field is missing or invalid.
     */
    private Transaction buildTransaction(Map<String, String> f, String line)
            throws CorruptedEntryException {
        String type = f.get("type");
        String category = f.get("category");
        String amountStr = f.get("amount");
        String description = f.getOrDefault("description", "");
        String dateStr = f.get("date");

        validateType(type, line);
        double amount = validateAmount(amountStr, line);
        LocalDate date = validateDate(dateStr, line);

        return switch (type) {
        case "income" -> new Income(category, amount, description, date);
        case "expense" -> new Expense(category, amount, description, date);
        default -> null;
        };
    }

    /**
     * Checks that all required fields are present and non-blank.
     *
     * @param f Map of field names to values.
     * @param line The original raw line, used for error context.
     * @throws CorruptedEntryException if any required field is missing or blank.
     */
    private void validateFields(Map<String, String> f, String line)
            throws CorruptedEntryException {
        for (String field : new String[]{"type", "category", "amount", "date"}) {
            if (f.get(field) == null || f.get(field).isBlank()) {
                throw new CorruptedEntryException(
                        "missing or empty field '" + field + "' in: " + line);
            }
        }
    }

    /**
     * Checks that the transaction type is one of the known valid values.
     *
     * @param type The type string to validate.
     * @param line The original raw line, used for error context.
     * @throws CorruptedEntryException if the type is not recognised.
     */
    private void validateType(String type, String line) throws CorruptedEntryException {
        if (!VALID_TYPES.contains(type)) {
            throw new CorruptedEntryException(
                    "unknown transaction type '" + type + "' in: " + line);
        }
    }

    /**
     * Parses and validates the amount string, ensuring it is a positive number.
     *
     * @param amountStr The amount string to validate.
     * @param line The original raw line, used for error context.
     * @return The parsed amount as a double.
     * @throws CorruptedEntryException if the amount is not a valid positive number.
     */
    private double validateAmount(String amountStr, String line) throws CorruptedEntryException {
        double amount = parseAmount(amountStr, line);
        checkAmountPositive(amount, line);
        return amount;
    }

    /**
     * Parses the amount string into a double.
     *
     * @param amountStr The amount string to parse.
     * @param line The original raw line, used for error context.
     * @return The parsed amount as a double.
     * @throws CorruptedEntryException if the amount string is not a valid number.
     */
    private double parseAmount(String amountStr, String line) throws CorruptedEntryException {
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new CorruptedEntryException(
                    "amount '" + amountStr + "' is not a valid number in: " + line);
        }
    }

    /**
     * Checks that the amount is a positive number.
     *
     * @param amount The amount to check.
     * @param line The original raw line, used for error context.
     * @throws CorruptedEntryException if the amount is not positive.
     */
    private void checkAmountPositive(double amount, String line) throws CorruptedEntryException {
        if (amount <= 0) {
            throw new CorruptedEntryException(
                    "amount must be positive but was '" + amount + "' in: " + line);
        }
    }

    /**
     * Parses and validates the date string as a YYYY-MM-DD date.
     *
     * @param dateStr The date string to validate.
     * @param line The original raw line, used for error context.
     * @return The parsed LocalDate.
     * @throws CorruptedEntryException if the date is not a valid YYYY-MM-DD date.
     */
    private LocalDate validateDate(String dateStr, String line) throws CorruptedEntryException {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new CorruptedEntryException(
                    "date '" + dateStr + "' is not a valid YYYY-MM-DD date in: " + line);
        }
    }

    /**
     * Parses a storage line into a key-value field map.
     *
     * @param line String, required — a raw line from the storage file.
     * @return Map of field names to values, or null if the line is malformed.
     */
    private Map<String, String> parseLine(String line) {
        assert line != null : "Line should not be null";
        assert line.startsWith(TXN_PREFIX) : "Line should start with TXN prefix";
        try {
            Map<String, String> fields = new LinkedHashMap<>();
            String[] parts = line.split("\\s*\\|\\s*");
            for (int i = 1; i < parts.length; i++) {
                int eq = parts[i].indexOf(KV_SEP);
                if (eq < 0) {
                    continue;
                }
                fields.put(parts[i].substring(0, eq).trim(),
                           parts[i].substring(eq + 1).trim());
            }
            return fields;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Atomically writes the transaction list to disk. Call after every mutating command.
     *
     * @param list TransactionList, required — the list to save.
     * @throws MoneyBagProMaxException if the file cannot be written.
     */
    public void save(TransactionList list, Budget budget) throws MoneyBagProMaxException {
        assert list != null : "TransactionList should not be null";
        assert budget != null : "Budget should not be null";
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            List<String> lines = new ArrayList<>();

            lines.add(BUDGET_PREFIX + FIELD_SEP + "amount" + KV_SEP + budget.getMonthlyBudget());
            for (int i = 0; i < list.size(); i++) {
                lines.add(serializeLine(list.get(i)));
            }

            Path target = Paths.get(DATA_FILE);
            Path tmp = Paths.get(DATA_FILE + ".tmp");
            Files.write(tmp, lines);
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new MoneyBagProMaxException("Failed to save data: " + e.getMessage());
        }
    }

    /**
     * Serializes a transaction into a pipe-delimited string for storage.
     *
     * @param t Transaction, required — the transaction to serialize.
     * @return String — the formatted storage line.
     */
    private String serializeLine(Transaction t) {
        assert t != null : "Transaction should not be null";
        assert t.getType() != null : "Transaction type should not be null";
        assert t.getCategory() != null : "Transaction category should not be null";
        assert t.getAmount() > 0 : "Transaction amount should be positive";
        assert t.getDate() != null : "Transaction date should not be null";
        return TXN_PREFIX + FIELD_SEP + String.join(FIELD_SEP,
                                                    "type" + KV_SEP + t.getType(),
                                                    "category" + KV_SEP + t.getCategory(),
                                                    "amount" + KV_SEP + t.getAmount(),
                                                    "description" + KV_SEP + t.getDescription(),
                                                    "date" + KV_SEP + t.getDate()
        );
    }

    /**
     * Populates the recurring transaction list from disk. Call once on app startup.
     *
     * @param list RecurringTransactionList, required — the list to load templates into.
     * @throws MoneyBagProMaxException if the file cannot be read.
     */
    public void loadRecurring(RecurringTransactionList list) throws MoneyBagProMaxException {
        assert list != null : "RecurringTransactionList should not be null";
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Path p = Paths.get(REC_FILE);
            if (!Files.exists(p)) {
                Files.createFile(p);
                return;
            }
            list.clear();
            for (String line : Files.readAllLines(p)) {
                if (!line.startsWith(REC_PREFIX)) {
                    continue;
                }
                try {
                    Map<String, String> f = parseRecurringLine(line);
                    String category    = f.get("category");
                    String amountStr   = f.get("amount");
                    String description = f.getOrDefault("description", "");
                    String freqStr     = f.get("frequency");
                    String startStr    = f.get("startDate");
                    String lastStr     = f.get("lastGeneratedDate");
                    if (category == null || amountStr == null || freqStr == null || startStr == null) {
                        continue;
                    }
                    boolean knownCategory = Income.VALID_CATEGORIES.contains(category.toLowerCase())
                            || Expense.VALID_CATEGORIES.contains(category.toLowerCase())
                            || CategoryManager.getInstance().getCustomCategories().contains(category.toLowerCase());
                    if (!knownCategory) {
                        logger.warning("Skipping recurring line with invalid category '"
                                + category + "': " + line);
                        continue;
                    }
                    double amount      = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        logger.warning("Skipping recurring line with non-positive amount: " + line);
                        continue;
                    }
                    Frequency freq     = Frequency.fromString(freqStr);
                    LocalDate start    = LocalDate.parse(startStr);
                    RecurringTransaction rt = new RecurringTransaction(category, amount, description, freq, start);
                    if (lastStr != null && !lastStr.equals("null")) {
                        rt.setLastGeneratedDate(LocalDate.parse(lastStr));
                    }
                    list.add(rt);
                } catch (Exception e) {
                    logger.warning("Skipping malformed recurring line '" + line + "': " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new MoneyBagProMaxException("Failed to load recurring data: " + e.getMessage());
        }
    }

    /**
     * Atomically writes the recurring transaction list to disk.
     *
     * @param list RecurringTransactionList, required — the list to save.
     * @throws MoneyBagProMaxException if the file cannot be written.
     */
    public void saveRecurring(RecurringTransactionList list) throws MoneyBagProMaxException {
        assert list != null : "RecurringTransactionList should not be null";
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                lines.add(serializeRecurringLine(list.get(i)));
            }
            Path target = Paths.get(REC_FILE);
            Path tmp    = Paths.get(REC_FILE + ".tmp");
            Files.write(tmp, lines);
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new MoneyBagProMaxException("Failed to save recurring data: " + e.getMessage());
        }
    }

    private Map<String, String> parseRecurringLine(String line) {
        assert line != null : "Line should not be null";
        Map<String, String> fields = new LinkedHashMap<>();
        String[] parts = line.split("\\s*\\|\\s*");
        for (int i = 1; i < parts.length; i++) {
            int eq = parts[i].indexOf(KV_SEP);
            if (eq < 0) {
                continue;
            }
            fields.put(parts[i].substring(0, eq).trim(),
                       parts[i].substring(eq + 1).trim());
        }
        return fields;
    }

    private String serializeRecurringLine(RecurringTransaction rt) {
        assert rt != null : "RecurringTransaction should not be null";
        String lastGenerated = rt.getLastGeneratedDate() == null ? "null"
                : rt.getLastGeneratedDate().toString();
        return REC_PREFIX + FIELD_SEP + String.join(FIELD_SEP,
                "category"          + KV_SEP + rt.getCategory(),
                "amount"            + KV_SEP + rt.getAmount(),
                "description"       + KV_SEP + rt.getDescription(),
                "frequency"         + KV_SEP + rt.getFrequency().name(),
                "startDate"         + KV_SEP + rt.getStartDate(),
                "lastGeneratedDate" + KV_SEP + lastGenerated
        );
    }

    /**
     * Thrown when a transaction entry in the storage file fails validation.
     */
    private static class CorruptedEntryException extends Exception {
        public CorruptedEntryException(String message) {
            super(message);
        }
    }
}
