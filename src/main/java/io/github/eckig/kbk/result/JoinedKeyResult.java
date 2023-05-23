package io.github.eckig.kbk.result;

import io.github.eckig.kbk.Key;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


final class JoinedKeyResult implements IKeyResult
{

    private final IKeyResult mResult2;
    private final IKeyResult mResult1;
    private final DoubleProperty mHitRate = new SimpleDoubleProperty();
    private final InvalidationListener mListener = obs -> update();

    public JoinedKeyResult(final IKeyResult p1, final IKeyResult p2)
    {
        p1.rateProperty().addListener(new WeakInvalidationListener(mListener));
        p2.rateProperty().addListener(new WeakInvalidationListener(mListener));
        mResult1 = p1;
        mResult2 = p2;
    }

    private void update()
    {
        int hits = 0;
        int total = 0;
        if (mResult1 instanceof KeyResult k1)
        {
            hits += k1.mHits;
            total += k1.mTotal;
        }
        else
        {
            // TODO
            throw new UnsupportedOperationException();
        }

        if (mResult2 instanceof KeyResult k2)
        {
            hits += k2.mHits;
            total += k2.mTotal;
        }
        else
        {
            // TODO
            throw new UnsupportedOperationException();
        }

        final double rate = (double) hits / (double) total;
        mHitRate.set(rate);
    }

    @Override
    public boolean isValidFor(final Key pKey)
    {
        return mResult1.isValidFor(pKey) || mResult2.isValidFor(pKey);
    }

    @Override
    public ReadOnlyDoubleProperty rateProperty()
    {
        return mHitRate;
    }

}
