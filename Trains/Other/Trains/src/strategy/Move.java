package strategy;

import map.DirectConnection;

/**
 * Class to represent a Move by a Player which is one of: - DirectConnection
 * object representing connection to attempt to acquire - boolean - True if
 * requesting to be dealt two color cards
 */
public class Move<T> {

    private T field;
    private Class<T> type;

    public T getMove() {
        return this.field;
    }

    public void setMove(T field) {
        if (field instanceof Boolean) {
            Boolean test = (Boolean) field;
            if (!test) {
                throw new IllegalArgumentException("Boolean must be true");
            }
            this.field = field;
        } else if (field instanceof DirectConnection) {
            this.field = field;
        } else {
            throw new IllegalArgumentException(
                    "Move must be one of DirectConnection or true");
        }
    }

    /**
     * Overridden equality method to compare Moves
     *
     * @param o other Move?
     *
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Move) {
            Move<T> other = (Move<T>) o;
            return this.field == other.field;
        }
        return false;
    }

    public String toString() {
        return this.getMove().toString();
    }

}
