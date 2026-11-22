package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * The land on the card, as far as its colouring has come: a sheet for each layer it is coloured in so far, each in
 * its layer's surface, drawn {@code up} times as high as it is. Drawn where a flight says, scaled about the spot under
 * the eye. Closed, all of it is gone from the card.
 */
public final class LandDrawn implements AutoCloseable {

    private final List<Formed> parts = new ArrayList<>();
    private final int laid;

    /** {@code grid}'s ground, coloured as far as {@code coloured} says, in {@code painting}'s surfaces. */
    public LandDrawn(final Card card, final Laid laid, final Painting painting, final Grid grid, final double up,
                     final Colouring.Coloured coloured) {
        this.laid = coloured.laid();
        Sheet.INSTANCE.parts(grid, up, coloured.layers()).forEach((layer, sheet) ->
                parts.add(new Formed(card, new Look(laid, painting.surface(layer)), sheet)));
    }

    /** How many layers it is coloured in. */
    public int laid() {
        return laid;
    }

    /** Drawn in {@code pass}, as {@code flight} has it, seen by {@code camera}. */
    public void drawn(final Pass pass, final Flight flight, final Camera camera) {
        parts.forEach(part -> part.drawn(pass, flight.land(), camera, 1));
    }

    @Override
    public void close() {
        parts.forEach(Formed::close);
    }
}
