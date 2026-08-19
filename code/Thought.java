import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single note/thought entry, capturing its text content
 * and the timestamp it was created. Provides formatted strings for
 * display (heading) and for use as a file name.
 */
class Thought {

    /** The text content of the thought. */
    private final String text;

    /** The date and time the thought was created. */
    private final LocalDateTime created;

    /** Format used for the human-readable heading, e.g. "2026-08-19 14:30". */
    private static final DateTimeFormatter formatHeading = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Format used for generating a file-safe timestamp, e.g. "20260819-143000". */
    private static final DateTimeFormatter formatFileName = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /**
     * Creates a new Thought with the given text, timestamped at the
     * moment of creation.
     *
     * @param text the content of the thought
     */
    Thought(String text) {
        this.text = text;
        created = LocalDateTime.now();
    }

    /**
     * Returns a human-readable heading for this thought based on its
     * creation timestamp.
     *
     * @return the formatted heading string
     */
    public String heading() {

        return created.format(formatHeading);
    }

    /**
     * Returns a file name for this thought based on its creation
     * timestamp, suitable for saving as a Markdown file.
     *
     * @return the generated file name, ending in ".md"
     */
    public String fileName() {

        return created.format(formatFileName) + ".md";
    }

    /**
     * Returns the text content of this thought.
     *
     * @return the thought's text
     */
    public String text() {
        return text;
    }

    
}