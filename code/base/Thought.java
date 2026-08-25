package base;

import static helper.Utils.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * One captured thought. Owns the file format in both directions: what a thought
 * file is named, what it contains, and how to read one back. Nothing outside
 * this class knows the timestamp pattern or the extension.
 *
 * The timestamp lives only in the filename, never in the body. So the body is
 * pure user text: no header to strip, no delimiter to escape.
 */
class Thought
{

    private final String text;

    private static final String extension = ".md";

    private final LocalDateTime created;

    /** Display only. Never written to disk. */
    private static final DateTimeFormatter formatHeading = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Fixed-width, so sorting filenames as text sorts them by time. */
    private static final DateTimeFormatter formatFileName = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    Thought(String text)
    {
        this.text = text;
        created = LocalDateTime.now();
    }

    /** Rebuilds an existing thought with its original timestamp, not now(). */
    Thought(String text, LocalDateTime localDateTime)
    {
        this.text = text;
        created = localDateTime;
    }

    /**
     * Inverse of fileName + toFileContent. Callers must check isThoughtFile first;
     * an invalid name throws.
     */
    public static Thought loadedThought(String fileName, String content)
    {

        fileName = fileName.substring(0,fileName.lastIndexOf("-"));;
        LocalDateTime loadedDateTime = LocalDateTime.parse(fileName, formatFileName);
        Thought thought = new Thought(content, loadedDateTime);
        return thought;
    }


    /**
     * Timestamp + counter. The counter carries no meaning; it exists only to make
     * the name unique when two thoughts land in the same second. Zero padded so
     * names still sort as text.
     */
    public String fileName(int n)
    {

        return created.format(formatFileName) + "-" + String.format("%02d", n) + extension;
    }

    /** The user's text and nothing else. */
    public String toFileContent()
    {
        return this.text;
    }

    public String createdDateTime()
    {
        return created.format(formatHeading);
    }

    /**
     * A file that is not a thought (stray README, editor backup) is expected, not
     * an error, so this answers with a boolean rather than throwing.
     */
    public static boolean isThoughtFile(String fileName)
    {
        if (!fileName.endsWith(extension) || !(fileName.lastIndexOf("-")>-1))
            return false;

        try
        {

            fileName = fileName.substring(0, fileName.lastIndexOf("-"));
            LocalDateTime.parse(fileName, formatFileName);
            return true;
        }
        catch (DateTimeParseException e)
        {
            return false;
        }

    }

}
