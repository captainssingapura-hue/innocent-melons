package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.core.Point;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * Engines on show: each on a stand of its own, side by side across the floor, its crank level with the others',
 * every one put together so it can be looked at alone or beside the rest. The stands are one steel part, painted,
 * the root; each engine bolted to its stand at its crank's front, its crank, rods and pistons in it.
 *
 * @param engines the engines shown, from the left
 */
public record EngineShow(List<EngineFigures> engines) {

    /** How far apart the stands are; how high the cranks stand over the floor; how high a stand is. */
    private static final double APART = 85;
    private static final double CRANK = 45;
    private static final double STAND = 4;

    public EngineShow {
        engines = List.copyOf(engines);
    }

    /** The whole range. */
    public EngineShow() {
        this(new EngineRange().all());
    }

    /** It, as a platform to be put together. */
    public Platform platform() {
        final Shapes shapes = new Shapes(16);
        final List<Point> points = new ArrayList<>();
        final List<Mesh> stands = new ArrayList<>();
        final List<Part> blocks = new ArrayList<>();
        for (int k = 0; k < engines.size(); k++) {
            final EngineFigures e = engines.get(k);
            final double x = (k - (engines.size() - 1) / 2.0) * APART;
            points.add(new Point(e.name(), new Vec3(x, CRANK, -e.length() / 2)));
            stands.add(shapes.box(new Vec3(x, STAND / 2, 0), 50, STAND, e.length() + 20));
            stands.add(shapes.box(new Vec3(x, (CRANK - 12 + STAND) / 2, 0), 8, CRANK - 12 - STAND, 8));
            blocks.add(new CastBlock(e).part());
        }
        final Part stand = new Part("engine stands", Group.ENGINE, false, points, shapes.joined(stands),
                new Made(Material.STEEL, false));
        final List<Connection> joined = new ArrayList<>();
        for (int k = 0; k < engines.size(); k++) {
            joined.add(new Connection(engines.get(k).name() + " on its stand", stand, engines.get(k).name(),
                    blocks.get(k), "crank, front", Joining.BOLTED));
            joined.addAll(new EngineInternals(engines.get(k)).joined(blocks.get(k)));
        }
        return new Platform("the GLM engines on show", stand, joined);
    }
}
