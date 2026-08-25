package base;

import static helper.Utils.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import static base.Thought.*;

/**
 * The unsorted pile: held in memory, mirrored on disk as one .md file per
 * thought.
 *
 * Only class that touches the filesystem. Thought decides the format but never
 * performs I/O. The pile is read into memory once at construction, so every
 * mutation must update both the list and the disk to keep them in step.
 */
class Inbox
{
    private final Path path;

    /**
     * Oldest first. A thought's position here is its display index — the index
     * is deliberately not stored on the Thought, so removals renumber free.
     */
    private ArrayList<Thought> thoughts = new ArrayList<>();

    Inbox(Path path)
    {
        this.path = path;
        safeLoad();
    }

    /**
     * A constructor cannot propagate a checked exception without forcing every
     * caller to handle it, and an inbox that failed to load is not usable —
     * continuing would show an empty pile and risk writing over thoughts that
     * were simply never read. So this exits instead.
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
     * Non-thought files are skipped, not treated as errors, so a stray file in
     * the folder cannot stop the program from starting.
     */
    private void load() throws IOException
    {
        Files.createDirectories(path);

        try (Stream<Path> entries = Files.list(path))
        {
            // Fixed-width filenames, so alphabetical order is chronological order.
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
     * Writes the thought to its own file, then adds it to the pile.
     *
     * CREATE_NEW means the filesystem, not a prior check, decides whether the
     * name is taken. A collision is expected, so it is handled here by bumping
     * the counter and retrying. Any other IO failure is not expected and is
     * left to escape.
     *
     * The list is updated only after the write succeeds, so a failed capture
     * cannot leave memory ahead of disk.
     */
    public Path add(String text) throws IOException
    {
        

        Thought thought = new Thought(text);
        Path thoughtPath;

        int counter = 1;

        while (true)
        {
            if(counter>99)
               throw new IllegalStateException("More than 99 entries in a second!");
            
            
            try
            {

                thoughtPath = path.resolve(thought.fileName(counter));

                String thoughtFileContent = thought.toFileContent();

                Files.writeString(thoughtPath, thoughtFileContent, StandardOpenOption.CREATE_NEW);

                thoughts.add(thought);

                

                return thoughtPath;
            }
            catch (FileAlreadyExistsException e)
            {
                counter++;

            }

            
        }

    }

    /**
     * Numbered from 1 for later commands (move/kill by index). Built from
     * memory, no disk access. The heading is generated at display time and is
     * not part of any file.
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
