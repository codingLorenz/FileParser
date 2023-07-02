package com.fileparser;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileParserController {
    @FXML
    private Button fileButton;

    @FXML
    private Button startButton;

    @FXML
    private TableView<WordOccurrenceTuple> wordStatisticsTable;
    @FXML
    private ProgressBar fileProgressbar;

    private ExecutorService executorService;

    private TextFileParser textFileParser;


    @FXML
    public void initialize() {
        startButton.setDisable(true);
        wordStatisticsTable.setEditable(true);
        TableColumn wordColumn = new TableColumn("word");
        wordColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
        TableColumn occurrenceColumn = new TableColumn("occurrence");
        occurrenceColumn.setCellValueFactory(new PropertyValueFactory<>("amountOfOccurrences"));
        wordStatisticsTable.getColumns().addAll(wordColumn, occurrenceColumn);
    }

    @FXML
    protected void onFileButtonClick(ActionEvent event) {
        Path selectedFile = chooseFile();
        textFileParser = new TextFileParser(selectedFile);
        bindObservableListToTable();
        fileProgressbar.progressProperty().bind(textFileParser.progress);
        startButton.setDisable(false);
    }

    private Path chooseFile() {
        FileChooser textFileChooser = new FileChooser();
        textFileChooser
                .getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return textFileChooser.showOpenDialog(null).toPath();
    }

    private void bindObservableListToTable() {
        wordStatisticsTable.setItems(textFileParser.wordOccurrenceList);
    }

    @FXML
    protected void onAbortButtonClick() {
        executorService.shutdownNow();
        fileProgressbar.progressProperty().unbind();
        wordStatisticsTable.setItems(null);
    }

    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        executeFileParsing();
    }

    private void executeFileParsing() {
        Task<Void> textFileParserTask = textFileParser.createTextFileParserTask();
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(textFileParserTask);
    }
}