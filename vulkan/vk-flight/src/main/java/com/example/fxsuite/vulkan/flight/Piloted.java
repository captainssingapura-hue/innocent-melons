package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.bench.play.Where;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Clicked;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.List;
import java.util.Optional;

/**
 * A tiltrotor flown ({@link Tiltrotor}), and the camera following it, stepped together in the engine's fixed steps,
 * each as its source tells it, kept and checked as any run is. Seen from behind ({@link Chasing}, swung by nothing,
 * since Q and E turn the nacelles) or from its cockpit; its display drawn over the picture ({@link Panel}), whose
 * buttons are clicked with the mouse, which is left free. The craft itself is drawn as its {@link Airframe} says.
 */
public final class Piloted implements Roaming {

    /** The pilot's view's angle; and how far behind the camera begins, and how far down it looks. */
    private static final double COCKPIT_FIELD = 62;
    private static final double BEHIND = 4_000;
    private static final double DOWN = 10;
    /** How high over the ground under its wheels the camera looks at it. */
    private static final double MIDDLE = 300;

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Optional<Skies> skies;
    private final Below below;
    private final Airframe airframe;
    private final Panel panel = new Panel();
    private Tiltrotor now;
    private Tiltrotor before;
    private Chasing chasing = new Chasing(0, DOWN, BEHIND);
    private Chasing chased = chasing;
    private Clock clock = Clock.START;
    private long step;

    /** {@code airframe} flown from {@code first} over {@code below}, told by {@code source}. */
    public Piloted(final Source source, final Optional<Keeping> keeping, final Optional<Skies> skies, final Below below,
                   final Airframe airframe, final Tiltrotor first) {
        this.source = source;
        this.keeping = keeping;
        this.skies = skies;
        this.below = below;
        this.airframe = airframe;
        this.now = first;
        this.before = first;
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        // nothing is in a flight's way but what is below it
    }

    @Override
    public double stepped(final double seconds) {
        final Clock.Ticked ticked = clock.advance(seconds);
        clock = ticked.clock();
        for (int done = 0; done < ticked.steps() && !over(); done++) {
            taken(source.step(step + 1, ticked.steps() - done));
        }
        return ticked.blend();
    }

    private void taken(final Step told) {
        before = now;
        chased = chasing;
        now = now.stepped(told, Clock.STEP, airframe, below);
        // the camera is swung by nothing and put behind by nothing: Q, E and C are the craft's
        chasing = chasing.stepped(new Step(told.held(), told.pressed(), 0, 0, false, 0, told.scrolled(), false),
                Clock.STEP);
        step++;
        skies.ifPresent(sky -> sky.stepped(Clock.STEP * now.faster()));
        final Where stood = new Where(now.at().x(), now.at().y(), now.at().z());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the craft was at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
    }

    /** The craft {@code blend} of the way from its step before the last to its last. */
    public Tiltrotor shown(final double blend) {
        return now.between(before, blend);
    }

    @Override
    public boolean over() {
        return source.over(step);
    }

    @Override
    public boolean ends() {
        return source.ends();
    }

    @Override
    public double seconds() {
        return source.ends() ? source.length() * Clock.STEP : Double.POSITIVE_INFINITY;
    }

    @Override
    public Vec3 head(final double blend) {
        final Tiltrotor shown = shown(blend);
        return shown.at().plus(shown.attitude().up().times(MIDDLE));
    }

    @Override
    public double yaw(final double blend) {
        return shown(blend).attitude().heading();
    }

    @Override
    public Camera camera(final double blend) {
        final Tiltrotor shown = shown(blend);
        if (!shown.cockpit()) {
            return chasing.between(chased, blend).camera(head(blend), shown.attitude().heading(), below);
        }
        final Attitude seen = shown.attitude();
        return new Camera(shown.at().plus(seen.turned(airframe.cockpit())), seen.right(), seen.up(), seen.ahead(),
                COCKPIT_FIELD, 10);
    }

    @Override
    public Vec3 foot(final double blend) {
        final Vec3 at = shown(blend).at();
        return new Vec3(at.x(), below.under(at.x(), at.z()), at.z());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        return List.of();
    }

    @Override
    public void carded(final Card card, final Laid laid) {
        airframe.carded(card, laid);
    }

    @Override
    public void drawn(final Pass pass, final Camera camera, final double blend) {
        final Tiltrotor shown = shown(blend);
        airframe.drawn(pass, camera, shown.attitude().placed(shown.at()), shown.tilt(), shown.turned());
    }

    @Override
    public void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
        panel.drawn(pass, over, shown(blend), below);
    }

    @Override
    public List<String> clicked(final Clicked click, final int width, final int height) {
        return panel.clicked(click, width, height);
    }

    @Override
    public boolean pointing() {
        return true;
    }

    @Override
    public void uncarded() {
        airframe.close();
    }

    /** How many steps have been taken. */
    public long steps() {
        return step;
    }
}
