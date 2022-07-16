package io.github.eckig.kbk;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;

public class KeyList
{
    private final List<Key> mKeys;
    private final BooleanProperty mActive = new SimpleBooleanProperty();
    private final String mLabel;

    public KeyList(final List<Key> pKeys, final String pLabel)
    {
        mKeys = pKeys;
        mLabel = pLabel;
    }

    public BooleanProperty activeProperty()
    {
        return mActive;
    }

    public List<Key> getKeys()
    {
        return mKeys;
    }

    public String getLabel()
    {
        return mLabel;
    }
}
