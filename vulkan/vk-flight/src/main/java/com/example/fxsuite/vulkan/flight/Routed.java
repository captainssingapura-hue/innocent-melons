package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A tiltrotor flying a route by itself, from its first point to its last, at a set speed, flying ahead: along the
 * route as it winds, heading a little ahead of where it is so it rounds each bend smoothly, banked as a turn that
 * tight at that speed asks, and holding so high over the highest of what lies below the next stretch, coming to
 * that height smoothly and pitched as it climbs or sinks. No keys: it ends when the route does, so it can be filmed.
 * Seen from behind, its display over the picture. In the renderer's frame; centimetres, seconds and degrees.
 */
public final class Routed implements Roaming {

    private static final double G = 981;
    /** How far ahead along the route it heads for, and looks for what is below; how quickly it comes to its height. */
    private static final double LOOK = 30_000;
    private static final double EASING = 4;
    private static final double BANKED = 35;
    private static final double BEHIND = 5_000;
    private static final double DOWN = 14;
    private static final double MIDDLE = 300;
    /** The power it flies at, for its display. */
    private static final double CRUISING = 0.62;

    private final List<Vec3> route;
    private final double[] along;
    private final double speed;
    private final double above;
    private final Below below;
    private final Airframe airframe;
    private final Optional<Skies> skies;
    private final Panel panel = new Panel();
    private final Chasing chasing = new Chasing(0, DOWN, BEHIND);
    private Tiltrotor now;
    private Tiltrotor before;
    private double gone;
    private double turned;
    private Clock clock = Clock.START;

    /**
     * {@code airframe} flying {@code route} (across and along; its height is its own) at {@code speed}, {@code above}
     * over what is {@code below}, its sky's clock {@code skies} moved on.
     */
    public Routed(final List<Vec3> route, final double speed, final double above, final Below below,
                  final Airframe airframe, final Optional<Skies> skies) {
        if (route.size() < 2 || !(speed > 0)) {
            throw new IllegalArgumentException("a route has two points or more and is flown at some speed");
        }
        this.route = List.copyOf(route);
        this.along = new double[route.size()];
        for (int i = 1; i < route.size(); i++) {
            along[i] = along[i - 1] + level(route.get(i).minus(route.get(i - 1))).length();
        }
        this.speed = speed;
        this.above = above;
        this.below = below;
        this.airframe = airframe;
        this.skies = skies;
        this.now = placed(0, highest(0) + above, 0, 1);
        this.before = now;
    }

    private Vec3 level(final Vec3 way) {
        return new Vec3(way.x(), 0, way.z());
    }

    /** Where along the route {@code s} centimetres from its start is, across and along. */
    private Vec3 at(final double s) {
        final double clamped = Math.clamp(s, 0, along[along.length - 1]);
        int i = 1;
        while (i < along.length - 1 && along[i] < clamped) {
            i++;
        }
        final double piece = along[i] - along[i - 1];
        final double share = piece > 0 ? (clamped - along[i - 1]) / piece : 0;
        return level(route.get(i - 1).plus(route.get(i).minus(route.get(i - 1)).times(share)));
    }

    /** The highest of what lies below the route from {@code s} to {@code LOOK} on. */
    private double highest(final double s) {
        double most = Double.NEGATIVE_INFINITY;
        for (int k = 0; k <= 10; k++) {
            final Vec3 p = at(s + LOOK * k / 10);
            most = Math.max(most, below.under(p.x(), p.z()));
        }
        return most;
    }

    /** The craft {@code s} along, {@code high}, heading for a little way on, climbing at {@code climbing}. */
    private Tiltrotor placed(final double s, final double high, final double climbing, final double seconds) {
        final Vec3 here = at(s);
        final Vec3 ahead = at(s + LOOK / 3).minus(here);
        final double heading = Math.toDegrees(Math.atan2(ahead.x(), ahead.z()));
        final double was = now == null ? heading : now.attitude().heading();
        final double turning = ((heading - was + 540) % 360 - 180) / seconds;
        final double roll = Math.clamp(Math.toDegrees(Math.atan(speed * Math.toRadians(turning) / G)), -BANKED,
                BANKED);
        final double pitch = Math.toDegrees(Math.atan2(climbing, speed));
        final Vec3 going = ahead.length() > 0 ? ahead.unit().times(speed).plus(new Vec3(0, climbing, 0))
                : new Vec3(0, climbing, 0);
        return new Tiltrotor(new Vec3(here.x(), high, here.z()), going, new Attitude(was + turning * seconds, pitch,
                roll), 0, 0, CRUISING, turned, 1, false);
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        // a route is flown over all of it
    }

    @Override
    public double stepped(final double seconds) {
        final Clock.Ticked ticked = clock.advance(seconds);
        clock = ticked.clock();
        for (int done = 0; done < ticked.steps() && !over(); done++) {
            before = now;
            gone += speed * Clock.STEP;
            final double wanted = highest(gone) + above;
            final double climbing = (wanted - now.at().y()) / EASING;
            turned = (turned + 333 * 6 * Clock.STEP) % 360;
            now = placed(gone, now.at().y() + climbing * Clock.STEP, climbing, Clock.STEP);
            skies.ifPresent(sky -> sky.stepped(Clock.STEP));
        }
        return ticked.blend();
    }

    @Override
    public boolean over() {
        return gone >= along[along.length - 1];
    }

    @Override
    public boolean ends() {
        return true;
    }

    @Override
    public double seconds() {
        return along[along.length - 1] / speed;
    }

    /** The craft {@code blend} of the way from its step before the last to its last. */
    public Tiltrotor shown(final double blend) {
        return now.between(before, blend);
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
        return chasing.camera(head(blend), yaw(blend), below);
    }

    @Override
    public Vec3 foot(final double blend) {
        final Vec3 at = shown(blend).at();
        return new Vec3(at.x(), below.under(at.x(), at.z()), at.z());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        return new ArrayList<>();
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
    public boolean pointing() {
        return true;
    }

    @Override
    public void uncarded() {
        airframe.close();
    }
}
