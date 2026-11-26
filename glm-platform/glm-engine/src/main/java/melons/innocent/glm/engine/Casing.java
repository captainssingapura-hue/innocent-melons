package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * How a block's casing is cast, in section, where its kinds differ: in line or in a V, its banks standing over the
 * crank ({@link UprightCase}); a boxer's, its banks lying flat out of a crankcase ({@link BoxerCase}); a twin's, a
 * crankcase its cylinders' barrels are bolted on ({@link TwinCase}). Its section is drawn across and up, round to
 * the left, starting along its open bottom.
 */
interface Casing {

    /** Its section, the same all along. */
    List<Spot> section();

    /** A bulkhead between cylinders, of the whole {@code section} or less. */
    List<Spot> between(List<Spot> section);

    /** Where the engine's mount is on side {@code side}. */
    Vec3 mount(int side);
}
