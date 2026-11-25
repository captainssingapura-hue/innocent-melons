package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Shapes: the shapes a thing that keeps its shape may have, as physics tells them apart, which is by how each meets
 * what it stands on. Flat faces only, and it rests on one and tips over an edge; no flat face, and it touches at a
 * spot and rolls; round one way about a line, and it rolls that way and tips the other; a round shape cut flat, and
 * it stands on the cut and rocks on the round; several joined, and it stands on feet; and the shapes of what never
 * moves and only holds others up. Whether a shape {@link RestsOnAFace}, {@link Tips}, {@link Rolls},
 * {@link RollsAnyWay}, {@link Rocks} or is {@link Unmoving} is said across the tree.
 *
 * <p>A shape here is a concept and not a mesh: what is drawn of a thing may be far finer than the shape it is
 * reckoned as. A kind is listed before any engine reckons with it; which are reckoned with is for each engine to
 * say, by the shapes it types to these leaves.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Shapes.md}.
 */
public record Shapes() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("shapes", new Shape(), List.of(new Box(), new Plate(), new Prism(), new Lump(),
                new Ball(), new Ellipsoid(), new Drum(), new Cone(), new Capsule(), new Ring(), new Dome(),
                new Joined(), new Level(), new Slope(), new Land(), new Shell(), new Mesh()),
                Meanings.INSTANCE.beside(Shapes.class));
    }

    /** The root: the shape of a thing that keeps its shape. */
    public record Shape() implements L0 {
    }

    // Faceted: flat faces only, meeting at edges
    public record Faceted() implements L1<Shape>, RestsOnAFace, Tips { public Shape parent() { return new Shape(); } }
    public record Box() implements Leaf<Faceted> { public Faceted parent() { return new Faceted(); } }
    public record Plate() implements Leaf<Faceted> { public Faceted parent() { return new Faceted(); } }
    public record Prism() implements Leaf<Faceted> { public Faceted parent() { return new Faceted(); } }
    public record Lump() implements Leaf<Faceted> { public Faceted parent() { return new Faceted(); } }

    // Rounded: no flat face anywhere, so touching at a spot
    public record Rounded() implements L1<Shape>, RollsAnyWay { public Shape parent() { return new Shape(); } }
    public record Ball() implements Leaf<Rounded> { public Rounded parent() { return new Rounded(); } }
    public record Ellipsoid() implements Leaf<Rounded> { public Rounded parent() { return new Rounded(); } }

    // Turned: round one way, about a line through it
    public record Turned() implements L1<Shape>, Rolls { public Shape parent() { return new Shape(); } }
    public record Drum() implements Leaf<Turned>, RestsOnAFace, Tips {
        public Turned parent() { return new Turned(); }
    }
    public record Cone() implements Leaf<Turned>, RestsOnAFace, Tips {
        public Turned parent() { return new Turned(); }
    }
    public record Capsule() implements Leaf<Turned> { public Turned parent() { return new Turned(); } }
    public record Ring() implements Leaf<Turned> { public Turned parent() { return new Turned(); } }

    // Halved: a round shape cut flat
    public record Halved() implements L1<Shape>, RestsOnAFace, Tips, Rocks {
        public Shape parent() { return new Shape(); }
    }
    public record Dome() implements Leaf<Halved> { public Halved parent() { return new Halved(); } }

    // Compound: shapes of other kinds made into one
    public record Compound() implements L1<Shape> { public Shape parent() { return new Shape(); } }
    public record Joined() implements Leaf<Compound> { public Compound parent() { return new Compound(); } }

    // Fixed: what never moves, and only holds others up
    public record Fixed() implements L1<Shape>, Unmoving { public Shape parent() { return new Shape(); } }
    public record Level() implements Leaf<Fixed> { public Fixed parent() { return new Fixed(); } }
    public record Slope() implements Leaf<Fixed> { public Fixed parent() { return new Fixed(); } }
    public record Land() implements Leaf<Fixed> { public Fixed parent() { return new Fixed(); } }
    public record Shell() implements Leaf<Fixed> { public Fixed parent() { return new Fixed(); } }
    public record Mesh() implements Leaf<Fixed> { public Fixed parent() { return new Fixed(); } }
}
