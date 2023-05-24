package io.github.eckig.kbk.impl;

public enum Priority
{
    HIGH(1.0),
    MID(0.5),
    NORMAL(0.1);

    private final double weight;

    Priority(final double pValue)
    {
        weight = pValue;
    }

    public double weight()
    {
        return weight;
    }
}
