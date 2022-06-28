package io.github.eckig.kbk;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

public class KeyResult
{
    private final ObservableValue<String> mKey;
    private int mHits;
    private int mTotal;
    private final DoubleProperty mHitRate = new SimpleDoubleProperty();

    KeyResult(final String pKey)
    {
        mKey = new ReadOnlyStringPropertyBase()
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
            public String get()
            {
                return pKey;
            }
        };
    }

    public ObservableValue<String> keyProperty()
    {
        return mKey;
    }

    public ReadOnlyDoubleProperty rateProperty()
    {
        return mHitRate;
    }

    public String getKey()
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
        mHitRate.set(((double) mHits / mTotal));
    }

    public void logMiss()
    {
        mTotal++;
        update();
    }
}
