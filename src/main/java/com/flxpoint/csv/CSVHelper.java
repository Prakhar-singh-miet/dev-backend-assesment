package com.flxpoint.csv;

import java.util.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.*;
import java.io.BufferedWriter;

public class CSVHelper {

    public static void main(String[] args) {
        String inputFile = "src/main/resources/input.csv";
        String outputFile = "src/main/resources/output.csv";

        List<String> lines = readCsvFile(inputFile);
        List<String> uniqueLines = removeDuplicates(lines);

        writeCsvFile(outputFile, uniqueLines);
        System.out.println("CSV file after removing duplicates has been saved at: " + outputFile);
    }

    private static List<String> readCsvFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading the CSV file: " + e.getMessage());
        }
        return lines;
    }

    private static List<String> removeDuplicates(List<String> lines) {
        if (lines.isEmpty()) return Collections.emptyList();

        List<String> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        String header = lines.get(0);
        result.add(header);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).replaceAll("\\s+", " ").trim();
            if (!seen.contains(line) && !line.contains(",,,")) {
                seen.add(line);
                result.add(line);
            }
        }
        return result;
    }

    private static void writeCsvFile(String filePath, List<String> lines) {
        try {
            Path path = Paths.get(filePath);
            System.out.println("Writing to file: " + path.toAbsolutePath());

            Files.createDirectories(path.getParent()); // Ensuring the directory exists

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("File has been successfully written at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Error while writing CSV file: " + e.getMessage());
        }
    }
}
