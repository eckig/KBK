package io.github.eckig.kbk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public record Key(String key) {

    private static final Map<String, Key> INDEX = new HashMap<>();

    public static Key of(final String pKey)
    {
        return INDEX.computeIfAbsent(pKey, Key::new);
    }

    public Key toUpperCase()
    {
        return of(key().toUpperCase(Locale.ENGLISH));
    }

    public Key toLowerCase()
    {
        return of(key().toLowerCase(Locale.ENGLISH));
    }
}
