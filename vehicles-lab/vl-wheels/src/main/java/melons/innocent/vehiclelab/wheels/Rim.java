package melons.innocent.vehiclelab.wheels;

import com.example.fxsuite.vulkan.form.Mesh;

/**
 * A rim of one design: made at any size, in the wheel's own frame, its axle across, its middle at nought, its face
 * toward the outside, more across, up up and ahead along; its barrel round the hub, 2 cm thick, four fifths as wide as
 * its tyre, set in from the tyre's sides; its centre bolted to the hub, whose face is {@code mount} out from the
 * wheel's middle.
 */
public interface Rim {

    /** It, at {@code size}, bolted to a hub whose face is {@code mount} out from the wheel's middle. */
    Mesh mesh(WheelSize size, double mount);

    /** How thick its barrel is. */
    default double thick() {
        return 2;
    }

    /** How far its barrel's inside is from the axle: what a brake behind it must keep inside. */
    default double inside(final WheelSize size) {
        return size.rimRadius() - thick();
    }

    /** How wide its barrel is. */
    default double barrel(final WheelSize size) {
        return size.wide() * 0.8;
    }
}
