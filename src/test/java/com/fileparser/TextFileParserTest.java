package com.fileparser;

import javafx.concurrent.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFileParserTest {

    private TextFileParser textFileParser;

    @BeforeEach
    void setUp() throws IOException {
        List<String> lines = Arrays.asList("The first line", "The second line");
        Path file = Paths.get("testFile.txt");
        Files.write(file, lines, StandardCharsets.UTF_8);
        textFileParser = new TextFileParser(file);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get("testFile.txt"));
    }

    @Test
    void createTextFileParserTask() {
        Task<Void> textFileParserTask = textFileParser.createTextFileParserTask();
        assertNotNull(textFileParserTask);
    }


    @Test
    void readContentFromFile() {
        String actualFileContent = textFileParser.readContentFromFile();
        String expectedFileContent = "The first line\n" +
                "The second line\n";
        assertEquals(expectedFileContent, actualFileContent);
    }

    @Test
    void getOccurrencesPerWordCountsWordsWithLineBreakSeperate() {
        List<WordOccurrenceTuple> expectedResult = Arrays.asList(
                new WordOccurrenceTuple("The", 1L),
                new WordOccurrenceTuple("line\n", 1L),
                new WordOccurrenceTuple("line\nThe", 1L),
                new WordOccurrenceTuple("first", 1L),
                new WordOccurrenceTuple("second", 1L)
        );
        List<WordOccurrenceTuple> actualResult = textFileParser.getOccurrencesPerWord("The first line\n" +
                "The second line\n");
        for (int i = 0; i < expectedResult.size(); i++) {
            assertTrue(expectedResult.get(i).equals(actualResult.get(i)));
        }
    }
}