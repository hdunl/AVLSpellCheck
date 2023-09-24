import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;

/**
 * A spell checker that uses various similarity algorithms for word suggestions.
 */
@SuppressWarnings({"CallToPrintStackTrace", "resource"})
public class SpellChecker {
    private AVLTree<String> dictionary;
    private final List<WeightedStringSimilarityAlgorithm> weightedSimilarityAlgorithms;
    private final int nGramSize;
    private final Map<String, List<String>> nGramsMap;

    /**
     * Creates a SpellChecker with an initial N-gram size.
     *
     * @param initialNGramSize The initial size of N-grams to use.
     */
    public SpellChecker(int initialNGramSize) {
        dictionary = new AVLTree<>();
        weightedSimilarityAlgorithms = new ArrayList<>();
        weightedSimilarityAlgorithms.add(new LevenshteinDistanceAlgorithm());
        weightedSimilarityAlgorithms.add(new MetaphoneAlgorithm());
        weightedSimilarityAlgorithms.add(new SoundexAlgorithm());
        weightedSimilarityAlgorithms.add(new JaroWinklerAlgorithm());
        nGramsMap = new HashMap<>();
        nGramSize = initialNGramSize;
    }

    /**
     * Sets the maximum Levenshtein distance for similarity algorithms.
     *
     * @param maxDistance The maximum Levenshtein distance.
     */
    public void setMaxDistance(int maxDistance) {
        for (WeightedStringSimilarityAlgorithm algorithm : weightedSimilarityAlgorithms) {
            algorithm.setMaxDistance(maxDistance);
        }
    }

    /**
     * Loads a dictionary file and populates the spell checker's dictionary.
     *
     * @param dictionaryFilename The name of the dictionary file to load.
     */
    public void loadDictionary(String dictionaryFilename) {
        dictionary = new AVLTree<>(); // Clear the dictionary
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                // Update N-grams for each word in the dictionary
                List<String> nGrams = generateCharacterNGrams(line, nGramSize);
                nGramsMap.put(line, nGrams); // Store N-grams for this word
                dictionary.insert(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a word is in the loaded dictionary.
     *
     * @param word The word to check.
     * @return true if the word is in the dictionary, false otherwise.
     */
    public boolean checkWord(String word) {
        return dictionary.search(word.toLowerCase());
    }

    /**
     * Suggests corrections for a misspelled word.
     *
     * @param word        The misspelled word to suggest corrections for.
     * @param maxDistance The maximum Levenshtein distance for suggestions.
     * @return A list of suggested corrections, sorted by similarity.
     */
    public List<String> suggestCorrections(String word, int maxDistance) {
        Map<String, Double> suggestions = new ConcurrentHashMap<>();

        ForkJoinPool pool = new ForkJoinPool();
        pool.submit(() ->
                traverseDictionaryInOrder(dictionary.getRoot(), word, maxDistance, suggestions)
        ).join();

        return suggestions.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void traverseDictionaryInOrder(AVLTree<String>.Node node, String word, int maxDistance, Map<String, Double> suggestions) {
        if (node != null) {
            traverseDictionaryInOrder(node.left, word, maxDistance, suggestions);
            String dictWord = node.data;

            for (WeightedStringSimilarityAlgorithm algorithm : weightedSimilarityAlgorithms) {
                double similarity = algorithm.calculateSimilarity(word.toLowerCase(), dictWord);
                int levenshteinDistance = LevenshteinDistance.getDefaultInstance().apply(word.toLowerCase(), dictWord);

                double similarityThreshold = 0.9999;
                if (similarity >= similarityThreshold && levenshteinDistance <= maxDistance) {
                    suggestions.putIfAbsent(dictWord, 0.0);

                    similarity *= algorithm.getWeight(); // Apply weight

                    List<String> nGrams = nGramsMap.get(dictWord.toLowerCase());
                    double nGramSimilarity = calculateNGramSimilarity(word.toLowerCase(), nGrams); // Pass nGramSize here
                    similarity = (similarity + nGramSimilarity) / 2.0;

                    suggestions.put(dictWord, suggestions.get(dictWord) + similarity);
                }
            }

            traverseDictionaryInOrder(node.right, word, maxDistance, suggestions);
        }
    }

    private double calculateNGramSimilarity(String word, List<String> dictionaryNGrams) {
        List<String> wordNGrams = generateCharacterNGrams(word, this.nGramSize);

        if (dictionaryNGrams == null) {
            // Handle the case where dictionaryNGrams is null
            return 0.0; // or any other appropriate value
        }

        Set<String> intersection = new HashSet<>(wordNGrams);
        intersection.retainAll(dictionaryNGrams);

        Set<String> union = new HashSet<>(wordNGrams);
        union.addAll(dictionaryNGrams);

        return (double) intersection.size() / union.size();
    }

    private List<String> generateCharacterNGrams(String word, int n) {
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= word.length() - n; i++) {
            nGrams.add(word.substring(i, i + n));
        }
        return nGrams;
    }

    public static class MetaphoneAlgorithm implements WeightedStringSimilarityAlgorithm {
        private final double weight;
        private final Metaphone metaphone;

        public MetaphoneAlgorithm() {
            weight = 0.5; // Adjust the weight as needed
            metaphone = new Metaphone();
        }

        @Override
        public double calculateSimilarity(String s1, String s2) {
            String metaphone1 = metaphone.encode(s1);
            String metaphone2 = metaphone.encode(s2);

            return metaphone1.equals(metaphone2) ? 1.0 : 0.0;
        }

        @Override
        public void setMaxDistance(int maxDistance) {
        }

        @Override
        public double getWeight() {
            return weight;
        }
    }

    public static class SoundexAlgorithm implements WeightedStringSimilarityAlgorithm {
        private final double weight;
        private final Soundex soundex;

        public SoundexAlgorithm() {
            weight = 0.7; // Adjust the weight as needed
            soundex = new Soundex();
        }

        @Override
        public double calculateSimilarity(String s1, String s2) {
            String soundex1 = soundex.encode(s1);
            String soundex2 = soundex.encode(s2);

            return soundex1.equals(soundex2) ? 1.0 : 0.0;
        }

        @Override
        public void setMaxDistance(int maxDistance) {
        }

        @Override
        public double getWeight() {
            return weight;
        }
    }

    public static class JaroWinklerAlgorithm implements WeightedStringSimilarityAlgorithm {
        private final double weight;

        public JaroWinklerAlgorithm() {
            weight = 0.9; // Adjust the weight as needed
        }

        @Override
        public double calculateSimilarity(String s1, String s2) {
            JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
            return similarity.apply(s1, s2);
        }

        @Override
        public void setMaxDistance(int maxDistance) {
            // Not used for JaroWinkler
        }

        @Override
        public double getWeight() {
            return weight;
        }
    }

    // Adapter for LevenshteinDistance
    public static class LevenshteinDistanceAlgorithm implements WeightedStringSimilarityAlgorithm {
        private final double weight;

        public LevenshteinDistanceAlgorithm() {
            weight = 0.8; // Adjust the weight as needed
        }

        @Override
        public double calculateSimilarity(String s1, String s2) {
            LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();
            int distanceValue = distance.apply(s1, s2);
            // Invert the distance to make it a similarity score
            return 1.0 / (1.0 + distanceValue);
        }

        @Override
        public void setMaxDistance(int maxDistance) {
        }

        @Override
        public double getWeight() {
            return weight;
        }
    }

    public interface WeightedStringSimilarityAlgorithm extends StringSimilarityAlgorithm {
        double getWeight();
    }
}
