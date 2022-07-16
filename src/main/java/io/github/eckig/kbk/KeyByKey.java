package io.github.eckig.kbk;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KeyByKey
{
    private static final List<Key> CHARS_UPPER =
            IntStream.rangeClosed('A', 'Z').mapToObj(c -> new Key(String.valueOf((char) c))).toList();
    private static final List<Key> CHARS_LOWER =
            IntStream.rangeClosed('a', 'z').mapToObj(c -> new Key(String.valueOf((char) c)))
                    .toList();
    private static final List<Key> DIGITS =
            IntStream.rangeClosed(0, 9).mapToObj(c -> new Key(String.valueOf(c))).toList();
    private final WeightedRandomKeySelector mKeySelector = new WeightedRandomKeySelector();
    private final BooleanProperty mIncludeDigits = new SimpleBooleanProperty()
    {
        @Override
        protected void invalidated()
        {
            if (get())
            {
                mKeySelector.addKeys(DIGITS);
            }
            else
            {
                mKeySelector.removeKeys(DIGITS);
            }
            advance();
        }
    };
    private final BooleanProperty mIncludeUppercase = new SimpleBooleanProperty()
    {
        @Override
        protected void invalidated()
        {
            if (get())
            {
                mKeySelector.addKeys(CHARS_UPPER);
            }
            else
            {
                mKeySelector.removeKeys(CHARS_UPPER);
            }
            advance();
        }
    };
    private final ObjectProperty<Key> mCurrent = new SimpleObjectProperty<>();


    private final ObservableList<KeyResult> mResults =
            FXCollections.observableArrayList(e -> new Observable[] {e.rateProperty()});

    KeyByKey()
    {
        mKeySelector.addKeys(CHARS_LOWER);
        advance();
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

    public Property<Boolean> includeUppercaseProperty()
    {
        return mIncludeUppercase;
    }

    public Property<Boolean> includeNumbersProperty()
    {
        return mIncludeDigits;
    }

    public ObservableList<KeyResult> getResults()
    {
        return mResults;
    }
}
