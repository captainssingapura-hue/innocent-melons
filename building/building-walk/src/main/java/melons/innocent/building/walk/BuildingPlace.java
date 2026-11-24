package melons.innocent.building.walk;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.forms.Facets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * A building as a place to walk round and into, on the card: drawn as finely as it is built, every solid, a mesh to
 * each coat in the surface it is given for it, its glass after all that is not seen through; and in the way as the
 * building itself says it is, its own shape, which the engine's avatar is stopped by, stands on and walks into. Its
 * doors alone are left out, neither drawn nor in the way ({@link Doorless}). It stands where it is built, in its own
 * frame, its front toward along. Closed, it is gone from the card.
 */
public final class BuildingPlace implements Place {

    private final List<Formed> solid = new ArrayList<>();
    private final List<Formed> clear = new ArrayList<>();
    private final Obstacles inTheWay;

    /** {@code structure} on {@code card}, each of its coats drawn in the surface {@code looks} gives for it. */
    public BuildingPlace(final Card card, final Laid laid, final Structure structure,
                         final Function<Facets.Coat, Surface> looks) {
        final Doorless open = new Doorless(structure);
        final List<Volume> shape = open.shape();
        this.inTheWay = Obstacles.NONE.with(shape, IntStream.range(0, shape.size()).boxed().toList());
        try {
            new Facets(open.solids(), 0).sheets().forEach((coat, sheet) -> {
                final Surface surface = looks.apply(coat);
                (surface.seenThrough() ? clear : solid).add(new Formed(card, new Look(laid, surface),
                        new Meshed(sheet).mesh()));
            });
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    @Override
    public Obstacles inTheWay() {
        return inTheWay;
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        solid.forEach(part -> part.drawn(pass, new Vec3(0, 0, 0), camera));
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        clear.forEach(part -> part.drawn(pass, new Vec3(0, 0, 0), camera));
    }

    @Override
    public void close() {
        clear.forEach(Formed::close);
        solid.forEach(Formed::close);
        clear.clear();
        solid.clear();
    }
}
