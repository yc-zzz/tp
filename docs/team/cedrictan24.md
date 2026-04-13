# Cedric Tan's Project Portfolio Page

## Project: MoneyBagProMax

MoneyBagProMax is a command-line personal finance management application that helps users track income and expenses, manage budgets, and gain insights into spending habits through financial statistics.

---

## Summary of Contributions

### New Feature: Budget Feature

What it does:  
Allows users to set a monthly budget and view budget status, including total expenses, remaining budget, and percentage used.

Justification:  
Helps users monitor spending and avoid exceeding their budget.

Highlights:  
Required filtering transactions by month and integrating budget calculations.

---

### New Feature: Statistics Feature

What it does:  
Displays financial statistics such as totals, highest/lowest transactions, most frequent category, averages, spending trend, and budget usage.

Justification:  
Provides insights into spending behaviour.

Highlights:  
Used HashMap for aggregation and implemented a month-based spending trend.

---

### Enhancements implemented

- Budget
  - Implemented budget set and budget status
  - Added tracking and persistence (`[BUDGET]`)
  - Added validation for invalid values

- Statistics
  - Implemented stats command
  - Designed month-based spending trend
  - Returned "Not enough data" when insufficient data

- Core Improvements
  - Improved delete handling for empty lists and invalid indices
  - Prevented runtime exceptions via better validation

---

### Code Contributed

[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=cedrictan24&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

---

### Contributions to UG

- Documented list, delete, budget, and stats commands with examples

---

### Contributions to DG

- Documented Budget and Statistics implementations
- Explained spending trend logic and design decisions
- Updated delete and list command documentation
- Created UML diagrams (sequence, class)

---

### Team Contributions

- Integrated features and ensured consistent validation and error handling

---

### Reviews

- Reviewed teammates’ PRs for correctness and code quality

---

### Beyond Project

- Reported bugs during PE-D with clear reproduction steps

---

### Tools

- Gradle, JUnit, Checkstyle, GitHub
