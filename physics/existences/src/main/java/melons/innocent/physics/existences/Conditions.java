package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Conditions: the state a surface is in at a spot, which changes with the weather and with what happens there, where
 * its form and what it is made of do not. Bare, and it is dry; with something slick on it, wet or icy; with something
 * lying over it, loose with grit or leaves, or flooded to some depth. It is a fact of each spot: a puddle lies on a
 * dry road.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Conditions.md}.
 */
public record Conditions() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("conditions", new Condition(), List.of(new Dry(), new Wet(), new Icy(), new Loose(),
                new Flooded()), Meanings.INSTANCE.beside(Conditions.class));
    }

    /** The root: the state a surface is in at a spot. */
    public record Condition() implements L0 {
    }

    // Bare: nothing on it but itself
    public record Bare() implements L1<Condition> { public Condition parent() { return new Condition(); } }
    public record Dry() implements Leaf<Bare> { public Bare parent() { return new Bare(); } }

    // Slick: something on it that lets go
    public record Slick() implements L1<Condition> { public Condition parent() { return new Condition(); } }
    public record Wet() implements Leaf<Slick> { public Slick parent() { return new Slick(); } }
    public record Icy() implements Leaf<Slick> { public Slick parent() { return new Slick(); } }

    // Covered: something lying over it
    public record Covered() implements L1<Condition> { public Condition parent() { return new Condition(); } }
    public record Loose() implements Leaf<Covered> { public Covered parent() { return new Covered(); } }
    public record Flooded() implements Leaf<Covered> { public Covered parent() { return new Covered(); } }
}
