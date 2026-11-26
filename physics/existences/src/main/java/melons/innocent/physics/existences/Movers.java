package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Movers: whatever goes over a surface, told apart by how it meets the surface and not by what it is. By footfalls,
 * and it steps; by patches that roll under it, and it is wheeled; by a long belt, tracked; by dragging, and it
 * slides; by the whole of itself turning, and it is a rolling body; by pushing a liquid aside, and it floats. Each
 * way of meeting a surface wants other things told of it: a foot its footing and the height of a step, a wheel that
 * the surface goes on unbroken. Whether a mover {@link DrivesItself} or is {@link OnlyPushed} is said across the
 * tree. What flies meets no surface, and is no mover until it lands.
 *
 * <p>A mover asks the surface it is on what it is; which engine works out how it then goes is not said here.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Movers.md}.
 */
public record Movers() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("movers", new Mover(), List.of(new Walker(), new Beast(), new Car(), new Motorcycle(),
                new Wagon(), new Crawler(), new Sled(), new Dragged(), new Roller(), new Boat(), new Raft()),
                Meanings.INSTANCE.beside(Movers.class));
    }

    /** The root: whatever goes over a surface. */
    public record Mover() implements L0 {
    }

    // Stepping: by footfalls
    public record Stepping() implements L1<Mover>, DrivesItself { public Mover parent() { return new Mover(); } }
    public record Walker() implements Leaf<Stepping> { public Stepping parent() { return new Stepping(); } }
    public record Beast() implements Leaf<Stepping> { public Stepping parent() { return new Stepping(); } }

    // Wheeled: by patches that roll under it
    public record Wheeled() implements L1<Mover> { public Mover parent() { return new Mover(); } }
    public record Car() implements Leaf<Wheeled>, DrivesItself { public Wheeled parent() { return new Wheeled(); } }
    public record Motorcycle() implements Leaf<Wheeled>, DrivesItself {
        public Wheeled parent() { return new Wheeled(); }
    }
    public record Wagon() implements Leaf<Wheeled>, OnlyPushed { public Wheeled parent() { return new Wheeled(); } }

    // Tracked: by a long belt laid under it
    public record Tracked() implements L1<Mover>, DrivesItself { public Mover parent() { return new Mover(); } }
    public record Crawler() implements Leaf<Tracked> { public Tracked parent() { return new Tracked(); } }

    // Sliding: by dragging over it
    public record Sliding() implements L1<Mover>, OnlyPushed { public Mover parent() { return new Mover(); } }
    public record Sled() implements Leaf<Sliding> { public Sliding parent() { return new Sliding(); } }
    public record Dragged() implements Leaf<Sliding> { public Sliding parent() { return new Sliding(); } }

    // Rolling: by the whole of itself turning
    public record Rolling() implements L1<Mover>, OnlyPushed { public Mover parent() { return new Mover(); } }
    public record Roller() implements Leaf<Rolling> { public Rolling parent() { return new Rolling(); } }

    // Floating: by pushing a liquid aside
    public record Floating() implements L1<Mover> { public Mover parent() { return new Mover(); } }
    public record Boat() implements Leaf<Floating>, DrivesItself { public Floating parent() { return new Floating(); } }
    public record Raft() implements Leaf<Floating>, OnlyPushed { public Floating parent() { return new Floating(); } }
}
