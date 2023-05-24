package io.github.eckig.kbk;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class KeyList implements Iterable<Key>
{

    private static final KeyList CHARS_LOWER = new KeyList(
            IntStream.rangeClosed('a', 'z').mapToObj(c -> Key.of(String.valueOf((char) c))).toList(), "Alphabet", true);
    private static final KeyList DIGITS = new KeyList(IntStream.rangeClosed(0, 9).mapToObj(String::valueOf).map(Key::of).toList(),
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

    public String getLabel()
    {
        return mLabel;
    }

    public boolean contains(final Key pKey)
    {
        return mKeys.contains(pKey);
    }

    @Override
    public Iterator<Key> iterator()
    {
        return mKeys.iterator();
    }

    @Override
    public Spliterator<Key> spliterator()
    {
        return mKeys.spliterator();
    }

    public Stream<Key> stream()
    {
        return mKeys.stream();
    }
}
