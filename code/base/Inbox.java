package base;

import static helper.Utils.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents the inbox: the single unsorted pile of captured thoughts. Each
 * thought is written as its own timestamped .md file directly inside the inbox
 * directory.
 */
class Inbox
{
    /** The inbox directory on disk. */
    private final Path path;

    /**
     * Creates an Inbox rooted at the given directory. The directory is not required
     * to exist yet — it is created on demand.
     *
     * @param path the inbox directory
     */
    Inbox(Path path)
    {
        this.path = path;
    }

    /**
     * Captures a new thought: writes its text to its own timestamped .md file
     * inside the inbox, creating the inbox directory first if needed.
     *
     * @param text the content of the thought
     * @return the path of the file the thought was written to
     * @throws IOException if the inbox directory or file cannot be created/written
     */
    public Path add(String text) throws IOException
    {
        Files.createDirectories(path);

        Thought thought = new Thought(text);

        Path thoughtPath = path.resolve(thought.fileName());

        Files.writeString(thoughtPath, "## " + thought.heading() + "\n" + thought.text());

        return thoughtPath;
    }

    /**
     * Lists every thought currently sitting in the inbox, oldest first, each
     * prefixed with a 1-based index for use by later commands (e.g. move/kill by
     * index).
     *
     * @return the formatted contents of the inbox, or a message if it is empty
     * @throws IOException if the inbox directory cannot be read
     */
    public String list() throws IOException
    {
        StringBuilder s1 = new StringBuilder();

        Files.createDirectories(path);

        try (Stream<Path> entries = Files.list(path))
        {
            // Sorted since filenames are yyyyMMdd-HHmmss.md, so alphabetical order is
            // chronological order.
            List<Path> allFiles = entries.sorted().toList();

            int index = 0;
            for (Path p : allFiles)
            {
                if (p.getFileName().toString().endsWith(".md"))
                {
                    s1.append(++index + ":" + "\n").append(Files.readString(p)).append("\n----------------\n");
                }
            }
        }

        if (s1.isEmpty())
            return "";
        return s1.toString();
    }
}
