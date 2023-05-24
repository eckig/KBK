package io.github.eckig.kbk.layout;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.eckig.kbk.Key;


public final class KeyboardLayout
{

    private final List<List<Key>> mLayout;

    private KeyboardLayout(final List<List<Key>> pLayout)
    {
        mLayout = pLayout;
    }

    public static KeyboardLayout get(final Layout pLayout)
    {
        final var resource = KeyboardLayout.class.getResource(pLayout.fileName());
        if (resource == null)
        {
            return new KeyboardLayout(List.of());
        }
        final List<List<Key>> rows = new ArrayList<>();
        try (var stream = Files.lines(Path.of(resource.toURI())))
        {
            stream.forEach(line -> {
                final var row = Arrays.stream(line.split(" ")).map(keyDef -> Key.of(keyDef.toLowerCase(Locale.ENGLISH))).toList();
                rows.add(List.copyOf(row));
            });
        }
        catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return new KeyboardLayout(List.copyOf(rows));
    }

    public Optional<Key> getKey(final int pRow, final int pColumn)
    {
        try
        {
            final var row = mLayout.get(pRow);
            return Optional.ofNullable(row.get(pColumn));
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public int getRows()
    {
        return mLayout.size();
    }

    public int getColumns()
    {
        return mLayout.stream().mapToInt(List::size).max().orElse(0);
    }

    public enum Layout
    {

        DVORAK, QWERTY;

        String fileName()
        {
            return name().toLowerCase(Locale.ENGLISH) + ".layout";
        }
    }
}
