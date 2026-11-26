package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Spot;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Stage;
import melons.innocent.road.surface.RoadSpec;
import melons.innocent.road.surface.RoadSurface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A road through a forest without end: the forest of samples ({@link Endless}, {@link SampledDrawn}), in winter,
 * standing in a strip either side of a road that wanders on for ever both ways ({@link Road}), and round the eye
 * wherever it goes off the road; the road paved as the road works surface a country road today ({@link Paved}): two
 * lanes and a narrow shoulder to each, with no tree on it or within three metres of its edge. The road is made as
 * it is travelled and is another road every time; the forest it runs through is the one forest, the same wherever
 * the road happens to cross it.
 *
 * <p>A square of the forest the road touches cannot be a copy of its sample, some of its trees being gone, so its
 * trees that are left are drawn one by one ({@link WoodDrawn}), each at the level its square is at, so they change
 * with the squares round them and by no distances of their own; every other square in the strip is its sample, as
 * in the forest without a road.
 *
 * <p>Nothing keeps the traveller to the road: it may go off among the trees as far as it likes, or back along the
 * road as far as it likes, which is the same road when it is come back to. Its pavement, to be stood on, and the
 * trunks near the eye are said again whenever the eye has gone two squares on.
 */
public final class RoadForestPlace implements Place {

    private static final double METRE = 100;
    private static final int EACH = 2;
    private static final long CHANCE = 20261124L;
    /** How far either side of the road's middle the forest stands, and how far past its edge no tree does. */
    private static final double STRIP = 15_000;
    private static final double VERGE = 300;
    /** The road's lanes and its shoulders, how wide; and a stretch's length, which a road's specification asks. */
    private static final double LANE = 300;
    private static final double SHOULDER = 75;
    private static final double STRETCH = 10_000;
    /** How far a road may be from a square's middle, across, and still touch it; beyond that it is not looked at. */
    private static final double TOUCHING = 4_500;
    /** Within how many metres of the eye its squares cast shadows by rays, at most. */
    private static final double SHADOWED = 300;

    private final List<WoodDrawn.Grown> grown = new ArrayList<>();
    /** Each sample's trees, in its own frame: kept, so nothing of a sample is worked out again. */
    private final List<List<WoodDrawn.Planted>> planted = new ArrayList<>();
    private final Endless endless;
    private final Reaches reaches;
    private final Road road;
    private final RoadDrawn drawn;
    /** Within how far of the road's middle no tree stands, in centimetres: its half width and its verge. */
    private final double clear;
    private final SampledDrawn forest;
    private final WoodDrawn wood;
    /** The squares the road touched when their trees were last planted, each as its two numbers in one. */
    private Set<String> touched = new HashSet<>();
    /** Which square each tree drawn one by one stands in, in the order planted. */
    private List<String> standing = new ArrayList<>();
    private Vec3 eye = new Vec3(0, 0, 0);
    /** The trunks near the eye, in its way. */
    private final TrunksInTheWay standingInTheWay;
    /** The dials it is drawn by, set while walking. */
    private final ForestDials dials = new ForestDials();
    /** How whoever looks at it is moving, as last told: standing still, until told. */
    private Motion moving = new Motion(new Spot(0, 0, 0));

    /** The forest and a road nobody chose, on {@code card}, as the run says the forest. */
    public RoadForestPlace(final Card card, final Laid laid) {
        this(card, laid, new Road(new Wandering()), Integer.getInteger("forest.samples", 32), new Reaches());
    }

    /** The forest of {@code samples} samples on {@code card}, with {@code road} through it. */
    public RoadForestPlace(final Card card, final Laid laid, final Road road, final int samples,
                           final Reaches reaches) {
        for (final Kind kind : Kind.values()) {
            for (int number = 1; number <= EACH; number++) {
                grown.add(new WoodDrawn.Grown(kind, kind.tree(number)));
            }
        }
        this.road = road;
        this.reaches = reaches;
        this.endless = new Endless(new Samples(samples, 25 * METRE, grown.size(), CHANCE), CHANCE);
        for (int sample = 0; sample < samples; sample++) {
            planted.add(endless.samples().planted(sample));
        }
        // a country road as the road works surface one today: two lanes of three metres, a narrow shoulder to each
        this.drawn = new RoadDrawn(card, laid, road, new RoadSurface().section(
                new RoadSpec<Civilization, CommonEras.Contemporary>(LANE, SHOULDER, STRETCH)));
        this.clear = drawn.wide() / 2 + VERGE;
        this.standingInTheWay = new TrunksInTheWay(endless, planted, grown, this::cleared);
        try {
            this.wood = new WoodDrawn(card, laid, grown, List.of(), Optional.empty());
        } catch (final RuntimeException refused) {
            drawn.close();
            throw refused;
        }
        try {
            this.forest = new SampledDrawn(card, laid, endless, grown, reaches,
                    Math.min(SHADOWED, reaches.far()) * METRE, true);
        } catch (final RuntimeException refused) {
            wood.close();
            drawn.close();
            throw refused;
        }
    }

    /** The road through it. */
    public Road road() {
        return road;
    }

    @Override
    public String said() {
        return String.format(java.util.Locale.ENGLISH, "A road: %.0f m along, %.0f m across; %d squares of it "
                + "tree by tree%n", eye.z() / METRE, road.has(eye.z()) ? road.across(eye.z()) / METRE : 0,
                touched.size()) + forest.said();
    }

    @Override
    public boolean onTheFlat() {
        return false;
    }

    /** Whether a tree standing at {@code across}, {@code along} is gone: on the road, or close beside it. */
    private boolean cleared(final double across, final double along) {
        return road.has(along) && road.off(across, along) <= clear;
    }

    @Override
    public List<Dial> dials() {
        return dials.dials();
    }

    @Override
    public void moving(final Motion observer) {
        moving = observer;
    }

    @Override
    public void readied(final Camera camera) {
        eye = camera.eye();
        if (road.reached(eye.z())) {
            drawn.remade();
            touched = new HashSet<>();
        }
        final double side = endless.samples().side();
        final List<Endless.Seen> samples = new ArrayList<>();
        final List<WoodDrawn.Planted> trees = new ArrayList<>();
        final Set<String> touching = new HashSet<>();
        final List<String> squares = new ArrayList<>();
        final Map<String, Stage> levels = new HashMap<>();
        wood.noFinerThan(dials.cap().stage());
        for (final Endless.Seen seen : endless.seen(eye, dials.reached(reaches), dials.looking(moving))) {
            final Vec3 middle = endless.middle(seen.across(), seen.along());
            final double off = Math.abs(middle.x() - road.across(Math.clamp(middle.z(), road.from(), road.to())));
            // the strip along the road, and round the eye wherever it has gone: the forest goes on every way
            if (off > STRIP + side && Math.hypot(middle.x() - eye.x(), middle.z() - eye.z()) > STRIP + side) {
                continue;
            }
            final List<WoodDrawn.Planted> stood = off > TOUCHING || !road.has(middle.z()) ? List.of()
                    : endless.placed(seen.across(), seen.along(), planted.get(seen.sample()));
            if (stood.stream().anyMatch(tree -> cleared(tree.placed().at(0, 3), tree.placed().at(2, 3)))) {
                final String square = seen.across() + " " + seen.along();
                touching.add(square);
                levels.put(square, seen.level());
                stood.stream().filter(tree -> !cleared(tree.placed().at(0, 3), tree.placed().at(2, 3)))
                        .forEach(tree -> {
                            trees.add(tree);
                            squares.add(square);
                        });
            } else {
                samples.add(seen);
            }
        }
        forest.readied(camera, samples);
        if (!touching.equals(touched)) {
            wood.replanted(trees);
            touched = touching;
            standing = squares;
        }
        // each tree at its square's level, so the trees beside the road change as the squares round them do
        wood.told(standing.stream().map(levels::get).toList());
    }

    @Override
    public Obstacles inTheWay() {
        final List<Block> blocks = new ArrayList<>(drawn.stoodOn(eye.z()));
        blocks.addAll(standingInTheWay.posted(eye));
        return new Obstacles(blocks);
    }

    @Override
    public Optional<Obstacles> rearranged() {
        return standingInTheWay.strayed(eye) ? Optional.of(inTheWay()) : Optional.empty();
    }

    @Override
    public boolean settled() {
        return forest.settled();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        drawn.drawn(pass, camera, new Mat4(new Vec3(eye.x(), 0, eye.z())));
        forest.drawn(pass, camera);
        wood.drawn(pass, camera);
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        wood.seenThrough(pass, camera);
    }

    /** How many squares the road touches, of those in reach: each drawn tree by tree. */
    public int touched() {
        return touched.size();
    }

    @Override
    public void close() {
        forest.close();
        wood.close();
        drawn.close();
    }
}
