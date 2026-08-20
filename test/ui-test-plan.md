# UI Test Plan

This file drives the `test-ui` skill. Each test case is run as an independent
program session: the compiled app is started fresh, the listed inputs are
typed in one after another (as if entered interactively, in order), and the
full console output is compared against the expected output.

## How to run the program under test

- Entry point: `MaggiGorengAyam` (default package)
- Sources: `src/main/java/*.java`
- Compile: `javac -d out src/main/java/*.java`
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

**Aim:** Verify `deadline <description> /by <when>` adds a `Deadline` task
with the description and the "by" field parsed and rendered correctly.

**Input:**
```
deadline return book /by Sunday
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
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC4: Add an event

**Aim:** Verify `event <description> /from <start> /to <end>` adds an
`Event` task with the description, "from" and "to" fields parsed and
rendered correctly.

**Input:**
```
event project meeting /from Mon 2pm /to 4pm
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
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
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
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
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
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: June 6th)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
