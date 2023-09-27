# SpellCheckerGUI Class - In-Depth Workflow Explanation

## Introduction

The `SpellCheckerGUI` class is a graphical user interface (GUI) application built on JavaFX that serves as the front-end for a spell-checking system. This detailed explanation provides insights into the inner workings of this class, which is a critical component of the broader spell-checking project.

## Initialization and Dictionary Population

### Constructor

The `SpellCheckerGUI` class starts by initializing several essential components:

- **SpellChecker**: An instance of the `SpellChecker` class is created, setting the initial n-gram size to 5. This `SpellChecker` object handles the core spell-checking functionality and maintains the dictionary.

- **Sliders and Labels**: Various GUI elements, including sliders (`maxDistanceSlider` and `maxSuggestionsSlider`) and corresponding labels (`maxDistanceValueLabel` and `maxSuggestionsValueLabel`), are set up to control spell-checking parameters.

- **Text Area and Labels**: A text area (`suggestionsTextArea`) is prepared to display spelling suggestions or results, while labels (`statsLabel`) will show relevant statistics.

### GUI Initialization

The `start` method initializes the graphical user interface (GUI) for the spell checker. It includes:

- **Grid Layout**: The GUI layout is created using a `GridPane`. This layout is structured to organize input elements, sliders, buttons, and result displays effectively.

- **Input Fields and Sliders**: It includes input fields (`inputWord`) for the word to be checked, sliders (`maxDistanceSlider` and `maxSuggestionsSlider`) for setting spell-checking parameters, and corresponding labels for slider values.

- **Check Spelling Button**: A "Check Spelling" button (`checkButton`) triggers the spell-checking process when pressed.

- **Result Display**: A text area (`suggestionsTextArea`) is provided to display the results of the spell-checking process, and a `statsLabel` displays relevant statistics.

### Slider Listeners

The sliders (`maxDistanceSlider` and `maxSuggestionsSlider`) are equipped with listeners to update their values and corresponding labels dynamically as the user interacts with them.

### PathStage and ListView

A secondary stage (`pathStage`) is created to display the search path taken by the spell-checker. It contains a `ListView` (`pathListView`) to list the nodes visited during the spell-checking process.

## Spell-Checking Process

### User Interaction

1. The user enters a word to be checked in the input field (`inputWord`).

2. The user can adjust the `maxDistance` and `maxSuggestions` sliders to configure spell-checking parameters.

3. Clicking the "Check Spelling" button initiates the spell-checking process.

### Error Handling

- If the input word is empty or contains digits, an error message is displayed using an `Alert` dialog.

### Spell-Checking

1. The spell-checker (`spellChecker`) is invoked to check the word's spelling.

2. The spell-checker maintains a search path (`path`) to keep track of visited nodes during the search process.

3. The system measures the time taken for the spell-checking operation.

4. Depending on the outcome:
   - If the word is spelled correctly, a message indicating correctness is displayed.
   - If the word is spelled incorrectly, suggestions for corrections are provided.

### Result Presentation

- The results are displayed in the `suggestionsTextArea`, including the word's correctness and, if incorrect, suggested corrections.

- The time taken for the recent search is displayed alongside the results.

### Search Path Display

- The search path (`path`) is displayed in the secondary stage (`pathStage`) using the `pathListView`.

## Statistics Update

- The `updateStats` method is responsible for updating the statistics displayed in the `statsLabel`. This includes the time taken for dictionary population and information about time complexity.

## Conclusion

The `SpellCheckerGUI` class facilitates user interaction, manages the spell-checking process, and provides a user-friendly interface for utilizing the spell-checker's functionality.

## Entry Point

The `main` method serves as the entry point for the GUI application, invoking the `launch` method to start the JavaFX application.

