import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;
import com.cedarsoftware.util.StringUtilities;

public class SpellChecker {
    private AVLTree<String> dictionary;
    private final List<StringSimilarityAlgorithm> similarityAlgorithms;
    private final int nGramSize;
    private final Map<String, List<String>> nGramsMap;
    protected List<String> path;

    public SpellChecker(int initialNGramSize) {
        dictionary = new AVLTree<>();
        similarityAlgorithms = new ArrayList<>();
        similarityAlgorithms.add(new DamerauLevenshteinAdapter());
        similarityAlgorithms.add(new MetaphoneAlgorithm());
        similarityAlgorithms.add(new SoundexAlgorithm());
        similarityAlgorithms.add(new JaroWinklerAlgorithm());
        nGramsMap = new HashMap<>();
        nGramSize = initialNGramSize;
        path = new ArrayList<>(); // Initialize the 'path' list
    }

    // Load the dictionary from a file
    public void loadDictionary(String dictionaryFilename) {
        dictionary = new AVLTree<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                List<String> nGrams = generateCharacterNGrams(line, nGramSize);
                nGramsMap.put(line, nGrams);
                dictionary.insert(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if a word is in the dictionary
    public boolean checkWord(String word) {
        // Reset the path for each new word check
        path.clear();
        return searchWord(dictionary.getRoot(), word.toLowerCase());
    }

    // Recursive method to search for a word in the dictionary
    private boolean searchWord(AVLTree<String>.Node node, String word) {
        if (node == null) {
            return false;
        }

        String dictWord = node.data;
        path.add(dictWord); // Add the current node to the search path

        int cmp = word.compareTo(dictWord);

        if (cmp < 0) {
            return searchWord(node.left, word);
        } else if (cmp > 0) {
            return searchWord(node.right, word);
        } else {
            return true;
        }
    }

    // Suggest corrections for a misspelled word
    public List<Map.Entry<String, Double>> suggestCorrections(String word, long maxSuggestions) {
        Map<String, Double> suggestions = new ConcurrentHashMap<>();

        ForkJoinPool pool = new ForkJoinPool();
        pool.submit(() ->
                traverseDictionaryInOrder(dictionary.getRoot(), word, suggestions)
        ).join();

        return suggestions.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(maxSuggestions) // Limit to a certain number of suggestions
                .collect(Collectors.toList());
    }

    // Clear the path
    public void clearPath() {
        path.clear();
    }

    // Get the search path
    public List<String> path() {
        return path;
    }

    // Traverse the dictionary in order and calculate word similarities
    private void traverseDictionaryInOrder(AVLTree<String>.Node node, String word, Map<String, Double> suggestions) {
        if (node != null) {
            String dictWord = node.data;

            double totalWeightedScore = 0.0;
            double totalWeight = 0.0;

            for (StringSimilarityAlgorithm algorithm : similarityAlgorithms) {
                double similarity = algorithm.calculateSimilarity(word.toLowerCase(), dictWord);

                double similarityThreshold = 0.7;
                if (similarity >= similarityThreshold) {
                    double weight = getWeightForAlgorithm(algorithm);
                    totalWeightedScore += similarity * (weight);
                    totalWeight += weight;
                }
            }

            if (totalWeight > 0) {
                double normalizedScore = totalWeightedScore / totalWeight;
                normalizedScore = Math.min(normalizedScore, 1.0); // Clamp the score to max 1.0

                List<String> nGrams = nGramsMap.get(dictWord.toLowerCase());
                double nGramSimilarity = calculateNGramSimilarity(word.toLowerCase(), nGrams);
                normalizedScore = (normalizedScore + nGramSimilarity) / 2.0; // Combine with nGram similarity

                suggestions.put(dictWord, Math.max(suggestions.getOrDefault(dictWord, 0.0), normalizedScore));
            }

            traverseDictionaryInOrder(node.left, word, suggestions);
            traverseDictionaryInOrder(node.right, word, suggestions);
        }
    }


    private double getWeightForAlgorithm(StringSimilarityAlgorithm algorithm) {
        if (algorithm instanceof DamerauLevenshteinAdapter) {
            return 0.9;
        } else if (algorithm instanceof MetaphoneAlgorithm) {
            return 0.7;
        } else if (algorithm instanceof SoundexAlgorithm) {
            return 0.6;
        } else if (algorithm instanceof JaroWinklerAlgorithm) {
            return 0.9;
        } else {
            return 1.0; // Default weight
        }
    }

    // Calculate n-gram similarity between two words
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

    // Generate character n-grams for a word
    private List<String> generateCharacterNGrams(String word, int n) {
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= word.length() - n; i++) {
            nGrams.add(word.substring(i, i + n));
        }
        return nGrams;
    }

    // Metaphone's similarity algorithm
    public static class MetaphoneAlgorithm implements StringSimilarityAlgorithm {
        private final Metaphone metaphone;

        public MetaphoneAlgorithm() {
            metaphone = new Metaphone();
        }

        @Override
        public double calculateSimilarity(String s1, String s2) {
            String metaphone1 = metaphone.encode(s1);
            String metaphone2 = metaphone.encode(s2);

            return metaphone1.equals(metaphone2) ? 1.0 : 0.0;
        }


    }

    // Soundex similarity algorithm
    public static class SoundexAlgorithm implements StringSimilarityAlgorithm {
        private final Soundex soundex;

        public SoundexAlgorithm() {
            soundex = new Soundex();
        }

        @Override
        public double calculateSimilarity(String s1, String s2) {
            String soundex1 = soundex.encode(s1);
            String soundex2 = soundex.encode(s2);

            return soundex1.equals(soundex2) ? 1.0 : 0.0;
        }


    }

    // Jaro-Winkler similarity algorithm
    public static class JaroWinklerAlgorithm implements StringSimilarityAlgorithm {
        @Override
        public double calculateSimilarity(String s1, String s2) {
            JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
            return similarity.apply(s1, s2);
        }
    }

    // Adapter for Damerau-Levenshtein Distance
    public static class DamerauLevenshteinAdapter implements StringSimilarityAlgorithm {
        @Override
        public double calculateSimilarity(String s1, String s2) {
            int distance = StringUtilities.damerauLevenshteinDistance(s1, s2);
            // Convert distance to similarity score
            // For simplicity, assuming the max length of s1 or s2 as the worst case
            int maxLength = Math.max(s1.length(), s2.length());
            return maxLength > 0 ? 1.0 - ((double) distance / maxLength) : 1.0;
        }

}}
