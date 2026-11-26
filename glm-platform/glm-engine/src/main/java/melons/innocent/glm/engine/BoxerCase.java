package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * How a boxer's block is cast, where it differs from a V's: its section a crankcase round the crank, open beneath
 * for the sump, and each bank lying flat out of it either side, as high as a cylinder with its liner, its water's
 * jacket and its wall, out to its deck; a main bearing between each two pins, the banks' pins taking turns along the
 * crank, each bulkhead across the crankcase only, short of where the pistons come down to, so the pistons of the
 * cylinders it stands beside go by it; and a boss for each mount under each bank, where it leaves the crankcase.
 *
 * @param figures the engine's figures
 * @param block   how the block is cast
 */
record BoxerCase(EngineFigures figures, BlockForm block) implements Casing {

    /** How thick the crankcase is cast round what turns; how far across its opening beneath is, of its round. */
    private static final double SHELL = 0.5;
    private static final double OPEN = 0.55;
    /** How far a bulkhead stops short of where the pistons come down to. */
    private static final double SHORT = 0.2;
    /** How far a mount's boss stands out from where its bank leaves the crankcase, and under the bank. */
    private static final double OUT = 1.0;
    private static final double UNDER = 1.5;

    /** How far round the crankcase is: clear of all that turns, and its shell. */
    double round() {
        return block.clear() + SHELL;
    }

    /** How far out each bank leaves the crankcase, where its walls meet the crankcase's round. */
    double leaves() {
        final double h = block.half();
        return Math.sqrt(Math.max(round() * round() - h * h, 1));
    }

    /**
     * The section, drawn across and up as a V's is, starting along the open bottom: up the crankcase to where the
     * right bank leaves it, along its lower wall out to its deck, up the deck, back along its upper wall, and over
     * the crank; and the left the same way down.
     */
    @Override
    public List<Spot> section() {
        final double c = round();
        final double s = c * OPEN;
        final double h = block.half();
        final double x = leaves();
        final double deck = figures.deck();
        final List<Spot> right = List.of(new Spot(x, -h), new Spot(deck, -h), new Spot(deck, h), new Spot(x, h),
                new Spot(s, c));
        final List<Spot> all = new ArrayList<>(List.of(new Spot(-s, -c), new Spot(s, -c)));
        all.addAll(right);
        all.addAll(right.reversed().stream().map(p -> new Spot(-p.u(), p.v())).toList());
        return all;
    }

    /** A bulkhead between two pins: the crankcase's section, short of where the pistons come down to either side. */
    @Override
    public List<Spot> between(final List<Spot> section) {
        final double c = round();
        final double s = c * OPEN;
        final double h = block.half();
        final double out = Math.max(s + 0.01, Math.min(leaves(), new Cylinder(figures, 0, 0).reach() - SHORT));
        final double t = (out - s) / (leaves() - s);
        final double y = c + t * (h - c);
        return List.of(new Spot(-s, -c), new Spot(s, -c), new Spot(out, -y), new Spot(out, y), new Spot(s, c),
                new Spot(-s, c), new Spot(-out, y), new Spot(-out, -y));
    }

    /** Where the bulkheads stand: at each end, and between each two pins along the crank. */
    List<Double> bulkheads(final double bulkhead) {
        final List<Double> pins = new ArrayList<>();
        for (int b = 0; b < figures.banks(); b++) {
            for (int k = 0; k < figures.perBank(); k++) {
                pins.add(figures.along(b, k));
            }
        }
        pins.sort(Double::compare);
        final List<Double> at = new ArrayList<>();
        at.add(bulkhead / 2);
        for (int j = 0; j + 1 < pins.size(); j++) {
            at.add((pins.get(j) + pins.get(j + 1)) / 2);
        }
        at.add(figures.length() - bulkhead / 2);
        return at;
    }

    /** Where the engine's mount is on side {@code side}: under its bank, where it leaves the crankcase. */
    @Override
    public Vec3 mount(final int side) {
        return new Vec3(side * (leaves() + OUT), -block.half() - UNDER, figures.length() * 0.42);
    }
}
