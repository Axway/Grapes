package org.axway.grapes.commons.datamodel;

/**
 * Decided to use generic Pair class instead of multivalued map. It is more structured and easy to add and to extract afterwards compared to multivalued map
 * <p>
 * Pair class used to collect pair.
 */
public class Pair<T, U> {

    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }


    public U getSecond() {
        return second;
    }

    public static <T, U> Pair<T, U> create(T first, U second) {
        return new Pair<T, U>(first, second);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Pair) {
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(first);
        sb.append(second);

        return sb.toString().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(first);
        sb.append(" (");
        sb.append(second);
        sb.append(")");

        return sb.toString();
    }
}
