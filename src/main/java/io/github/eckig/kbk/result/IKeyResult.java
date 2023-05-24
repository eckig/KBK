package io.github.eckig.kbk.result;

import io.github.eckig.kbk.Key;
import javafx.beans.property.ReadOnlyDoubleProperty;


public sealed interface IKeyResult permits KeyResult
{

    ReadOnlyDoubleProperty rateProperty();

    boolean matchesIgnoreCase(final Key pKey);
}
