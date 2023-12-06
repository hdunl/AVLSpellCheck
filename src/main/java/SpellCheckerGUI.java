import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpellCheckerGUI extends Application {
    private final SpellChecker spellChecker;
    private Slider maxDistanceSlider;
    private Slider maxSuggestionsSlider;
    private TextArea suggestionsTextArea;
    private Label maxDistanceValueLabel;
    private Label maxSuggestionsValueLabel;
    private Label statsLabel;

    private Stage pathStage;
    private ListView<String> pathListView;

    public SpellCheckerGUI() {
        spellChecker = new SpellChecker(2);
        spellChecker.loadDictionary("dictionary.txt");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spell Checker");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        Label inputLabel = new Label("Enter a word:");
        TextField inputWord = new TextField();
        inputWord.setPrefWidth(300);

        Label maxDistanceLabel = new Label("Max Distance:");
        maxDistanceSlider = new Slider(1, 5, 2);
        maxDistanceSlider.setPrefWidth(300);
        maxDistanceValueLabel = new Label(String.valueOf((int) maxDistanceSlider.getValue()));

        Label maxSuggestionsLabel = new Label("Max Suggestions:");
        maxSuggestionsSlider = new Slider(1, 20, 5);
        maxSuggestionsSlider.setPrefWidth(300);
        maxSuggestionsValueLabel = new Label(String.valueOf((int) maxSuggestionsSlider.getValue()));

        Button checkButton = new Button("Check Spelling");
        suggestionsTextArea = new TextArea();
        suggestionsTextArea.setWrapText(true);
        suggestionsTextArea.setEditable(false);
        suggestionsTextArea.setPrefHeight(300);

        CheckBox showPathCheckBox = new CheckBox("Show Search Path");
        gridPane.add(showPathCheckBox, 0, 6);

        statsLabel = new Label();

        gridPane.add(inputLabel, 0, 0);
        gridPane.add(inputWord, 1, 0);
        gridPane.add(maxDistanceLabel, 0, 1);
        gridPane.add(maxDistanceSlider, 1, 1);
        gridPane.add(maxDistanceValueLabel, 2, 1);
        gridPane.add(maxSuggestionsLabel, 0, 2);
        gridPane.add(maxSuggestionsSlider, 1, 2);
        gridPane.add(maxSuggestionsValueLabel, 2, 2);
        gridPane.add(checkButton, 0, 3);
        GridPane.setColumnSpan(suggestionsTextArea, 3);
        gridPane.add(suggestionsTextArea, 0, 4);
        GridPane.setColumnSpan(statsLabel, 3);
        gridPane.add(statsLabel, 0, 5);

        maxDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int maxDistance = newValue.intValue();
            maxDistanceValueLabel.setText(String.valueOf(maxDistance));
        });

        maxSuggestionsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int maxSuggestions = newValue.intValue();
            maxSuggestionsValueLabel.setText(String.valueOf(maxSuggestions));
        });

        pathStage = new Stage();
        pathStage.setTitle("Search Path");
        pathStage.initStyle(StageStyle.UTILITY);
        pathStage.setResizable(false);

        pathListView = new ListView<>();
        pathListView.setPrefSize(200, 300);
        pathListView.setEditable(false);

        VBox pathLayout = new VBox();
        pathLayout.getChildren().addAll(pathListView);
        Scene pathScene = new Scene(pathLayout, 200, 300);
        pathStage.setScene(pathScene);

        checkButton.setOnAction(e -> {
            String wordToCheck = inputWord.getText();

            // Clear the previous items from the path list view
            pathListView.getItems().clear();

            // Only show the search path dialog if the checkbox is selected
            if (showPathCheckBox.isSelected()) {
                pathListView.getItems().addAll(spellChecker.path());
                pathStage.show(); // Show the dialog only if the checkbox is selected
            } else {
                pathStage.hide(); // Optionally hide the dialog if it's not selected
            }

            if (wordToCheck.isEmpty() || wordToCheck.matches(".*\\d.*")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);

                if (wordToCheck.isEmpty()) {
                    alert.setContentText("Please enter a word to check.");
                } else {
                    alert.setContentText("Please enter a valid word without numbers.");
                }


                alert.showAndWait();
                return;
            }

            int maxDistance = (int) maxDistanceSlider.getValue();
            int maxSuggestions = (int) maxSuggestionsSlider.getValue();

            spellChecker.clearPath();

            // Start measuring time
            long startTime = System.nanoTime();

            boolean isSpelledCorrectly = spellChecker.checkWord(wordToCheck);
            List<Map.Entry<String, Double>> suggestions = spellChecker.suggestCorrections(wordToCheck, maxDistance, maxSuggestions);

            // Stop measuring time
            long endTime = System.nanoTime();
            double searchTimeMillis = (double) (endTime - startTime) / 1_000_000;

            // Formatting the output
            DecimalFormat df = new DecimalFormat("#.##");
            StringBuilder resultText = new StringBuilder();

            if (isSpelledCorrectly) {
                resultText.append(wordToCheck).append(" is spelled correctly.\n");
            } else {
                resultText.append(wordToCheck).append(" is spelled incorrectly. Did you mean:\n");
                for (Map.Entry<String, Double> suggestion : suggestions) {
                    resultText.append(suggestion.getKey())
                            .append(" (Score: ")
                            .append(df.format(suggestion.getValue()))
                            .append(")\n");
                }
            }

            // Append the search time to the result text
            resultText.append("\nSearch Time: ").append(df.format(searchTimeMillis)).append(" ms");

            // Set the text to the suggestions text area
            suggestionsTextArea.setText(resultText.toString());
            updateStats();
        });

        Scene scene = new Scene(gridPane, 600, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateStats() {
        System.out.println("Updating stats...");
        spellChecker.loadDictionary("dictionary.txt");
        String statsText = "Dictionary Population Time Complexity: O(N) - Linear\n";
        statsText += "Average Search Time Complexity: O(log N) - Logarithmic\n";

        statsLabel.setText(statsText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}