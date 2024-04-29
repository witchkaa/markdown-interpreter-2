import org.example.Interpreter;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterTest {
    @Test
    void testConvertMarkdownToHtml() throws Exception {
        Interpreter interpreter = new Interpreter();
        String markdownContent = "This is a **bold** text. This is an _italic_ text.";
        Path tempFile = Files.createTempFile("test", ".md");
        Files.writeString(tempFile, markdownContent);
        String htmlOutput = interpreter.convert(tempFile.toString());
        assertEquals("<p>This is a <b>bold</b> text. This is an <i>italic</i> text.</p>\n", htmlOutput);
        Files.deleteIfExists(tempFile);
    }
    @Test
    void testConvertMarkdownToHtml2() throws Exception {
        Interpreter interpreter = new Interpreter();
        String markdownContent = """
                This is a **bold** text. This is an _italic_ text. Here is `monospaced`.

                ```This **text** is _preformatted_ so it `won't` change```""";
        Path tempFile = Files.createTempFile("test", ".md");
        Files.writeString(tempFile, markdownContent);
        String htmlOutput = interpreter.convert(tempFile.toString());
        assertEquals("""
                <p>This is a <b>bold</b> text. This is an <i>italic</i> text. Here is <tt>monospaced</tt>.</p>
                <p><pre>This **text** is _preformatted_ so it `won't` change</pre></p>
                """, htmlOutput);
        Files.deleteIfExists(tempFile);
    }
    @Test
    void testConvertMarkdownToHtmlEmpty() throws Exception {
        Interpreter interpreter = new Interpreter();
        String markdownContent = "";
        Path tempFile = Files.createTempFile("test", ".md");
        Files.writeString(tempFile, markdownContent);
        String htmlOutput = interpreter.convert(tempFile.toString());
        assertEquals("", htmlOutput);
        Files.deleteIfExists(tempFile);
    }
    @Test
    void testConvertMarkdownToHtmlWithNestedMarkers() {
        Interpreter interpreter = new Interpreter();
        String markdownWithNestedMarkers = "Some **bold _and_ italic** text.";

        Throwable exception = assertThrows(IllegalStateException.class, () -> {
            interpreter.convertMdToHtml(markdownWithNestedMarkers);
        });

        assertEquals("Error: invalid markdown (nested tags not allowed). Review your input file and try again.", exception.getMessage());
    }

    @Test
    void testConvertMarkdownToHtmlWithUnbalancedMarkers() {
        Interpreter interpreter = new Interpreter();
        String markdownWithUnbalancedMarkers = "**Unbalanced *markers";

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            interpreter.convertMdToHtml(markdownWithUnbalancedMarkers);
        });

        assertEquals("Error: invalid markdown (some markup element was not closed). Review your input file and try again.", exception.getMessage());
    }
}