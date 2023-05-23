package io.github.eckig.kbk.result;

import io.github.eckig.kbk.Key;
import javafx.beans.property.ReadOnlyDoubleProperty;


public interface IKeyResult
{

    default IKeyResult join(final IKeyResult pResult)
    {
        return new JoinedKeyResult(this, pResult);
    }

    ReadOnlyDoubleProperty rateProperty();

    boolean isValidFor(final Key pKey);
}
