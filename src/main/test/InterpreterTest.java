import org.example.Interpreter;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterTest {
    @Test
    void testConvertMarkdownToHtmlBold() {
        String input = "**Bold text**";
        String result = "<p><b>Bold text</b></p>\n";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "html"));
    }
    @Test
    void testConvertMarkdownToHtmlItalic() {
        String input = "_Italic text_";
        String result = "<p><i>Italic text</i></p>\n";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "html"));
    }
    @Test
    void testConvertMarkdownToHtmlMonosp() {
        String input = "`Monospaced text`";
        String result = "<p><tt>Monospaced text</tt></p>\n";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "html"));
    }
    @Test
    void testConvertMarkdownToHtml() throws Exception {
        Interpreter interpreter = new Interpreter();
        String markdownContent = "This is a **bold** text. This is an _italic_ text.";
        Path tempFile = Files.createTempFile("test", ".md");
        Files.writeString(tempFile, markdownContent);
        String htmlOutput = interpreter.convert(tempFile.toString(), "html");
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
        String htmlOutput = interpreter.convert(tempFile.toString(), "html");
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
        String htmlOutput = interpreter.convert(tempFile.toString(), "html");
        assertEquals("", htmlOutput);
        Files.deleteIfExists(tempFile);
    }
    @Test
    void testConvertMarkdownToHtmlWithNestedMarkers() {
        Interpreter interpreter = new Interpreter();
        String markdownWithNestedMarkers = "Some **bold _and_ italic** text.";

        Throwable exception = assertThrows(IllegalStateException.class, () -> {
            interpreter.convertMdToHtml(markdownWithNestedMarkers, "html");
        });

        assertEquals("Error: invalid markdown (nested tags not allowed). Review your input file and try again.", exception.getMessage());
    }

    @Test
    void testConvertMarkdownToHtmlWithUnbalancedMarkers() {
        Interpreter interpreter = new Interpreter();
        String markdownWithUnbalancedMarkers = "**Unbalanced *markers";

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            interpreter.convertMdToHtml(markdownWithUnbalancedMarkers, "html");
        });

        assertEquals("Error: invalid markdown (some markup element was not closed). Review your input file and try again.", exception.getMessage());
    }
    @Test
    void testConvertMarkdownToAnsiBold() {
        String input = "**Bold text**";
        String result = """
                \u001B[1mBold text\u001B[22m""";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "ansi"));
    }
    @Test
    void testConvertMarkdownToAnsiItalic() {
        String input = "_Italic text_";
        String result = """
                \u001B[3mItalic text\u001B[23m""";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "ansi"));
    }
    @Test
    void testConvertMarkdownToAnsiMonosp() {
        String input = "`Monospaced text`";
        String result = """
                \u001B[7mMonospaced text\u001B[27m""";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "ansi"));
    }
    @Test
    void testConvertMarkdownToAnsiCombined() {
        String input = "`Monospaced text` and _italic_ and also **bold**.";
        String result = """
                \u001B[7mMonospaced text\u001B[27m and \u001B[3mitalic\u001B[23m and also \u001B[1mbold\u001B[22m.""";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "ansi"));
    }
    @Test
    void TestConvertMdToAnsiPreformatted() {
        String arrangeMarkdown = """
                ```
                Preformatted `text` **won't** change
                ```
                """;
        String expectedAnsi = """
                \u001B[7m
                Preformatted `text` **won't** change
                \u001B[27m
                """;
        Interpreter i = new Interpreter();
        assertEquals(expectedAnsi, i.convertMdToHtml(arrangeMarkdown, "ansi"));
    }
    @Test
    void testConvertMarkdownToAnsiSpacedMarkers() {
        String input = "** Not Bold text **";
        String result = "** Not Bold text **";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "ansi"));
    }
    @Test
    void testConvertMarkdownToAnsiMarkerAtTheEnd() {
        String input = "Some text`";
        String result = "Some text`";
        Interpreter i = new Interpreter();
        assertEquals(result, i.convertMdToHtml(input, "ansi"));
    }
}


