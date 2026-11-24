package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Point.Spatial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A form kept as text, to read and edit by hand: its name; a line for each landmark, its name and where it stands; one
 * for each edge, by its landmarks' names, and how it runs if not straight; one for each face, its four sides' names
 * in turn, the sides parted by {@code |}. Names are quoted; lines it does not know, blank ones and {@code #} comments
 * are passed over when it is read.
 *
 * <pre>
 * form "scapula"
 * landmark "superior angle" 7.2 47.8 -10
 * edge "superior angle" "root" bow 0 0 -0.5 2
 * face "infraspinous" "root" "inferior" | "inferior" "glenoid" | "glenoid" "acromial angle" | "acromial angle" "root"
 * </pre>
 */
public final class FormText {

    /** {@code form} as text. */
    public String written(final Form form) {
        final StringBuilder text = new StringBuilder("form ").append(quoted(form.name())).append('\n');
        for (final Landmark l : form.landmarks()) {
            text.append("landmark ").append(quoted(l.name())).append(numbers(l.at())).append('\n');
        }
        for (final Edge<Landmark, Course> e : form.edges()) {
            text.append("edge ").append(quoted(e.one().name())).append(' ').append(quoted(e.other().name()))
                    .append(course(e.label())).append('\n');
        }
        for (final Face f : form.faces()) {
            text.append("face ").append(quoted(f.name()));
            for (int i = 0; i < 4; i++) {
                text.append(i == 0 ? "" : " |");
                f.sides().get(i).forEach(n -> text.append(' ').append(quoted(n)));
            }
            text.append('\n');
        }
        return text.toString();
    }

    /** The form {@code text} says. */
    public Form read(final String text) {
        FormBuilder form = new FormBuilder("unnamed");
        final List<List<String>> later = new ArrayList<>();
        for (final String line : text.split("\\R")) {
            final List<String> words = words(line);
            if (words.isEmpty() || words.getFirst().startsWith("#")) {
                continue;
            }
            switch (words.getFirst()) {
                case "form" -> form = new FormBuilder(words.get(1));
                case "landmark" -> form.landmark(words.get(1), number(words, 2), number(words, 3), number(words, 4));
                case "edge", "face" -> later.add(words);
                default -> {
                    // for whatever uses it
                }
            }
        }
        for (final List<String> words : later) {
            if (words.getFirst().equals("edge")) {
                form.edge(words.get(1), words.get(2), course(words));
            } else {
                form.face(words.get(1), sides(words.subList(2, words.size())));
            }
        }
        return form.made();
    }

    private String course(final Course c) {
        return switch (c) {
            case Course.Straight s -> "";
            case Course.Arc a -> " arc" + numbers(a.rise());
            case Course.Bow b -> " bow" + numbers(b.bow()) + " " + number(b.fullness()) + (b.from() == null ? ""
                    : numbers(b.from()) + numbers(b.to()) + " " + number(b.t0()) + " " + number(b.t1()));
            case Course.Corner k -> " corner" + numbers(k.corner()) + " " + number(k.squareness()) + numbers(k.from())
                    + numbers(k.to()) + " " + number(k.t0()) + " " + number(k.t1());
        };
    }

    private Course course(final List<String> w) {
        if (w.size() <= 3) {
            return new Course.Straight();
        }
        return switch (w.get(3)) {
            case "arc" -> new Course.Arc(spatial(w, 4));
            case "bow" -> w.size() > 8 ? new Course.Bow(spatial(w, 4), number(w, 7), spatial(w, 8), spatial(w, 11),
                    number(w, 14), number(w, 15)) : new Course.Bow(spatial(w, 4), number(w, 7));
            case "corner" -> new Course.Corner(spatial(w, 4), number(w, 7), spatial(w, 8), spatial(w, 11),
                    number(w, 14), number(w, 15));
            default -> throw new IllegalArgumentException("an edge running " + w.get(3) + ": " + w);
        };
    }

    private List<List<String>> sides(final List<String> words) {
        final List<List<String>> sides = new ArrayList<>(List.of(new ArrayList<>()));
        for (final String w : words) {
            if (w.equals("|")) {
                sides.add(new ArrayList<>());
            } else {
                sides.getLast().add(w);
            }
        }
        return sides;
    }

    /** {@code line}'s words: spaced apart, or quoted, a quoted word's spaces its own. */
    private List<String> words(final String line) {
        final List<String> words = new ArrayList<>();
        final StringBuilder word = new StringBuilder();
        boolean quoted = false;
        boolean any = false;
        for (final char c : line.toCharArray()) {
            if (c == '"') {
                quoted = !quoted;
                any = true;
            } else if (Character.isWhitespace(c) && !quoted) {
                if (any) {
                    words.add(word.toString());
                }
                word.setLength(0);
                any = false;
            } else {
                word.append(c);
                any = true;
            }
        }
        if (any) {
            words.add(word.toString());
        }
        return words;
    }

    private String quoted(final String name) {
        return '"' + name + '"';
    }

    private String numbers(final Spatial p) {
        return " " + number(p.x()) + " " + number(p.y()) + " " + number(p.z());
    }

    private String number(final double n) {
        return String.format(Locale.ROOT, "%.4f", n).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private double number(final List<String> words, final int at) {
        return Double.parseDouble(words.get(at));
    }

    private Spatial spatial(final List<String> words, final int at) {
        return new Spatial(number(words, at), number(words, at + 1), number(words, at + 2));
    }
}
