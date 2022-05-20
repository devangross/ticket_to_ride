package strategy;

/**
 * Generic simple class used to represent a pair of two objects of the same
 * type. Attributes first and second are set on construction and are final. No
 * mutation is allowed.
 */
public class Pair<K> {
    private final K first;
    private final K second;

    /**
     * Construct a pair of two generic objects
     */
    public Pair(K x, K y) {
        this.first = x;
        this.second = y;
    }

    public K getFirst() {
        return this.first;
    }

    public K getSecond() {
        return this.second;
    }
}
