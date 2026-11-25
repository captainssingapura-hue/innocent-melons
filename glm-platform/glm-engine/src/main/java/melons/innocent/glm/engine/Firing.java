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
 * 1-7-5-11-3-9-6-12-2-8-4-10, an even order its crank makes. Other figures fire as their crank brings each cylinder
 * up, the lowest numbered first. The first cylinder fires at its top in the first turn, each after it the cycle
 * shared evenly on; refused if its crank does not bring each cylinder to its top then.
 */
public final class Firing {

    /** The four strokes' cycle, in degrees of crank. */
    public static final double CYCLE = 720;
    private static final double TURN = 360;
    private static final double CLOSE = 1e-6;
    /** The orders the engines it is after fire in, by how many banks and how many cylinders to a bank. */
    private static final Map<List<Integer>, List<Integer>> KNOWN = Map.of(
            List.of(1, 4), List.of(1, 3, 4, 2),
            List.of(1, 5), List.of(1, 2, 4, 5, 3),
            List.of(2, 3), List.of(1, 4, 3, 6, 2, 5),
            List.of(2, 4), List.of(1, 3, 7, 2, 6, 5, 4, 8),
            List.of(2, 6), List.of(1, 7, 5, 11, 3, 9, 6, 12, 2, 8, 4, 10));

    private final EngineFigures figures;
    private final CrankFigures crank;
    private final List<Integer> order;

    /** {@code figures}' firing, refused if its crank does not bring each cylinder to its top as it fires. */
    public Firing(final EngineFigures figures) {
        this.figures = figures;
        this.crank = new CrankFigures(figures);
        final List<Integer> known = KNOWN.get(List.of(figures.banks(), figures.perBank()));
        this.order = known != null ? known : derived();
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
        final int first = order.getFirst();
        return around(top(bank(first), k(first)) + order.indexOf(number(bank, k)) * CYCLE / order.size(), CYCLE);
    }

    /** How far the crank has turned since that cylinder last fired, the crank turned {@code turned} from rest. */
    public double since(final int bank, final int k, final double turned) {
        return around(turned - fires(bank, k), CYCLE);
    }

    /** Its order written as its makers write it: 1-3-4-2. */
    public String written() {
        return String.join("-", order.stream().map(String::valueOf).toList());
    }

    /** The order its crank fires in where none is known: at each step of the cycle, the lowest numbered up then. */
    private List<Integer> derived() {
        final int all = figures.cylinders();
        final List<Integer> made = new ArrayList<>(List.of(1));
        for (int i = 1; i < all; i++) {
            final double at = top(0, 0) + i * CYCLE / all;
            for (int n = 2; n <= all && made.size() <= i; n++) {
                if (!made.contains(n) && apart(at, top(bank(n), k(n)), TURN) < CLOSE) {
                    made.add(n);
                }
            }
            if (made.size() <= i) {
                throw new IllegalArgumentException(figures.name() + ": its crank fires unevenly");
            }
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
