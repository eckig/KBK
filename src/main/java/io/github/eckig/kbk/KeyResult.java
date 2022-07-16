package io.github.eckig.kbk;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

public class KeyResult
{
    private final ObservableValue<Key> mKey;
    private int mHits;
    private int mTotal;
    private final DoubleProperty mHitRate = new SimpleDoubleProperty();

    KeyResult(final Key pKey)
    {
        mKey = new ReadOnlyObjectPropertyBase<>()
        {
            @Override
            public Object getBean()
            {
                return this;
            }

            @Override
            public String getName()
            {
                return "key";
            }

            @Override
            public Key get()
            {
                return pKey;
            }
        };
    }

    public ObservableValue<Key> keyProperty()
    {
        return mKey;
    }

    public ReadOnlyDoubleProperty rateProperty()
    {
        return mHitRate;
    }

    public Key getKey()
    {
        return mKey.getValue();
    }

    public void logHit()
    {
        mHits++;
        mTotal++;
        update();
    }

    private void update()
    {
        final double rate = (double) mHits / mTotal;
        mHitRate.set(rate);
    }

    public void logMiss()
    {
        mTotal++;
        update();
    }
}
