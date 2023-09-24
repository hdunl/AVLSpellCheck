import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

/**
 * A JavaFX-based Spell Checker GUI application.
 */
public class SpellCheckerGUI extends Application {

    private final SpellChecker spellChecker;
    private Slider maxDistanceSlider;
    private Slider maxSuggestionsSlider;
    private TextArea suggestionsTextArea;
    private Label maxDistanceValueLabel;
    private Label maxSuggestionsValueLabel;
    private Label statsLabel;
    private long dictionaryPopulationTime;

    /**
     * Constructor for SpellCheckerGUI.
     */
    public SpellCheckerGUI() {
        spellChecker = new SpellChecker(5);
        spellChecker.loadDictionary("dictionary.txt"); // Load dictionary

        dictionaryPopulationTime = 0;
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
        suggestionsTextArea.setPrefHeight(300); // Increased height

        statsLabel = new Label(); // Label for displaying stats

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
        GridPane.setColumnSpan(statsLabel, 3); // Span statsLabel across three columns
        gridPane.add(statsLabel, 0, 5);

        maxDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int maxDistance = newValue.intValue();
            spellChecker.setMaxDistance(maxDistance);
            maxDistanceValueLabel.setText(String.valueOf(maxDistance));
        });

        maxSuggestionsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int maxSuggestions = newValue.intValue();
            maxSuggestionsValueLabel.setText(String.valueOf(maxSuggestions));
        });

        checkButton.setOnAction(e -> {
            String wordToCheck = inputWord.getText();

            // Check if the word is blank or contains numbers
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
                return; // Exit the method
            }

            int maxDistance = (int) maxDistanceSlider.getValue();
            int maxSuggestions = (int) maxSuggestionsSlider.getValue();

            long startTime = System.nanoTime();
            boolean isSpelledCorrectly = spellChecker.checkWord(wordToCheck);
            long endTime = System.nanoTime();

            double recentSearchTimeMillis = (double) (endTime - startTime) / 1_000_000;
            DecimalFormat decimalFormat = new DecimalFormat("0.000");

            String resultText;
            if (isSpelledCorrectly) {
                resultText = wordToCheck + " is spelled correctly.";
            } else {
                List<String> suggestions = spellChecker.suggestCorrections(wordToCheck, maxDistance);
                StringBuilder suggestionsText = new StringBuilder();
                for (int i = 0; i < Math.min(maxSuggestions, suggestions.size()); i++) {
                    String suggestion = suggestions.get(i);
                    if (i > 0) {
                        suggestionsText.append('\n');
                    }
                    suggestionsText.append(suggestion);
                }
                resultText = wordToCheck + " is spelled incorrectly. Did you mean:\n" + suggestionsText;
            }

            suggestionsTextArea.setText(resultText + "\nSearch Time: " + decimalFormat.format(recentSearchTimeMillis) + "ms");
            updateStats(); // Update stats with search time
        });

        Scene scene = new Scene(gridPane, 600, 500); // Increased scene height
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateStats() {
        System.out.println("Updating stats...");
        long startTime = System.currentTimeMillis();
        spellChecker.loadDictionary("dictionary.txt");
        dictionaryPopulationTime = System.currentTimeMillis() - startTime;

        String statsText = "Dictionary Population Time: " + dictionaryPopulationTime + " ms\n";
        statsText += "Dictionary Population Time Complexity: O(N) - Linear\n";
        statsText += "Average Search Time Complexity: O(log N) - Logarithmic\n";

        statsLabel.setText(statsText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}