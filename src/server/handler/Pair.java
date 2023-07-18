package server.handler;

import java.util.Objects;

public class Pair<U, V> {
    public final U key;
    public final V value;
    public Pair(U first, V second)
    {
        this.key = first;
        this.value = second;
    }

    public U getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
    public static <U, V> Pair <U, V> of(U a, V b)
    {
        return new Pair<>(a, b);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (!Objects.equals(key, pair.key)) return false;
        return Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
