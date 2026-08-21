package base;

import static helper.Utils.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import static base.Thought.*;

/**
 * The inbox: the single unsorted pile of captured thoughts, held in memory and
 * mirrored on disk as one .md file per thought.
 *
 * <p>
 * This is the only class that touches the filesystem. Everything above it works
 * with {@link Thought} objects and never opens a file; {@code Thought} itself
 * decides the file format but never performs I/O.
 *
 * <p>
 * The full pile is read into memory once at construction and kept there.
 * {@link #list()} therefore renders from objects rather than re-reading the
 * directory, and every mutation must update both the list and the disk to keep
 * them in step.
 */
class Inbox
{
    /** The inbox directory on disk. Created on demand if missing. */
    private final Path path;

    /**
     * Every thought currently in the inbox, oldest first. A thought's position
     * here is its display index — the index is deliberately not stored on the
     * Thought itself, so removals renumber automatically.
     */
    private ArrayList<Thought> thoughts = new ArrayList<>();

    /**
     * Creates an Inbox rooted at the given directory and immediately loads
     * whatever is already there.
     *
     * @param path the inbox directory
     */
    Inbox(Path path)
    {
        this.path = path;
        safeLoad();
    }

    /**
     * Loads the inbox, treating failure as fatal.
     *
     * <p>
     * A constructor cannot propagate a checked exception without forcing every
     * caller to handle it, and an inbox that could not be read is not usable —
     * continuing would show the user an empty pile and risk writing over
     * thoughts that were simply never loaded. So this exits instead.
     */
    private void safeLoad()
    {
        try
        {
            load();
        }
        catch (IOException e)
        {
            pl("Error Loading the folder ! , this is a CRASH !!!!");
            System.exit(1);
        }
    }

    /**
     * Reads every thought file in the inbox directory into memory.
     *
     * <p>
     * Files that are not thoughts are skipped rather than treated as errors, so
     * a stray README or editor backup in the folder cannot stop the program
     * from starting.
     *
     * @throws IOException if the directory cannot be created or read
     */
    private void load() throws IOException
    {
        Files.createDirectories(path);

        try (Stream<Path> entries = Files.list(path))
        {
            // Filenames are yyyyMMdd-HHmmss.md, a fixed-width format, so sorting
            // them alphabetically also sorts them chronologically.
            List<Path> allFiles = entries.sorted().toList();

            for (Path p : allFiles)
            {
                String fileName = p.getFileName().toString();

                if (isThoughtFile(fileName))
                {

                    String content = Files.readString(p);

                    Thought thought = loadedThought(fileName, content);

                    thoughts.add(thought);

                }
            }
        }
    }

    /**
     * Captures a new thought: writes it to its own file and adds it to the
     * in-memory pile.
     *
     * <p>
     * The write uses CREATE_NEW so an existing file is never overwritten. If two
     * thoughts land in the same second their filenames collide, and this fails
     * loudly with an exception rather than silently destroying the earlier one.
     * The in-memory list is only updated after the write succeeds, so a failed
     * capture cannot leave a thought in memory that is not on disk.
     *
     * @param text the content of the thought
     * @return the path the thought was written to
     * @throws IOException if the file cannot be created or written
     */
    public Path add(String text) throws IOException
    {
        Files.createDirectories(path);

        Thought thought = new Thought(text);

        Path thoughtPath = path.resolve(thought.fileName());

        String thoughtFileContent = thought.toFileContent();

        Files.writeString(thoughtPath, thoughtFileContent, StandardOpenOption.CREATE_NEW);

        thoughts.add(thought);

        return thoughtPath;

    }

    /**
     * Renders the whole inbox for display, oldest first, each entry numbered
     * from 1 for use by later commands (move/kill by index).
     *
     * <p>
     * Built entirely from the in-memory list — no disk access. The heading shown
     * here is generated at display time and is not part of any file.
     *
     * @return the formatted listing, or an empty string if the inbox is empty
     */
    public String list()
    {
        StringBuilder s1 = new StringBuilder();

        int index = 0;
        for (Thought t : thoughts)
        {
            s1.append(++index + ":\n").append("# " + t.createdDateTime() + "\n").append(t.toFileContent() + "\n");
        }

        return s1.toString();
    }
}
