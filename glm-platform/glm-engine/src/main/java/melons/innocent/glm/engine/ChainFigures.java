package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * How a bank's timing chain runs, in a plane of its own across the crank at the engine's front, ahead of the block,
 * each bank's a little further back than the one before: a roller chain of a quarter inch pitch, as cars' timing
 * chains are, from a sprocket of 21 teeth on the crank's nose up round the bank's two camshafts' sprockets of 42, so
 * the cams turn half as fast as the crank; wrapped round the three to the left, the crank's sprocket first
 * ({@link Belt}). The run the crank pulls the chain down is its tight side, along a fixed guide; the other its slack
 * side, along the tensioner's blade.
 *
 * @param figures the engine's figures
 * @param bank    its bank, from nought
 */
record ChainFigures(EngineFigures figures, int bank) {

    /** How far one link is from the next; how wide the chain is, along the crank. */
    static final double PITCH = 0.635;
    static final double WIDE = 0.6;
    /** How many teeth the crank's sprocket has, and a camshaft's. */
    private static final int CRANK_TEETH = 21;
    private static final int CAM_TEETH = 42;
    /** Where the first bank's chain runs along the crank, ahead of the block; how far back each next bank's is. */
    private static final double FIRST = -2.2;
    private static final double APART = 1.0;

    /** Where its plane is, along the crank. */
    double plane() {
        return FIRST + bank * APART;
    }

    /** How far round the crank's sprocket is to the chain's line, and a camshaft's. */
    double crank() {
        return round(CRANK_TEETH);
    }

    double cam() {
        return round(CAM_TEETH);
    }

    /** How many teeth the crank's sprocket has, and a camshaft's. */
    int crankTeeth() {
        return CRANK_TEETH;
    }

    int camTeeth() {
        return CAM_TEETH;
    }

    /** Where a camshaft's middle is across the crank, the inlets' or the exhausts', in the block's frame. */
    Spot cam(final boolean inlet) {
        final HeadFigures head = new HeadFigures(figures);
        final Vec3 at = head.at(bank, 0, head.cam(inlet));
        return new Spot(at.x(), at.y());
    }

    /** The line it runs along: round the crank's sprocket first, then the camshafts', to the left round them. */
    Belt belt() {
        final List<Belt.Wheel> wheels = new ArrayList<>(List.of(new Belt.Wheel(new Spot(0, 0), crank()),
                new Belt.Wheel(cam(true), cam()), new Belt.Wheel(cam(false), cam())));
        final double u = wheels.stream().mapToDouble(w -> w.middle().u()).average().orElse(0);
        final double v = wheels.stream().mapToDouble(w -> w.middle().v()).average().orElse(0);
        final Comparator<Belt.Wheel> round = Comparator.comparingDouble(w -> around(Math.atan2(w.middle().v() - v,
                w.middle().u() - u) - Math.atan2(-v, -u)));
        wheels.sort(round);
        return new Belt(wheels);
    }

    /** How many links it has, all round. */
    int links() {
        return (int) Math.round(belt().length() / PITCH);
    }

    /** How far one link is from the next, as many as go round evenly. */
    double pitch() {
        return belt().length() / links();
    }

    /**
     * Its tight side, which the fixed guide runs beside: the run from the crank's sprocket as it is wrapped, down
     * which the crank, turning the other way, pulls the chain.
     */
    Belt.Run tight() {
        return belt().run(0);
    }

    /** Its slack side, which the tensioner presses on: the run the crank pays the chain out up. */
    Belt.Run slack() {
        return belt().run(2);
    }

    /** How far round a sprocket of {@code teeth} is to the chain's line, the pitch its chord. */
    private double round(final int teeth) {
        return PITCH / (2 * Math.sin(Math.PI / teeth));
    }

    private double around(final double a) {
        final double turn = 2 * Math.PI;
        return (a % turn + turn) % turn;
    }
}
