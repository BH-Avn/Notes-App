# QuickCap

A CLI tool for dumping thoughts instantly, sorting them slowly, and never losing them. Captured thoughts are stored as plain `.md` files — no database, no proprietary format.

## What it's for

QuickCap splits note-taking into two separate steps: **capture**, which is instant and asks nothing (no title, no folder, no tags), and **sort**, a deliberate later pass where you decide what each thought is worth and where it belongs. It's meant for the moment a thought needs to get out of your head *right now*, without a form to fill in first.

## Features

- [x] **Capture** — write a thought, it's saved as its own timestamped `.md` file in the inbox.
- [x] **List** — view every thought currently in the inbox, indexed and in chronological order.
- [ ] **Query language** — sort thoughts out of the inbox in batches by text match, index, or date range (`move`, `kill`, `list`, combined with `and` / `or` / `not`).
- [ ] **Trash** — killed thoughts are moved to `.trash`, not deleted.

## Project structure

```
code/
  Thought.java   a single captured thought — text, timestamp, and its formatted heading/filename
  Inbox.java     the unsorted pile — writes new thoughts, lists what's captured
vault/
  inbox/         where captured thoughts land (not tracked in git — it's user data)
```

## Requirements

- JDK 17+

## Building & running

There's no `main` entry point yet — `Thought` and `Inbox` are the underlying pieces, not a runnable CLI. Until that lands, compile and use them directly:

```
javac -d out code/*.java
```

## How it will work

### Capture

Run it, type, Enter. The thought is saved as its own file, timestamped, in the inbox. Keep typing to dump more. Empty line exits.

### Sort

A small query language moves thoughts out of the inbox in batches — by text match, by index, by date range, combined with `and` / `or` / `not`.

```
list                          show the pile
move "calc" -> study          matching thoughts into notes/study.md
move 3-9 -> dump               by index range
kill "asdf"                   to trash
move "calc" and not "doubt" -> study
```

Nothing is destroyed. Killed thoughts go to `.trash`.

## Design principles

- **Plain text, plain folders.** A vault is `.md` files in directories. No database, no proprietary format. Readable in Notepad, works in Obsidian unchanged, survives this program being abandoned.
- **One thought, one file.** Moving a file always succeeds or doesn't; rewriting a shared file can fail halfway. It also means content and structure never touch, so there's no delimiter to escape — a thought can contain anything.
- **Never lose a thought.** Write, verify, then remove. Never the reverse.
- **Speed over structure at capture time.** Every prompt the program asks is a reason not to use it. Titles are generated, not requested.

## Roadmap

- **v1** — capture
- **v2** — the query language
- **later** — separating events (what happened, dated, never edited) from conclusions (what you now believe, which can change, and which points back to the events that caused it), so a belief can always be traced to its cause

## License

None yet — all rights reserved by default until one is added.
