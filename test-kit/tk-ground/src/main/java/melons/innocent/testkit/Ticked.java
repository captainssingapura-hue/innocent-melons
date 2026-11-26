package melons.innocent.testkit;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Clicked;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Motion;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A going about with something else told each of its steps ({@link Stepping}): all as the going it wraps has it;
 * once it has taken a frame's steps the last of them is told on, the others having been as the next was asked for
 * ({@link Told}); and over the picture, low in its middle, the lines it is given are written.
 */
final class Ticked implements Roaming {

    /** How high the letters over the picture are, how far apart their lines, and how low the lowest is. */
    private static final double LETTER = 0.04;
    private static final double LINE = 0.075;
    private static final double FOOT = -0.78;

    private final Roaming going;
    private final Told told;
    private final Supplier<List<String>> hints;

    Ticked(final Roaming going, final Told told, final Supplier<List<String>> hints) {
        this.going = going;
        this.told = told;
        this.hints = hints;
    }

    @Override
    public double stepped(final double seconds) {
        final double blend = going.stepped(seconds);
        told.caught();
        return blend;
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        going.inTheWay(blocks);
    }

    @Override
    public boolean over() {
        return going.over();
    }

    @Override
    public boolean ends() {
        return going.ends();
    }

    @Override
    public double seconds() {
        return going.seconds();
    }

    @Override
    public Vec3 head(final double blend) {
        return going.head(blend);
    }

    @Override
    public double yaw(final double blend) {
        return going.yaw(blend);
    }

    @Override
    public double tilt(final double blend) {
        return going.tilt(blend);
    }

    @Override
    public void steadied(final boolean steady) {
        going.steadied(steady);
    }

    @Override
    public Camera camera(final double blend) {
        return going.camera(blend);
    }

    @Override
    public Vec3 foot(final double blend) {
        return going.foot(blend);
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        return going.figures(blend);
    }

    @Override
    public List<Courier.Placed> seen(final double blend) {
        return going.seen(blend);
    }

    @Override
    public Optional<Motion> motion() {
        return going.motion();
    }

    @Override
    public void heard(final Hearing hearing, final double blend) {
        going.heard(hearing, blend);
    }

    @Override
    public void carded(final Card card, final Laid laid) {
        going.carded(card, laid);
    }

    @Override
    public void drawn(final Pass pass, final Camera camera, final double blend) {
        going.drawn(pass, camera, blend);
    }

    @Override
    public void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
        going.overlaid(pass, over, blend);
        final Vec3 amber = new Vec3(1, 0.78, 0.25);
        final List<String> lines = hints.get();
        for (int line = 0; line < lines.size(); line++) {
            over.text(pass, lines.get(line), -over.wide(lines.get(line), LETTER) / 2,
                    FOOT + (lines.size() - 1 - line) * LINE, LETTER, amber);
        }
    }

    @Override
    public List<String> clicked(final Clicked click, final int width, final int height) {
        return going.clicked(click, width, height);
    }

    @Override
    public boolean pointing() {
        return going.pointing();
    }

    @Override
    public void uncarded() {
        going.uncarded();
    }
}
