package org.example;

import org.apache.commons.cli.*;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        Interpreter interpreter = new Interpreter();

        options.addOption("o", "out", true, "Path to result HTML file");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length != 1) {
                System.err.println("Please provide the path to source file.");
                return;
            }
            String htmlContent = interpreter.convert(remainingArgs[0]);

            if (cmd.hasOption("o")) {
                String outputFile = cmd.getOptionValue("o");
                writeToFile(htmlContent, outputFile);
            } else {
                System.out.println(htmlContent);
            }

        } catch (ParseException e) {
            System.err.println("Error reading cmd line: " + e.getMessage());
            System.exit(1);
        }
    }
    private static void writeToFile(String htmlContent, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}