package melons.innocent.glm.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * When an engine's cylinders fire, as its crank and its cam make them: each cylinder's piston comes to the top of its
 * bore once each turn, when its pin points up its bank's bore; the cam has it fire at one of those two tops in the
 * four strokes' two turns. In the order the engines it is after fire, where they are known, in Audi's numbering, the
 * first bank's cylinders, on the right seen from behind, first, front to back, then the second's: the four 1-3-4-2,
 * the five 1-2-4-5-3, the V6 1-4-3-6-2-5, the V8 1-3-7-2-6-5-4-8, as their makers' training says; the V12, after none,
 * 1-7-5-11-3-9-6-12-2-8-4-10, an even order its crank makes; the flat six 1-6-2-4-3-5, as the group's flat sixes
 * fire, and the flat four, as its crank brings them, 1-2-3-4. Other figures fire as their crank brings each cylinder
 * up, the lowest numbered first. The first cylinder fires at its top in the first turn, each after it at whichever of
 * its two tops comes nearest an even share of the cycle on from the one before: evenly where its crank lets it, and
 * where not, as a twin does, 270 degrees on and then 450. Refused if its crank does not bring each cylinder to its
 * top then, or if they do not all fire within the cycle.
 */
public final class Firing {

    /** The four strokes' cycle, in degrees of crank. */
    public static final double CYCLE = 720;
    private static final double TURN = 360;
    private static final double CLOSE = 1e-6;
    /** The orders the engines it is after fire in, by how many banks, how many cylinders to a bank, and the V. */
    private static final Map<List<Integer>, List<Integer>> KNOWN = Map.of(
            List.of(1, 4, 0), List.of(1, 3, 4, 2),
            List.of(1, 5, 0), List.of(1, 2, 4, 5, 3),
            List.of(2, 3, 90), List.of(1, 4, 3, 6, 2, 5),
            List.of(2, 4, 90), List.of(1, 3, 7, 2, 6, 5, 4, 8),
            List.of(2, 6, 60), List.of(1, 7, 5, 11, 3, 9, 6, 12, 2, 8, 4, 10),
            List.of(2, 3, 180), List.of(1, 6, 2, 4, 3, 5));

    private final EngineFigures figures;
    private final CrankFigures crank;
    private final List<Integer> order;
    private final List<Double> fired;

    /** {@code figures}' firing, refused if its crank does not bring each cylinder to its top as it fires. */
    public Firing(final EngineFigures figures) {
        this.figures = figures;
        this.crank = new CrankFigures(figures);
        final List<Integer> known = KNOWN.get(List.of(figures.banks(), figures.perBank(), (int) figures.vee()));
        this.order = known != null ? known : derived();
        final List<Double> at = new ArrayList<>(List.of(top(bank(order.getFirst()), k(order.getFirst()))));
        for (int i = 1; i < order.size(); i++) {
            at.add(next(at.getLast(), order.get(i)));
        }
        if (at.getLast() - at.getFirst() >= CYCLE - CLOSE) {
            throw new IllegalArgumentException(figures.name() + ": its cylinders do not all fire within the cycle");
        }
        this.fired = at.stream().map(a -> around(a, CYCLE)).toList();
        for (final int n : order) {
            if (apart(fires(bank(n), k(n)), top(bank(n), k(n)), TURN) > CLOSE) {
                throw new IllegalArgumentException(figures.name() + ": its crank does not bring cylinder " + n
                        + " to its top as it fires " + order);
            }
        }
    }

    /** The cylinders, by number from one, in the order they fire. */
    public List<Integer> order() {
        return order;
    }

    /** The number of bank {@code bank}'s cylinder {@code k}, from one. */
    public int number(final int bank, final int k) {
        return bank * figures.perBank() + k + 1;
    }

    /** The bank cylinder {@code n} is in, from nought. */
    public int bank(final int n) {
        return (n - 1) / figures.perBank();
    }

    /** Which of its bank cylinder {@code n} is, from nought at the front. */
    public int k(final int n) {
        return (n - 1) % figures.perBank();
    }

    /** How far the crank has turned from rest when bank {@code bank}'s cylinder {@code k}'s piston is at its top. */
    public double top(final int bank, final int k) {
        return around(figures.lean(bank) - crank.angle(bank, k), TURN);
    }

    /** How far through the cycle that cylinder fires, from the crank at rest. */
    public double fires(final int bank, final int k) {
        return fired.get(order.indexOf(number(bank, k)));
    }

    /** How far the crank has turned since that cylinder last fired, the crank turned {@code turned} from rest. */
    public double since(final int bank, final int k, final double turned) {
        return around(turned - fires(bank, k), CYCLE);
    }

    /** How far the crank turns from each cylinder's firing to the next's, in order, and from the last to the first. */
    public List<Double> gaps() {
        final List<Double> gaps = new ArrayList<>();
        for (int i = 0; i < fired.size(); i++) {
            gaps.add(around(fired.get((i + 1) % fired.size()) - fired.get(i), CYCLE));
        }
        return gaps;
    }

    /** Whether each cylinder fires as far on from the one before as every other does. */
    public boolean even() {
        return gaps().stream().allMatch(g -> Math.abs(g - CYCLE / order.size()) < CLOSE);
    }

    /** Its order written as its makers write it: 1-3-4-2. */
    public String written() {
        return String.join("-", order.stream().map(String::valueOf).toList());
    }

    /** Where in the cycle cylinder {@code n} fires next after {@code after}: its top nearest an even share on. */
    private double next(final double after, final int n) {
        final double even = after + CYCLE / figures.cylinders();
        double d = around(top(bank(n), k(n)) - even, TURN);
        if (d > TURN / 2) {
            d -= TURN;
        }
        return even + d;
    }

    /**
     * The order its crank fires in where none is known: at each step, the cylinder up nearest an even share on, the
     * lowest numbered of those as near.
     */
    private List<Integer> derived() {
        final int all = figures.cylinders();
        final List<Integer> made = new ArrayList<>(List.of(1));
        double at = top(0, 0);
        for (int i = 1; i < all; i++) {
            final double even = at + CYCLE / all;
            int best = 0;
            for (int n = 2; n <= all; n++) {
                if (!made.contains(n) && (best == 0
                        || Math.abs(next(at, n) - even) < Math.abs(next(at, best) - even) - CLOSE)) {
                    best = n;
                }
            }
            made.add(best);
            at = next(at, best);
        }
        return List.copyOf(made);
    }

    private double around(final double angle, final double whole) {
        return (angle % whole + whole) % whole;
    }

    /** How far {@code a} is from {@code b}, the nearer way round {@code whole}. */
    private double apart(final double a, final double b, final double whole) {
        final double d = around(a - b, whole);
        return Math.min(d, whole - d);
    }
}
