package melons.innocent.demos.village;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.building.bench.LabLooks;
import melons.innocent.building.walk.Meshed;
import melons.innocent.forms.Facets;

import java.util.ArrayList;
import java.util.List;

/**
 * The village's buildings on the card, each as the buildings bench looks into it: every solid, a mesh to each coat in
 * the surface the bench gives for it ({@link LabLooks}), set down where the building stands; its glass drawn after all
 * that is not seen through.
 */
public final class BuiltDrawn implements AutoCloseable {

    /** A mesh of a building's, and where it stands. */
    private record Stood(Formed formed, Mat4 placed) {
    }

    private final List<Stood> solid = new ArrayList<>();
    private final List<Stood> clear = new ArrayList<>();

    /** {@code buildings} on {@code card}, laid out as {@code laid} says. */
    public BuiltDrawn(final Card card, final Laid laid, final List<Built> buildings) {
        final LabLooks looks = new LabLooks();
        try {
            for (final Built building : buildings) {
                final Mat4 placed = building.placed();
                new Facets(building.solids(), 0).sheets().forEach((coat, sheet) -> {
                    final Surface surface = looks.surface(coat);
                    (surface.seenThrough() ? clear : solid).add(new Stood(new Formed(card, new Look(laid, surface),
                            new Meshed(sheet).mesh()), placed));
                });
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** Everything of them that is not seen through. */
    public void drawn(final Pass pass, final Camera camera) {
        solid.forEach(s -> s.formed().drawn(pass, s.placed(), camera, 1));
    }

    /** Their glass. */
    public void seenThrough(final Pass pass, final Camera camera) {
        clear.forEach(s -> s.formed().drawn(pass, s.placed(), camera, 1));
    }

    @Override
    public void close() {
        clear.forEach(s -> s.formed().close());
        solid.forEach(s -> s.formed().close());
        clear.clear();
        solid.clear();
    }
}
