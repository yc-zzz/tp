package seedu.duke.transaction;

import seedu.duke.MoneyBagProMaxException;

import java.time.LocalDate;

/**
 * Represents the frequency of a recurring transaction.
 */
public enum Frequency {
    DAILY, WEEKLY, MONTHLY;

    /**
     * Parses a string into a Frequency enum value (case-insensitive).
     *
     * @param s the string to parse
     * @return the corresponding Frequency
     * @throws MoneyBagProMaxException if the string does not match any frequency
     */
    public static Frequency fromString(String s) throws MoneyBagProMaxException {
        assert s != null : "Frequency string should not be null";
        String trimmed = s.trim();
        for (Frequency f : values()) {
            if (f.name().equalsIgnoreCase(trimmed)) {
                return f;
            }
        }
        throw new MoneyBagProMaxException(
                "Invalid frequency '" + s + "'. Valid frequencies: daily, weekly, monthly");
    }

    /**
     * Advances a date by one period of this frequency.
     *
     * @param date the date to advance
     * @return the next date after one period
     */
    public LocalDate next(LocalDate date) {
        assert date != null : "Date should not be null";
        switch (this) {
        case DAILY:
            return date.plusDays(1);
        case WEEKLY:
            return date.plusWeeks(1);
        case MONTHLY:
            return date.plusMonths(1);
        default:
            throw new IllegalStateException("Unknown frequency: " + this);
        }
    }
}
