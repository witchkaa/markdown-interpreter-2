package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Interpreter {
    public String convert(String sourceFile) {
        String md = readMarkdownFile(sourceFile);
        return convertMarkdownToHtml(md);
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

    private String convertMarkdownToHtml(String markdownContent) {
        String htmlContent = markdownContent.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

        //TODO: Add other regex patterns

        return htmlContent;
    }
}