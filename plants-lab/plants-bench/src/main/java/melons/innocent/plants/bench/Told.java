package melons.innocent.plants.bench;

import melons.innocent.plants.walk.WoodDrawn;

import java.util.Arrays;
import java.util.Locale;

/**
 * What the bench is told to have on show to begin with, by its run's options: {@code -Dbench.subject=} tree or patch
 * (a tree unless said), {@code -Dbench.kinds=} oak, walnut, birch or mixed (oak unless said), {@code -Dbench.stage=}
 * a stage by its name (whatever its distance calls for unless said), and {@code -Dbench.beside=} whether the same
 * stands beside it as grown (it does unless said otherwise), {@code -Dbench.rows=} how many trees along a patch's
 * side (four unless said), {@code -Dbench.one=true} what is on show drawn as one mesh, and for an area of forest
 * ({@code -Dbench.subject=area}) {@code -Dbench.outline=} circle, square, oval, star or crescent and {@code
 * -Dbench.across=} how far across in metres (a circle of 100 unless said). Names in any case, spaces for
 * underscores.
 */
record Told() {

    /** What is on show to begin with. */
    OnShow first() {
        return new OnShow(
                Subject.valueOf(named(System.getProperty("bench.subject", "tree"))),
                Kinds.valueOf(named(System.getProperty("bench.kinds", "oak"))),
                new WoodDrawn.Held(System.getProperty("bench.stage", "")).stage(),
                Boolean.parseBoolean(System.getProperty("bench.beside", "true")),
                Integer.parseInt(System.getProperty("bench.rows", "4")),
                Boolean.getBoolean("bench.one") ? Joined.AS_ONE : Joined.APART,
                Outline.valueOf(System.getProperty("bench.outline", "circle").trim().toUpperCase(Locale.ROOT)),
                Integer.parseInt(System.getProperty("bench.across", "100")));
    }

    /** A name as an enum's constant is written. */
    private String named(final String said) {
        final String name = said.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        if (Arrays.stream(Kinds.values()).noneMatch(kinds -> kinds.name().equals(name))
                && Arrays.stream(Subject.values()).noneMatch(subject -> subject.name().equals(name))) {
            throw new IllegalArgumentException("the bench has no subject or kinds called " + said);
        }
        return name;
    }
}
