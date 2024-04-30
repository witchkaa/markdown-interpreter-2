import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.example.Main;

class MainTest {
    @Test
    void testConvertMarkdownToHtmlMissingSourceFile() {
        String[] args = {};

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        Main.main(args);

        String printedOutput = outContent.toString().trim();

        assertEquals("Please provide the path to source file.", printedOutput);
    }
    @Test
    void testConvertMarkdownNoFlags() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String sourceFilePath = "src/main/test/file.md";
        String[] args = {sourceFilePath};

        Main.main(args);
        String consoleOutput = outContent.toString().trim();
        assertEquals("this file was created for \u001B[1mtesting\u001B[22m purposes", consoleOutput);
    }

    @Test
    void testConvertMarkdownToHtmlOutputFileOption() {
        String sourceFilePath = "src/main/test/file.md";
        String outputFilePath = "src/main/test/file.html";
        String[] args = {sourceFilePath, "-o", outputFilePath};

        Main.main(args);

        File outputFile = new File(outputFilePath);
        assertTrue(outputFile.exists(), "Output file should be created");
        assertTrue(outputFile.isFile(), "Output should be a file");
        assertTrue(outputFile.length() > 0, "Output file should not be empty");

        outputFile.delete();
    }
}