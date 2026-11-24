package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Forming;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A road on the card, and the ground it lies on: the road a ribbon of bare earth four metres wide following its
 * curve, a hair over the ground, made again whenever the road is made on or lets go of its past; the ground the
 * walks' own plain green, a sheet under wherever it is asked for. And, from the same road, the walls nobody sees
 * that keep a traveller to it: one along each side, some way out, and one across it behind.
 */
public final class RoadDrawn implements AutoCloseable {

    /** Half the road's width, how far over the ground it lies, and how far apart its points are, in centimetres. */
    private static final double HALF = 200;
    private static final double OVER = 1;
    private static final double PIECE = 250;
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
    private final Looked earth;
    private final List<Shaped> ribbon = new ArrayList<>();

    /** {@code road} on {@code card}. */
    public RoadDrawn(final Card card, final Laid laid, final Road road) {
        this.card = card;
        this.laid = laid;
        this.road = road;
        this.ground = new Formed(card, new Look(laid, new Surface(new Colour(GRASS), List.of(), new Colour(0, 0, 0),
                1)), new Cuboid(1, 1, 1).formed());
        try {
            this.earth = new Looked(card, new Look(laid, new Shades().of(new Materials.Topsoil())));
        } catch (final RuntimeException refused) {
            ground.close();
            throw refused;
        }
        remade();
    }

    /** The ribbon made again from the road as it now is. */
    public void remade() {
        ribbon.forEach(Shaped::close);
        ribbon.clear();
        ribbon.add(new Shaped(card, laid, formed(), false));
    }

    /** The road as a mesh: a ribbon along all of it that is had. */
    public Mesh formed() {
        final Vec3 up = new Vec3(0, 1, 0);
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final List<Integer> left = new ArrayList<>();
        final List<Integer> right = new ArrayList<>();
        for (double along = road.from(); along <= road.to(); along += PIECE) {
            final double slope = road.slope(along);
            final Vec3 beside = new Vec3(1, 0, -slope).unit().times(HALF);
            final Vec3 middle = new Vec3(road.across(along), OVER, along);
            left.add(forming.point(middle.minus(beside), up));
            right.add(forming.point(middle.plus(beside), up));
        }
        for (int piece = 0; piece + 1 < left.size(); piece++) {
            forming.faced(left.get(piece), right.get(piece), right.get(piece + 1));
            forming.faced(left.get(piece), right.get(piece + 1), left.get(piece + 1));
        }
        return forming.formed();
    }

    /**
     * The walls that keep a traveller at {@code along} to the road, who has been as far as {@code furthest}: a run
     * after a run along each side, and one across behind.
     */
    public List<Block> walls(final double along, final double furthest) {
        final List<Block> walls = new ArrayList<>();
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
        ribbon.forEach(shape -> earth.drawn(pass, shape, new Mat4(), camera, 1));
    }

    @Override
    public void close() {
        ribbon.forEach(Shaped::close);
        ribbon.clear();
        earth.close();
        ground.close();
    }
}
