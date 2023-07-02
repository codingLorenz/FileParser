package com.fileparser;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TextFileParser {
    public Path filePath;
    public DoubleProperty progress;
    public ObservableList wordOccurrenceList;
    private static final int BUFFER_SIZE = 4096;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public TextFileParser(Path path) {
        this.filePath = path;
        this.wordOccurrenceList = FXCollections.observableArrayList();
        this.progress = new SimpleDoubleProperty();
    }

    public Task<Void> createTextFileParserTask() {
        return new Task<Void>() {
            @Override
            public Void call() {
                String fileContent = readContentFromFile();
                List<WordOccurrenceTuple> wordOccurrenceList = getOccurrencesPerWord(fileContent);
                TextFileParser.this.wordOccurrenceList.setAll(wordOccurrenceList);
                return null;
            }
        };
    }

    public String readContentFromFile() {
        StringBuilder textFileContentBuilder = new StringBuilder();
        try {
            FileChannel textFile = FileChannel.open(filePath);
            ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (fileHasBytesRemaining(textFile, byteBuffer)) {
                textFileContentBuilder.append(readByteBuffer(byteBuffer));
                byteBuffer.clear();
                progress.set(limitProgressTo50Percent((double) textFile.position() / textFile.size()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textFileContentBuilder.toString();
    }

    public List<WordOccurrenceTuple> getOccurrencesPerWord(String fileContent) {
        List<String> words = Arrays.asList(fileContent.split(" "));
        AtomicReference<Double> counter = new AtomicReference<>((double) 0);
        double currentProgress = progress.doubleValue();
        Map<String, Long> wordsStatistic = groupWordsByOccurrences(words, counter, currentProgress);
        List<WordOccurrenceTuple> wordOccurrenceTuples = wordsStatistic
                .entrySet()
                .stream()
                .map(TextFileParser::getWordOccurrenceTuple)
                .sorted(makeDescendingComparison())
                .toList();
        return wordOccurrenceTuples;
    }

    private Map<String, Long> groupWordsByOccurrences(List<String> words, AtomicReference<Double> counter, double currentProgress) {
        return words
                .stream()
                .peek(element -> {
                    incrementBy1(counter);
                    progress.set(currentProgress + (counter.get() / words.size()));
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private static Double incrementBy1(AtomicReference<Double> counter) {
        return counter.getAndSet(new Double((counter.get() + 1)));
    }

    private double limitProgressTo50Percent(double fullProgress) {
        return fullProgress / 2;
    }

    private static WordOccurrenceTuple getWordOccurrenceTuple(Map.Entry<String, Long> entry) {
        return new WordOccurrenceTuple(entry.getKey(), entry.getValue());
    }


    private String readByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        return CHARSET.decode(byteBuffer).toString();
    }

    private static boolean fileHasBytesRemaining(FileChannel textFileChannel, ByteBuffer byteBuffer) throws IOException {
        return readBytesFromFileToBuffer(textFileChannel, byteBuffer) > -1;
    }

    private static int readBytesFromFileToBuffer(FileChannel textFileChannel, ByteBuffer byteBuffer) throws IOException {
        return textFileChannel.read(byteBuffer);
    }

    private static Comparator<WordOccurrenceTuple> makeDescendingComparison() {
        return (firstValue, secondValue) -> Long.compare(secondValue.getAmountOfOccurrences(), firstValue.getAmountOfOccurrences());
    }
}
