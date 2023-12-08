# Spell Checker with Various Similarity Algorithms

This is a Java-based spell-checker application that uses multiple similarity algorithms to suggest corrections for misspelled words. It provides an easy-to-use graphical user interface (GUI) for checking the spelling of words.

## Write-ups for each class
- [spellchecker.AVLTree.java](explanations/spellchecker.AVLTree.md)
- [Spellchecker.java](explanations/Spellchecker.md)
- [SpellcheckerGUI.java](explanations/SpellcheckerGUI.md)
- [String Similarity Algorithm](explanations/StringSimilarityAlgorithm.md)

## Features

- Check the spelling of words using various similarity algorithms.
- Suggest corrections for misspelled words based on Levenshtein distance, Metaphone, Soundex, and Jaro-Winkler similarity.
- Adjustable parameters for maximum Levenshtein distance and the number of suggestions to display.
- Detailed statistics on dictionary population time and search complexity.
- Utilizes an efficient AVL Tree data structure for dictionary storage.
- Supports loading custom dictionaries.
- Displays path taken by the program after each search

## Prerequisites

Before running the Spell Checker GUI, ensure you have the following prerequisites installed on your system:

- Java Development Kit (JDK) 8 or later
- JavaFX (included with JDK 8, separate download for later versions)
- Apache Commons Codec Library 1.16.0
- Apache Commons Lang3 Library 3.13.0
- Apache Commons Text Library 1.10.0

You can find the required Apache libraries in the repository under the following filenames:

- `commons-codec-1.16.0.jar`
- `commons-lang3-3.13.0.jar`
- `commons-text-1.10.0.jar`

Make sure to include these JAR files in your project's classpath.

## Usage

1. Launch the Spell Checker GUI.
2. Enter a word in the "Enter a word" text field.
3. Adjust the "Max Distance" slider to set the maximum Levenshtein distance for similarity algorithms.
4. Use the "Max Suggestions" slider to control the number of suggestions displayed.
5. Click the "Check Spelling" button to check the spelling of the word.
6. The application will display suggestions and statistics in the text area below.

## Dictionary

The application comes with a default dictionary file named `dictionary.txt`. You can replace this file with your own custom dictionary if needed. Each line in the dictionary file should contain a valid word to be used for spell-checking.

## AVL Tree

This project efficiently utilizes an AVL Tree data structure to store the dictionary. AVL Trees are self-balancing binary search trees, ensuring that searches for words are performed in O(log N) time complexity, making the spell checker fast and efficient.

## License

This project is licensed under the MIT License

## Acknowledgments

This project uses the Apache Commons Codec, Lang3, and Text libraries for string similarity calculations.
