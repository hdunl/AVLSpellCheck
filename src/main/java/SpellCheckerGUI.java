import com.google.gson.JsonArray;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Button;
import org.asynchttpclient.*;


public class SpellCheckerGUI extends Application {
    private final SpellChecker spellChecker;
    private Slider maxSuggestionsSlider;
    private TextArea suggestionsTextArea;
    private Label maxSuggestionsValueLabel;
    private Stage pathStage;
    private ListView<String> pathListView;


    public SpellCheckerGUI() {
        spellChecker = new SpellChecker(2);
        spellChecker.loadDictionary("dictionary.txt");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AVSpell");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        Label inputLabel = new Label("Enter a word:");
        TextField inputWord = new TextField();
        inputWord.setPrefWidth(300);


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



        CheckBox showPathCheckBox = new CheckBox("Show Search Path");
        gridPane.add(showPathCheckBox, 0, 5);
        gridPane.add(layoutGrid, 0, 7);
        CheckBox aiToggle = new CheckBox("Use AI API for Spell Checking");
        gridPane.add(aiToggle, 0, 6);

        Button aiInfoButton = new Button("Info");
        aiInfoButton.getStyleClass().add("small-info-button");
        gridPane.add(aiInfoButton, 1, 6);




        Label statsLabel = new Label();

        gridPane.add(inputLabel, 0, 0);
        gridPane.add(inputWord, 1, 0);
        gridPane.add(maxSuggestionsLabel, 0, 2);
        gridPane.add(maxSuggestionsSlider, 1, 2);
        gridPane.add(maxSuggestionsValueLabel, 2, 2);
        gridPane.add(checkButton, 0, 3);
        GridPane.setColumnSpan(suggestionsTextArea, 3);
        gridPane.add(suggestionsTextArea, 0, 4);
        GridPane.setColumnSpan(statsLabel, 3);
        gridPane.add(statsLabel, 0, 5);

        maxSuggestionsSlider.valueProperty().addListener((observable, oldValue, newValue) -> maxSuggestionsValueLabel.setText(String.valueOf(newValue.intValue())));

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

        aiInfoButton.setOnAction(e -> {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Ginger API Information");
            infoAlert.setHeaderText("Ginger Spell and Grammar Checker API");
            infoAlert.setContentText("The Ginger API provides advanced spell checking and grammar correction. " +
                    "For more information, visit: https://rapidapi.com/ginger-software-ginger-software-default/api/ginger4");

            infoAlert.showAndWait();
        });

        // Button event handler
        checkButton.setOnAction(e -> {
            String wordToCheck = inputWord.getText().trim();
            if (aiToggle.isSelected()) {
                // Use Ginger API
                String resultText = checkUsingGingerAPI(wordToCheck);
                suggestionsTextArea.setText(resultText);
            }
            else {

            pathListView.getItems().clear();
            if (showPathCheckBox.isSelected()) {
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

            int maxSuggestions = (int) maxSuggestionsSlider.getValue();
            spellChecker.clearPath();

            String resultText;
                long startTime = System.nanoTime();
                boolean isSpelledCorrectly = spellChecker.checkWord(wordToCheck);
                List<Map.Entry<String, Double>> suggestions = spellChecker.suggestCorrections(wordToCheck, maxSuggestions);
                long endTime = System.nanoTime();

                double searchTimeMillis = (double) (endTime - startTime) / 1_000_000;
                DecimalFormat df = new DecimalFormat("#.##");
                resultText = formatSuggestions(isSpelledCorrectly, wordToCheck, suggestions);
                resultText += "\nSearch Time: " + df.format(searchTimeMillis) + " ms";

            suggestionsTextArea.setText(resultText);
            updateStats();
        }});

        Scene scene = new Scene(gridPane, 600, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateStats() {
        System.out.println("Updating stats...");
        spellChecker.loadDictionary("dictionary.txt");
    }

    private String checkUsingGingerAPI(String text) {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        try {
            CompletableFuture<Response> futureResponse = client.prepare("POST", "https://ginger4.p.rapidapi.com/correction?lang=US&generateRecommendations=false&flagInformalLanguage=true")
                    .setHeader("content-type", "text/plain")
                    .setHeader("X-RapidAPI-Key", "16fbc9e08emsh71b08b0f258851fp150171jsnecf065972717")
                    .setBody(text)
                    .execute()
                    .toCompletableFuture();

            Response response = futureResponse.join();
            return parseGingerResponse(response.getResponseBody());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String parseGingerResponse(String jsonResponse) {
        StringBuilder result = new StringBuilder();
        JsonObject jobject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject gingerResult = jobject.getAsJsonObject("GingerTheDocumentResult");

        if (gingerResult != null) {
            JsonArray corrections = gingerResult.getAsJsonArray("Corrections");
            if (corrections != null) {
                for (JsonElement correctionElement : corrections) {
                    JsonObject correction = correctionElement.getAsJsonObject();
                    JsonArray suggestions = correction.getAsJsonArray("Suggestions");
                    if (suggestions != null && !suggestions.isEmpty()) {
                        JsonObject firstSuggestion = suggestions.get(0).getAsJsonObject();
                        String suggestionText = firstSuggestion.get("Text").getAsString();
                        result.append(suggestionText).append("\n");
                    }
                }
            } else {
                result.append("No corrections found.");
            }
        } else {
            result.append("Invalid response format.");
        }

        return result.toString();
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
