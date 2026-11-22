package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A tree with no leaves, as finely as it is made: every bough from the trunk to the twigs. It is grown, not drawn:
 * a habit says how its kind grows, and a number says which tree of that kind this is, so the same habit and the
 * same number are the same tree every time, and another number is another tree of the same kind.
 *
 * <p>How it grows. A bough runs its length in a few pieces, thinning as it goes, straying a little from straight
 * and drawn a little upward. At its end, unless it is by then a twig, it forks in two. The two share out the
 * bough's thickness so that what they are through together is what the bough was through, which is how wood
 * grows: so the thicker of the two is nearly as thick as the bough, and turns less from the way the bough was
 * going, and the thinner turns more. Each is shorter than the bough before it.
 *
 * <p>This is the fine model, and the only one written. Coarser forms of the same tree, for a forest seen from
 * far off, are to be worked out from these boughs.
 */
public record Tree(Habit habit, long number) {

    /** How long a piece of a bough is, at most, of the bough's thickness at its near end; and the fewest pieces. */
    private static final double PIECE = 6;
    private static final int FEWEST = 2;
    private static final double FULL = 360;
    /** How unevenly a fork shares out its bough: the thicker of the two has from a half to this much of it. */
    private static final double MOST = 0.78;

    /** An oak, the first of its kind. */
    public Tree() {
        this(new Habit(), 1);
    }

    /** Every bough of it, the trunk first, each after the bough it forks from. */
    public List<Bough> boughs() {
        final List<Bough> boughs = new ArrayList<>();
        grown(boughs, new Random(number), new Sprout(new Vec3(0, 0, 0), new Vec3(0, 1, 0), habit.trunkThick(),
                habit.trunkTall(), 0, -1));
        return boughs;
    }

    /** How high it stands. */
    public double height() {
        return boughs().stream().flatMap(bough -> bough.points().stream()).mapToDouble(Vec3::y).max().orElse(0);
    }

    /** How far its furthest twig is from its trunk, across the ground. */
    public double reach() {
        return boughs().stream().flatMap(bough -> bough.points().stream())
                .mapToDouble(point -> Math.hypot(point.x(), point.z())).max().orElse(0);
    }

    /** A bough about to grow: where from, which way, how thick and how long, and what it forks from. */
    private record Sprout(Vec3 from, Vec3 heading, double thick, double length, int forks, int parent) {
    }

    /** The bough {@code sprout} grows into, added to {@code boughs}, and every bough that forks from it after it. */
    private void grown(final List<Bough> boughs, final Random chance, final Sprout sprout) {
        final int pieces = Math.max(FEWEST, (int) Math.ceil(sprout.length() / (PIECE * sprout.thick() + 20)));
        final List<Vec3> points = new ArrayList<>(List.of(sprout.from()));
        final List<Double> thick = new ArrayList<>(List.of(sprout.thick() * (sprout.forks() == 0 ? habit.flare() : 1)));
        Vec3 heading = sprout.heading();
        for (int piece = 1; piece <= pieces; piece++) {
            // the trunk stands nearly straight; a bough strays, and is drawn upward
            final double strays = sprout.forks() == 0 ? habit.wander() / 4 : habit.wander();
            heading = heading.plus(anyWay(chance).times(strays))
                    .plus(new Vec3(0, sprout.forks() == 0 ? 0 : habit.rising(), 0)).unit();
            points.add(points.getLast().plus(heading.times(sprout.length() / pieces)));
            thick.add(sprout.thick() * (1 - (1 - habit.taper()) * piece / pieces));
        }
        final int here = boughs.size();
        boughs.add(new Bough(points, thick, sprout.forks(), sprout.parent()));
        final double left = thick.getLast();
        if (left < habit.twig()) {
            return;
        }
        // the fork: the bough's thickness shared out by what each is through, the thicker turning the less
        final double share = 0.5 + chance.nextDouble() * (MOST - 0.5);
        final Vec3 aside = aside(heading, chance.nextDouble() * FULL);
        for (final double part : new double[] {share, 1 - share}) {
            final double turn = Math.toRadians(2 * habit.spread() * (1 - part)) * (part == share ? 1 : -1);
            final Vec3 way = heading.times(Math.cos(turn)).plus(aside.times(Math.sin(turn))).unit();
            grown(boughs, chance, new Sprout(points.getLast(), way, left * Math.sqrt(part),
                    sprout.length() * habit.shorter() * (0.75 + 0.5 * part), sprout.forks() + 1, here));
        }
    }

    /** A way out from a point, any way at all, one long. */
    private Vec3 anyWay(final Random chance) {
        final Vec3 way = new Vec3(chance.nextGaussian(), chance.nextGaussian(), chance.nextGaussian());
        return way.length() < 1e-9 ? new Vec3(1, 0, 0) : way.unit();
    }

    /** A way square to {@code heading}, {@code round} degrees round it from wherever such ways begin. */
    private Vec3 aside(final Vec3 heading, final double round) {
        final Vec3 any = Math.abs(heading.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        final Vec3 one = heading.cross(any).unit();
        final Vec3 other = heading.cross(one).unit();
        return one.times(Math.cos(Math.toRadians(round))).plus(other.times(Math.sin(Math.toRadians(round))));
    }
}
