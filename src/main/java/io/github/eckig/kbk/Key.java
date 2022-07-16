package io.github.eckig.kbk;

import java.util.Locale;
import java.util.Objects;

public record Key(String key)
{
    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Key key1 = (Key) o;
        return equalsIgnoreCase(key, key1.key);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key);
    }

    @Override
    public String toString()
    {
        return key;
    }

    private static boolean equalsIgnoreCase(String a, String b)
    {
        return a != null && a.equalsIgnoreCase(b);
    }
}
