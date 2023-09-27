# SpellChecker Class - Detailed Operational Workflow

## Management of the Dictionary

Upon the instantiation of a `SpellChecker` object, several crucial components are initialized:

- An AVL tree (`AVLTree<String>`), referred to as `dictionary`, is established to house the dictionary words efficiently.
- A collection of string similarity algorithms (`similarityAlgorithms`) is introduced, including Levenshtein Distance, Metaphone, Soundex, and Jaro-Winkler.
- A map (`nGramsMap`) is created to record the character n-grams of dictionary words.
- The initial size of n-grams (`nGramSize`) is configured based on user preferences, with a default value of 5.
- An empty list (`path`) is initialized, serving as a tracking mechanism for the search path.

The method `loadDictionary(dictionaryFilename)` takes charge of loading the dictionary from an external file (`dictionary.txt`). This procedure encompasses the subsequent steps:

- A fresh AVL tree (`dictionary`) is initialized, effectively clearing any pre-existing data.
- Words from the dictionary file are read, converted to lowercase to ensure uniformity, and subsequently inserted into the AVL tree.
- For each dictionary word, character n-grams are generated and stored in the `nGramsMap`. This step plays a pivotal role in later calculations for n-gram similarity.

## Spell Checking of Words

Upon submission of a word for spell checking, the `checkWord(word)` method is invoked, which systematically carries out the spell checking process as follows:

- The search path is reset to an empty state using `clearPath()`.
- The process commences at the root of the AVL tree (`dictionary`).
- A comparison is performed between the submitted word and the word stored at the current node. This comparison is executed using the `compareTo` method, a standard procedure accessible for all objects implementing the `Comparable` interface in Java.
- Based on the outcome of this comparison:
  - If the result is less than 0, the search proceeds to the left child node since the submitted word is considered lexicographically smaller.
  - If the result is greater than 0, the search advances to the right child node.
  - If the result is precisely 0, an exact match is detected, signifying the correctness of the spelling.
- Throughout this process, the search path is meticulously documented, ensuring a comprehensive record of traversal.
- The process is conducted recursively until either an exact match is found or all potential branches of the tree have been explored.

## Suggestions for Misspelled Words

In the event an exact match remains elusive after the search, and all avenues within the tree have been exhaustively examined, the `suggestCorrections(word, maxDistance)` method is brought into play. This method employs a spectrum of string similarity algorithms to discern words within the dictionary that bear resemblance to the misspelled word.

### String Similarity Algorithms

For each word in the dictionary, the `suggestCorrections` method undertakes a calculation of similarity scores, leveraging an array of string similarity algorithms, including Levenshtein Distance, Metaphone, Soundex, and Jaro-Winkler. These algorithms assign scores to prospective corrections predicated on their likeness to the misspelled word.

### Ranking and Presentation

The suggested corrections are subjected to a ranking process contingent upon their similarity scores and adherence to the maximum allowable edit distance. The most pertinent suggestions are then presented to the user within the graphical user interface (GUI). Each suggestion encompasses both the corrected word and its associated similarity score.
