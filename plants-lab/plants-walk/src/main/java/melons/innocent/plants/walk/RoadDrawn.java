package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.road.surface.Section;

import java.util.ArrayList;
import java.util.List;

/**
 * A road on the card, and the ground it lies on: the road paved as its section says ({@link Paved}), asphalt with
 * its lines painted on it, made again whenever the road is made on or lets go of its past; the ground the walks' own
 * plain green, a sheet under wherever it is asked for. And, from the same road, the walls nobody sees
 * that keep a traveller to it: one along each side, some way out, and one across it behind.
 */
public final class RoadDrawn implements AutoCloseable {

    /** How far from the road's middle each wall beside it runs, and how far behind the furthest reached the one
     * across it stands; how long a run of wall is, how thick, how high, and how far each way they are put up. */
    private static final double CONFINED = 3_000;
    private static final double BACK = 10_000;
    private static final double RUN = 1_000;
    private static final double THICK = 200;
    private static final double HIGH = 2_000_000;
    private static final double WALLED = 20_000;
    /** The walks' own ground: its colour, how thick the sheet is, and how far it reaches each way. */
    private static final String GRASS = "#8fa66f";
    private static final double DEEP = 100;
    private static final double GROUND = 800_000;

    private final Card card;
    private final Laid laid;
    private final Road road;
    private final Formed ground;
    private final Paved paved;
    private final Looked asphalt;
    private final Looked paint;
    private final List<Shaped> pavement = new ArrayList<>();
    private final List<Shaped> lines = new ArrayList<>();

    /** {@code road} on {@code card}, paved as {@code section} says. */
    public RoadDrawn(final Card card, final Laid laid, final Road road, final Section section) {
        this.card = card;
        this.laid = laid;
        this.road = road;
        this.paved = new Paved(road, section);
        this.ground = new Formed(card, new Look(laid, new Surface(new Colour(GRASS), List.of(), new Colour(0, 0, 0),
                1)), new Cuboid(1, 1, 1).formed());
        try {
            this.asphalt = new Looked(card, new Look(laid, new Shades().of(new Materials.Asphalt())));
        } catch (final RuntimeException refused) {
            ground.close();
            throw refused;
        }
        try {
            this.paint = new Looked(card, new Look(laid, new Shades().of(new Materials.RoadPaint())));
        } catch (final RuntimeException refused) {
            asphalt.close();
            ground.close();
            throw refused;
        }
        remade();
    }

    /** Its pavement and its lines made again from the road as it now is. */
    public void remade() {
        pavement.forEach(Shaped::close);
        lines.forEach(Shaped::close);
        pavement.clear();
        lines.clear();
        pavement.add(new Shaped(card, laid, paved.pavement(), false));
        final Mesh painted = paved.lines();
        if (painted.triangles() > 0) {
            lines.add(new Shaped(card, laid, painted, false));
        }
    }

    /** How wide the road is, all told, in centimetres. */
    public double wide() {
        return paved.section().wide();
    }

    /**
     * What of the road is in the way of a traveller at {@code along} who has been as far as {@code furthest}: its
     * pavement, to be stood on; and the walls that keep the traveller to it, a run after a run along each side and
     * one across behind.
     */
    public List<Block> walls(final double along, final double furthest) {
        final List<Block> walls = new ArrayList<>(paved.stoodOn(along - WALLED, along + WALLED));
        final double behind = Math.max(road.from(), furthest - BACK);
        final double from = Math.max(behind, along - WALLED);
        final double to = Math.min(road.to() - RUN, along + WALLED);
        for (double at = from; at < to; at += RUN) {
            for (final double side : new double[] {-1, 1}) {
                final Vec3 begins = new Vec3(road.across(at) + side * CONFINED, 0, at);
                final Vec3 ends = new Vec3(road.across(at + RUN) + side * CONFINED, 0, at + RUN);
                walls.add(run(begins, ends));
            }
        }
        final double across = road.across(behind);
        walls.add(run(new Vec3(across - CONFINED - RUN, 0, behind), new Vec3(across + CONFINED + RUN, 0, behind)));
        return walls;
    }

    /** A run of wall from {@code begins} to {@code ends}. */
    private Block run(final Vec3 begins, final Vec3 ends) {
        final Vec3 way = ends.minus(begins).unit();
        final Vec3 middle = begins.plus(ends).times(0.5);
        // the engine turns a block as it turns anything about the upright: its own length then runs this way
        return new Block(middle.x(), middle.z(), ends.minus(begins).length() / 2 + THICK, THICK / 2,
                Math.toDegrees(Math.atan2(-way.z(), way.x())), 0, HIGH);
    }

    /** The ground, under where {@code under} says, and the road on it, drawn by {@code pass}. */
    public void drawn(final Pass pass, final Camera camera, final Mat4 under) {
        ground.drawn(pass, under.times(new Mat4(new Vec3(0, -DEEP / 2, 0)))
                .times(new Sized(GROUND, DEEP, GROUND).matrix()), camera, 1);
        pavement.forEach(shape -> asphalt.drawn(pass, shape, new Mat4(), camera, 1));
        lines.forEach(shape -> paint.drawn(pass, shape, new Mat4(), camera, 1));
    }

    @Override
    public void close() {
        pavement.forEach(Shaped::close);
        lines.forEach(Shaped::close);
        pavement.clear();
        lines.clear();
        paint.close();
        asphalt.close();
        ground.close();
    }
}
