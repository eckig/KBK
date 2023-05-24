package io.github.eckig.kbk.result;

import io.github.eckig.kbk.Key;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public final class KeyResult implements IKeyResult
{

    private final Key mKey;
    int mHits = 0;
    int mTotal = 0;
    private final DoubleProperty mHitRate = new SimpleDoubleProperty();

    public KeyResult(final Key pKey)
    {
        mKey = pKey;
    }

    public ReadOnlyDoubleProperty rateProperty()
    {
        return mHitRate;
    }

    public Key getKey()
    {
        return mKey;
    }

    public void logHit()
    {
        mHits++;
        mTotal++;
        update();
    }

    private void update()
    {
        final double rate = (double) mHits / (double) mTotal;
        mHitRate.set(rate);
    }

    @Override
    public boolean matchesIgnoreCase(final Key pKey)
    {
        return mKey.matchesIgnoreCase(pKey);
    }

    @Override
    public String toString()
    {
        return "KeyResult{" + "mKey=" + mKey + ", mHits=" + mHits + ", mTotal=" + mTotal + ", mHitRate=" + mHitRate.get() + '}';
    }

    public void logMiss()
    {
        mTotal++;
        update();
    }
}
