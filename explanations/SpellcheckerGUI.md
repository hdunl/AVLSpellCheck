# SpellCheckerGUI Class - Comprehensive Workflow Explanation

## Introduction

The `SpellCheckerGUI` class is the graphical user interface (GUI) component of the spell-checking application developed using JavaFX. This in-depth explanation delves into the intricate details of this class, which plays a pivotal role in the larger spell-checking project.

## Initialization and Dictionary Population

### Constructor

The journey begins with the class constructor. Key components are initialized here:

- **SpellChecker Instance**: An instance of the `SpellChecker` class is created. The initial n-gram size is set to 5. This `SpellChecker` object serves as the core engine for spell-checking and manages the dictionary.

- **User Interface Elements**: Various user interface elements are set up, including:
  - **Sliders and Labels**: Sliders (`maxDistanceSlider` and `maxSuggestionsSlider`) are introduced to control spell-checking parameters. Corresponding labels (`maxDistanceValueLabel` and `maxSuggestionsValueLabel`) display the current slider values.
  - **Text Area and Labels**: A text area (`suggestionsTextArea`) is designated for displaying spelling suggestions or results. Additionally, labels (`statsLabel`) are employed to present pertinent statistics.

### GUI Initialization

The `start` method orchestrates the initialization of the graphical user interface (GUI) for the spell checker. This involves the following steps:

- **Grid Layout**: A `GridPane` is employed to structure the GUI layout effectively. This layout organizes input elements, sliders, buttons, and result displays in a structured manner.

- **Input Fields and Sliders**: The GUI accommodates input fields (`inputWord`) for entering the word to be spell-checked, sliders (`maxDistanceSlider` and `maxSuggestionsSlider`) to configure spell-checking parameters, and labels to display slider values.

- **Check Spelling Button**: A "Check Spelling" button (`checkButton`) is included to trigger the spell-checking process when pressed.

- **Result Display**: The GUI features a text area (`suggestionsTextArea`) to display the results of the spell-checking process, and a `statsLabel` to showcase relevant statistics.

### Slider Listeners

The sliders (`maxDistanceSlider` and `maxSuggestionsSlider`) are equipped with listeners that enable dynamic updates of their values and the corresponding labels as users interact with them.

### PathStage and ListView

A secondary stage (`pathStage`) is established to visualize the search path followed by the spell-checker. This secondary stage comprises a `ListView` (`pathListView`) that enumerates the nodes visited during the spell-checking operation.

## Spell-Checking Process

### User Interaction

1. **User Input**: The spell-checking process is initiated when the user inputs a word to be checked into the designated input field (`inputWord`).

2. **Parameter Adjustment**: Users have the option to fine-tune spell-checking parameters by manipulating the `maxDistance` and `maxSuggestions` sliders.

3. **Triggering Spell-Check**: By clicking the "Check Spelling" button, users initiate the spell-checking operation.

### Error Handling

- If the input word is either empty or contains numerical digits, an error message is presented via an `Alert` dialog, prompting users to enter a valid word.

### Spell-Checking

1. **Spell-Checker Invocation**: The spell-checker (`spellChecker`) is called upon to scrutinize the spelling of the word.

2. **Path Tracking**: Throughout the spell-checking process, the spell-checker keeps tabs on the search path (`path`), maintaining a record of nodes visited.

3. **Execution Time Measurement**: The system measures the time taken for the spell-checking operation, facilitating performance evaluation.

4. **Result Determination**:
   - If the input word is correctly spelled, the GUI displays a message confirming its correctness.
   - In the event of a spelling error, the GUI generates suggestions for corrections.

### Result Presentation

- The results of the spell-checking endeavor, including the spelling correctness of the word, are exhibited in the `suggestionsTextArea`. If the word is found to be incorrect, the GUI provides a list of suggested corrections.

- Concurrently, the time taken for the most recent search is showcased alongside the results.

### Search Path Display

- The search path (`path`) maintained by the spell-checker is revealed in the secondary stage (`pathStage`) through the use of the `pathListView`. This list enumerates the nodes traversed during the spell-checking process.

## Statistics Update

- The `updateStats` method is responsible for updating the statistics displayed in the `statsLabel`. These statistics include the time taken for dictionary population and insights into time complexity.
