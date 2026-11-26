package melons.innocent.vehicles.boat;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Lofting;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A fun boat for simple waterways, after a Singapore river boat: its hull ({@link Hull}) on its typed lines
 * ({@link HullLines}); aft and amidships an open timber cabin, posts and two rails along each side, benches inside,
 * a roof with deep eaves and red paper lanterns hung all along them; at the cabin's front a wheelhouse, its wheel
 * standing square across the boat for a helmsman behind it; forward an open foredeck railed in steel, a bollard on it;
 * tyres hung over the sides for fenders, and one on the stem. In its own frame: across to its right, up from the
 * water's line, along to its bow; centimetres. Its figures and its shapes are the builder's.
 */
public record RiverBoat(HullLines lines) {

    /** The deck's height amidships; the cabin's posts' tops; how far aft and forward the cabin runs. */
    private static final double DECK = 96;
    private static final double ROOF = 310;
    private static final double AFT = -620;
    private static final double FORE = 130;
    private static final double POST = 9;
    /** The wheel's hub: how high over the deck, how far forward; the wheel's reach. */
    private static final double HUB_UP = 110;
    private static final double HUB_ALONG = 95;
    private static final double WHEEL = 30;

    /** The river boat. */
    public RiverBoat() {
        this(new HullLines());
    }

    /** How far below the water's line its keel reaches. */
    public double draught() {
        return 62;
    }

    /** How long it is, overall, and how wide. */
    public double length() {
        return 1318;
    }

    /** Its beam: its hull's widest, where its sheer swells amidships. */
    public double beam() {
        return 328;
    }

    /** Where its wheel's hub is, in its own frame. */
    public Vec3 hub() {
        return new Vec3(0, DECK + HUB_UP, HUB_ALONG);
    }

    /** Where a helmsman's eye is, behind the wheel. */
    public Vec3 helm() {
        return new Vec3(0, DECK + 180, HUB_ALONG - 130);
    }

    /** Every part of it. */
    public List<BoatPart> parts() {
        final List<BoatPart> parts = new ArrayList<>();
        new Hull(lines).painted().forEach((paint, mesh) -> parts.add(
                new BoatPart("hull, " + paint.name().toLowerCase(java.util.Locale.ROOT), BoatPart.Finish.PAINT,
                        paint.colour(), false, mesh)));
        parts.add(new BoatPart("cabin", BoatPart.Finish.TIMBER, new Vec3(1, 1, 1), false, cabin()));
        parts.add(new BoatPart("lanterns", BoatPart.Finish.LANTERN, new Vec3(1, 0.18, 0.1), false, lanterns()));
        parts.add(new BoatPart("rails", BoatPart.Finish.STEEL, new Vec3(1, 1, 1), false, rails()));
        parts.add(new BoatPart("tyres", BoatPart.Finish.RUBBER, new Vec3(1, 1, 1), false, tyres()));
        parts.add(new BoatPart("wheel", BoatPart.Finish.TIMBER, new Vec3(1, 1, 1), true, wheel()));
        return parts;
    }

    /** The cabin's posts, rails, benches and roof, and the wheelhouse's front, in timber. */
    private Mesh cabin() {
        final Forming forming = new Forming(new Vec3(0, 200, -200));
        final Lofting lofting = new Lofting(forming);
        for (final int side : new int[] {-1, 1}) {
            for (double z = AFT; z <= FORE + 1; z += (FORE - AFT) / 7) {
                lofting.box(new Vec3(side * 150, (DECK + ROOF) / 2, z), new Vec3(POST, ROOF - DECK, POST));
            }
            lofting.box(new Vec3(side * 150, DECK + 85, (AFT + FORE) / 2), new Vec3(7, 6, FORE - AFT));
            lofting.box(new Vec3(side * 150, DECK + 40, (AFT + FORE) / 2), new Vec3(5, 5, FORE - AFT));
            lofting.box(new Vec3(side * 118, DECK + 43, (AFT + 20) / 2), new Vec3(44, 6, 20 - AFT - 40));
        }
        lofting.box(new Vec3(0, ROOF + 7, (AFT + FORE) / 2 - 10), new Vec3(350, 14, FORE - AFT + 50));
        // the wheelhouse's front: a panel to the helmsman's waist across the cabin, and a stand for the wheel
        lofting.box(new Vec3(0, DECK + 50, FORE), new Vec3(300, 100, 6));
        lofting.box(new Vec3(0, DECK + HUB_UP / 2, HUB_ALONG + 12), new Vec3(16, HUB_UP, 12));
        return forming.formed();
    }

    /** Red lanterns hung all along both eaves. */
    private Mesh lanterns() {
        final Forming forming = new Forming(new Vec3(0, 280, 0));
        final Lofting lofting = new Lofting(forming);
        for (final int side : new int[] {-1, 1}) {
            for (double z = AFT + 20; z <= FORE; z += 95) {
                final List<List<Vec3>> rings = new ArrayList<>();
                final double[][] shape = {{ROOF - 34, 0.5}, {ROOF - 26, 1}, {ROOF - 14, 1}, {ROOF - 6, 0.5}};
                for (final double[] s : shape) {
                    rings.add(lofting.ring(new Vec3(side * 168, s[0], z), new Vec3(1, 0, 0), new Vec3(0, 0, 1),
                            12 * s[1], 12 * s[1], 2, 10));
                }
                lofting.lofted(rings);
            }
        }
        return forming.formed();
    }

    /** The foredeck's rail of steel posts and a top rail, round from the cabin to the bow, and a bollard. */
    private Mesh rails() {
        final Forming forming = new Forming(new Vec3(0, 150, 400));
        final Lofting lofting = new Lofting(forming);
        final List<Vec3> sheer = lines.line(HullLine.Kind.SHEER, 40);
        for (final int side : new int[] {-1, 1}) {
            Vec3 before = null;
            for (final Vec3 at : sheer) {
                if (at.z() < FORE + 20) {
                    continue;
                }
                final Vec3 foot = new Vec3(side * (at.x() - 6), at.y(), at.z());
                final Vec3 top = foot.plus(new Vec3(0, 80, 0));
                if (before == null || top.minus(before).length() > 90) {
                    lofting.bar(foot, top, 2.2, 8);
                    if (before != null) {
                        lofting.bar(before, top, 2.2, 8);
                    }
                    before = top;
                }
            }
        }
        lofting.bar(new Vec3(0, DECK + 2, 470), new Vec3(0, DECK + 30, 470), 9, 12);
        return forming.formed();
    }

    /** Tyres hung over each side for fenders, and one on the stem. */
    private Mesh tyres() {
        final Forming forming = new Forming(new Vec3(0, 60, 0));
        final List<Vec3> sheer = lines.line(HullLine.Kind.SHEER, 40);
        for (final int side : new int[] {-1, 1}) {
            for (final double z : new double[] {-560, -300, -40, 220, 430}) {
                final Vec3 at = nearest(sheer, z);
                new Tyre(32, 11).added(forming, new Vec3(side * (at.x() + 12), at.y() - 38, z), new Vec3(1, 0, 0));
            }
        }
        new Tyre(32, 11).added(forming, new Vec3(0, 95, 678), new Vec3(0, 0, 1));
        return forming.formed();
    }

    private Vec3 nearest(final List<Vec3> line, final double z) {
        return line.stream().min(java.util.Comparator.comparingDouble(p -> Math.abs(p.z() - z))).orElseThrow();
    }

    /** The wheel about its hub: a rim, six spokes and the hub, square to its third way. */
    private Mesh wheel() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Lofting lofting = new Lofting(forming);
        new Tyre(WHEEL, 2.2).added(forming, new Vec3(0, 0, 0), new Vec3(0, 0, 1));
        for (int k = 0; k < 6; k++) {
            final double t = Math.PI * k / 3;
            final Vec3 out = new Vec3(Math.cos(t), Math.sin(t), 0);
            lofting.bar(out.times(4), out.times(WHEEL + 9), 1.6, 6);
        }
        lofting.bar(new Vec3(0, 0, -5), new Vec3(0, 0, 5), 5, 10);
        return forming.formed();
    }
}
