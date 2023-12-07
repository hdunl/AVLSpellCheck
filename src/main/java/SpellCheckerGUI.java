import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.asynchttpclient.*;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
    private Button infoButton;
    private CheckBox useTextGearsCheckbox;

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

        // Create a GridPane for layout
        GridPane layoutGrid = new GridPane();
        layoutGrid.setHgap(10);
        layoutGrid.setVgap(10);
        layoutGrid.setPadding(new Insets(20));

// Checkbox for "Enhance with AI
        useTextGearsCheckbox = new CheckBox("Enhance with AI");

// "Details" button
        Button detailsButton = new Button("[Details]");
        detailsButton.getStyleClass().add("details-button"); // Add CSS class for styling
        detailsButton.setOnAction(e -> showApiInfoDialog());
        detailsButton.setStyle("-fx-font-size: 10px;"); // Adjust the font size

// Add "Enhance with AI" checkbox and "Details" button to the GridPane
        layoutGrid.add(useTextGearsCheckbox, 0, 0); // Adjust the row and column indices as needed
        layoutGrid.add(detailsButton, 1, 0); // Adjust the row and column indices as needed

// Add the GridPane to your main layout (adjust the row and column indices accordingly)
        CheckBox showPathCheckBox = new CheckBox("Show Search Path");
        gridPane.add(showPathCheckBox, 0, 6); // Adjust the row and column indices as needed
        gridPane.add(layoutGrid, 0, 7); // Adjust the row and column indices as needed

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

        // Event listeners for sliders
        maxDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxDistanceValueLabel.setText(String.valueOf(newValue.intValue()));
        });

        maxSuggestionsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxSuggestionsValueLabel.setText(String.valueOf(newValue.intValue()));
        });

        // Set up the path stage
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

        // Button event handler
        CheckBox finalShowPathCheckBox = showPathCheckBox;
        checkButton.setOnAction(e -> {
            String wordToCheck = inputWord.getText().trim();

            pathListView.getItems().clear();
            if (finalShowPathCheckBox.isSelected()) {
                pathListView.getItems().addAll(spellChecker.path());
                pathStage.show();
            } else {
                pathStage.hide();
            }

            if (wordToCheck.isEmpty() || wordToCheck.matches(".*\\d.*")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(wordToCheck.isEmpty() ? "Please enter a word to check." : "Please enter a valid word without numbers.");
                alert.showAndWait();
                return;
            }

            int maxDistance = (int) maxDistanceSlider.getValue();
            int maxSuggestions = (int) maxSuggestionsSlider.getValue();
            spellChecker.clearPath();

            String resultText;
            if (useTextGearsCheckbox.isSelected()) {
                resultText = callTextGearsAPI(wordToCheck);
            } else {
                long startTime = System.nanoTime();
                boolean isSpelledCorrectly = spellChecker.checkWord(wordToCheck);
                List<Map.Entry<String, Double>> suggestions = spellChecker.suggestCorrections(wordToCheck, maxDistance, maxSuggestions);
                long endTime = System.nanoTime();

                double searchTimeMillis = (double) (endTime - startTime) / 1_000_000;
                DecimalFormat df = new DecimalFormat("#.##");
                resultText = formatSuggestions(isSpelledCorrectly, wordToCheck, suggestions);
                resultText += "\nSearch Time: " + df.format(searchTimeMillis) + " ms";
            }

            suggestionsTextArea.setText(resultText);
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

    // Method to call the TextGears API
    private String callTextGearsAPI(String text) {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        String apiKey = "16fbc9e08emsh71b08b0f258851fp150171jsnecf065972717"; // Replace with your actual API key
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

        CompletableFuture<String> futureResponse = new CompletableFuture<>();

        client.preparePost("https://textgears-textgears-v1.p.rapidapi.com/spelling")
                .setHeader("content-type", "application/x-www-form-urlencoded")
                .setHeader("X-RapidAPI-Key", apiKey)
                .setHeader("X-RapidAPI-Host", "textgears-textgears-v1.p.rapidapi.com")
                .setBody("text=" + encodedText)
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    // Parse the response and format the suggestions
                    String suggestions = parseTextGearsResponse(response.getResponseBody());
                    futureResponse.complete(suggestions);
                });

        try {
            return futureResponse.get(); // This will wait until the future is completed
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showApiInfoDialog() {
        Alert apiInfoAlert = new Alert(Alert.AlertType.INFORMATION);
        apiInfoAlert.setTitle("API Information");
        apiInfoAlert.setHeaderText("TextGears API Details");
        apiInfoAlert.setContentText("https://rapidapi.com/Textgears/api/textgears/");

        apiInfoAlert.showAndWait();
    }

    private String parseTextGearsResponse(String jsonResponse) {
        StringBuilder result = new StringBuilder();
        JsonParser parser = new JsonParser();
        JsonObject rootObj = parser.parse(jsonResponse).getAsJsonObject();

        if (rootObj.has("status") && rootObj.get("status").getAsBoolean()) {
            if (rootObj.has("response") && rootObj.getAsJsonObject("response").has("errors")) {
                JsonArray errors = rootObj.getAsJsonObject("response").getAsJsonArray("errors");

                for (JsonElement errorElement : errors) {
                    JsonObject errorObj = errorElement.getAsJsonObject();
                    String badWord = errorObj.get("bad").getAsString();
                    JsonArray betterOptions = errorObj.get("better").getAsJsonArray();

                    for (JsonElement option : betterOptions) {
                        String suggestion = option.getAsString();
                        double similarityScore = calculateSimilarity(badWord, suggestion);
                        result.append(suggestion).append(", Similarity: ").append(String.format("%.2f%%\n", similarityScore * 100));
                    }
                    result.append("\n");
                }
            }
        } else {
            result.append("No suggestions found or error in API call.");
        }

        return result.toString();
    }

    private double calculateSimilarity(String word1, String word2) {
        int maxLength = Math.max(word1.length(), word2.length());
        int commonChars = 0;

        for (int i = 0; i < word1.length() && i < word2.length(); i++) {
            if (word1.charAt(i) == word2.charAt(i)) {
                commonChars++;
            }
        }

        int totalDifferences = maxLength - commonChars;
        double positionFactor = 1.0;

        // Check for character position differences
        for (int i = 0; i < Math.min(word1.length(), word2.length()); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                // Decrease the position factor based on the position of the difference
                positionFactor -= 0.1 * (1.0 - (double)i / maxLength);
            }
        }

        return (double)commonChars / maxLength * positionFactor;
    }


    private String formatSuggestions(boolean isSpelledCorrectly, String word, List<Map.Entry<String, Double>> suggestions) {
        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder resultText = new StringBuilder();

        if (isSpelledCorrectly) {
            resultText.append(word).append(" is spelled correctly.\n");
        } else {
            resultText.append(word).append(" is spelled incorrectly. Did you mean:\n");
            for (Map.Entry<String, Double> suggestion : suggestions) {
                resultText.append(suggestion.getKey())
                        .append(" (Score: ")
                        .append(df.format(suggestion.getValue()))
                        .append(")\n");
            }
        }
        return resultText.toString();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
