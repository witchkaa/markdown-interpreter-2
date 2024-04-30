package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    public String convert(String sourceFile, String format) {
        String md = readMarkdownFile(sourceFile);
        return convertMdToHtml(md, format);
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

    public String convertMdToHtml(String markdownContent, String format) {
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
        Checker checker = new Checker();
        String findBold = "(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])";
        String findItalic = "(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])";
        String findMonosp = "(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])";
        List<String> boldBlocks = getMatchPatternList(findBold, markdownContent);
        List<String> italicBlocks = getMatchPatternList(findItalic, markdownContent);
        List<String> monospacedBlocks = getMatchPatternList(findMonosp, markdownContent);

        boolean firstCheck = checker.checkForNestedMarkers(findItalic, findMonosp, boldBlocks);
        boolean secondCheck = checker.checkForNestedMarkers(findBold, findItalic, monospacedBlocks);
        boolean thirdCheck = checker.checkForNestedMarkers(findBold, findMonosp, italicBlocks);
        if (firstCheck || secondCheck || thirdCheck) {
            throw new IllegalStateException("Error: invalid markdown (nested tags not allowed). Review your input file and try again.");
        }
        blueprint = blueprint.replaceAll(findBold, "boldBlock");
        blueprint = blueprint.replaceAll(findItalic, "italicBlock");
        blueprint = blueprint.replaceAll(findMonosp, "monospacedBlock");
        if (checker.checkForUnbalancedMarkers("`", blueprint) ||
                checker.checkForUnbalancedMarkers("```", blueprint) ||
                checker.checkForUnbalancedMarkers("**", blueprint) ||
                checker.checkForUnbalancedMarkers("_", blueprint)) {
            throw new IllegalArgumentException("Error: invalid markdown (some markup element was not closed). Review your input file and try again.");
        }



        String htmlContent = markdownContent;
        if (format.equals("html")) {
            htmlContent = htmlContent.replaceAll(findBold, "<b>$1</b>")
                    .replaceAll(findItalic, "<i>$1</i>")
                    .replaceAll(findMonosp, "<tt>$1</tt>");
            String[] paragraphs = htmlContent.split("\n{2,}");
            StringBuilder builder = new StringBuilder();
            for (String paragraph : paragraphs) {
                if (!paragraph.trim().isEmpty()) {
                    builder.append("<p>").append(paragraph.trim()).append("</p>\n");
                }
            }
            htmlContent = builder.toString();
            for (int i = 0; i < mdBlocks.size(); i++) {
                htmlContent = htmlContent.replace("block" + i, "<pre>" + mdBlocks.get(i) + "</pre>");
            }
        } else if (format.equals("ansi")) {
            htmlContent = htmlContent.replaceAll(findBold, "\u001B[1m$1\u001B[22m")
                    .replaceAll(findItalic, "\u001B[3m$1\u001B[23m")
                    .replaceAll(findMonosp, "\u001B[7m$1\u001B[27m");
            for (int i = 0; i < mdBlocks.size(); i++) {
                htmlContent = htmlContent.replace("block" + i, "\u001B[7m" + mdBlocks.get(i) + "\u001B[27m");
            }
        } else {
            throw new IllegalArgumentException("Error: unsupported output format. Use 'html' or 'ansi'.");
        }

        return htmlContent;
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




