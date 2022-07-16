package io.github.eckig.kbk;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KeyByKey
{
    private static final KeyList CHARS_UPPER =
            new KeyList(IntStream.rangeClosed('A', 'Z').mapToObj(c -> new Key(String.valueOf((char) c))).toList(),
                    "Uppercase");
    private static final KeyList CHARS_LOWER =
            new KeyList(IntStream.rangeClosed('a', 'z').mapToObj(c -> new Key(String.valueOf((char) c))).toList(),
                    "Lowercase");
    private static final KeyList DIGITS =
            new KeyList(IntStream.rangeClosed(0, 9).mapToObj(c -> new Key(String.valueOf(c))).toList(), "Digits");
    private static final KeyList SYMBOLS =
            new KeyList(List.of(new Key("("), new Key(")"), new Key("{"), new Key("}"), new Key("["), new Key("]"),
                    new Key("<"), new Key(">"), new Key("."), new Key(","), new Key(";"), new Key(":"), new Key("?"),
                    new Key("/"), new Key("!"), new Key("&"), new Key("="), new Key("+"), new Key("-")), "Symbols");

    private static final List<KeyList> LIST = List.of(CHARS_UPPER, CHARS_LOWER, DIGITS, SYMBOLS);
    private final WeightedRandomKeySelector mKeySelector = new WeightedRandomKeySelector();
    private final ObjectProperty<Key> mCurrent = new SimpleObjectProperty<>();

    private final ObservableList<KeyResult> mResults =
            FXCollections.observableArrayList(e -> new Observable[] {e.rateProperty()});

    KeyByKey()
    {
        LIST.forEach(this::register);
        CHARS_LOWER.activeProperty().set(true);
        advance();
    }

    private void register(final KeyList pKeyList)
    {
        pKeyList.activeProperty().addListener((w, o, n) ->
        {
            if (n)
            {
                mKeySelector.addKeys(pKeyList);
            }
            else
            {
                mKeySelector.removeKeys(pKeyList);
            }
            advance();
        });
    }

    public ObjectProperty<Key> nextProperty()
    {
        return mCurrent;
    }

    public void tryNext(String pCharacter)
    {
        final var expected = mCurrent.get();
        final var result =
                mResults.stream()
                        .filter(r -> Objects.equals(r.getKey(), expected))
                        .findFirst()
                        .orElseGet(() ->
                        {
                            final var r = new KeyResult(expected);
                            mResults.add(r);
                            r.rateProperty().addListener((w, o, n) -> hitRateChangedOn(expected, n));
                            return r;
                        });
        if (Objects.equals(expected.key(), pCharacter))
        {
            advance();
            result.logHit();
        }
        else
        {
            result.logMiss();
        }
    }

    private void hitRateChangedOn(Key pKey, Number pRate)
    {
        final double rate = pRate == null ? 0.0 : pRate.doubleValue();
        if (rate < 0.5)
        {
            mKeySelector.setWeight(pKey, Priority.HIGH);
        }
        else if (rate < 1.0)
        {
            mKeySelector.setWeight(pKey, Priority.MID);
        }
        else
        {
            mKeySelector.setWeight(pKey, Priority.NORMAL);
        }
    }

    private void advance()
    {
        final Key prev = mCurrent.get();
        Key next;
        do
        {
            next = mKeySelector.selectNext();
        }
        while (next.equals(prev));
        mCurrent.set(next);
    }

    public List<KeyList> getKeys()
    {
        return LIST;
    }

    public ObservableList<KeyResult> getResults()
    {
        return mResults;
    }
}
