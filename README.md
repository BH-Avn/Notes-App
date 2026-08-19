# QuickCap

A place to dump thoughts instantly, sort them slowly, and never lose them.

## The problem

Most note apps ask questions before they let you write. Which notebook? What title? Which folder? Which tag?

Every question is a reason not to open the app. The thought was three seconds long and the app wants twenty. So the thought is lost.

Sorting is the useful part — but it belongs later, when you have time, not in the moment you're trying to get something out of your head.

## The idea

Split capture and organisation completely.

**Capture is instant.** One command, type, Enter, done. No folder, no title, no tags. Everything lands in one pile.

**Sorting is deliberate.** Later, when you have time, you work through the pile. Keep it, move it into an existing note, or throw it away.

That sorting step is the thinking. The app doesn't do it for you and shouldn't. Deciding a thought was worth keeping — and where it belongs — is the part that makes it yours.

## How it works

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

## Design rules

- **Plain text, plain folders.** A vault is `.md` files in directories. No database, no proprietary format. Readable in Notepad, works in Obsidian unchanged, survives this program being abandoned.
- **One thought, one file.** Removal is the operation that decides the design, not capture. Moving a file always succeeds or doesn't; rewriting a shared file can fail halfway. It also means content and structure never touch, so there's no delimiter to escape — your thought can contain anything.
- **Never lose a thought.** Write, verify, then remove. Never the reverse. A tool that eats one thought is never trusted again.
- **Speed over structure at capture time.** Every prompt the program asks is a reason not to use it. Titles are generated, not requested.

## Roadmap

- **v1** — capture
- **v2** — the query language
- **later** — separating events (what happened, dated, never edited) from conclusions (what you now believe, which can change, and which points back to the events that caused it), so a belief can always be traced to its cause

## Why it exists

Written to learn Java properly — file I/O, immutability, class design, and a recursive descent parser for the query language.

The only real test: am I still using it six weeks in, on a day I'm busy and irritated. If yes, it works. If no, nothing else matters.
