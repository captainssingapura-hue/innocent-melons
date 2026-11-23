package melons.innocent.terrain.drawn;

import com.example.fxsuite.vulkan.term.Surface;
import melons.innocent.terrain.Sample;

import java.util.List;

/**
 * The ground painted plainly: all of it the one layer, {@code label}, in the one {@code surface}, a lab's material.
 *
 * @param label   what the legend says of it
 * @param surface what it is drawn in
 */
public record Plainly(String label, Surface surface) implements Painting {

    /** How light the legend's swatch is, there being no one colour to a material. */
    private static final double GREY = 0.7;

    @Override
    public List<Shown> legend() {
        return List.of(new Shown(label, GREY, GREY, GREY));
    }

    @Override
    public Surface surface(final int layer) {
        return surface;
    }

    @Override
    public boolean covers(final int layer, final Sample at) {
        return layer == 0;
    }

    @Override
    public String at(final double x, final double z) {
        return label;
    }
}
