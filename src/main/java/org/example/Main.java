package org.example;

import org.apache.commons.cli.*;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        Interpreter interpreter = new Interpreter();

        options.addOption("o", "out", true, "Path to result HTML file");
        options.addOption("f", "format", true, "Output format (ansi or html)");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length != 1) {
                System.err.println("Please provide the path to source file.");
                return;
            }
            String format = "ansi"; //default
            if (cmd.hasOption("f")) {
                format = cmd.getOptionValue("f");
            }
            String htmlContent = interpreter.convert(remainingArgs[0], format);

            if (cmd.hasOption("o")) {
                String outputFile = cmd.getOptionValue("o");
                writeToFile(htmlContent, outputFile);
            } else {
                System.out.println(htmlContent);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Error: invalid markdown (some markup element was not closed). Review your input file and try again.");
            System.exit(1);
        } catch (IllegalStateException e) {
            System.err.println("Error: invalid markdown (nested tags not allowed). Review your input file and try again.");
            System.exit(1);
        } catch (Exception e) {
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