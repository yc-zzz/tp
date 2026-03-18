package seedu.duke.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Income extends Transaction {
    public Income(String category, double amount, String description, LocalDate date) {
        super(category, amount, description, date);
        assert getType().equals("income") : "Type should be income";
    }

    public Income(String category, double amount, String description) {
        super(category, amount, description);
        assert getType().equals("income") : "Type should be income";
    }

    public Income(String category, double amount) {
        super(category, amount);
        assert getType().equals("income") : "Type should be income";
    }

    @Override
    public String getType() {
        return "income";
    }

    @Override
    public String toString() {
        assert category != null && !category.isEmpty() : "Category should not be null or empty";
        assert amount > 0 : "Amount should be positive";
        assert description != null : "Description should not be null";
        assert date != null : "Date should not be null";
        
        String descriptionSuffix = description.isEmpty() ? "" : " \"" + description + "\"";
        return String.format("[Income] %s%s $%.2f (%s)", category, descriptionSuffix, amount, date);
    }
}
