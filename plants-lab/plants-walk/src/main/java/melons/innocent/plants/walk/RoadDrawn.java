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
 * its lines painted on it, made again whenever what the road has to hand changes; the ground the walks' own plain
 * green, a sheet under wherever it is asked for. And, from the same road, its pavement near a traveller, to be
 * stood on: nothing keeps the traveller to it.
 */
public final class RoadDrawn implements AutoCloseable {

    /** How far each way of a traveller its pavement is put in the way, to be stood on. */
    private static final double NEAR = 20_000;
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

    /** What of the road is in the way of a traveller at {@code along}: its pavement near there, to be stood on. */
    public List<Block> stoodOn(final double along) {
        return paved.stoodOn(along - NEAR, along + NEAR);
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
