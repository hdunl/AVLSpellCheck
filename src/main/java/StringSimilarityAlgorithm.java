public interface StringSimilarityAlgorithm {
    double calculateSimilarity(String s1, String s2);
    void setMaxDistance(int maxDistance);
}
