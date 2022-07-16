package io.github.eckig.kbk;

import java.util.ArrayList;
import java.util.List;

public class WeightedRandomKeySelector
{
    private final List<KeyElement> mElements = new ArrayList<>();

    public Key selectNext()
    {
        double totalWeight = 0.0;
        for (final var i : mElements)
        {
            totalWeight += i.mWeight;
        }

        // Now choose a random item.
        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < mElements.size() - 1; ++idx)
        {
            r -= mElements.get(idx).mWeight;
            if (r <= 0.0)
            {
                break;
            }
        }
        return mElements.get(idx).mKey;
    }

    public void addKeys(final KeyList pKeys)
    {
        if (pKeys != null)
        {
            for (final var key : pKeys.getKeys())
            {
                if (key != null)
                {
                    if (mElements.isEmpty() || mElements.stream().noneMatch(e -> matchesExact(e.mKey, key)))
                    {
                        mElements.add(new KeyElement(key));
                    }
                }
            }
        }
    }

    public void removeKeys(final KeyList pKeys)
    {
        if (pKeys != null)
        {
            for (final var key : pKeys.getKeys())
            {
                if (key != null)
                {
                    mElements.removeIf(e -> matchesExact(e.mKey, key));
                }
            }
        }
    }

    private static boolean matchesExact(final Key p1, final Key p2)
    {
        return p1 == p2 || p1 != null && p1.key().equals(p2.key());
    }

    public void setWeight(final Key pKey, final Priority pWeight)
    {
        if (pKey != null)
        {
            mElements.stream()
                    .filter(e -> matchesExact(e.mKey, pKey))
                    .findFirst()
                    .ifPresent(e -> e.mWeight = pWeight.weight());
        }
    }

    private static final class KeyElement
    {
        private final Key mKey;
        private double mWeight = 0.1;

        private KeyElement(final Key pKey)
        {
            mKey = pKey;
        }
    }
}
