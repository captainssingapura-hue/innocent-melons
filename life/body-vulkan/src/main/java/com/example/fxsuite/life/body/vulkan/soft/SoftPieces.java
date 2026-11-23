package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.soft.Fascia;
import com.example.fxsuite.life.body.soft.Lattice;
import com.example.fxsuite.life.body.soft.RegionPieces;
import com.example.fxsuite.life.body.soft.SoftRegion;
import com.example.fxsuite.life.body.vulkan.Sheet;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Ellipsoid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * What the soft bench on the card draws of a region and what it takes in ({@link RegionPieces}), as the card draws
 * it, in the body's frame: each muscle taken in, its part in the region in muscle and tendon, its part beyond a darker
 * paint, fixed where it is cut; the region's twelve edges; where the taken are cut, blue; where held to their bones,
 * yellow. Every muscle's surface made ready for the card once ({@link Sheet}), each muscle's whole for when it is held.
 */
public final class SoftPieces {

    /** How thick the region's edges are drawn, and how big a cut and a hold, cm. */
    private static final double EDGE = 0.12;
    private static final double CUT = 0.18;
    private static final double HOLD = 0.45;
    /** How far tendon a triangle is, on the average of its corners, to be drawn as tendon. */
    private static final double TENDON = 0.5;
    /** The colours: beyond the region, a cut, a hold, an edge. */
    private static final Vec3 BEYOND = new Vec3(0.35, 0.12, 0.10);
    private static final Vec3 CUTS = new Vec3(0.18, 0.5, 0.84);
    private static final Vec3 HOLDS = new Vec3(0.95, 0.76, 0.19);
    private static final Vec3 EDGES = new Vec3(0.23, 0.23, 0.23);
    /** The colours of each taken muscle's elements, in turn; of those fixed. */
    private static final List<Vec3> HUES = List.of(new Vec3(0.85, 0.33, 0.25), new Vec3(0.95, 0.6, 0.2),
            new Vec3(0.3, 0.65, 0.35), new Vec3(0.62, 0.8, 0.3), new Vec3(0.6, 0.35, 0.75), new Vec3(0.9, 0.8, 0.3),
            new Vec3(0.3, 0.75, 0.75), new Vec3(0.85, 0.45, 0.6));
    private static final Vec3 FIXED = new Vec3(0.45, 0.45, 0.45);

    /** What a thing drawn is: a taken muscle's surface, its elements, or a mark of the region's. */
    public enum Kind { SURFACE, ELEMENTS, MARK, FASCIA }

    /**
     * Something drawn: of the kind {@code kind}, in {@code material}, of the colour {@code tint} where the material
     * takes one.
     */
    public record Drawn(Kind kind, Material material, Vec3 tint, Mesh mesh) {
    }

    /**
     * What is drawn of one region: of the taken, each by its number among all the muscles; the rest, the marks and
     * the edges.
     *
     * @param taken  the numbers of the muscles taken in
     * @param drawn  what is drawn of them, and the region's edges, cuts and holds
     */
    public record Found(List<Integer> taken, List<Drawn> drawn) {
    }

    private final List<RegionPieces.Piece> all;
    private final Sheet[] sheets;
    private final Map<RegionPieces.Piece, Integer> numbers = new IdentityHashMap<>();
    private final ElementsDrawn elements = new ElementsDrawn();

    /** The pieces of {@code all}, every muscle on both sides, each made ready for the card. */
    public SoftPieces(final List<RegionPieces.Piece> all) {
        this.all = List.copyOf(all);
        this.sheets = new Sheet[all.size()];
        for (int i = 0; i < sheets.length; i++) {
            sheets[i] = new Sheet(all.get(i).surface().mesh().points(), all.get(i).surface().mesh().triangles());
            numbers.put(all.get(i), i);
        }
    }

    /** Every muscle whole, in its order, to be drawn as it is held round the region. */
    public List<Mesh> wholes() {
        final List<Mesh> made = new ArrayList<>();
        for (final Sheet s : sheets) {
            made.add(s.mesh());
        }
        return made;
    }

    /**
     * What is drawn of {@code region} and what it takes in, {@code taken}: their surfaces, and their elements,
     * {@code lattices}, one for each taken, in their order.
     */
    public Found found(final SoftRegion region, final RegionPieces.Taken taken, final List<Lattice> lattices) {
        return found(region, taken, lattices, false);
    }

    /**
     * The same, each taken muscle's surface where its lattice carries it if {@code carried}: as a solve has moved it.
     */
    public Found found(final SoftRegion region, final RegionPieces.Taken taken, final List<Lattice> lattices,
                       final boolean carried) {
        return found(region, taken, lattices, carried, Optional.empty());
    }

    /** The same, and the fascia wrapping them, {@code sleeve}, if any. */
    public Found found(final SoftRegion region, final RegionPieces.Taken taken, final List<Lattice> lattices,
                       final boolean carried, final Optional<Fascia.Sleeve> sleeve) {
        final List<Integer> mine = new ArrayList<>();
        final List<Drawn> drawn = new ArrayList<>();
        for (int m = 0; m < taken.taken().size(); m++) {
            final RegionPieces.Piece p = taken.taken().get(m);
            final int n = numbers.get(p);
            mine.add(n);
            final Sheet s = carried ? new Sheet(lattices.get(m).carrying(p.surface().mesh().points()),
                    p.surface().mesh().triangles()) : sheets[n];
            final float[] tendon = p.surface().tendon();
            final boolean[] in = new boolean[s.triangleCount()];
            final boolean[] isTendon = new boolean[in.length];
            for (int t = 0; t < in.length; t++) {
                V3 mid = V3.ZERO;
                double how = 0;
                for (int k = 0; k < 3; k++) {
                    final Vec3 v = s.point(s.corner(t, k));
                    mid = mid.plus(new V3(v.x() / 3, v.y() / 3, v.z() / 3));
                    how += tendon[s.corner(t, k)] / 3;
                }
                in[t] = region.has(mid);
                isTendon[t] = how >= TENDON;
            }
            add(drawn, Kind.SURFACE, new Materials.Muscle(), BEYOND, s.mesh(t -> in[t] && !isTendon[t]));
            add(drawn, Kind.SURFACE, new Materials.Tendon(), BEYOND, s.mesh(t -> in[t] && isTendon[t]));
            add(drawn, Kind.SURFACE, new Materials.GlossPaint(), BEYOND, s.mesh(t -> !in[t]));
        }
        for (int i = 0; i < lattices.size(); i++) {
            add(drawn, Kind.ELEMENTS, new Materials.GlossPaint(), HUES.get(i % HUES.size()),
                    elements.of(lattices.get(i), false));
            add(drawn, Kind.ELEMENTS, new Materials.GlossPaint(), FIXED, elements.of(lattices.get(i), true));
        }
        sleeve.ifPresent(f -> add(drawn, Kind.FASCIA, new Materials.Glass(), new Vec3(1, 1, 1), tube(f)));
        add(drawn, Kind.MARK, new Materials.GlossPaint(), CUTS, marks(taken.cuts(), CUT));
        add(drawn, Kind.MARK, new Materials.GlossPaint(), HOLDS, marks(taken.attached(), HOLD));
        for (final Mesh edge : edges(region)) {
            add(drawn, Kind.MARK, new Materials.GlossPaint(), EDGES, edge);
        }
        return new Found(mine, drawn);
    }

    /** How many muscles there are, both sides. */
    public int count() {
        return all.size();
    }

    /** The muscle numbered {@code n}. */
    public RegionPieces.Piece piece(final int n) {
        return all.get(n);
    }

    private void add(final List<Drawn> drawn, final Kind kind, final Material m, final Vec3 tint, final Mesh mesh) {
        if (!mesh.corners().isEmpty()) {
            drawn.add(new Drawn(kind, m, tint, mesh));
        }
    }

    /** The fascia {@code f} as a tube: each two rings with muscle in them joined, facing out. */
    private Mesh tube(final Fascia.Sleeve f) {
        final List<Vec3> points = new ArrayList<>();
        for (int i = 0; i < f.points().length; i += 3) {
            points.add(new Vec3(f.points()[i], f.points()[i + 1], f.points()[i + 2]));
        }
        final List<Integer> corners = new ArrayList<>();
        final int n = f.round();
        Boolean outward = null;
        for (int r = 0; r + 1 < f.rings(); r++) {
            if (!f.filled()[r] || !f.filled()[r + 1]) {
                continue;
            }
            if (outward == null) {
                outward = facesOut(points, r * n, n);
            }
            for (int a = 0; a < n; a++) {
                final int p = r * n + a;
                final int q = r * n + (a + 1) % n;
                final int[][] two = outward ? new int[][] {{p, q, q + n}, {p, q + n, p + n}}
                        : new int[][] {{p, q + n, q}, {p, p + n, q + n}};
                for (final int[] t : two) {
                    corners.add(t[0]);
                    corners.add(t[1]);
                    corners.add(t[2]);
                }
            }
        }
        final List<Vec3> facings = new ArrayList<>();
        for (int r = 0; r < f.rings(); r++) {
            Vec3 middle = new Vec3(0, 0, 0);
            for (int a = 0; a < n; a++) {
                middle = middle.plus(points.get(r * n + a).times(1.0 / n));
            }
            for (int a = 0; a < n; a++) {
                final Vec3 out = points.get(r * n + a).minus(middle);
                facings.add(out.length() < 1e-9 ? new Vec3(0, 1, 0) : out.unit());
            }
        }
        return new Mesh(points, facings, corners);
    }

    /** Whether a ring's first quad, as {@link #tube} first winds it, faces away from the ring's middle. */
    private boolean facesOut(final List<Vec3> points, final int first, final int n) {
        Vec3 middle = new Vec3(0, 0, 0);
        for (int a = 0; a < n; a++) {
            middle = middle.plus(points.get(first + a).times(1.0 / n));
        }
        final Vec3 p = points.get(first);
        final Vec3 face = points.get(first + 1).minus(p).cross(points.get(first + 1 + n).minus(p));
        return face.dot(p.minus(middle)) > 0;
    }

    /** A small ball {@code r} across at each of {@code at}, all one mesh. */
    private Mesh marks(final List<V3> at, final double r) {
        final Mesh ball = new Ellipsoid(2 * r, 2 * r, 2 * r).formed(8, 5);
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>();
        for (final V3 a : at) {
            final int first = points.size();
            for (int i = 0; i < ball.points().size(); i++) {
                points.add(ball.points().get(i).plus(new Vec3(a.x(), a.y(), a.z())));
                facings.add(ball.facings().get(i));
            }
            for (final int c : ball.corners()) {
                corners.add(first + c);
            }
        }
        return new Mesh(points, facings, corners);
    }

    /** The region's twelve edges, each a thin box along one of the body's ways. */
    private List<Mesh> edges(final SoftRegion region) {
        final double[] lo = {region.lo().x(), region.lo().y(), region.lo().z()};
        final double[] hi = {region.hi().x(), region.hi().y(), region.hi().z()};
        final List<Mesh> edges = new ArrayList<>();
        for (int along = 0; along < 3; along++) {
            for (int k = 0; k < 4; k++) {
                final double[] mid = new double[3];
                final double[] size = new double[3];
                int bit = 0;
                for (int c = 0; c < 3; c++) {
                    if (c == along) {
                        mid[c] = (lo[c] + hi[c]) / 2;
                        size[c] = Math.max(EDGE, hi[c] - lo[c]);
                    } else {
                        mid[c] = (k >> bit & 1) == 0 ? lo[c] : hi[c];
                        size[c] = EDGE;
                        bit++;
                    }
                }
                edges.add(moved(new Cuboid(size[0], size[1], size[2]).formed(), new Vec3(mid[0], mid[1], mid[2])));
            }
        }
        return edges;
    }

    /** {@code m} moved by {@code by}. */
    private Mesh moved(final Mesh m, final Vec3 by) {
        final List<Vec3> points = new ArrayList<>();
        for (final Vec3 p : m.points()) {
            points.add(p.plus(by));
        }
        return new Mesh(points, m.facings(), m.corners());
    }
}
