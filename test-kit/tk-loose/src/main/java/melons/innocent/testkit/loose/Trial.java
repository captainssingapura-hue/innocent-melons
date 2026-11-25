package melons.innocent.testkit.loose;

import melons.innocent.core.loose.Loose;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.ShapeImpl;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The trial of the world at rest and the loose body, set out to be walked round: four scenes side by side, twelve
 * metres ahead of where a walk begins. On the left a crate described a metre in the air; then a stack of three
 * crates on a pillar; then a steel beam lying across two pillars; and on the right a ramp sloping down toward the
 * walk's beginning, with a watermelon and a ball held near its top by a chock, and a crate lying on it beside them.
 * It is ticked with the walk, a tick a step. Any thing of it is taken away when that is asked, by its name: the
 * chock, and the melon and the ball roll down and out across the ground. Its supports go of themselves so many
 * seconds in where the run says, the stack's pillar and the beam's right-hand one first and the beam's left-hand one
 * as long after; and it can be begun again as it was described, its ramp at another slope. Any thing of it but
 * the ramp can be thrown, with so much energy, a way round and a way up, and the last throw is followed: how high
 * it rose and where it came down ({@link Flown}).
 */
public final class Trial {

    private static final double TICK = 1.0 / 120;
    private static final double AHEAD = 1_200;
    /** How far across the ramp's middle is, and how long the ramp is. */
    private static final double RAMP = 1_600;
    private static final double LONG = 500;
    /** What goes at the first asking, unless the run says, and at the second, of what is still there. */
    private static final String FIRST = "the stack's pillar, the beam's right pillar";
    private static final String SECOND = "the beam's left pillar";
    /** The one thing of it that never moves. */
    private static final String FIXED = "the ramp";

    /** What each thing is made of, by its name, in the order they are described. */
    private final Map<String, Stuff> made = new LinkedHashMap<>();
    private final long after;
    /** The names of what goes at the first asking: as the run says by {@code -Dloose.first=}, a comma between. */
    private final String first = System.getProperty("loose.first", FIRST);
    private World world;
    /** How steep its ramp is, in degrees. */
    private double slope;
    private long ticks;
    /** How many times its supports have been taken away since it began. */
    private int taken;
    private int asked;
    /** The things asked by name to be taken away at the next tick. */
    private final List<String> named = new ArrayList<>();
    /** How many times what is at rest in it has changed: a thing let go, come to rest, or gone. */
    private long changes;
    /** What is to be thrown at the next tick, each with the motion it is given; and the last throw, followed. */
    private final Map<String, Moved> flung = new TreeMap<>();
    private Optional<Flown> flown = Optional.empty();

    /**
     * A trial as the run says: {@code -Dloose.after=} seconds to each taking away of itself, none unless said;
     * {@code -Dloose.first=} the names of what goes first, the stack's pillar and the beam's right one unless said;
     * and {@code -Dloose.slope=} degrees to its ramp, twelve unless said.
     */
    public Trial() {
        this(Double.parseDouble(System.getProperty("loose.after", "-1")),
                Double.parseDouble(System.getProperty("loose.slope", "12")));
    }

    /**
     * A trial whose supports go of themselves {@code after} seconds in and again as long after, never under 0, and
     * whose ramp slopes so many degrees.
     */
    public Trial(final double after, final double slope) {
        this.after = after < 0 ? -1 : Math.max(1, Math.round(after / TICK));
        this.slope = slope;
        this.world = described();
    }

    /** The four scenes as they are described, nothing yet checked. */
    private World described() {
        made.clear();
        final World described = new World(new Ground(), new Loose());
        stood(described, "the crate in the air", Stuff.TIMBER, new Box(100, 100, 100), new Spot(-700, 150, AHEAD));
        stood(described, "the stack's pillar", Stuff.CONCRETE, new Box(40, 100, 40), new Spot(0, 50, AHEAD));
        stood(described, "the lowest crate", Stuff.TIMBER, new Box(60, 60, 60), new Spot(0, 130, AHEAD));
        stood(described, "the middle crate", Stuff.TIMBER, new Box(60, 60, 60), new Spot(0, 190, AHEAD));
        stood(described, "the highest crate", Stuff.TIMBER, new Box(60, 60, 60), new Spot(0, 250, AHEAD));
        stood(described, "the beam's left pillar", Stuff.CONCRETE, new Box(40, 100, 40), new Spot(520, 50, AHEAD));
        stood(described, "the beam's right pillar", Stuff.CONCRETE, new Box(40, 100, 40), new Spot(880, 50, AHEAD));
        stood(described, "the beam", Stuff.STEEL, new Box(400, 20, 40), new Spot(700, 110, AHEAD));
        // the ramp: its top's lower edge on the ground, twelve metres ahead, and on it what is to roll
        sloped(described, FIXED, Stuff.CONCRETE, new Slab(300, 20, LONG), 0, -10, 0);
        sloped(described, "the chock", Stuff.TIMBER, new Box(280, 34, 10), 0, 17, 150);
        sloped(described, "the melon", Stuff.RIND, new Ellipsoid(36, 28, 26), -80, 14, 168);
        sloped(described, "the ball", Stuff.RUBBER, new Ellipsoid(30), 0, 15, 170);
        sloped(described, "the crate on the ramp", Stuff.TIMBER, new Box(50, 50, 50), 95, 25, 190);
        return described;
    }

    private void stood(final World described, final String name, final Stuff stuff, final Box box, final Spot at) {
        made.put(name, stuff);
        described.stood(new Rigid(name, box, stuff.dense()), new Placed(at));
    }

    /**
     * A thing lying on the ramp, turned as the ramp is: its middle so far {@code across} the ramp from the ramp's
     * middle, so far {@code over} the ramp's top, and so far {@code upSlope} from the middle of its length.
     */
    private void sloped(final World described, final String name, final Stuff stuff, final ShapeImpl<?> shape,
                        final double across, final double over, final double upSlope) {
        final double tilt = Math.toRadians(slope);
        final Turn tilted = tilt == 0 ? new Turn() : new Turn(new Spot(1, 0, 0), -tilt);
        final Spot top = new Spot(RAMP, LONG / 2 * Math.sin(tilt), AHEAD + LONG / 2 * Math.cos(tilt));
        made.put(name, stuff);
        described.stood(new Rigid(name, shape, stuff.dense()),
                new Placed(top.plus(tilted.turned(new Spot(across, over, upSlope))), tilted));
    }

    /** A tick: what is asked to go, or due to go of itself, is destroyed, and the world is a step on. */
    public List<Happened> ticked() {
        ticks++;
        if (after > 0 && (ticks == after || ticks == 2 * after)) {
            asked++;
        }
        if (ticks == after && !System.getProperty("loose.thrown", "").isBlank()) {
            thrown(System.getProperty("loose.thrown"), new Throw(Double.parseDouble(System.getProperty(
                    "loose.joules", "500")), Double.parseDouble(System.getProperty("loose.round", "90")),
                    Double.parseDouble(System.getProperty("loose.up", "45"))));
        }
        final List<String> going = new ArrayList<>();
        while (taken < asked) {
            going.addAll(List.of((taken == 0 ? first : taken == 1 ? SECOND : "").split(", ")));
            taken++;
        }
        going.addAll(named);
        named.clear();
        going.removeIf(name -> !world.things().contains(name));
        flung.keySet().removeIf(name -> !world.things().contains(name) || going.contains(name));
        final List<Happened> happened = world.ticked(going, flung, TICK);
        flung.clear();
        flown.ifPresent(last -> last.followed(world, happened));
        changes += happened.stream().filter(one -> !(one instanceof Happened.Landed)).count();
        return happened;
    }

    /** Its next supports are to be taken away at the next tick. */
    public void take() {
        asked++;
    }

    /** The thing called {@code name} is to be taken away at the next tick, if it is still there. */
    public void take(final String name) {
        named.add(name);
    }

    /**
     * The thing called {@code name} is to be thrown at the next tick as {@code thrown} says, if it is still there;
     * and that throw is the one followed from now on.
     */
    public void thrown(final String name, final Throw thrown) {
        if (!world.things().contains(name)) {
            return;
        }
        flung.put(name, thrown.of(world.thing(name)));
        flown = Optional.of(new Flown(name, world.placed(name).orElseThrow().at(), thrown.speed(world.thing(name)),
                thrown.upward()));
    }

    /** The last throw, followed: none, where nothing has been thrown since it began. */
    public Optional<Flown> flown() {
        return flown;
    }

    /** The names of the things it describes that can be thrown: all but what never moves. */
    public List<String> movable() {
        return made.keySet().stream().filter(name -> !FIXED.equals(name)).toList();
    }

    /** Begun again, as it was described, its ramp sloping so many {@code degrees}. */
    public void again(final double degrees) {
        slope = degrees;
        world = described();
        ticks = 0;
        taken = 0;
        asked = 0;
        named.clear();
        flung.clear();
        flown = Optional.empty();
        changes++;
    }

    /** The world it is trying. */
    public World world() {
        return world;
    }

    /** How steep its ramp is, in degrees. */
    public double slope() {
        return slope;
    }

    /** The names of the things it describes, in the order they are described: those destroyed since too. */
    public List<String> names() {
        return List.copyOf(made.keySet());
    }

    /** What each thing it describes is made of, by name: those destroyed since too. */
    public Map<String, Stuff> made() {
        return Map.copyOf(made);
    }

    /** How many times what is at rest in it has changed since it was made. */
    public long changes() {
        return changes;
    }
}
