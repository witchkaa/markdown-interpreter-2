package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    public String convert(String sourceFile) {
        String md = readMarkdownFile(sourceFile);
        return convertMdToHtml(md);
    }
    private String readMarkdownFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    public String convertMdToHtml(String markdownContent) {
        List<String> mdBlocks = new ArrayList<>();

        Pattern pattern = Pattern.compile("(?m)(^\\n?|^)```(.*?)```(\\n?|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(markdownContent);
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            String prefix = matcher.group(1);
            String suffix = matcher.group(3);
            mdBlocks.add(matcher.group(2));
            String replacement = (prefix.isEmpty() ? "" : "\n") + "block" + index++ + (suffix.isEmpty() ? "" : "\n");
            matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(stringBuilder);
        markdownContent = stringBuilder.toString();
        String blueprint = markdownContent;

        String findBold = "(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])";
        String findItalic = "(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])";
        String findMonosp = "(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])";

        List<String> boldBlocks = getMatchPatternList(findBold, markdownContent);
        List<String> italicBlocks = getMatchPatternList(findItalic, markdownContent);
        List<String> monospacedBlocks = getMatchPatternList(findMonosp, markdownContent);

        Checker checker = new Checker();
        checker.checkForNestedMarkers(findItalic, findMonosp, boldBlocks);
        checker.checkForNestedMarkers(findBold, findItalic, monospacedBlocks);
        checker.checkForNestedMarkers(findBold, findMonosp, italicBlocks);

        markdownContent = markdownContent.replaceAll(findBold, "<b>$1</b>");
        blueprint = blueprint.replaceAll(findBold, "boldBlock");
        markdownContent = markdownContent.replaceAll(findItalic, "<i>$1</i>");
        blueprint = blueprint.replaceAll(findItalic, "italicBlock");
        markdownContent = markdownContent.replaceAll(findMonosp, "<tt>$1</tt>");
        blueprint = blueprint.replaceAll(findMonosp, "monospacedBlock");

        String[] paragraphs = markdownContent.split("\n{2,}");
        StringBuilder builder = new StringBuilder();
        for (String paragraph : paragraphs) {
            if (!paragraph.isEmpty()) {
                builder.append("<p>").append(paragraph).append("</p>\n");
            }
        }
        markdownContent = builder.toString();
        for (int i = 0; i < mdBlocks.size(); i++) {
            markdownContent = markdownContent.replace("block" + i, "<pre>" + mdBlocks.get(i) + "</pre>");
        }
        if (checker.checkForUnbalancedMarkers("`", blueprint) ||
                checker.checkForUnbalancedMarkers("```", blueprint) ||
                checker.checkForUnbalancedMarkers("**", blueprint) ||
                checker.checkForUnbalancedMarkers("_", blueprint)) {
            throw new Error("Error: invalid markdown (some markup element was not closed). Review your input file and try again.");
        }
        return markdownContent;
    }
    private List<String> getMatchPatternList(String regex, String html) {
        List<String> regexBlocks = new ArrayList<>();
        Pattern regexPatten = Pattern.compile(regex, Pattern.DOTALL);
        Matcher boldMatcher = regexPatten.matcher(html);
        while (boldMatcher.find()) {
            regexBlocks.add(boldMatcher.group(1));
        }
        return regexBlocks;
    }
}