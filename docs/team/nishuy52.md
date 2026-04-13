# Jason Chen's Project Portfolio Page

## Project: MoneyBagProMax

MoneyBagProMax is a command-line personal finance management application designed to help users track
income and expenses, manage budgets, and gain insights into spending habits through financial statistics.
The user interacts with it using a CLI, and the application is written in Java.

Given below are my contributions to the project.

---

## Summary of Contributions

### New Feature: Sort Command
**What it does:** Sorts and displays transactions by date (ascending), amount (descending), or
category (alphabetical) without altering the underlying list order.

**Justification:** Users accumulate transactions in insertion order, making it hard to spot the
largest expenses or review chronological history — sort gives an instant reordered view on demand.

**Highlights:** The sort operates on a defensive copy, so insertion-order indices used by `delete`
and `edit` are never affected, and `isMutating()` returns false so undo/redo history is untouched.

---

### New Feature: Undo/Redo
**What it does:** Reverses or reapplies the most recent mutating command (add, delete, edit);
multiple sequential undos and redos are supported.

**Justification:** Accidental deletions or edits are a common data-entry mistake; without undo the
only recovery is re-entering data manually, which is error-prone and frustrating.

**Highlights:** Uses a dual-stack design in `UndoRedoManager` — each mutating command pushes an
`ActionPair` (action type, transaction, index) onto the undo stack and clears the redo stack,
following the standard linear-history contract used by text editors.

---

### New Feature: Recurring Transactions
**What it does:** Lets users define recurring templates (e.g. monthly salary, weekly groceries)
that are resolved into real transactions when `generate-recurring` is run.

**Justification:** Many real-world entries repeat on a fixed schedule; without this, users must
re-enter identical transactions every period, which is tedious and error-prone.

**Highlights:** `RecurringTransaction` is deliberately *not* a subclass of `Transaction` — it is
a template with a frequency, start date, and last-generated watermark, keeping non-transactional
metadata out of the main list. Income/expense type is inferred automatically from category.

---

### New Feature: Transaction Date and Description Fields
**What it does:** Extended `Transaction` with an optional `LocalDate` date field (defaults to today)
and an optional free-text description field, parsed from `d/` and `desc/` tokens.

**Justification:** Without a date field it is impossible to backfill historical entries; the
description field lets users annotate transactions for easier recall.

**Highlights:** Both fields are optional so existing commands work unchanged. The date field is
the sort key in `SortCommand` and a filter criterion in `FilterCommand`, making it a foundational
addition that multiple features depend on.

---

### Enhancements Implemented
- Added `assert` statements to `Transaction`, `Income`, `Expense`, `UndoRedoManager`, and
  `RecurringTransaction` constructors and `execute()` methods to catch violated preconditions early.
- Added Javadoc to all authored classes and methods.
- Wrote JUnit tests for `SortCommand`, `UndoRedoManager` (add/delete/edit undo and redo),
  `RecurringTransaction` (construction, type inference, generation logic), and `Transaction`
  enhancements (date and description fields).

---

### Code Contributed
[Jason's RepoSense](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=Nishuy52&breakdown=true)

---

### Contributions to the User Guide
- Sort command
- Undo command
- Redo command
- Add recurring transaction command
- Delete recurring transaction command
- List recurring transactions command
- Generate recurring transactions command

---

### Contributions to the Developer Guide
- **Sort Transaction Feature**: architecture and flow, design considerations, `SortSequenceDiagram.png`, `SortClassDiagram.png`
- **Undo and Redo Feature**: architecture, implementation details, design considerations, `UndoSequenceDiagram.png`, `RedoSequenceDiagram.png`, `UndoRedoClassDiagram.png`
- **Recurring Transactions Feature**: full feature section covering all four commands, storage persistence (`[REC]` format), and design considerations
- **Transaction Class**: abstract class overview, field table, `protected final` immutability rationale

---

### Contributions to Team-Based Tasks
- Reviewed teammates' pull requests and left inline comments
  ([#84](https://github.com/AY2526S2-CS2113-T14-4/tp/pull/84),
  [#85](https://github.com/AY2526S2-CS2113-T14-4/tp/pull/85),
  [#88](https://github.com/AY2526S2-CS2113-T14-4/tp/pull/88)).
- Actively managed the team's GitHub Issues board and assigned issues to relevant team members.
- Fixed checkstyle issues and improved code quality across the codebase.

---

### Beyond-Team Contributions
- Reported 22 bugs during the Practical Exam (Dry Run) for another team's product.

---

## Contributions to the User Guide — Extract

The following is an extract from the User Guide for the Sort command, one of the sections I authored.

---

### Sorting Transactions: `sort`
Displays transactions sorted by the specified criterion. The underlying list order is **not changed** — this is a display-only operation.

**Format**: `sort by/CRITERIA`

**Valid criteria:**
- `date` — ascending (earliest first)
- `amount` — descending (largest first)
- `category` — alphabetical A–Z (case-insensitive)

**Examples**:
- `sort by/date` — shows all transactions from earliest to latest.
- `sort by/amount` — shows all transactions from highest to lowest amount.
- `sort by/category` — shows all transactions sorted alphabetically by category.

> [!NOTE]
> Sort does not change the indices used by `delete` and `edit`. Use `list` to see the original insertion order.

---

## Contributions to the Developer Guide — Extract

The following is an extract from the Developer Guide for the Undo and Redo feature, one of the sections I authored.

---

## Undo and Redo Feature

### Overview
The `undo` and `redo` commands allow users to reverse and reapply the last mutating operation
(add, delete, or edit). They provide a safety net against accidental changes. The feature uses
a dual-stack pattern: an undo stack records performed actions and a redo stack records undone
actions, enabling bidirectional navigation of the action history.

### Architecture and Flow
`UndoRedoManager` is instantiated once in `MoneyBagProMax` (the main class) and injected into
the `Parser`. When a mutating command (`AddCommand`, `DeleteCommand`, `EditCommand`) executes,
it calls the appropriate `record*()` method on `UndoRedoManager`, which pushes an `ActionPair`
onto the undo stack and clears the redo stack. When the user types `undo`, the `Parser` creates
an `UndoCommand` that holds a reference to the shared `UndoRedoManager`. During execution,
`UndoCommand` pops the top action from the undo stack, pushes it onto the redo stack, and applies
the inverse operation to `TransactionList`. `redo` works symmetrically.

### Sequence Diagram for Undo Command
![Undo Sequence Diagram](../diagrams/UndoSequenceDiagram.png)

### Sequence Diagram for Redo Command
![Redo Sequence Diagram](../diagrams/RedoSequenceDiagram.png)

### Implementation Details
- **Recording actions:** Each mutating command calls `recordAdd()`, `recordDelete()`, or
  `recordEdit()` on `UndoRedoManager` after modifying the list. Each method creates an
  `ActionPair` capturing the action type, the affected transaction(s), and the list index,
  then clears the redo stack to invalidate any future redo history.
- **ActionPair:** An inner static class of `UndoRedoManager` that stores:
  - `ActionType`: enum (`ADD`, `DELETE`, `EDIT`)
  - `transaction`: the transaction that was added/deleted, or the new version after an edit
  - `oldTransaction`: the previous version before an edit (null for ADD and DELETE)
  - `index`: the position in the list at the time the action was performed
- **Undo logic:** `UndoCommand.execute()` calls `getUndoAction()`, which pops from the undo
  stack and pushes onto the redo stack. The command then switches on the action type to perform
  the inverse operation:
  - `ADD` → `list.remove(index)`
  - `DELETE` → `list.insert(index, transaction)`
  - `EDIT` → `list.remove(index)` then `list.insert(index, oldTransaction)`
- **Redo logic:** `RedoCommand.execute()` calls `getRedoAction()`, which pops from the redo
  stack and pushes onto the undo stack. The command then reapplies the original action:
  - `ADD` → `list.insert(index, transaction)`
  - `DELETE` → `list.remove(index)`
  - `EDIT` → `list.remove(index)` then `list.insert(index, transaction)`
- **Mutating flag:** Both `UndoCommand` and `RedoCommand` override `isMutating()` to return
  `true`, which triggers auto-save to storage after execution.

### Class Diagram
![Undo Redo Class Diagram](../diagrams/UndoRedoClassDiagram.png)

### Design Considerations
- **Dual-stack delta vs. Memento pattern:** The Memento pattern stores a full `TransactionList`
  snapshot per action — O(n) memory. The dual-stack approach records only the delta (action type,
  one or two transaction objects, and an index), using O(1) memory per action, which is
  significantly more efficient for long sessions.
- **Clearing the redo stack on new action:** When a new mutating action follows one or more undoes,
  the redo stack is cleared. This enforces a linear history (the same contract used by text
  editors), avoiding the complexity and user confusion of branching redo trees.
