package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.PartAt;
import melons.innocent.vehiclelab.drawn.Coats;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.ArrayList;
import java.util.List;

/**
 * A platform put together, on the card: each part on its side sent once, in its own frame, the left as the right's
 * mirror, and drawn standing where the assembly placed it, in whatever its group is finished in ({@link Coats}); a
 * group not shown left out. What is seen through is drawn apart, after everything else. Closed, it is gone from the
 * card.
 *
 * @see Assembled
 */
public final class PlatformDrawn implements AutoCloseable {

    /** A part on the card, and where it stands. */
    private record Drawn(PartAt part, Shaped form, Mat4 at) {
    }

    private final List<Drawn> drawn = new ArrayList<>();
    private final Coats coats;

    /** {@code built}, on {@code card}, its layouts as {@code laid} says. */
    public PlatformDrawn(final Card card, final Laid laid, final Assembled built) {
        try {
            this.coats = new Coats(card, laid);
            built.placed().forEach((part, at) -> drawn.add(new Drawn(part, new Shaped(card, laid,
                    part.shape().form()), new Mat4(at))));
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** What of it is not seen through, as {@code shown} says, drawn by {@code pass} as {@code camera} sees it. */
    public void drawn(final Pass pass, final Camera camera, final OnShow shown) {
        drawn(pass, camera, shown, false);
    }

    /** What of it is seen through, drawn the same way: after everything else. */
    public void seenThrough(final Pass pass, final Camera camera, final OnShow shown) {
        drawn(pass, camera, shown, true);
    }

    private void drawn(final Pass pass, final Camera camera, final OnShow shown, final boolean seenThrough) {
        for (final Drawn part : drawn) {
            if (!shown.hidden().contains(part.part().part().group())) {
                final Coat coat = shown.coats().get(part.part().part().group());
                if (coats.seenThrough(coat) == seenThrough) {
                    coats.drawn(pass, part.form(), part.at(), camera, coat);
                }
            }
        }
    }

    @Override
    public void close() {
        drawn.forEach(d -> d.form().close());
        drawn.clear();
        if (coats != null) {
            coats.close();
        }
    }
}
