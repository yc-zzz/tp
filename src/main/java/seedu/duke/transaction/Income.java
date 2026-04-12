package seedu.duke.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Income extends Transaction {

    public static final List<String> VALID_CATEGORIES = List.of(
            "salary", "freelance", "investment", "business", "gift"
    );

    private static final Logger logger = Logger.getLogger(Income.class.getName());
    // sets the logger to only trigger logger levels WARNING and SEVERE to reduce clutter
    static {
        logger.setLevel(Level.WARNING);
    }

    public Income(String category, double amount, String description, LocalDate date) {
        super(category, amount, description, date);
        assert VALID_CATEGORIES.contains(category) : "Income category must be one of: " + VALID_CATEGORIES;
        logger.log(Level.INFO, "Created Income — category: {0}, amount: {1}, description: \"{2}\", date: {3}",
                new Object[]{category, amount, description, date});
    }

    @Override
    public String getType() {
        return "income";
    }

    @Override
    public String toString() {
        String descriptionSuffix = description.isEmpty() ? "" : " \"" + description + "\"";
        return String.format("[Income] %s%s $%.2f (%s)", category, descriptionSuffix, amount, date);
    }
}
