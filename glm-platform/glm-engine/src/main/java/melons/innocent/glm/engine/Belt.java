package melons.innocent.glm.engine;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The line a chain or a belt runs along, wrapped round wheels in a plane: round each wheel it touches, and straight
 * from each to the next on their outsides, the wheels given in the order it runs round them, to the left, each one's
 * middle to the left of its run. A place along it is found by how far along it is from where it leaves the first.
 *
 * @param wheels the wheels it is wrapped round, in order
 */
record Belt(List<Wheel> wheels) {

    /** A wheel it runs round: its middle and how far round it is to the line. */
    record Wheel(Spot middle, double round) {
    }

    /** A place along it: where, and which way it runs there, in radians from across toward up. */
    record On(Spot at, double heading) {
    }

    /** A straight run, from the wheel it leaves to the one it meets. */
    record Run(Spot from, Spot to) {

        /** How long it is. */
        double length() {
            return Math.hypot(to.u() - from.u(), to.v() - from.v());
        }

        /** Which way it runs, in radians from across toward up. */
        double heading() {
            return Math.atan2(to.v() - from.v(), to.u() - from.u());
        }
    }

    public Belt {
        wheels = List.copyOf(wheels);
        if (wheels.size() < 2) {
            throw new IllegalArgumentException("a belt is wrapped round two wheels at least");
        }
    }

    /** The run leaving wheel {@code i}, to the next. */
    Run run(final int i) {
        final Wheel a = wheels.get(i);
        final Wheel b = wheels.get((i + 1) % wheels.size());
        final double side = side(i);
        return new Run(new Spot(a.middle().u() + a.round() * Math.cos(side), a.middle().v() + a.round()
                * Math.sin(side)), new Spot(b.middle().u() + b.round() * Math.cos(side), b.middle().v() + b.round()
                * Math.sin(side)));
    }

    /** How long it is, all round. */
    double length() {
        double all = 0;
        for (int i = 0; i < wheels.size(); i++) {
            all += run(i).length() + wrapped(i) * wheels.get(i).round();
        }
        return all;
    }

    /** Where it is {@code along} from where it leaves the first wheel, round again past its length. */
    On at(final double along) {
        double left = (along % length() + length()) % length();
        for (int i = 0; i < wheels.size(); i++) {
            final Run run = run(i);
            if (left <= run.length()) {
                final double t = left / run.length();
                return new On(new Spot(run.from().u() + t * (run.to().u() - run.from().u()), run.from().v()
                        + t * (run.to().v() - run.from().v())), run.heading());
            }
            left -= run.length();
            final int next = (i + 1) % wheels.size();
            final Wheel wheel = wheels.get(next);
            final double arc = wrapped(next) * wheel.round();
            if (left <= arc) {
                final double a = side(i) + left / wheel.round();
                return new On(new Spot(wheel.middle().u() + wheel.round() * Math.cos(a), wheel.middle().v()
                        + wheel.round() * Math.sin(a)), a + Math.PI / 2);
            }
            left -= arc;
        }
        return at(0);
    }

    /** Which way out of its wheels the run leaving wheel {@code i} lies, in radians from across toward up. */
    private double side(final int i) {
        final Wheel a = wheels.get(i);
        final Wheel b = wheels.get((i + 1) % wheels.size());
        final double du = b.middle().u() - a.middle().u();
        final double dv = b.middle().v() - a.middle().v();
        return Math.atan2(dv, du) - Math.acos((a.round() - b.round()) / Math.hypot(du, dv));
    }

    /** How far round wheel {@code i} it is wrapped, in radians: from the run that meets it to the one leaving it. */
    private double wrapped(final int i) {
        final int before = (i - 1 + wheels.size()) % wheels.size();
        final double turn = 2 * Math.PI;
        return ((side(i) - side(before)) % turn + turn) % turn;
    }
}
