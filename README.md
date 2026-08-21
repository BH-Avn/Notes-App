# QuickCap

A CLI tool for dumping thoughts instantly, sorting them slowly, and never losing them. Captured thoughts are stored as plain `.md` files — no database, no proprietary format.

## What it's for

QuickCap splits note-taking into two separate steps: **capture**, which is instant and asks nothing (no title, no folder, no tags), and **sort**, a deliberate later pass where you decide what each thought is worth and where it belongs. It's meant for the moment a thought needs to get out of your head *right now*, without a form to fill in first.

## Features

- [x] **Capture** — write a thought, it's saved as its own timestamped `.md` file in the inbox.
- [x] **Load** — existing thoughts are read back off disk into memory at startup, so the inbox survives restarts as real objects, not just files.
- [x] **List** — view every thought currently in the inbox, indexed and in chronological order.
- [ ] **Query language** — sort thoughts out of the inbox in batches by text match, index, or date range (`move`, `kill`, `list`, combined with `and` / `or` / `not`).
- [ ] **Trash** — killed thoughts are moved to `.trash`, not deleted.

## Project structure

```
code/
  base/
    Thought.java   a single captured thought — owns the file format in both directions
    Inbox.java     the unsorted pile — the only class that touches disk
    Main.java      the CLI menu — capture, list, exit
  helper/
    Utils.java     shared console I/O helpers (p, pl, inpint, inpdob)
vault/
  inbox/         where captured thoughts land (not tracked in git — it's user data)
```

## File format

A thought file is named for the moment it was captured, and contains nothing but the text:

```
vault/inbox/20260821-210925.md
```
```
the actual thought, verbatim
```

The timestamp lives in the **filename only** — it is the thought's identity. Nothing is stored inside the file except what the user typed, which means there is no header to strip, no front matter, and no delimiter to escape. A thought can begin with `##`, contain blank lines, or hold anything at all without confusing the reader.

`Thought` owns this format in both directions: it builds the filename and body when writing, and parses them back when loading. Nothing else in the codebase knows the timestamp pattern or the file extension, so the format can be changed in one place.

Because the filename format is fixed-width, sorting filenames alphabetically also sorts them chronologically — the listing gets its order for free.

**Known limitation:** the timestamp is precise to the second, so two thoughts captured within the same second would want the same filename. Files are written with `CREATE_NEW`, so this fails loudly instead of silently overwriting the earlier thought — but it is not yet handled gracefully. See the roadmap.

## Requirements

- JDK 17+

## Building & running

```
javac -d out code/base/*.java code/helper/*.java
java -cp out base.Main
```

This launches a menu-driven CLI: capture a thought, list everything in the inbox, or exit.

## How it will work

### Capture

Run it, choose "Enter a Thought", type, Enter. The thought is saved as its own file, timestamped, in the inbox. Back at the menu, capture again or list what's piled up.

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
- **Metadata goes in the filename, never in the file.** The file body is the user's text and nothing else. Anything the program needs to know about a thought is encoded in its name, so no thought can ever be mistaken for a header.
- **One owner per format.** Whatever writes a format also parses it, and the two live side by side. A writer and a reader in different classes are invisible to the compiler and drift apart silently.
- **Never lose a thought.** Write, verify, then remove. Never the reverse. Nothing overwrites an existing file.
- **Only one class touches disk.** `Inbox` performs all I/O. `Thought` decides what the bytes look like but never writes them.
- **Speed over structure at capture time.** Every prompt the program asks is a reason not to use it. Titles are generated, not requested.

## Roadmap

- **v1** — capture, load, list
- **next** — same-second filename collisions handled rather than merely refused; unreadable files reported instead of silently skipped; verify-after-write
- **v2** — the query language
- **later** — separating events (what happened, dated, never edited) from conclusions (what you now believe, which can change, and which points back to the events that caused it), so a belief can always be traced to its cause

## License

None yet — all rights reserved by default until one is added.
