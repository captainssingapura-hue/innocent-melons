package com.example.fxsuite.vulkan.afloat;

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
import com.example.fxsuite.vulkan.flight.Attitude;
import com.example.fxsuite.vulkan.flight.Chasing;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.List;
import java.util.Optional;

/**
 * A boat driven on a place's waters ({@link Afloat}), and the camera following it, stepped together in the engine's
 * fixed steps, each as its source tells it, kept and checked as any run is. Seen from behind, or from its helm; its
 * display over the picture ({@link Helm}), whose buttons are clicked with the mouse, which is left free. The boat
 * itself is drawn as its {@link Vessel} says.
 */
public final class Cruised implements Roaming {

    private static final double HELM_FIELD = 60;
    private static final double BEHIND = 2_400;
    private static final double DOWN = 12;
    private static final double MIDDLE = 200;

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Optional<Skies> skies;
    private final Waters waters;
    private final Vessel vessel;
    private final Helm panel = new Helm();
    private Afloat now;
    private Afloat before;
    private Chasing chasing = new Chasing(0, DOWN, BEHIND);
    private Chasing chased = chasing;
    private Clock clock = Clock.START;
    private long step;

    /** {@code vessel} driven from {@code first} on {@code waters}, told by {@code source}. */
    public Cruised(final Source source, final Optional<Keeping> keeping, final Optional<Skies> skies,
                   final Waters waters, final Vessel vessel, final Afloat first) {
        this.source = source;
        this.keeping = keeping;
        this.skies = skies;
        this.waters = waters;
        this.vessel = vessel;
        this.now = first;
        this.before = first;
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        // nothing is in a boat's way but the shore and the shallows
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
        now = now.stepped(told, Clock.STEP, vessel, waters);
        chasing = chasing.stepped(new Step(told.held(), told.pressed(), 0, 0, false, 0, told.scrolled(), false),
                Clock.STEP);
        step++;
        skies.ifPresent(sky -> sky.stepped(Clock.STEP * now.faster()));
        final Where stood = new Where(now.at().x(), now.at().y(), now.at().z());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the boat was at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
    }

    /** The boat {@code blend} of the way from its step before the last to its last. */
    public Afloat shown(final double blend) {
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
        return shown(blend).floating().plus(new Vec3(0, MIDDLE, 0));
    }

    @Override
    public double yaw(final double blend) {
        return shown(blend).heading();
    }

    @Override
    public Camera camera(final double blend) {
        final Afloat shown = shown(blend);
        if (!shown.helm()) {
            return chasing.between(chased, blend).camera(head(blend), shown.heading(), waters.below());
        }
        final Attitude seen = shown.attitude();
        return new Camera(shown.floating().plus(seen.turned(vessel.helm())), seen.right(), seen.up(), seen.ahead(),
                HELM_FIELD, 10);
    }

    @Override
    public Vec3 foot(final double blend) {
        return shown(blend).at();
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        return List.of();
    }

    @Override
    public void carded(final Card card, final Laid laid) {
        vessel.carded(card, laid);
    }

    @Override
    public void drawn(final Pass pass, final Camera camera, final double blend) {
        final Afloat shown = shown(blend);
        vessel.drawn(pass, camera, shown.attitude().placed(shown.floating()), shown.wheel());
    }

    @Override
    public void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
        final Afloat shown = shown(blend);
        panel.drawn(pass, over, shown, waters.deep(shown.at().x(), shown.at().z()) - vessel.draught());
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
        vessel.close();
    }
}
