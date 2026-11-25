package melons.innocent.testkit.loose;

import melons.innocent.core.loose.Loose;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The trial of the world at rest and the loose body, set out to be walked round: three scenes side by side, twelve
 * metres ahead of where a walk begins. On the left a crate described a metre in the air; in the middle a stack of
 * three crates on a pillar; on the right a steel beam lying across two pillars. It is ticked with the walk, a tick a
 * step. Any thing of it is taken away when that is asked, by its name; its supports go of themselves so many seconds
 * in where the run says, the stack's pillar and the beam's right-hand one first and the beam's left-hand one as
 * long after; and it can be begun again as it was described.
 */
public final class Trial {

    private static final double TICK = 1.0 / 120;
    private static final double AHEAD = 1_200;
    /** What goes at the first asking and at the second, of what is still there. */
    private static final String FIRST = "the stack's pillar, the beam's right pillar";
    private static final String SECOND = "the beam's left pillar";

    /** What each thing is made of, by its name, in the order they are described. */
    private final Map<String, Stuff> made = new LinkedHashMap<>();
    private final long after;
    private World world;
    private long ticks;
    /** How many times its supports have been taken away since it began. */
    private int taken;
    private int asked;
    /** The things asked by name to be taken away at the next tick. */
    private final List<String> named = new ArrayList<>();
    /** How many times what is at rest in it has changed: a thing let go, come to rest, or gone. */
    private long changes;

    /** A trial as the run says: {@code -Dloose.after=} seconds to each taking away of itself; none unless said. */
    public Trial() {
        this(Double.parseDouble(System.getProperty("loose.after", "-1")));
    }

    /** A trial whose supports go of themselves {@code after} seconds in and again as long after; never, under 0. */
    public Trial(final double after) {
        this.after = after < 0 ? -1 : Math.max(1, Math.round(after / TICK));
        this.world = described();
    }

    /** The three scenes as they are described, nothing yet checked. */
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
        return described;
    }

    private void stood(final World described, final String name, final Stuff stuff, final Box box, final Spot at) {
        made.put(name, stuff);
        described.stood(new Rigid(name, box, stuff.dense()), new Placed(at));
    }

    /** A tick: what is asked to go, or due to go of itself, is destroyed, and the world is a step on. */
    public List<Happened> ticked() {
        ticks++;
        if (after > 0 && (ticks == after || ticks == 2 * after)) {
            asked++;
        }
        final List<String> going = new ArrayList<>();
        while (taken < asked) {
            going.addAll(List.of((taken == 0 ? FIRST : taken == 1 ? SECOND : "").split(", ")));
            taken++;
        }
        going.addAll(named);
        named.clear();
        going.removeIf(name -> !world.things().contains(name));
        final List<Happened> happened = world.ticked(going, TICK);
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

    /** Begun again, as it was described. */
    public void again() {
        world = described();
        ticks = 0;
        taken = 0;
        asked = 0;
        named.clear();
        changes++;
    }

    /** The world it is trying. */
    public World world() {
        return world;
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
