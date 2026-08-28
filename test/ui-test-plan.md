# UI Test Plan

This file drives the `test-ui` skill. Each test case is run as an independent
program session: the compiled app is started fresh, the listed inputs are
typed in one after another (as if entered interactively, in order), and the
full console output is compared against the expected output.

## How to run the program under test

- Entry point: `MaggiGorengAyam` (default package)
- Sources: `src/main/java/*.java`
- Compile: `javac -d out src/main/java/*.java`
- The program saves tasks to, and loads them from, `data/maggigorengayam.txt`
  (relative to the current working directory) so the task list survives
  between runs. **Before every test case** (unless the test case says
  otherwise), delete the `data/` directory if it exists, so the run starts
  from a genuinely empty task list, matching the "independent session"
  assumption below.
- Run: `java -cp out MaggiGorengAyam`, feeding the test case's `Input` lines
  to stdin in order (one command per line), then closing stdin.

## Comparison rules

- Compare the full captured stdout of the run against `Expected Output`,
  verbatim (exact text, including the separator lines and leading spaces on
  each printed line).
- Normalize line endings before comparing: treat `\r\n` and `\n` as
  equivalent (e.g. strip `\r`). `System.out.println` uses the platform line
  separator (`\r\n` on Windows), which is not a meaningful difference for
  this test plan.
- A trailing newline difference at the very end of the output does not count
  as a mismatch; everything else must match exactly.

---

## TC1: Greeting and exit

**Aim:** Verify the welcome banner is printed on startup and `bye` prints the
farewell message and ends the session.

**Input:**
```
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC2: Add a todo

**Aim:** Verify `todo <description>` adds a `ToDo` task and confirms it with
the "Got it" message, including the running task count.

**Input:**
```
todo borrow book
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC3: Add a deadline

**Aim:** Verify `deadline <description> /by <date>` adds a `Deadline` task,
that the date is understood as an actual date (parsed as a `LocalDate`)
rather than stored as a raw string, and rendered in the "MMM d yyyy"
display format (e.g. an input of `2019-12-02` displays as `Dec 2 2019`) -
note the day is not zero-padded, matching `2` rather than `02`.

**Input:**
```
deadline return book /by 2019-12-02
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 2 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC4: Add an event

**Aim:** Verify `event <description> /from <date> /to <date>` adds an
`Event` task with "from"/"to" understood as actual dates/times (each
optionally carrying a 24-hour `HHmm` time after the date), rendered in
"MMM d yyyy[, h[:mm]a]" display format - e.g. `2019-12-02 1400` displays
as `Dec 2 2019, 2pm` (no ":00" clutter when minutes are zero).

**Input:**
```
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC5: List multiple task types

**Aim:** Verify `list` prints every task, in insertion order, each prefixed
with the correct type icon (`[T]`/`[D]`/`[E]`) and status icon.

**Input:**
```
todo read book
deadline return book /by 2019-12-06
event project meeting /from 2019-12-06 1400 /to 2019-12-06 1600
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 6 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 6 2019, 2pm to: Dec 6 2019, 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 6 2019)
 3.[E][ ] project meeting (from: Dec 6 2019, 2pm to: Dec 6 2019, 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC6: Mark and unmark tasks

**Aim:** Verify `mark <n>` and `unmark <n>` toggle the done status of the
n-th task (1-indexed) and that `list` reflects the change afterwards.

**Input:**
```
todo read book
todo return book
mark 1
list
unmark 1
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[T][ ] return book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC7: Empty description errors

**Aim:** Verify `todo`, `deadline`, and `event` each reject a missing
description with a specific "OOPS!!!" message (via `MaggiGorengAyamException`)
instead of adding a blank task.

**Input:**
```
todo
deadline
event
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! What TODO you want bro, I'll give you maggi goreng ayam
____________________________________________________________
____________________________________________________________
 OOPS!!! Woah I don't know how to read mind bro, please type in ur description and deadline
____________________________________________________________
____________________________________________________________
 OOPS!!! Huhhhh, sry i got no telepathy feature...
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC8: Unknown command

**Aim:** Verify a command that isn't recognized (e.g. `blah`) produces the
"Huhhh???" OOPS message instead of crashing or being silently ignored.

**Input:**
```
blah
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Huhhh???
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC9: Deadline with missing or empty `/by`

**Aim:** Verify `deadline` rejects input that has no `/by` marker at all,
and input where `/by` is present but has no date/time after it (both are
reported via the "put the deadline using '/by'" message). Note: because the
whole argument string is trimmed before the `/by` check, a trailing
`/by` with nothing after it never leaves a lone trailing space for the
`" /by "` match, so it also falls into the "no marker" case rather than a
separate "empty by" case — this test documents that actual behavior.

**Input:**
```
deadline return book
deadline return book /by
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Yo, put the deadline using '/by', e.g. 'deadline return book /by Sunday'. Dont make me put the deadline next min.
____________________________________________________________
____________________________________________________________
 OOPS!!! Yo, put the deadline using '/by', e.g. 'deadline return book /by Sunday'. Dont make me put the deadline next min.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC10: Event with missing `/from` or `/to`

**Aim:** Verify `event` rejects input missing the `/from` marker, and
separately, input that has `/from` but is missing the `/to` marker.

**Input:**
```
event meeting
event meeting /from Mon
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please use '/from', e.g. 'event project meeting /from Mon 2pm /to 4pm'.
____________________________________________________________
____________________________________________________________
 OOPS!!! Till when? forever? Please use '/to', e.g. 'event project meeting /from Mon 2pm /to 4pm'.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC11: Invalid `mark`/`unmark` task numbers

**Aim:** Verify `mark`/`unmark` reject a missing task number, a
non-numeric task number, and a task number that is out of range (including
`0`), each with a specific OOPS message, rather than throwing an unhandled
exception.

**Input:**
```
mark
mark abc
mark 5
unmark 0
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please specify which task to mark, e.g. 'mark 2'.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number, e.g. 'mark 2'.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 5 does not exist. You have 0 task(s) in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 0 does not exist. You have 0 task(s) in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC12: State integrity after invalid `todo`/`deadline` inputs

**Aim:** Verify that a run of invalid `todo`/`deadline` commands (interleaved
after one successful add) each report their own specific error and do not
add a task, corrupt the task count, or otherwise disturb the tasks already
in the list.

**Input:**
```
todo buy milk
todo
deadline
deadline buy bread
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! What TODO you want bro, I'll give you maggi goreng ayam
____________________________________________________________
____________________________________________________________
 OOPS!!! Woah I don't know how to read mind bro, please type in ur description and deadline
____________________________________________________________
____________________________________________________________
 OOPS!!! Yo, put the deadline using '/by', e.g. 'deadline return book /by Sunday'. Dont make me put the deadline next min.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC13: `mark`/`unmark` state integrity under invalid task numbers

**Aim:** Verify that out-of-range and non-numeric `mark`/`unmark` attempts
(interleaved with valid ones) are each rejected with the correct message,
never change any task's done status, and that valid `mark`/`unmark` calls
in between still work correctly against the untouched list.

**Input:**
```
todo task A
todo task B
mark 5
mark abc
mark 0
list
mark 2
list
unmark 2
unmark 5
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task A
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task B
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 5 does not exist. You have 2 task(s) in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number, e.g. 'mark 2'.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 0 does not exist. You have 2 task(s) in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] task A
 2.[T][ ] task B
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] task B
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] task A
 2.[T][X] task B
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] task B
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 5 does not exist. You have 2 task(s) in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] task A
 2.[T][ ] task B
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC14: Command case-sensitivity and prefix matching

**Aim:** Verify that command matching is case-sensitive (`Todo`, `TODO` are
not recognized as `todo`) and that a mistyped command sharing `todo` as a
prefix but without the required trailing space (`todox`) is treated as
unknown rather than accidentally matching. Confirms these rejected inputs
add nothing, so the list still contains only the one task added by the
correctly-typed command that follows.

**Input:**
```
Todo read book
TODO read book
todox read book
todo read book
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Huhhh???
____________________________________________________________
____________________________________________________________
 OOPS!!! Huhhh???
____________________________________________________________
____________________________________________________________
 OOPS!!! Huhhh???
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC15: Whitespace-tolerant parsing (extra internal/trailing spaces)

**Aim:** Verify that extra spaces around `/by`, `/from`, `/to`, around the
description itself, and around/within the date-time value (between the
date and the time), are trimmed away cleanly, while spaces that are
genuinely part of a `todo` description (no delimiter to trim around) are
preserved as-is.

**Input:**
```
todo   buy   milk
deadline   return book   /by   2019-12-02   1800
event   meet friend   /from   2019-12-02   1400   /to   2019-12-02   1600
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy   milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 2 2019, 6pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meet friend (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy   milk
 2.[D][ ] return book (by: Dec 2 2019, 6pm)
 3.[E][ ] meet friend (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC16: Numeric edge cases for `mark`/`unmark` task numbers

**Aim:** Verify `Integer.parseInt` edge cases for the task-number argument:
a leading `+` sign is accepted (`+2` parses as `2`), a negative number
parses successfully but is then rejected as out-of-range (not a "whole
number" error), and non-integer-looking input (`1.5`, `3abc`, `2 3`) is
rejected with the "whole number" message. Interleaved with valid
`mark`/`unmark` calls to confirm the invalid attempts never change task
done-status.

**Input:**
```
todo t1
todo t2
todo t3
mark +2
list
mark -1
mark 1.5
mark 3abc
mark 2 3
unmark +2
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t1
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t2
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t3
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] t2
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] t1
 2.[T][X] t2
 3.[T][ ] t3
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number -1 does not exist. You have 3 task(s) in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number, e.g. 'mark 2'.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number, e.g. 'mark 2'.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number, e.g. 'mark 2'.
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] t2
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] t1
 2.[T][ ] t2
 3.[T][ ] t3
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC17: Task list grows past the old fixed-size capacity

**Aim:** Tasks are now stored in an `ArrayList<Task>` instead of a
fixed-size array, so there is no longer a hard cap on the number of
tasks. Verify that adding 105 tasks (5 more than the old `MAX_TASKS`
of 100) all succeed with no "task list is full" error, and that
`list` afterward shows all 105 tasks, correctly numbered.

**Input:**
```
todo task1
todo task2
todo task3
todo task4
todo task5
todo task6
todo task7
todo task8
todo task9
todo task10
todo task11
todo task12
todo task13
todo task14
todo task15
todo task16
todo task17
todo task18
todo task19
todo task20
todo task21
todo task22
todo task23
todo task24
todo task25
todo task26
todo task27
todo task28
todo task29
todo task30
todo task31
todo task32
todo task33
todo task34
todo task35
todo task36
todo task37
todo task38
todo task39
todo task40
todo task41
todo task42
todo task43
todo task44
todo task45
todo task46
todo task47
todo task48
todo task49
todo task50
todo task51
todo task52
todo task53
todo task54
todo task55
todo task56
todo task57
todo task58
todo task59
todo task60
todo task61
todo task62
todo task63
todo task64
todo task65
todo task66
todo task67
todo task68
todo task69
todo task70
todo task71
todo task72
todo task73
todo task74
todo task75
todo task76
todo task77
todo task78
todo task79
todo task80
todo task81
todo task82
todo task83
todo task84
todo task85
todo task86
todo task87
todo task88
todo task89
todo task90
todo task91
todo task92
todo task93
todo task94
todo task95
todo task96
todo task97
todo task98
todo task99
todo task100
todo task101
todo task102
todo task103
todo task104
todo task105
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task1
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task2
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task3
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task4
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task5
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task6
 Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task7
 Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task8
 Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task9
 Now you have 9 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task10
 Now you have 10 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task11
 Now you have 11 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task12
 Now you have 12 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task13
 Now you have 13 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task14
 Now you have 14 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task15
 Now you have 15 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task16
 Now you have 16 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task17
 Now you have 17 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task18
 Now you have 18 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task19
 Now you have 19 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task20
 Now you have 20 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task21
 Now you have 21 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task22
 Now you have 22 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task23
 Now you have 23 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task24
 Now you have 24 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task25
 Now you have 25 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task26
 Now you have 26 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task27
 Now you have 27 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task28
 Now you have 28 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task29
 Now you have 29 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task30
 Now you have 30 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task31
 Now you have 31 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task32
 Now you have 32 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task33
 Now you have 33 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task34
 Now you have 34 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task35
 Now you have 35 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task36
 Now you have 36 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task37
 Now you have 37 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task38
 Now you have 38 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task39
 Now you have 39 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task40
 Now you have 40 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task41
 Now you have 41 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task42
 Now you have 42 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task43
 Now you have 43 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task44
 Now you have 44 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task45
 Now you have 45 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task46
 Now you have 46 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task47
 Now you have 47 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task48
 Now you have 48 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task49
 Now you have 49 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task50
 Now you have 50 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task51
 Now you have 51 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task52
 Now you have 52 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task53
 Now you have 53 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task54
 Now you have 54 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task55
 Now you have 55 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task56
 Now you have 56 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task57
 Now you have 57 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task58
 Now you have 58 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task59
 Now you have 59 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task60
 Now you have 60 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task61
 Now you have 61 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task62
 Now you have 62 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task63
 Now you have 63 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task64
 Now you have 64 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task65
 Now you have 65 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task66
 Now you have 66 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task67
 Now you have 67 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task68
 Now you have 68 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task69
 Now you have 69 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task70
 Now you have 70 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task71
 Now you have 71 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task72
 Now you have 72 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task73
 Now you have 73 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task74
 Now you have 74 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task75
 Now you have 75 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task76
 Now you have 76 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task77
 Now you have 77 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task78
 Now you have 78 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task79
 Now you have 79 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task80
 Now you have 80 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task81
 Now you have 81 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task82
 Now you have 82 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task83
 Now you have 83 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task84
 Now you have 84 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task85
 Now you have 85 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task86
 Now you have 86 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task87
 Now you have 87 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task88
 Now you have 88 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task89
 Now you have 89 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task90
 Now you have 90 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task91
 Now you have 91 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task92
 Now you have 92 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task93
 Now you have 93 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task94
 Now you have 94 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task95
 Now you have 95 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task96
 Now you have 96 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task97
 Now you have 97 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task98
 Now you have 98 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task99
 Now you have 99 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task100
 Now you have 100 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task101
 Now you have 101 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task102
 Now you have 102 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task103
 Now you have 103 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task104
 Now you have 104 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task105
 Now you have 105 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] task1
 2.[T][ ] task2
 3.[T][ ] task3
 4.[T][ ] task4
 5.[T][ ] task5
 6.[T][ ] task6
 7.[T][ ] task7
 8.[T][ ] task8
 9.[T][ ] task9
 10.[T][ ] task10
 11.[T][ ] task11
 12.[T][ ] task12
 13.[T][ ] task13
 14.[T][ ] task14
 15.[T][ ] task15
 16.[T][ ] task16
 17.[T][ ] task17
 18.[T][ ] task18
 19.[T][ ] task19
 20.[T][ ] task20
 21.[T][ ] task21
 22.[T][ ] task22
 23.[T][ ] task23
 24.[T][ ] task24
 25.[T][ ] task25
 26.[T][ ] task26
 27.[T][ ] task27
 28.[T][ ] task28
 29.[T][ ] task29
 30.[T][ ] task30
 31.[T][ ] task31
 32.[T][ ] task32
 33.[T][ ] task33
 34.[T][ ] task34
 35.[T][ ] task35
 36.[T][ ] task36
 37.[T][ ] task37
 38.[T][ ] task38
 39.[T][ ] task39
 40.[T][ ] task40
 41.[T][ ] task41
 42.[T][ ] task42
 43.[T][ ] task43
 44.[T][ ] task44
 45.[T][ ] task45
 46.[T][ ] task46
 47.[T][ ] task47
 48.[T][ ] task48
 49.[T][ ] task49
 50.[T][ ] task50
 51.[T][ ] task51
 52.[T][ ] task52
 53.[T][ ] task53
 54.[T][ ] task54
 55.[T][ ] task55
 56.[T][ ] task56
 57.[T][ ] task57
 58.[T][ ] task58
 59.[T][ ] task59
 60.[T][ ] task60
 61.[T][ ] task61
 62.[T][ ] task62
 63.[T][ ] task63
 64.[T][ ] task64
 65.[T][ ] task65
 66.[T][ ] task66
 67.[T][ ] task67
 68.[T][ ] task68
 69.[T][ ] task69
 70.[T][ ] task70
 71.[T][ ] task71
 72.[T][ ] task72
 73.[T][ ] task73
 74.[T][ ] task74
 75.[T][ ] task75
 76.[T][ ] task76
 77.[T][ ] task77
 78.[T][ ] task78
 79.[T][ ] task79
 80.[T][ ] task80
 81.[T][ ] task81
 82.[T][ ] task82
 83.[T][ ] task83
 84.[T][ ] task84
 85.[T][ ] task85
 86.[T][ ] task86
 87.[T][ ] task87
 88.[T][ ] task88
 89.[T][ ] task89
 90.[T][ ] task90
 91.[T][ ] task91
 92.[T][ ] task92
 93.[T][ ] task93
 94.[T][ ] task94
 95.[T][ ] task95
 96.[T][ ] task96
 97.[T][ ] task97
 98.[T][ ] task98
 99.[T][ ] task99
 100.[T][ ] task100
 101.[T][ ] task101
 102.[T][ ] task102
 103.[T][ ] task103
 104.[T][ ] task104
 105.[T][ ] task105
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC18: Empty description and missing date/time across ToDo, Deadline, Event

**Aim:** Verify error handling for missing/empty fields on all three task
types, interleaved with a valid add of each type so a failed add can be
shown not to corrupt the list. Covers: empty description for `todo`,
`deadline`, and `event`; and missing date/time for `deadline` (`/by`) and
`event` (`/from`, `/to`). Note: an empty-but-present date/time (e.g. a
trailing `/by` with nothing after it) is reported via the same "marker
missing" message as a wholly absent marker, per the parsing behavior
documented in TC9/TC10 — there is no separate reachable "empty date" error
distinct from "missing marker". Final `list` confirms exactly the 3
successfully-added tasks are present, correctly numbered, with none of the
6 failed attempts having left a trace.

**Input:**
```
todo read book
todo
deadline return book /by 2019-12-09
deadline
deadline return book
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
event
event meeting
event meeting /from Mon
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! What TODO you want bro, I'll give you maggi goreng ayam
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 9 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Woah I don't know how to read mind bro, please type in ur description and deadline
____________________________________________________________
____________________________________________________________
 OOPS!!! Yo, put the deadline using '/by', e.g. 'deadline return book /by Sunday'. Dont make me put the deadline next min.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Huhhhh, sry i got no telepathy feature...
____________________________________________________________
____________________________________________________________
 OOPS!!! Please use '/from', e.g. 'event project meeting /from Mon 2pm /to 4pm'.
____________________________________________________________
____________________________________________________________
 OOPS!!! Till when? forever? Please use '/to', e.g. 'event project meeting /from Mon 2pm /to 4pm'.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 9 2019)
 3.[E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC19: Delete a task

**Aim:** Verify `delete <n>` removes the n-th task (1-indexed) from the
list, prints the "Noted. I've removed this task" confirmation showing
the removed task and the updated count, and that the task no longer
appears afterward.

**Input:**
```
todo read book
deadline return book /by 2019-12-06
event project meeting /from 2019-12-06 1400 /to 2019-12-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 6 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 6 2019, 2pm to: Dec 6 2019, 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] join sports club
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Dec 6 2019)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] join sports club
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Dec 6 2019)
 3.[E][ ] project meeting (from: Dec 6 2019, 2pm to: Dec 6 2019, 4pm)
 4.[T][X] join sports club
 5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Dec 6 2019, 2pm to: Dec 6 2019, 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC20: `delete` error handling, re-indexing, and emptying the list

**Aim:** Verify `delete` rejects a missing task number, a non-numeric
task number, and an out-of-range number (`0`) the same way `mark`/
`unmark` do, without changing the list; that deleting a task shifts
later tasks' numbers down by one (deleting task 2 makes the old task 3
the new task 2) rather than leaving a gap; and that repeatedly deleting
down to zero tasks works, including `list` on an empty list (just the
header/footer lines, no task rows) and correctly reporting "You have 0
task(s) in the list" if `delete` is attempted again.

**Input:**
```
todo t1
todo t2
todo t3
todo t4
delete 5
delete abc
delete 0
list
delete 2
list
delete 1
delete 1
list
delete 1
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t1
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t2
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t3
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] t4
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 5 does not exist. You have 4 task(s) in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number, e.g. 'delete 2'.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task number 0 does not exist. You have 4 task(s) in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] t1
 2.[T][ ] t2
 3.[T][ ] t3
 4.[T][ ] t4
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] t2
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] t1
 2.[T][ ] t3
 3.[T][ ] t4
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] t1
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] t3
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] t4
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] t4
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC21: Task list persists across separate program runs

**Aim:** Verify that tasks (including done/not-done status and, since a
`Deadline`'s date is now a `LocalDate`/`LocalTime` rather than a raw
string, its date-with-time value) are saved to `data/maggigorengayam.txt`
as they are added/marked, and are correctly loaded back — reformatted
correctly, not just echoed as text — when the program is started again in
a fresh process, without the user re-entering anything.

**Special setup for this test case only:** delete the `data/` directory
before **Run 1**, but do **not** delete it between Run 1 and Run 2 — Run 2
must see the file Run 1 left behind. This is the one test case in this plan
where state intentionally carries over between runs.

**Run 1 — Input:**
```
todo read book
deadline return book /by 2019-12-06 1800
mark 1
bye
```

**Run 1 — Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 6 2019, 6pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

**Run 2 — Input (fresh process, same `data/` directory from Run 1):**
```
list
bye
```

**Run 2 — Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 6 2019, 6pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC22: Corrupted data file lines are skipped on load, not crashed on

**Aim:** Verify that if `data/maggigorengayam.txt` contains lines that
cannot be parsed (wrong format, unknown type letter, missing fields —
e.g. from manual editing or a bug in some other tool), the program skips
just those lines, loads whatever valid tasks remain instead of crashing
or refusing to start, and — since silently losing saved tasks would
otherwise go completely unnoticed — reports at startup how many lines
were skipped.

**Special setup for this test case only:** delete the `data/` directory,
then create `data/maggigorengayam.txt` by hand with exactly this content
(a mix of one valid line and several invalid ones) before running the
program:
```
T | 1 | good task
GARBAGE LINE
X | 0 | bad type
D | 0 | incomplete
```

**Input:**
```
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! 3 saved task(s) in the data file could not be read and were skipped.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] good task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC23: Blank input lines are ignored; whole-line whitespace is trimmed

**Aim:** Verify that a blank line (empty, or whitespace-only) typed at the
prompt produces no output and does not disturb the task list, and that
leading/trailing whitespace around an otherwise-valid command line (not
just around the description, which TC15 already covers) does not cause
the command to be misread as unrecognized.

**Input:**
```

   
  todo buy milk  
  list  
   bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC24: `|` character rejected in task descriptions (save-format delimiter clash)

**Aim:** Tasks are saved to disk as `|`-delimited lines (e.g.
`D | 0 | return book | 2019-12-02`), so a description that itself contains
`" | "` would be split into the wrong number of parts on the next load and
silently dropped (see TC22's skipped-line warning). Verify that `todo`,
`deadline`, and `event` each reject a `|` character in the description
with a clear OOPS message, and add nothing to the list, rather than
allowing data that would later be silently lost. (The `/by`, `/from`,
`/to` date fields don't need this check anymore, now that they're parsed
as actual dates: any text containing `|` already fails as "not a valid
date" - see TC28.)

**Input:**
```
todo buy a | b
deadline return book | urgent /by 2019-12-02
event meet friend | urgent /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Sorry, the '|' character can't be used in a task description because it's used internally to save your tasks. Please remove it and try again.
____________________________________________________________
____________________________________________________________
 OOPS!!! Sorry, the '|' character can't be used in a task description because it's used internally to save your tasks. Please remove it and try again.
____________________________________________________________
____________________________________________________________
 OOPS!!! Sorry, the '|' character can't be used in a task description because it's used internally to save your tasks. Please remove it and try again.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC25: Load I/O failure falls back to an empty list instead of crashing

**Aim:** Verify that if the data file cannot be read for a reason other
than "it doesn't exist yet" (e.g. `data/maggigorengayam.txt` is actually a
directory, simulating a permissions error or filesystem oddity), the
program reports a graceful OOPS warning at startup and continues with an
empty task list rather than crashing before the prompt loop even starts.

**Special setup for this test case only:** delete the `data/` directory,
then create `data/maggigorengayam.txt` **as a directory** (not a file)
before running the program, e.g. `mkdir -p data/maggigorengayam.txt`.

**Input:**
```
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! I couldn't load saved tasks from disk. Starting with an empty list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC26: Save I/O failure keeps the task in memory instead of crashing

**Aim:** Verify that if a task-list change cannot be saved to disk (e.g.
`data/maggigorengayam.txt`'s directory `data/` cannot be created because a
plain file of that name already exists in its place), the program reports
a graceful OOPS warning instead of the normal "Got it"/"Noted"/etc.
confirmation, and — critically — does not crash: the task is still applied
to the in-memory list (confirmed here via `list`), it's only the on-disk
copy that lags behind until a save succeeds.

**Special setup for this test case only:** delete any existing `data/`
directory, then create a plain **file** (not a directory) named `data` in
the project root before running the program, so `data/maggigorengayam.txt`
can never be created. Delete that `data` file again after this test case,
since it would otherwise block every later test case's save from working.

**Input:**
```
todo new task
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! I couldn't save the task list to disk. Your change is only in memory for now.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] new task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC27: Deadline/Event dates accept an optional 24-hour time

**Aim:** Verify that `/by`, `/from`, and `/to` each accept either a
date-only value (`yyyy-MM-dd`) or a date followed by a single space and a
4-digit 24-hour time (`yyyy-MM-dd HHmm`), and that both are understood as
actual dates/times (not raw strings) and displayed in "MMM d yyyy" format,
with ", h[:mm]a" appended when a time was given - e.g. `1800` displays as
`6pm` (no ":00" clutter), while `0930` displays as `9:30am` (minutes kept
when non-zero).

**Input:**
```
deadline return book /by 2019-12-02
deadline submit report /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 2 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Dec 2 2019, 6pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 2 2019)
 2.[D][ ] submit report (by: Dec 2 2019, 6pm)
 3.[E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC28: Invalid date/time values are rejected with a clear error

**Aim:** Verify that `deadline`'s `/by` and `event`'s `/from`/`/to` reject
text that doesn't parse as `yyyy-MM-dd[ HHmm]`, rather than silently
accepting it as a free-form string the way the pre-dates-and-times version
of this program did. Covers: a non-date word (`Sunday`), a date with
non-zero-padded month/day (`2019-2-3` - the parser requires exactly 2
digits for month and day), and a time value with the wrong digit count
(`999`). None of these should add anything to the list.

**Input:**
```
deadline return book /by Sunday
deadline return book /by 2019-2-3
deadline return book /by 2019-12-02 999
event meet friend /from Mon 2pm /to 2019-12-02 1600
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't understand 'Sunday' as a deadline date. Please use yyyy-MM-dd, optionally followed by a 24-hour time, e.g. '2019-12-02' or '2019-12-02 1800'.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't understand '2019-2-3' as a deadline date. Please use yyyy-MM-dd, optionally followed by a 24-hour time, e.g. '2019-12-02' or '2019-12-02 1800'.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't understand '2019-12-02 999' as a deadline date. Please use yyyy-MM-dd, optionally followed by a 24-hour time, e.g. '2019-12-02' or '2019-12-02 1800'.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't understand 'Mon 2pm' as a start date/time. Please use yyyy-MM-dd, optionally followed by a 24-hour time, e.g. '2019-12-02' or '2019-12-02 1800'.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC29: `on <date>` finds deadlines/events occurring on a given date

**Aim:** Stretch-goal command: verify `on <yyyy-MM-dd>` lists only the
deadlines whose date matches, and events whose `[from, to]` date range
covers the given date (inclusive on both ends, so a multi-day event shows
up for every date it spans, not just its start date) - numbered `1.`, `2.`
... within the filtered results, not by their position in the full list.
`todo`s never match (they have no date). Also verifies: a date with no
matching tasks prints an empty list (not an error), and a missing/invalid
date argument is rejected the same way other commands validate their
arguments.

**Input:**
```
todo buy milk
deadline return book /by 2019-12-02
event conference /from 2019-12-01 /to 2019-12-03
deadline submit report /by 2019-12-05
on 2019-12-02
on 2019-12-05
on 2019-12-10
on
on abc
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 2 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] conference (from: Dec 1 2019 to: Dec 3 2019)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Dec 5 2019)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks on Dec 2 2019:
 1.[D][ ] return book (by: Dec 2 2019)
 2.[E][ ] conference (from: Dec 1 2019 to: Dec 3 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks on Dec 5 2019:
 1.[D][ ] submit report (by: Dec 5 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks on Dec 10 2019:
____________________________________________________________
____________________________________________________________
 OOPS!!! Which date? e.g. 'on 2019-12-02'.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't understand 'abc' as a date. Please use yyyy-MM-dd, e.g. 'on 2019-12-02'.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC30: A corrupted date value in the save file is skipped, not crashed on

**Aim:** Extends TC22's "corrupted line" handling to specifically cover a
line that is otherwise well-formed (right type letter, right number of
`|`-separated fields) but whose date field isn't a valid date - e.g. hand
edited into an invalid state. Verify it's counted and skipped exactly like
any other corrupted line, rather than crashing the whole load.

**Special setup for this test case only:** delete the `data/` directory,
then create `data/maggigorengayam.txt` by hand with exactly this content:
```
T | 1 | good task
D | 0 | bad deadline | not-a-date
E | 0 | bad event | 2019-12-01 | not-a-date
```

**Input:**
```
list
bye
```

**Expected Output:**
```
____________________________________________________________
  __  __  _____    _    
 |  \/  |/ ____|  / \   
 | \  / ||   __  / _ \  
 | |\/| ||  |_ |/ ___ \ 
 |_|  |_|\_____/_/   \_\
Hello! I'm Maggi Goreng Ayam.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! 2 saved task(s) in the data file could not be read and were skipped.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] good task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
