package io.github.eckig.kbk.impl;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import io.github.eckig.kbk.Key;
import io.github.eckig.kbk.KeyList;
import io.github.eckig.kbk.result.KeyResult;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


public class KeyByKey
{

    private final BooleanProperty mUppercase = new SimpleBooleanProperty();
    private final WeightedRandomKeySelector mKeySelector = new WeightedRandomKeySelector();
    private final ObjectProperty<Key> mCurrent = new SimpleObjectProperty<>();
    private final Random mRandom = new Random();

    private final ObservableMap<String, KeyResult> mResults = FXCollections.observableHashMap();

    public KeyByKey()
    {
        KeyList.DEFAULTS.forEach(this::register);
        advance();
    }

    private void register(final KeyList pKeyList)
    {
        pKeyList.activeProperty().addListener((w, o, n) -> keyListActiveChanged(pKeyList, n));
        keyListActiveChanged(pKeyList, pKeyList.activeProperty().get());
    }

    private void keyListActiveChanged(final KeyList pKeyList, final boolean pActive)
    {
        if (pActive)
        {
            mKeySelector.addKeys(pKeyList);
        }
        else
        {
            mKeySelector.removeKeys(pKeyList);
        }
        advance();
    }

    public ObjectProperty<Key> nextProperty()
    {
        return mCurrent;
    }

    public BooleanProperty upperCaseProperty()
    {
        return mUppercase;
    }

    public void tryNext(String pCharacter)
    {
        final var expected = mCurrent.get();
        final var result = getResult(expected.key());
        if (Objects.equals(expected.key(), pCharacter))
        {
            result.logHit();
            advance();
        }
        else
        {
            // record miss on both the missed key press and the one pressed instead:
            if (pCharacter != null && !pCharacter.isBlank())
            {
                getResult(pCharacter).logMiss();
            }
            result.logMiss();
        }
    }

    public KeyResult getResult(final String pKey)
    {
        return mResults.computeIfAbsent(pKey, c -> {
            final Key key = Key.of(c.toLowerCase(Locale.ENGLISH));
            final var r = new KeyResult(key);
            r.rateProperty().addListener((w, o, n) -> hitRateChangedOn(key, n));
            return r;
        });
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
        if (mUppercase.get() && mRandom.nextBoolean())
        {
            next = next.toUpperCase();
        }
        mCurrent.set(next);
    }
}
