# AVL Spell Checker

A Java-based spell checker with various similarity algorithms for word suggestions.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Customization](#customization)

## Introduction

The Spell Checker project is a Java-based spell-checking tool that provides word suggestions for misspelled words. It uses several similarity algorithms, including Levenshtein distance, Metaphone, Soundex, and Jaro-Winkler similarity, to find suggested corrections for misspelled words.

## Features

- Spell checking with multiple similarity algorithms.
- Suggestions sorted by similarity.
- Customizable maximum Levenshtein distance.
- Detailed statistics on dictionary population and search time.
- Easily customizable with additional similarity algorithms.

## Getting Started

To get started with the Spell Checker project, follow these steps:

1. **Prerequisites**: Ensure you have the following prerequisites installed on your system:

   - Java Development Kit (JDK) installed (Java 8 or higher).
   - JavaFX library installed. (JavaFX is included in Oracle JDK 8 and OpenJFX 11 and later.)
   - Apache Commons Text library installed. You can download it from [here](https://commons.apache.org/proper/commons-text/download_text.cgi).
   - Apache Commons Codec library installed. You can download it from [here](https://commons.apache.org/proper/commons-codec/download_codec.cgi).

2. **Clone Repository**: Clone this repository to your local machine.

3. **Compile Java Files**: Compile the Java files in the project directory.

4. **Run Spell Checker GUI**: Run the Spell Checker GUI



## Usage

To use the spell checker, follow these steps:

1. Ensure you have the dictionary file (e.g., `dictionary.txt`) in the project directory.

2. Compile the Java files:

3. Run the Spell Checker GUI:

4. In the GUI, enter a word to check and set the maximum Levenshtein distance and maximum suggestions.

5. Click the "Check Spelling" button to see suggestions and search time statistics.

## Project Structure

The project is organized as follows:

- `SpellChecker.java`: The main spell checker class.
- `SpellCheckerGUI.java`: The JavaFX-based graphical user interface.
- `AVLTree.java`: The AVL Tree data structure implementation.
- `styles.css`: CSS stylesheet for the GUI.
- `dictionary.txt`: Sample dictionary file (you can replace it with your own).

## Customization

You can customize the spell checker by adding or modifying similarity algorithms in the `SpellChecker.java` file. Each similarity algorithm is implemented as a separate class and can be weighted differently for fine-tuning suggestions.

```java
public class CustomSimilarityAlgorithm implements WeightedStringSimilarityAlgorithm {
    // Implement the similarity algorithm here
}
