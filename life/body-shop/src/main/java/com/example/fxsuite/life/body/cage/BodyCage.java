package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Fleshing;
import com.example.fxsuite.life.body.flesh.Landmark;
import com.example.fxsuite.life.body.flesh.Physique;
import com.example.fxsuite.life.body.flesh.Torso;
import com.example.fxsuite.life.body.flesh.Tube;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A body's flesh as one cage ({@link Cage}), held as it is bound ({@link Fleshing#bind}): its torso rings of
 * {@link #COLUMNS} points each, from low on its hips to under its jaw ({@link TorsoRing}), each where its profile puts
 * it at that height and angle round, as its shaping moves it ({@link Shaping}); shut over the top. A leg hung from each
 * half of the lowest ring, round through the crotch; an arm from a loop of eight points at each side, from the armpits
 * to the shoulders; each limb's rings its tubes' ({@link LimbCage}). The right put together and the left its mirror.
 * Its points where the skin is, in the body's centimetres: the cage to smooth found from them ({@link Fitting}).
 */
public record BodyCage() implements StatelessFunctionalObject {

    public static final BodyCage INSTANCE = new BodyCage();

    /** How many points round each torso ring. */
    public static final int COLUMNS = 20;
    /** The columns an arm goes out between, on the right. */
    private static final int ARM = 5;

    /** {@code physique}'s cage as drawn, its points where the skin is, its muscles as defined as {@code definition}. */
    public Cage drawn(final Physique physique, final double definition) {
        final Rig drawn = new Rig(Proportions.DRAWN, Fleshing.INSTANCE.bind(), false);
        final Rig bind = new Rig(physique.proportions(), Fleshing.INSTANCE.bind(), false);
        final double scale = physique.proportions().scale();
        final Shaping shaping = physique == Physique.MAN ? ManCage.INSTANCE.shaping()
                : WomanCage.INSTANCE.shaping();
        final Building b = new Building();
        final Profile profile = new Profile(new Torso(shaping.keys(), List.of(), definition), drawn);
        final int[][] grid = torso(b, profile, drawn, scale, shaping, definition);
        final double low = Landmark.CROTCH.y(drawn);
        final int crotch = b.add(new V3(0, low, profile.middle(low)).times(scale), "the crotch");
        b.fellows(crotch, crotch);
        final int limbs = b.size();
        final int limbFaces = b.faceCount();
        final int[] leg = new int[COLUMNS / 2 + 2];
        for (int j = 0; j <= COLUMNS / 2; j++) {
            leg[j] = grid[0][j];
        }
        leg[COLUMNS / 2 + 1] = crotch;
        final List<Tube> limbsOf = physique.tubes();
        LimbCage.INSTANCE.hang(b, leg, List.of(new LimbCage.Piece(tube(limbsOf, Part.RIGHT_THIGH), 1, 1.1, 0, 2.2, 1.4, 0.6),
                new LimbCage.Piece(tube(limbsOf, Part.RIGHT_SHIN), 1, 1.08)), bind, physique.female());
        final int a = TorsoRing.ARMPITS.ordinal();
        final int[] arm = {grid[a][ARM + 1], grid[a][ARM], grid[a][ARM - 1], grid[a + 1][ARM - 1],
            grid[a + 2][ARM - 1], grid[a + 2][ARM], grid[a + 2][ARM + 1], grid[a + 1][ARM + 1]};
        LimbCage.INSTANCE.hang(b, arm, List.of(new LimbCage.Piece(tube(limbsOf, Part.RIGHT_UPPER_ARM), 1, 1),
                new LimbCage.Piece(tube(limbsOf, Part.RIGHT_FOREARM), 1, 1)), bind, physique.female());
        b.mirror(limbs, limbFaces);
        lines(b, grid, shaping, definition);
        return b.cage();
    }

    /** {@code physique}'s cage, found so that the surface smoothed from it passes through its points as drawn. */
    public Cage of(final Physique physique, final double definition) {
        return Fitting.INSTANCE.fitted(drawn(physique, definition), definition);
    }

    /** The torso's rings, every point numbered in the grid by ring and column; none where an arm goes out. */
    private int[][] torso(final Building b, final Profile profile, final Rig drawn, final double scale,
                          final Shaping shaping, final double definition) {
        final TorsoRing[] rings = TorsoRing.values();
        final Map<Shaping.Spot, Shaping.Push> pushes = new HashMap<>();
        shaping.pushes().forEach(p -> pushes.put(p.at(), p));
        final Map<Shaping.Spot, Double> depths = depths(shaping, definition);
        final int[][] grid = new int[rings.length][COLUMNS];
        for (int r = 0; r < rings.length; r++) {
            for (int j = 0; j < COLUMNS; j++) {
                grid[r][j] = -1;
                if (hole(r, j)) {
                    continue;
                }
                final int right = j <= COLUMNS / 2 ? j : COLUMNS - j;
                final double sign = j <= COLUMNS / 2 ? 1 : -1;
                final Shaping.Spot spot = new Shaping.Spot(rings[r], right);
                final Shaping.Push p = pushes.getOrDefault(spot, new Shaping.Push("", spot, 0, 0, 0, 0));
                final double h = rings[r].height().y(drawn) + p.up();
                final V3 at = profile.at(h, sign * (360.0 * right / COLUMNS + p.turn()),
                        p.out() - depths.getOrDefault(spot, 0.0)).plus(new V3(0, 0, p.ahead()));
                grid[r][j] = b.add(at.times(scale), rings[r].said() + ", " + column(j));
            }
        }
        for (int r = 0; r < rings.length; r++) {
            for (int j = 0; j <= COLUMNS / 2; j++) {
                if (grid[r][j] >= 0) {
                    b.fellows(grid[r][j], grid[r][(COLUMNS - j) % COLUMNS]);
                }
            }
        }
        for (int r = 0; r + 1 < rings.length; r++) {
            for (int j = 0; j < COLUMNS; j++) {
                final int n = (j + 1) % COLUMNS;
                if (!opening(r, j)) {
                    b.face(grid[r][j], grid[r][n], grid[r + 1][n], grid[r + 1][j]);
                }
            }
        }
        b.face(grid[rings.length - 1]);
        return grid;
    }

    /** Whether ring {@code r}'s point in column {@code j} is where an arm goes out, and so none. */
    private boolean hole(final int r, final int j) {
        return r == TorsoRing.UNDER_SHOULDERS.ordinal() && (j == ARM || j == COLUMNS - ARM);
    }

    /** Whether the face above ring {@code r} from column {@code j} is in an arm's opening. */
    private boolean opening(final int r, final int j) {
        final int a = TorsoRing.ARMPITS.ordinal();
        final boolean high = r == a || r == a + 1;
        return high && (j == ARM - 1 || j == ARM || j == COLUMNS - ARM - 1 || j == COLUMNS - ARM);
    }

    /** How far each point is pressed in by the lines through it, the deepest's. */
    private Map<Shaping.Spot, Double> depths(final Shaping shaping, final double definition) {
        final Map<Shaping.Spot, Double> d = new HashMap<>();
        for (final Shaping.Line l : shaping.lines()) {
            final double depth = l.depth() * (l.muscle() ? definition : 1);
            l.path().forEach(s -> d.merge(s, depth, Math::max));
        }
        return d;
    }

    /** The shaping's lines creased along the torso's edges, each on both sides if said. */
    private void lines(final Building b, final int[][] grid, final Shaping shaping, final double definition) {
        for (final Shaping.Line l : shaping.lines()) {
            final List<Integer> path = l.path().stream().map(s -> grid[s.ring().ordinal()][s.column()]).toList();
            b.crease(new Crease(l.said() + (l.both() ? ", right" : ""), path, l.sharpness(), l.muscle()));
            if (l.both()) {
                b.crease(new Crease(l.said() + ", left", path.stream().map(b::fellow).toList(), l.sharpness(),
                        l.muscle()));
            }
        }
    }

    /** The tube on part {@code p}. */
    private Tube tube(final List<Tube> tubes, final Part p) {
        return tubes.stream().filter(t -> t.part() == p).findFirst().orElseThrow();
    }

    /** Column {@code j} in words. */
    private String column(final int j) {
        final int deg = 360 * j / COLUMNS;
        return j == 0 ? "ahead" : j == COLUMNS / 2 ? "behind" : j < COLUMNS / 2 ? deg + "° right"
                : (360 - deg) + "° left";
    }
}
