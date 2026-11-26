package melons.innocent.core.surface;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.physics.existences.Movers;
import melons.innocent.physics.existences.Surfaces;

import java.util.List;

/**
 * Whether a kind of mover can go on a form of surface: said once, for every engine, by the mover's way of meeting a
 * surface against the surface's form. Feet take a stair and wheels do not; a boat goes on a liquid and on nothing
 * else. Where it is to a limit, a grade or a depth, the figure is the mover's own and is not said here.
 *
 * @param use how far it can be used
 * @param how the same in words, as the table has it
 */
public record Passage(Use use, String how) {

    /** How far a mover can use a surface. */
    public enum Use {

        /** It goes on it. */
        FREELY,
        /** It goes on it up to a limit of its own, or only as the surface sends it. */
        TO_A_LIMIT,
        /** It cannot go on it: the surface is in its way, or does not hold it. */
        NOT
    }

    /** How a mover of the kind {@code mover} can use a surface of the form {@code form}. */
    public Passage(final Node mover, final Node form) {
        this(new Passage(Use.NOT, "no").between(mover, form));
    }

    private Passage(final Passage same) {
        this(same.use(), same.how());
    }

    /** The table: a row to each way of meeting a surface, a column to each form. */
    private Passage between(final Node mover, final Node form) {
        final List<Node> ways = List.of(new Movers.Stepping(), new Movers.Wheeled(), new Movers.Tracked(),
                new Movers.Sliding(), new Movers.Rolling(), new Movers.Floating());
        final List<Node> forms = List.of(new Surfaces.Level(), new Surfaces.Slope(), new Surfaces.Stepped(),
                new Surfaces.Land(), new Surfaces.Liquid(), new Surfaces.Upright());
        final List<List<String>> table = List.of(
                List.of("yes", "to a grade", "yes", "yes", "wades, to a depth", "climbs"),
                List.of("yes", "to a grade", "no", "to a roughness", "no", "no"),
                List.of("yes", "to a steeper grade", "low steps", "yes", "no", "no"),
                List.of("yes", "slides down", "tumbles down", "yes", "no", "no"),
                List.of("yes", "rolls down", "bounces down", "yes", "floats or sinks", "no"),
                List.of("no", "no", "no", "no", "yes", "no"));
        final int way = ways.indexOf(mover.lineage().stream().filter(ways::contains).findFirst().orElse(mover));
        final int column = forms.indexOf(form);
        if (way < 0 || column < 0) {
            throw new IllegalArgumentException("a kind of mover on a form of surface: " + mover.name() + " on "
                    + form.name());
        }
        final String said = table.get(way).get(column);
        return new Passage(said.equals("yes") ? Use.FREELY : said.equals("no") ? Use.NOT : Use.TO_A_LIMIT, said);
    }
}
