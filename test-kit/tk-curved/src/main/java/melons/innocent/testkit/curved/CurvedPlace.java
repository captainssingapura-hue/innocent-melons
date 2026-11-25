package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Placed;

import java.util.Map;
import java.util.TreeMap;

/**
 * The trial of curved surfaces as a place: each shell drawn as one mesh made from its sheet, in the lab's material
 * it is made of, and in the avatar's way as the engine's own pavement told the sheet's height at every spot, in
 * bands of height where a shell passes over itself. So what is drawn, what is stood on and what is said of the surface
 * all come from the one sheet.
 */
public final class CurvedPlace implements Place {

    private final Curves curves;
    private final Treading treading;
    private final Map<String, Looked> looks = new TreeMap<>();
    private final Map<String, Shaped> shapes = new TreeMap<>();

    /** {@code curves} on {@code card}, with what is trodden on said as {@code treading} has it. */
    CurvedPlace(final Card card, final Laid laid, final Curves curves, final Treading treading) {
        this.curves = curves;
        this.treading = treading;
        final Shades shades = new Shades();
        try {
            for (final String name : curves.names()) {
                looks.put(name, new Looked(card, new Look(laid, shades.of(curves.made(name)))));
                shapes.put(name, new Shaped(card, laid, new Meshed(curves.shell(name)).formed()));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The trial on {@code card}, nobody walking it yet. */
    public CurvedPlace(final Card card, final Laid laid) {
        this(card, laid, new Curves());
    }

    private CurvedPlace(final Card card, final Laid laid, final Curves curves) {
        this(card, laid, curves, new Treading(curves));
    }

    @Override
    public Obstacles inTheWay() {
        return curves.inTheWay();
    }

    @Override
    public String said() {
        return "Curved surfaces: a spiral ramp, a vault and a dome, each one sheet" + System.lineSeparator()
                + treading.said();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        for (final String name : curves.names()) {
            looks.get(name).drawn(pass, shapes.get(name), stands(curves.placed(name)), camera, 1,
                    new Vec3(1, 1, 1));
        }
    }

    /** What takes a thing's own frame to the world, where it stands as {@code placed} says. */
    private Mat4 stands(final Placed placed) {
        final Turn back = placed.turned().back();
        final Spot across = back.turned(new Spot(1, 0, 0));
        final Spot up = back.turned(new Spot(0, 1, 0));
        final Spot along = back.turned(new Spot(0, 0, 1));
        return new Mat4(new Vec3(placed.at().across(), placed.at().up(), placed.at().along())).times(new Mat4(
                new Vec3(across.across(), across.up(), across.along()), new Vec3(up.across(), up.up(), up.along()),
                new Vec3(along.across(), along.up(), along.along())));
    }

    @Override
    public void close() {
        shapes.values().forEach(Shaped::close);
        looks.values().forEach(Looked::close);
    }
}
