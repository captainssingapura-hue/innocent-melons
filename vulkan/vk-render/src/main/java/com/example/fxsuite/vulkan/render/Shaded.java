package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Standing;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A frame drawn with its shadows, found whichever way is asked ({@link Shadowing}). It keeps what each way needs
 * on the card: the sun's own picture, always; and, where the looks are laid out to ask rays, what stands in the
 * frame for them to be asked of. A way the card has not is not offered, and asked for all the same is answered
 * with the sun's picture. Closed, all of it is gone from the card.
 */
public final class Shaded implements AutoCloseable {

    private final Shadows pictured;
    private final Optional<Standing> stood;

    /**
     * Shadows on {@code card} for looks laid out as {@code laid} says; the sun's picture {@code pixels} wide and
     * tall.
     */
    public Shaded(final Card card, final Laid laid, final int pixels) {
        this.pictured = new Shadows(card, laid, pixels);
        try {
            this.stood = laid.rays() ? Optional.of(card.standing()) : Optional.empty();
        } catch (final RuntimeException refused) {
            pictured.close();
            throw refused;
        }
    }

    /** The ways shadows can be found here, the one any card has first. */
    public List<Shadowing> offered() {
        return stood.isPresent() ? List.of(Shadowing.PICTURE, Shadowing.RAYS) : List.of(Shadowing.PICTURE);
    }

    /** How many pixels wide and tall the sun's picture is. */
    public int pixels() {
        return pictured.pixels();
    }

    /**
     * A frame drawn on {@code target} under {@code sky}, as {@code framed} tells it, its shadows found
     * {@code by}: whatever {@code casting} draws casts a shadow, and {@code all} is the whole frame. The sun's
     * picture, where it is the way, is drawn through {@code beam}, moved to a whole number of its pixels.
     */
    public void drawn(final Target target, final Vec3 sky, final Framed framed, final Shadowing by,
                      final Beam beam, final Consumer<Pass> casting, final Consumer<Pass> all) {
        final double aspect = (double) target.width() / target.height();
        if (by == Shadowing.RAYS && stood.isPresent()) {
            target.drawn(sky, framed.rayed(aspect), stood.get().stood(casting), all);
        } else {
            final Beam settled = beam.settled(pictured.pixels());
            target.drawn(sky, framed.filling(aspect, settled, pictured.pixels()),
                    pictured.cast(framed, settled, casting), all);
        }
    }

    @Override
    public void close() {
        stood.ifPresent(Standing::close);
        pictured.close();
    }
}
