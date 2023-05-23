package io.github.eckig.kbk;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class KeyList
{

    public static final KeyList CHARS_LOWER = new KeyList(
            IntStream.rangeClosed('a', 'z').mapToObj(c -> Key.of(String.valueOf((char) c))).toList(), "Lowercase", true);
    private static final KeyList DIGITS = new KeyList(IntStream.rangeClosed(0, 9).mapToObj(c -> Key.of(String.valueOf(c))).toList(),
            "Digits");
    private static final KeyList SYMBOLS = new KeyList(
            Stream.of("(", ")", "{", "}", "[", "]", "<", ">", ".", ",", ";", ":", "?", "/", "!", "&", "=", "+", "-", "'", "|").map(Key::of)
                    .toList(),
            "Symbols");

    public static final List<KeyList> DEFAULTS = List.of(CHARS_LOWER, DIGITS, SYMBOLS);
    private final List<Key> mKeys;
    private final BooleanProperty mActive = new SimpleBooleanProperty();
    private final String mLabel;

    public KeyList(final List<Key> pKeys, final String pLabel)
    {
        this(pKeys, pLabel, false);
    }

    public KeyList(final List<Key> pKeys, final String pLabel, final boolean pDefaultActive)
    {
        mKeys = pKeys;
        mLabel = pLabel;
        mActive.set(pDefaultActive);
    }

    public BooleanProperty activeProperty()
    {
        return mActive;
    }

    public List<Key> getKeys()
    {
        return mKeys;
    }

    public String getLabel()
    {
        return mLabel;
    }

    public boolean contains(final Key pKey)
    {
        // TODO quick hack...
        if (this == CHARS_LOWER)
        {
            return mKeys.contains(pKey) || mKeys.contains(pKey.toLowerCase());
        }
        else
        {
            return mKeys.contains(pKey);
        }
    }
}
