package melons.innocent.testkit.loose;

import com.example.fxsuite.existence.Things;
import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The trial of the world at rest and the loose body, set out to be walked round ({@link Scenes}): a crate in the
 * air, a stack on a pillar, a beam on two pillars, a ramp with a watermelon and a ball chocked on it, a wagon on a
 * track, and by the walk's beginning two watermelons and an apple. It is ticked with the walk, a tick a step. Any
 * thing of it is taken away when that is asked, by its name. Its supports go of themselves so many seconds in where
 * the run says, the stack's pillar and the beam's right-hand one first and the beam's left-hand one as long after;
 * and it can be begun again as it was described, its ramp at another slope. Any thing of it but the ramp and the
 * wagon can be thrown, with so much energy, a way round and a way up, and the last throw is followed: how high it
 * rose and where it came down ({@link Flown}). The wagon is set going and stopped.
 *
 * <p>A thing may be taken up out of it into someone's hands ({@link Lifted}) and put back, where it is put down or
 * as it is thrown. A melon is cut into slices, which stand where it lay ({@link Slices}). And what is thrown is
 * followed for the blow it strikes where it comes down ({@link Blows}): a thing broken is gone, a melon broken is
 * in slices.
 */
public final class Trial {

    private static final double TICK = 1.0 / 120;
    /** What goes at the first asking, unless the run says, and at the second, of what is still there. */
    private static final String FIRST = "the stack's pillar, the beam's right pillar";
    private static final String SECOND = "the beam's left pillar";
    /** How far along its track the wagon stops of itself. */
    private static final double FAR = 30_000;

    /** What each thing is, by its name, in the order they are described. */
    private final Listed listed = new Listed();
    private final long after;
    /** The names of what goes at the first asking: as the run says by {@code -Dloose.first=}, a comma between. */
    private final String first = System.getProperty("loose.first", FIRST);
    private World world;
    /** Whoever walks it, whom the world is told of each tick. */
    private final Walker walker = new Walker();
    /** How steep its ramp is, in degrees. */
    private double slope;
    private long ticks;
    /** How many times its supports have been taken away since it began. */
    private int taken;
    private int asked;
    /** The things asked by name to be taken away at the next tick, to be taken up, and to be cut. */
    private final List<String> named = new ArrayList<>();
    private final List<String> lifted = new ArrayList<>();
    private final List<String> cutting = new ArrayList<>();
    /** How many times what is at rest in it has changed: a thing let go, come to rest, or gone. */
    private long changes;
    /** How many times it has been begun. */
    private long begun;
    /** What is to be thrown at the next tick, each with the motion it is given; and the last throw, followed. */
    private final Map<String, Moved> flung = new TreeMap<>();
    private Optional<Flown> flown = Optional.empty();
    private final Blows blows = new Blows();
    /** How the wagon is to be driven from the next tick, where that has been asked. */
    private Optional<Moved> drive = Optional.empty();

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
        this.world = new Scenes(listed, slope).described();
    }

    /** A tick: what is asked to go, or due to go of itself, is destroyed, and the world is a step on. */
    /** Whoever walks it now stands with their feet at {@code feet}, facing {@code yaw} degrees round. */
    public void stood(final Spot feet, final double yaw) {
        walker.stood(feet, yaw);
    }

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
        final List<Slices.Slice> slices = cut(going);
        lifted.removeIf(name -> !world.things().contains(name) || going.contains(name));
        flung.keySet().removeIf(name -> !world.things().contains(name) || going.contains(name)
                || lifted.contains(name));
        blows.before(world);
        final List<Happened> happened = world.ticked(new Asked(going, lifted, flung, driving(going),
                walker.outsiders()), TICK);
        lifted.clear();
        slices.forEach(slice -> put(slice.rigid(), slice.placed(), Stuff.FLESH, new Things.MelonSlice()));
        flung.keySet().forEach(blows::thrown);
        flung.clear();
        flown.ifPresent(last -> last.followed(world, happened));
        // what a blow has broken goes at the next tick: a melon into slices, anything else altogether
        for (final String broken : blows.after(world, happened, listed)) {
            (listed.kind(broken).lineage().contains(new Things.Melon()) ? cutting : named).add(broken);
        }
        changes += happened.stream().filter(one -> !(one instanceof Happened.Landed)).count();
        return happened;
    }

    /** The slices of what is to be cut and is still there, each melon cut added to what is {@code going}. */
    private List<Slices.Slice> cut(final List<String> going) {
        final List<Slices.Slice> slices = new ArrayList<>();
        for (final String name : cutting) {
            if (world.things().contains(name) && !going.contains(name)) {
                slices.addAll(new Slices().of(world.thing(name), world.placed(name).orElseThrow()));
                going.add(name);
            }
        }
        cutting.clear();
        return slices;
    }

    /** How the wagon is driven otherwise this tick, where that is asked or it has gone far out along its track. */
    private Map<String, Moved> driving(final List<String> going) {
        if (ticks == 1 && !System.getProperty("loose.wagon", "").isBlank()) {
            driven(Double.parseDouble(System.getProperty("loose.wagon")));
        }
        if (world.things().contains(Scenes.WAGON) && world.moved(Scenes.WAGON).velocity().length() > 0
                && world.placed(Scenes.WAGON).orElseThrow().at().across() > FAR) {
            driven(0);
        }
        final Map<String, Moved> driving = drive.isPresent() && world.things().contains(Scenes.WAGON)
                && !going.contains(Scenes.WAGON) ? Map.of(Scenes.WAGON, drive.get()) : Map.of();
        drive = Optional.empty();
        return driving;
    }

    /** Its next supports are to be taken away at the next tick. */
    public void take() {
        asked++;
    }

    /** The thing called {@code name} is to be taken away at the next tick, if it is still there. */
    public void take(final String name) {
        named.add(name);
    }

    /** The thing called {@code name} is to be cut into slices at the next tick, if it is still there. */
    public void cut(final String name) {
        cutting.add(name);
    }

    /** The thing called {@code name}, which is there, taken up out of the world at the next tick: as it is held. */
    public Lifted lifted(final String name) {
        lifted.add(name);
        return new Lifted(world.thing(name), listed.stuff(name), listed.kind(name),
                world.placed(name).orElseThrow().turned());
    }

    /** A thing taken up, put back into the world placed so: at rest there until the next tick's check. */
    public void put(final Lifted held, final Placed placed) {
        put(held.rigid(), placed, held.stuff(), held.kind());
    }

    private void put(final Rigid rigid, final Placed placed, final Stuff stuff, final Node kind) {
        listed.is(rigid.name(), kind, stuff);
        world.stood(rigid, placed);
        changes++;
    }

    /**
     * The thing called {@code name} is to be thrown at the next tick as {@code thrown} says, if it is still there;
     * and that throw is the one followed from now on.
     */
    public void thrown(final String name, final Throw thrown) {
        thrown(name, thrown, new Spot(0, 0, 0));
    }

    /** The same, by someone going as {@code carried} says, which the thing keeps on top of the throw. */
    public void thrown(final String name, final Throw thrown, final Spot carried) {
        if (!world.things().contains(name)) {
            return;
        }
        final Rigid rigid = world.thing(name);
        flung.put(name, new Moved(thrown.of(rigid).velocity().plus(carried), new Spot(0, 0, 0)));
        flown = Optional.of(new Flown(name, world.placed(name).orElseThrow().at(), thrown.speed(rigid),
                thrown.upward(), thrown.round(), world.moved(name).velocity().plus(carried)));
    }

    /** The wagon is to go so many kilometres an hour along its track from the next tick: none, and it stops. */
    public void driven(final double hourly) {
        drive = Optional.of(new Moved(new Spot(hourly * 100_000 / 3_600, 0, 0), new Spot(0, 0, 0)));
    }

    /** The last throw, followed: none, where nothing has been thrown since it began. */
    public Optional<Flown> flown() {
        return flown;
    }

    /** What came of the last blow a thing thrown struck, in words: nothing, where none has. */
    public Optional<String> struck() {
        return blows.said();
    }

    /** The names of the things it describes that can be thrown: all but what never moves and what is driven. */
    public List<String> movable() {
        return listed.names().stream().filter(name -> !Scenes.FIXED.equals(name) && !Scenes.WAGON.equals(name)
                && !listed.kind(name).equals(new Things.MelonSlice())).toList();
    }

    /** Begun again, as it was described, its ramp sloping so many {@code degrees}. */
    public void again(final double degrees) {
        slope = degrees;
        listed.clear();
        world = new Scenes(listed, slope).described();
        ticks = 0;
        taken = 0;
        asked = 0;
        named.clear();
        lifted.clear();
        cutting.clear();
        flung.clear();
        blows.clear();
        flown = Optional.empty();
        drive = Optional.empty();
        changes++;
        begun++;
    }

    /** The world it is trying. */
    public World world() {
        return world;
    }

    /** How steep its ramp is, in degrees. */
    public double slope() {
        return slope;
    }

    /** The names of the things it describes, in the order they are described: those gone since, and made since. */
    public List<String> names() {
        return listed.names();
    }

    /** What each thing it describes is made of, by name: those gone since too. */
    public Map<String, Stuff> made() {
        return listed.made();
    }

    /** Which kind of thing the one called {@code name} is. */
    public Node kind(final String name) {
        return listed.kind(name);
    }

    /** How many times what is at rest in it has changed since it was made. */
    public long changes() {
        return changes;
    }

    /** How many times it has been begun again. */
    public long begun() {
        return begun;
    }
}
