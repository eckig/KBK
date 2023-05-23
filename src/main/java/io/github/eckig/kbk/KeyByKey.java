package io.github.eckig.kbk;

import java.util.Objects;

import io.github.eckig.kbk.result.KeyResult;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


public class KeyByKey
{

    private final WeightedRandomKeySelector mKeySelector = new WeightedRandomKeySelector();
    private final ObjectProperty<Key> mCurrent = new SimpleObjectProperty<>();

    private final ObservableMap<String, KeyResult> mResults = FXCollections.observableHashMap();

    KeyByKey()
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

    public void tryNext(String pCharacter)
    {
        final var expected = mCurrent.get();
        final var result = getResult(expected);
        if (Objects.equals(expected.key(), pCharacter))
        {
            result.logHit();
            advance();
        }
        else
        {
            // record miss on both the missed key press and the one pressed instead:
            getResult(pCharacter).logMiss();
            result.logMiss();
        }
    }

    public KeyResult getResult(final String pKey)
    {
        return mResults.computeIfAbsent(pKey, c -> {
            final Key key = Key.of(c);
            final var r = new KeyResult(key);
            r.rateProperty().addListener((w, o, n) -> hitRateChangedOn(key, n));
            return r;
        });
    }

    public KeyResult getResult(final Key pKey)
    {
        return mResults.computeIfAbsent(pKey.key(), c -> {
            final var r = new KeyResult(pKey);
            r.rateProperty().addListener((w, o, n) -> hitRateChangedOn(pKey, n));
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
        mCurrent.set(next);
    }
}
