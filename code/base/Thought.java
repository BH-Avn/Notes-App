package base;

import static helper.Utils.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A single captured thought: its text and the moment it was created.
 *
 * <p>
 * This class owns the on-disk representation of a thought in <em>both</em>
 * directions — it decides what a thought file is named ({@link #fileName()}),
 * what it contains ({@link #toFileContent()}), and how to turn one back into an
 * object ({@link #loadedThought(String, String)}). Nothing outside this class
 * knows the timestamp pattern or the file extension, so the format can be
 * changed here alone.
 *
 * <p>
 * The creation timestamp is stored <em>only</em> in the filename, never in the
 * file body. That keeps the file contents pure user text with no header to
 * strip and no delimiter to escape — a thought can start with "##" or contain
 * anything at all without confusing the parser.
 */
class Thought
{

    /** The text content of the thought, exactly as the user typed it. */
    private final String text;

    /** The moment this thought was captured. */
    private final LocalDateTime created;

    /** Display-only format, e.g. "2026-08-19 14:30". Never written to disk. */
    private static final DateTimeFormatter formatHeading = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Filename format, e.g. "20260819-143000". Fixed-width and lexically
     * ordered, so sorting filenames as text also sorts them by time.
     */
    private static final DateTimeFormatter formatFileName = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /**
     * Creates a brand-new thought, stamped at the current moment.
     *
     * @param text the content of the thought
     */
    Thought(String text)
    {
        this.text = text;
        created = LocalDateTime.now();
    }

    /**
     * Rebuilds a thought that already exists, using its original timestamp
     * rather than the current time. Used when loading from disk.
     *
     * @param text            the content of the thought
     * @param localDateTime   the moment the thought was originally captured
     */
    Thought(String text, LocalDateTime localDateTime)
    {
        this.text = text;
        created = localDateTime;
    }

    /**
     * Reconstructs a thought from what is on disk. This is the exact inverse of
     * {@link #fileName()} and {@link #toFileContent()} — the timestamp is
     * recovered from the filename, the text is the file body verbatim.
     *
     * <p>
     * Callers must check {@link #isThoughtFile(String)} first; passing a name
     * that is not a valid thought filename throws.
     *
     * @param fileName the file's name, including the ".md" extension
     * @param content  the file's entire contents
     * @return the reconstructed thought
     */
    public static Thought loadedThought(String fileName, String content)
    {

        fileName = fileName.replace(".md", "");
        LocalDateTime loadedDateTime = LocalDateTime.parse(fileName, formatFileName);
        Thought thought = new Thought(content, loadedDateTime);
        return thought;
    }

    /**
     * Returns the timestamp portion of this thought's filename, without the
     * extension.
     *
     * @return the formatted timestamp, e.g. "20260819-143000"
     */
    public String fileNameNoExn()
    {
        return created.format(formatFileName);
    }

    /**
     * Returns this thought's filename. The timestamp is the thought's identity,
     * which is why nothing else needs to be stored in the file.
     *
     * <p>
     * Known limitation: the timestamp is only precise to the second, so two
     * thoughts captured within the same second would produce the same name.
     * {@link Inbox#add(String)} guards against silent overwrites by refusing to
     * write over an existing file.
     *
     * @return the filename, ending in ".md"
     */
    public String fileName()
    {

        return created.format(formatFileName) + ".md";
    }

    /**
     * Returns exactly what should be written to this thought's file: the user's
     * text and nothing else. No heading, no front matter, no delimiters.
     *
     * @return the file contents
     */
    public String toFileContent()
    {
        return this.text;
    }

    /**
     * Returns a human-readable creation time for display in listings. This is
     * rendered fresh each time from the stored timestamp and is never persisted,
     * so it can be reformatted freely without touching any file.
     *
     * @return the formatted timestamp, e.g. "2026-08-19 14:30"
     */
    public String createdDateTime()
    {
        return created.format(formatHeading);
    }

    /**
     * Reports whether a given filename is one this class wrote — correct
     * extension and a parseable timestamp.
     *
     * <p>
     * A file that is not a thought (a stray README, an editor backup) is an
     * expected condition, not an error, so this answers with a boolean. The
     * parse exception is caught here and never escapes to the caller.
     *
     * @param fileName the name to test
     * @return true if the file can be loaded as a thought
     */
    public static boolean isThoughtFile(String fileName)
    {
        if (!fileName.endsWith(".md"))
            return false;

        try
        {
            fileName = fileName.replace(".md", "");
            LocalDateTime.parse(fileName, formatFileName);
            return true;
        }
        catch (DateTimeParseException e)
        {
            return false;
        }

    }

}
