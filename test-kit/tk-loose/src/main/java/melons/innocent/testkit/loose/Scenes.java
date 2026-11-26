package melons.innocent.testkit.loose;

import com.example.fxsuite.existence.Things;
import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.loose.Loose;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Globe;
import melons.innocent.core.rest.Gravity;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.ShapeImpl;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.World;
import melons.innocent.core.step.Explaining;
import melons.innocent.core.tick.Ticker;

import java.util.Locale;

/**
 * The trial's scenes as they are described, nothing yet checked. Twelve metres ahead of where a walk begins, side by
 * side: a crate a metre in the air; a stack of three crates on a pillar; a steel beam across two pillars; and a ramp
 * sloping down toward the walk's beginning, with a watermelon and a ball held near its top by a chock, and a crate
 * beside them. Six metres ahead, a flat wagon on a track across the walk's way, with a ball, a watermelon chocked
 * before and behind, and a crate. And close by the walk's beginning, to its left, two watermelons and an apple
 * lying on the ground, to be taken up. Each thing is listed as it is described: what it is made of, and which kind
 * of thing it is.
 */
final class Scenes {

    /** The one thing that never moves, and the one that is driven. */
    static final String FIXED = "the ramp";
    static final String WAGON = "the wagon";
    private static final double AHEAD = 1_200;
    /** How far across the ramp's middle is, and how long the ramp is. */
    private static final double RAMP = 1_600;
    private static final double LONG = 500;
    /** How far ahead the wagon's track lies, and where along it the wagon begins. */
    private static final double TRACK = 600;
    private static final double BEGINS = -2_000;
    /** How far ahead what lies by the walk's beginning is. */
    private static final double NEAR = 300;

    private final Listed listed;
    private final double slope;
    /**
     * On the Earth, or on the globe the run names by {@code -Dloose.globe=} (earth, moon, mars); and the loose
     * body explains what it works out where the run says {@code -Dloose.explain=true}.
     */
    private final World world = new World(new Ground(),
            new Gravity(Globe.valueOf(System.getProperty("loose.globe", "earth").toUpperCase(Locale.ROOT))),
            new Loose(new Explaining(Boolean.getBoolean("loose.explain"))), new Ticker());

    /** The scenes, each thing listed in {@code listed}, the ramp sloping so many degrees. */
    Scenes(final Listed listed, final double slope) {
        this.listed = listed;
        this.slope = slope;
    }

    /** The world as it is described. */
    World described() {
        final Node crate = new Things.Crate();
        final Node pillar = new Things.Pillar();
        final Node melon = new Things.Watermelon();
        stood("the crate in the air", crate, Stuff.TIMBER, new Box(100, 100, 100), new Spot(-700, 150, AHEAD));
        stood("the stack's pillar", pillar, Stuff.CONCRETE, new Box(40, 100, 40), new Spot(0, 50, AHEAD));
        stood("the lowest crate", crate, Stuff.TIMBER, new Box(60, 60, 60), new Spot(0, 130, AHEAD));
        stood("the middle crate", crate, Stuff.TIMBER, new Box(60, 60, 60), new Spot(0, 190, AHEAD));
        stood("the highest crate", crate, Stuff.TIMBER, new Box(60, 60, 60), new Spot(0, 250, AHEAD));
        stood("the beam's left pillar", pillar, Stuff.CONCRETE, new Box(40, 100, 40), new Spot(520, 50, AHEAD));
        stood("the beam's right pillar", pillar, Stuff.CONCRETE, new Box(40, 100, 40), new Spot(880, 50, AHEAD));
        stood("the beam", new Things.Beam(), Stuff.STEEL, new Box(400, 20, 40), new Spot(700, 110, AHEAD));
        // the ramp: its top's lower edge on the ground, twelve metres ahead, and on it what is to roll
        sloped(FIXED, new Things.Ramp(), Stuff.CONCRETE, new Slab(300, 20, LONG), 0, -10, 0);
        sloped("the chock", new Things.Chock(), Stuff.TIMBER, new Box(280, 34, 10), 0, 17, 150);
        sloped("the melon", melon, Stuff.RIND, new Ellipsoid(36, 28, 26), -80, 14, 168);
        sloped("the ball", new Things.Ball(), Stuff.RUBBER, new Ellipsoid(30), 0, 15, 170);
        sloped("the crate on the ramp", crate, Stuff.TIMBER, new Box(50, 50, 50), 95, 25, 190);
        // the wagon: on a track across the walk's way, six metres ahead, standing until it is set going
        listed.is(WAGON, new Things.Wagon(), Stuff.CONCRETE);
        world.driven(new Rigid(WAGON, new Box(1_200, 100, 300), Stuff.CONCRETE.dense()),
                new Placed(new Spot(BEGINS, 50, TRACK)), new Moved());
        stood("the wagon's ball", new Things.Ball(), Stuff.RUBBER, new Ellipsoid(30),
                new Spot(BEGINS - 300, 115, TRACK));
        stood("the wagon's melon", melon, Stuff.RIND, new Ellipsoid(26, 28, 36), new Spot(BEGINS, 114, TRACK));
        // chocked before and behind, so it rides when the wagon sets off or stops at once; the ball is not
        final Box chock = new Box(10, 34, 60);
        stood("the wagon's rear chock", new Things.Chock(), Stuff.TIMBER, chock, new Spot(BEGINS - 18, 117, TRACK));
        stood("the wagon's front chock", new Things.Chock(), Stuff.TIMBER, chock, new Spot(BEGINS + 18, 117, TRACK));
        stood("the wagon's crate", crate, Stuff.TIMBER, new Box(60, 60, 60), new Spot(BEGINS + 300, 130, TRACK));
        // by the walk's beginning, to be taken up: two watermelons and an apple
        stood("the near melon", melon, Stuff.RIND, new Ellipsoid(36, 28, 26), new Spot(-90, 14, NEAR));
        stood("the spare melon", melon, Stuff.RIND, new Ellipsoid(36, 28, 26), new Spot(-160, 14, NEAR));
        stood("the apple", new Things.Apple(), Stuff.PEEL, new Ellipsoid(8), new Spot(-40, 4, NEAR));
        return world;
    }

    private void stood(final String name, final Node kind, final Stuff stuff, final ShapeImpl<?> shape,
                       final Spot at) {
        listed.is(name, kind, stuff);
        world.stood(new Rigid(name, shape, stuff.dense()), new Placed(at));
    }

    /**
     * A thing lying on the ramp, turned as the ramp is: its middle so far {@code across} the ramp from the ramp's
     * middle, so far {@code over} the ramp's top, and so far {@code upSlope} from the middle of its length.
     */
    private void sloped(final String name, final Node kind, final Stuff stuff, final ShapeImpl<?> shape,
                        final double across, final double over, final double upSlope) {
        final double tilt = Math.toRadians(slope);
        final Turn tilted = tilt == 0 ? new Turn() : new Turn(new Spot(1, 0, 0), -tilt);
        final Spot top = new Spot(RAMP, LONG / 2 * Math.sin(tilt), AHEAD + LONG / 2 * Math.cos(tilt));
        listed.is(name, kind, stuff);
        world.stood(new Rigid(name, shape, stuff.dense()),
                new Placed(top.plus(tilted.turned(new Spot(across, over, upSlope))), tilted));
    }
}
