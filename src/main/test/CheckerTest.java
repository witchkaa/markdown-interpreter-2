import org.example.Checker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class CheckerTest {
    @Test
    void testCheckForUnbalancedMarkersBalanced() {
        Checker checker = new Checker();
        assertTrue(checker.checkForUnbalancedMarkers("**", "**some text** with _different_ markers"));
    }
    @Test
    void testCheckForUnbalancedMarkersUnbalanced() {
        Checker checker = new Checker();
        assertTrue(checker.checkForUnbalancedMarkers("**", "**text**"));
    }
    @Test
    void testCheckForUnbalancedMarkersNoMarkers() {
        Checker checker = new Checker();
        assertFalse(checker.checkForUnbalancedMarkers("**", "text without markers"));
    }
    @Test
    void testCheckForUnbalancedMarkersEmptyString() {
        Checker checker = new Checker();
        assertFalse(checker.checkForUnbalancedMarkers("**", ""));
    }
    @Test
    void testCheckForNestedMarkersNested() {
        Checker checker = new Checker();
        List<String> blocks = new ArrayList<>();
        blocks.add("**There**");
        blocks.add("_**There** is nested_");
        assertTrue(checker.checkForNestedMarkers(Pattern.quote("**"), Pattern.quote("_"), blocks));
    }
    @Test
    void testCheckForNestedMarkersNoNested() {
        Checker checker = new Checker();
        List<String> blocks = new ArrayList<>();
        blocks.add("There are no nested markers");
        assertFalse(checker.checkForNestedMarkers(Pattern.quote("**"), Pattern.quote("_"), blocks));
    }
}