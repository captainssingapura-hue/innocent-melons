package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.PartAt;
import melons.innocent.vehiclelab.drawn.Coats;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    /** Every part where it was put; every part in its group's finish. */
    private static final Function<PartAt, Mat4> STILL = part -> new Mat4();
    private static final Function<PartAt, Optional<Coat>> AS_SHOWN = part -> Optional.empty();

    private final List<Drawn> drawn = new ArrayList<>();
    private final Coats coats;

    /** {@code built}, on {@code card}, its layouts as {@code laid} says. */
    public PlatformDrawn(final Card card, final Laid laid, final Assembled built) {
        this(card, laid, built, new Vec3(0, 0, 0));
    }

    /** The same, its own nought standing at {@code at}, as somewhere it is walked round has it. */
    public PlatformDrawn(final Card card, final Laid laid, final Assembled built, final Vec3 at) {
        try {
            this.coats = new Coats(card, laid);
            built.placed().forEach((part, placed) -> drawn.add(new Drawn(part, new Shaped(card, laid,
                    part.shape().form()), new Mat4(at.plus(placed)))));
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** What of it is not seen through, as {@code shown} says, drawn by {@code pass} as {@code camera} sees it. */
    public void drawn(final Pass pass, final Camera camera, final OnShow shown) {
        drawn(pass, camera, shown, new Mat4(), STILL, AS_SHOWN, false);
    }

    /** What of it is seen through, drawn the same way: after everything else. */
    public void seenThrough(final Pass pass, final Camera camera, final OnShow shown) {
        drawn(pass, camera, shown, new Mat4(), STILL, AS_SHOWN, true);
    }

    /**
     * What of it is not seen through, the whole of it carried by {@code by}, turned and moved as it drives, say; and
     * each part moved first, in the platform's own frame, as {@code each} says: a wheel steered and turning, say.
     */
    public void drawn(final Pass pass, final Camera camera, final OnShow shown, final Mat4 by,
                      final Function<PartAt, Mat4> each) {
        drawn(pass, camera, shown, by, each, AS_SHOWN, false);
    }

    /**
     * The same, each part in the finish {@code lit} gives it where it gives one, a lamp lit and glowing, say, and in
     * its group's where it gives none.
     */
    public void drawn(final Pass pass, final Camera camera, final OnShow shown, final Mat4 by,
                      final Function<PartAt, Mat4> each, final Function<PartAt, Optional<Coat>> lit) {
        drawn(pass, camera, shown, by, each, lit, false);
    }

    /** What of it is seen through, drawn the same way. */
    public void seenThrough(final Pass pass, final Camera camera, final OnShow shown, final Mat4 by,
                            final Function<PartAt, Mat4> each) {
        drawn(pass, camera, shown, by, each, AS_SHOWN, true);
    }

    /** What of it is seen through, drawn the same way, each part in the finish {@code lit} gives it, if any. */
    public void seenThrough(final Pass pass, final Camera camera, final OnShow shown, final Mat4 by,
                            final Function<PartAt, Mat4> each, final Function<PartAt, Optional<Coat>> lit) {
        drawn(pass, camera, shown, by, each, lit, true);
    }

    private void drawn(final Pass pass, final Camera camera, final OnShow shown, final Mat4 by,
                       final Function<PartAt, Mat4> each, final Function<PartAt, Optional<Coat>> lit,
                       final boolean seenThrough) {
        for (final Drawn part : drawn) {
            if (!shown.hidden().contains(part.part().part().group())) {
                final Coat coat = lit.apply(part.part()).orElseGet(() -> shown.coat(part.part().part()));
                if (coats.seenThrough(coat) == seenThrough) {
                    coats.drawn(pass, part.form(), by.times(each.apply(part.part())).times(part.at()), camera, coat);
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
