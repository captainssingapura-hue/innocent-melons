package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Spot;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Kind;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A forest without end, in winter, made of a set of samples ({@link Samples}, {@link Endless}) and drawn from them
 * alone ({@link SampledDrawn}): oaks, walnuts and white birches, two grown of each. The run says how many samples
 * there are and how big each is, {@code -Dforest.samples=} (32) and {@code -Dforest.sample=} metres (25); how far
 * each level reaches ({@link Reaches}); and within how many metres of the eye its squares cast shadows by rays,
 * {@code -Dforest.shadows=} (300).
 *
 * <p>Nothing of it is made beforehand but its samples: its ground and what is in the avatar's way go with the eye
 * as its trees do. Its ground is the walks' own plain green, a sheet under wherever the eye is that reaches four
 * kilometres each way and at least as far as the trees are drawn, so there is ground under every tree in sight
 * however far the avatar has gone, and ground on to the skyline where the trees are cut off nearer than that. And its
 * trunks are in the way as posts within some hundred and fifty metres of the eye, said again whenever the eye has
 * gone two squares from where they were last said, which is long before the avatar could reach their edge.
 */
public final class EndlessForestPlace implements Place {

    private static final double METRE = 100;
    private static final int EACH = 2;
    private static final long CHANCE = 20261124L;
    /** How many squares each way from the eye's own the trunks are in the way, and how far it goes before more. */
    private static final long POSTED = 6;
    private static final long STRAYED = 2;
    /** The walks' own ground: its colour, and how thick the sheet is, in centimetres. */
    private static final String GRASS = "#8fa66f";
    private static final double THICK = 100;
    /** How far the ground reaches each way from the eye at least, in centimetres: on past where the trees end. */
    private static final double GROUND = 400_000;

    private final List<WoodDrawn.Grown> grown = new ArrayList<>();
    /** Each grown tree's trunk: kept, since asking a tree for its boughs grows it afresh. */
    private final List<Bough> trunks = new ArrayList<>();
    /** Each sample's trees, in its own frame: kept, so saying what is in the way works nothing out again. */
    private final List<List<WoodDrawn.Planted>> planted = new ArrayList<>();
    private final Endless endless;
    private final Reaches reaches;
    private final SampledDrawn forest;
    private final Formed ground;
    /** The square the eye was last readied over, and the one the trunks in the way were last said round. */
    private long across;
    private long along;
    private long postedAcross;
    private long postedAlong;
    /**
     * How many seconds ahead the eye looks, a square being drawn as it will be needed at the nearest the eye comes
     * to it in that while; and the speed above which nothing is drawn at its finest, every twig. Set while
     * walking, and to begin with as the run says: {@code -Dforest.ahead=} (3) and {@code -Dforest.coarser=}
     * kilometres an hour (50).
     */
    /** How far its finest level reaches, and its level with twigs gone, in metres: as its reaches have them first. */
    private final Dial fine = new Dial("Finest out to, metres", 5, 100, new Reaches().fine());
    private final Dial twigs = new Dial("Twigs gone out to, metres", 20, 250, new Reaches().twigs());
    private final Dial ahead = new Dial("Looked ahead, seconds", 0, 60,
            Double.parseDouble(System.getProperty("forest.ahead", "3")));
    private final Dial coarser = new Dial("Finest level off above, km an hour", 10, 200,
            Double.parseDouble(System.getProperty("forest.coarser", "50")));
    /** The finest level any of its plants is drawn at, whatever its other rules would have: chosen while walking. */
    private final Cap cap = new Cap();
    /** How whoever looks at it is moving, as last told: standing still, until told. */
    private Motion moving = new Motion(new Spot(0, 0, 0));

    /** The forest on {@code card}, as the run says it. */
    public EndlessForestPlace(final Card card, final Laid laid) {
        this(card, laid, Integer.getInteger("forest.samples", 32),
                Double.parseDouble(System.getProperty("forest.sample", "25")), new Reaches(),
                Double.parseDouble(System.getProperty("forest.shadows", "300")),
                Boolean.parseBoolean(System.getProperty("forest.beforehand", "true")));
    }

    /**
     * The forest on {@code card}, of {@code samples} samples {@code side} metres each way, casting shadows by rays
     * within {@code shadows} metres of the eye: none, for nothing; {@code beforehand}, with every level in use baked
     * for every sample before it is first drawn, so that nothing is baked or sent while the eye moves.
     */
    public EndlessForestPlace(final Card card, final Laid laid, final int samples, final double side,
                              final Reaches reaches, final double shadows, final boolean beforehand) {
        for (final Kind kind : Kind.values()) {
            for (int number = 1; number <= EACH; number++) {
                grown.add(new WoodDrawn.Grown(kind, kind.tree(number)));
                trunks.add(grown.getLast().tree().boughs().getFirst());
            }
        }
        this.reaches = reaches;
        this.endless = new Endless(new Samples(samples, side * METRE, grown.size(), CHANCE), CHANCE);
        for (int sample = 0; sample < samples; sample++) {
            planted.add(endless.samples().planted(sample));
        }
        this.ground = new Formed(card, new Look(laid, new Surface(new Colour(GRASS), List.of(), new Colour(0, 0, 0),
                1)), new Cuboid(1, 1, 1).formed());
        try {
            this.forest = new SampledDrawn(card, laid, endless, grown, reaches, shadows * METRE, beforehand);
        } catch (final RuntimeException refused) {
            ground.close();
            throw refused;
        }
    }

    @Override
    public String said() {
        return forest.said();
    }

    @Override
    public boolean onTheFlat() {
        return false;
    }

    @Override
    public Obstacles inTheWay() {
        postedAcross = across;
        postedAlong = along;
        final List<Block> posts = new ArrayList<>();
        for (long b = along - POSTED; b <= along + POSTED; b++) {
            for (long a = across - POSTED; a <= across + POSTED; a++) {
                final Mat4 placed = endless.placed(a, b);
                for (final WoodDrawn.Planted tree : planted.get(endless.sample(a, b))) {
                    final Bough trunk = trunks.get(tree.grown());
                    final double thick = trunk.thick().getFirst() * tree.times();
                    final Mat4 stands = placed.times(tree.placed());
                    posts.add(new Block(stands.at(0, 3), stands.at(2, 3), thick, thick, 0, 0,
                            trunk.ends().y() * tree.times()));
                }
            }
        }
        return new Obstacles(posts);
    }

    @Override
    public Optional<Obstacles> rearranged() {
        return Math.abs(across - postedAcross) >= STRAYED || Math.abs(along - postedAlong) >= STRAYED
                ? Optional.of(inTheWay()) : Optional.empty();
    }

    /** Its reaches as its dials now have them: the two nearest as set, the rest as they were. */
    private Reaches reached() {
        final double finest = fine.value();
        final double gone = Math.max(finest, twigs.value());
        return new Reaches(finest, gone, Math.max(gone, reaches.boughs()), Math.max(gone, reaches.limbs()),
                Math.max(gone, reaches.far()));
    }

    @Override
    public List<Dial> dials() {
        return List.of(cap.dial(), fine, twigs, ahead, coarser);
    }

    @Override
    public void moving(final Motion observer) {
        moving = observer;
    }

    @Override
    public void readied(final Camera camera) {
        across = (long) Math.floor(camera.eye().x() / endless.samples().side());
        along = (long) Math.floor(camera.eye().z() / endless.samples().side());
        forest.readied(camera, endless.seen(camera.eye(), reached(),
                cap.over(new Going(moving, ahead.value(), coarser.value()))));
    }

    @Override
    public boolean settled() {
        return forest.settled();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        final double side = endless.samples().side();
        final double wide = 2 * Math.max(GROUND, reaches.far() * METRE + 2 * side);
        ground.drawn(pass, new Mat4(new Vec3((across + 0.5) * side, -THICK / 2, (along + 0.5) * side))
                .times(new Sized(wide, THICK, wide).matrix()), camera, 1);
        forest.drawn(pass, camera);
    }

    /** How many triangles the squares readied last come to. */
    public long triangles() {
        return forest.triangles();
    }

    @Override
    public void close() {
        forest.close();
        ground.close();
    }
}
