package io.github.eckig.kbk;

import java.util.Objects;
import java.util.Random;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KeyByKey
{
    private static final String CHARS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final Random RANDOM = new Random();

    private final BooleanProperty mIncludeDigits = new SimpleBooleanProperty()
    {
        @Override
        protected void invalidated()
        {
            advance();
        }
    };
    private final BooleanProperty mIncludeUppercase = new SimpleBooleanProperty()
    {
        @Override
        protected void invalidated()
        {
            advance();
        }
    };
    private final StringProperty mCurrent = new SimpleStringProperty();

    private final ObservableList<KeyResult> mResults =
            FXCollections.observableArrayList(e -> new Observable[] {e.rateProperty()});

    KeyByKey()
    {
        advance();
    }

    public ObservableValue<String> nextProperty()
    {
        return mCurrent;
    }

    public void tryNext(String pCharacter)
    {
        final var expected = mCurrent.get();
        final var result =
                mResults.stream().filter(r -> Objects.equals(r.getKey(), expected)).findFirst().orElseGet(() ->
                {
                    final var r = new KeyResult(expected);
                    mResults.add(r);
                    return r;
                });
        if (Objects.equals(expected, pCharacter))
        {
            advance();
            result.logHit();
        }
        else
        {
            result.logMiss();
        }
    }

    private void advance()
    {
        final var txtDigits = mIncludeDigits.get() ? DIGITS : "";
        final var txtUpper = mIncludeUppercase.get() ? CHARS_UPPER : "";
        final var txt = CHARS_UPPER.toLowerCase() + txtUpper + txtDigits;
        final var next = txt.charAt(RANDOM.nextInt(txt.length()));
        mCurrent.set(String.valueOf(next));
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
