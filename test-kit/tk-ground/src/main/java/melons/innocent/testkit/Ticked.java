package melons.innocent.testkit;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.sim.Clock;
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

/**
 * A going about with something else ticked for each of its steps ({@link Ticking}): all as the going it wraps has
 * it, and beside it a clock of the engine's own kind, given the same seconds, which says how many steps those
 * seconds have earned; the other thing is ticked that many times, once the going has taken them.
 */
final class Ticked implements Roaming {

    private final Roaming going;
    private final Runnable tick;
    private Clock clock = new Clock(0);

    Ticked(final Roaming going, final Runnable tick) {
        this.going = going;
        this.tick = tick;
    }

    @Override
    public double stepped(final double seconds) {
        final double blend = going.stepped(seconds);
        final Clock.Ticked ticked = clock.advance(seconds);
        clock = ticked.clock();
        for (int step = 0; step < ticked.steps() && !going.over(); step++) {
            tick.run();
        }
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
