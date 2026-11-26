package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * How a block whose banks stand over the crank is cast, in line or in a V: each bank's deck standing as high over the
 * crank as its top, as wide as a cylinder with its liner, its water's jacket and its wall; its outer walls falling
 * from the decks to a deep skirt below the crank, open beneath for the sump; in a V, the valley between the banks
 * closed low over the crank and open above, where the turbochargers go. In line, a web between each two cylinders,
 * siamesed, under them; in a V, the whole section; and a boss each side on the skirt for the engine's mount.
 *
 * @param figures the engine's figures
 * @param block   how the block is cast
 */
record UprightCase(EngineFigures figures, BlockForm block) implements Casing {

    /** How far the skirt reaches under the crank; how far the valley stands over the crank's swing. */
    static final double SKIRT = 7;
    private static final double SWING = 7.5;
    /** How far down a bank's outer wall runs straight, at most, as a share of the deck's height. */
    private static final double OUTER = 0.35;

    /**
     * Where bank {@code bank}'s outer wall stops running straight down from its deck and the crankcase steps out to
     * the skirt: low, but no nearer the crank's axis than {@link BlockForm#clear()}.
     */
    Vec3 shoulder(final int bank) {
        final double clear = block.clear();
        final double half = block.half();
        final double h = Math.max(figures.deck() * OUTER, Math.sqrt(Math.max(0, clear * clear - half * half)) + 0.3);
        return block.up(bank).times(h).plus(block.out(bank).times(half));
    }

    /** How far across the skirt reaches each side: as far as the shoulders, and clear of all that turns. */
    double skirt() {
        double widest = Math.max(figures.stroke() / 2 + 5, block.clear());
        for (int b = 0; b < figures.banks(); b++) {
            widest = Math.max(widest, Math.abs(shoulder(b).x()));
        }
        return widest;
    }

    /**
     * The section, drawn across and up, round to the left, starting along the open bottom: up the skirt each side,
     * across to the shoulder where it is further in, up its bank's outer wall to the deck; in a V, down its inner wall
     * to the valley's floor.
     */
    @Override
    public List<Spot> section() {
        final double s = skirt();
        final double half = block.half();
        final List<Spot> right = new ArrayList<>();
        final List<Spot> left = new ArrayList<>();
        if (figures.banks() == 1) {
            right.addAll(flank(0, s));
            right.add(spot(block.up(0).times(figures.deck()).plus(block.out(0).times(half))));
            final List<Spot> all = new ArrayList<>(List.of(new Spot(-s, -SKIRT), new Spot(s, -SKIRT)));
            all.addAll(right);
            all.addAll(right.reversed().stream().map(p -> new Spot(-p.u(), p.v())).toList());
            return all;
        }
        for (final int b : List.of(0, 1)) {
            final Vec3 up = block.up(b);
            final List<Spot> side = figures.lean(b) > 0 ? right : left;
            final Vec3 deck = up.times(figures.deck());
            side.addAll(flank(b, s));
            side.add(spot(deck.plus(block.out(b).times(half))));
            final Vec3 inner = deck.minus(block.out(b).times(half));
            side.add(spot(inner));
            final double floor = Math.max(SWING + figures.stroke() / 2, inner.y() - inner.x() / up.x() * up.y() + 1.5);
            side.add(spot(inner.minus(up.times((inner.y() - floor) / up.y()))));
        }
        final List<Spot> all = new ArrayList<>(List.of(new Spot(-s, -SKIRT), new Spot(s, -SKIRT)));
        all.addAll(right);
        all.addAll(left.reversed());
        return all;
    }

    /** A bulkhead between cylinders: in a V, the whole {@code section}; in line, a web under the cylinders. */
    @Override
    public List<Spot> between(final List<Spot> section) {
        if (figures.banks() > 1) {
            return section;
        }
        final double s = skirt();
        final double top = figures.deck() - block.deep();
        return List.of(new Spot(-s, -SKIRT), new Spot(s, -SKIRT), new Spot(s, top), new Spot(-s, top));
    }

    /** Where the engine's mount is on side {@code side}: on the skirt's face, a little ahead of the middle. */
    @Override
    public Vec3 mount(final int side) {
        return new Vec3(side * (skirt() + 1.5), -1, figures.length() * 0.42);
    }

    /**
     * Bank {@code bank}'s side of the crankcase, from the skirt {@code s} across up to its shoulder: straight up the
     * skirt to the shoulder's height and across to it, where the shoulder is further in; the shoulder.
     */
    List<Spot> flank(final int bank, final double s) {
        final Spot shoulder = spot(shoulder(bank));
        final double side = Math.signum(shoulder.u());
        return Math.abs(shoulder.u()) < s - 1e-6 ? List.of(new Spot(side * s, shoulder.v()), shoulder)
                : List.of(shoulder);
    }

    private Spot spot(final Vec3 p) {
        return new Spot(p.x(), p.y());
    }
}
