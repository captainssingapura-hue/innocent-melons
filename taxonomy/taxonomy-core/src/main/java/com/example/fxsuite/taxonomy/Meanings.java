package com.example.fxsuite.taxonomy;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * What a tree's nodes mean, written in markdown beside the class that holds them, to be read and reviewed by those who
 * do not read Java: a section to a node, headed by its title, saying what it is for and never how it looks.
 *
 * <pre>
 * ## HotelRooms
 *
 * Rooms let by the night, served from a lobby.
 * </pre>
 */
public final class Meanings {

    private Meanings() {
    }

    /**
     * The meanings beside {@code holder}: its resource of its own name, {@code Uses.md} beside {@code Uses}; each
     * section's title and its text. None, if there is no such file: every node is then reported as meaning nothing.
     */
    public static Map<String, String> beside(final Class<?> holder) {
        try (InputStream in = holder.getResourceAsStream(holder.getSimpleName() + ".md")) {
            return in == null ? Map.of() : read(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** The sections of {@code markdown}, in order: each one's title (after {@code ## }) and its text, trimmed. */
    public static Map<String, String> read(final String markdown) {
        final Map<String, String> meanings = new LinkedHashMap<>();
        String title = null;
        StringBuilder text = new StringBuilder();
        for (final String line : markdown.split("\\R")) {
            if (line.startsWith("## ")) {
                if (title != null) {
                    meanings.put(title, text.toString().strip());
                }
                title = line.substring(3).strip();
                text = new StringBuilder();
            } else if (title != null) {
                text.append(line).append('\n');
            }
        }
        if (title != null) {
            meanings.put(title, text.toString().strip());
        }
        return meanings;
    }
}
