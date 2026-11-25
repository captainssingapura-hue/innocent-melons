package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Surfaces: the forms a surface that is moved over may have, which is geometry alone. Flat and square to down, and
 * it is level; flat and tilted, a slope; rising in steps of one rise and run, stepped; rolling, with a height at each
 * spot, land; level and yielding, a liquid; to be climbed and not walked, upright. What a surface is made of and what
 * state it is in are apart from its form ({@link Conditions}, and the materials, which are the material lab's). A
 * road, a floor, a stair and a roof are not forms: they are things that provide surfaces of these forms.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Surfaces.md}.
 */
public record Surfaces() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("surfaces", new Surface(), List.of(new Level(), new Slope(), new Stepped(), new Land(),
                new Liquid(), new Upright()), Meanings.INSTANCE.beside(Surfaces.class));
    }

    /** The root: the form of a surface that is moved over. */
    public record Surface() implements L0 {
    }

    // Firm: it holds what is on it
    public record Firm() implements L1<Surface> { public Surface parent() { return new Surface(); } }
    public record Level() implements Leaf<Firm> { public Firm parent() { return new Firm(); } }
    public record Slope() implements Leaf<Firm> { public Firm parent() { return new Firm(); } }
    public record Stepped() implements Leaf<Firm> { public Firm parent() { return new Firm(); } }
    public record Land() implements Leaf<Firm> { public Firm parent() { return new Firm(); } }

    // Yielding: it gives way to what is on it
    public record Yielding() implements L1<Surface> { public Surface parent() { return new Surface(); } }
    public record Liquid() implements Leaf<Yielding> { public Yielding parent() { return new Yielding(); } }

    // Climbed: gone up and not along
    public record Climbed() implements L1<Surface> { public Surface parent() { return new Surface(); } }
    public record Upright() implements Leaf<Climbed> { public Climbed parent() { return new Climbed(); } }
}
