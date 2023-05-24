package io.github.eckig.kbk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public record Key(String key, boolean upperCase) {

    private static final Map<String, Key> INDEX = new HashMap<>();

    private static Key of(final String pKey, final boolean pUpperCase)
    {
        if (pKey.isBlank())
        {
            throw new IllegalArgumentException("Can not create empty key!");
        }
        return INDEX.computeIfAbsent(pKey, k -> new Key(k, pUpperCase));
    }

    public static Key of(final String pKey)
    {
        return of(pKey, false);
    }

    public Key toUpperCase()
    {
        return of(key().toUpperCase(Locale.ENGLISH), true);
    }

    public boolean matchesIgnoreCase(final Key pKey)
    {
        return pKey != null && key().equalsIgnoreCase(pKey.key());
    }
}
