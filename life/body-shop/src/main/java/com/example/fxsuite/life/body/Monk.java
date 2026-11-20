package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * A monk, dressed: a long robe with a crossed collar, its left lapel over its right, its sleeves wide bells from the
 * elbow, its skirt a cone hanging from the waist to above the ankles, wide enough at every height to clear the legs as
 * they walk ({@link Cloth#skirt}); the kasaya, a broad band of cloth over the left shoulder, across the chest and the
 * back to the right hip; leggings below the hem, cloth shoes; head shaved. How tall and how broad, in what colours.
 * What is fitted to a part moves with it ({@link #drawn}); what hangs is worked out afresh as the body moves
 * ({@link #hanging}).
 *
 * @param name        what it is called
 * @param proportions how it is made
 * @param palette     its colours: its skin, robe, kasaya, trim, leggings and shoes
 * @param ease        how loose the robe is on the body, cm
 * @param hem         how high the skirt's hem is over the ground, cm
 * @param flare       how far the skirt flares, as a share of the hips' width out from them at the hem
 */
public record Monk(String name, Proportions proportions, Palette palette, double ease, double hem, double flare) {

    /** The monks of the temple, as the game has them: a small one in red, a tall one in blue, a stout one in saffron. */
    public static final List<Monk> TEMPLE = List.of(
            monk("Small monk", 162, 1, "#a3242b", "#d7a17a"),
            monk("Tall monk", 188, 1, "#2e5a9e", "#c99670"),
            monk("Stout monk", 172, 1.3, "#d9a521", "#b98762"));

    private static Monk monk(final String name, final double tall, final double build, final String robe,
                             final String skin) {
        final Proportions p = new Proportions(tall, build, Proportions.PAPERS.legs(), Proportions.PAPERS.arms(),
                Proportions.PAPERS.shoulders(), Proportions.PAPERS.hips());
        final Palette colours = Palette.DEFAULT.with(Paint.SKIN, skin).with(Paint.ROBE, robe)
                .with(Paint.KASAYA, darker(robe, 0.6)).with(Paint.TRIM, darker(robe, 0.3));
        return new Monk(name, p, colours, 1.5, 11, 0.35);
    }

    /** {@code colour} as dark as {@code share} of itself. */
    static String darker(final String colour, final double share) {
        final int c = Integer.parseInt(colour.substring(1), 16);
        final int r = (int) Math.round(((c >> 16) & 0xff) * share);
        final int g = (int) Math.round(((c >> 8) & 0xff) * share);
        final int b = (int) Math.round((c & 0xff) * share);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public Monk with(final double looser, final double higher, final double wider) {
        return new Monk(name, proportions, palette, looser, higher, wider);
    }

    public Monk with(final Palette colours) {
        return new Monk(name, proportions, colours, ease, hem, flare);
    }

    /** What is drawn of {@code p}, fitted to it, in its own frame as on the right side: its robe, or its skin. */
    public List<Surface> drawn(final Part p) {
        final List<Surface> skin = Skin.shapes(p, Muscles.Bulge.REST, Puppet.TWIST, Skin.HUMAN);
        final List<Surface> s = new ArrayList<>();
        switch (p) {
            case HEAD, NECK, RIGHT_HAND, LEFT_HAND -> s.addAll(skin);
            case CHEST -> {
                skin.forEach(k -> s.add(Cloth.fitted(k, ease, "robe", Paint.ROBE)));
                s.addAll(collar(s.getFirst()));
            }
            case ABDOMEN -> skin.forEach(k -> s.add(Cloth.fitted(k, ease, "robe", Paint.ROBE)));
            case PELVIS -> skin.forEach(k -> s.add(Cloth.fitted(k, ease + 0.5, "robe", Paint.ROBE)));
            // over the shoulders close, so they do not stand up round the neck
            case RIGHT_GIRDLE, LEFT_GIRDLE -> skin.forEach(k -> s.add(Cloth.fitted(k, 0.5, "robe", Paint.ROBE)));
            case RIGHT_UPPER_ARM, LEFT_UPPER_ARM ->
                    skin.forEach(k -> s.add(Cloth.fitted(k, ease, "robe", Paint.ROBE)));
            // the sleeve a wide bell from the elbow, its hem short of the wrist
            case RIGHT_FOREARM, LEFT_FOREARM -> s.addAll(Cloth.bell("sleeve", Paint.ROBE, Paint.TRIM,
                    new V3(0, 3, 0), new V3(0, -22, 0.5), 5 + ease, 13 + ease));
            case RIGHT_SHIN, LEFT_SHIN -> skin.forEach(k -> s.add(Cloth.fitted(k, 0.8, "legging", Paint.LEGGINGS)));
            case RIGHT_FOOT, LEFT_FOOT, RIGHT_TOES, LEFT_TOES ->
                    skin.forEach(k -> s.add(Cloth.fitted(k, 0.6, "shoe", Paint.CLOTH_SHOES)));
            // under the robe, out of sight: not drawn
            case RIGHT_THIGH, LEFT_THIGH -> {
            }
        }
        return s;
    }

    /**
     * The crossed collar on {@code robe}, the chest's: a band round the back of the neck, its left lapel crossing over
     * to the right under the right arm, the right's edge short under it.
     */
    private static List<Surface> collar(final Surface robe) {
        final List<V3> over = List.of(on(robe, 6, 31, -1), on(robe, 0, 32, -1), on(robe, -6, 31, -1),
                on(robe, -5, 30, 1), on(robe, 0, 24, 1), on(robe, 6, 18, 1), on(robe, 11, 12, 1));
        final List<V3> under = List.of(on(robe, 5, 30, 1), on(robe, 1, 26, 1));
        final V3 middle = new V3(0, 15, 0);
        final List<Surface> s = new ArrayList<>(Cloth.band("collar", Paint.TRIM, over, middle, 3.6, 0.7));
        s.addAll(Cloth.band("under collar", Paint.TRIM, under, middle, 3, 0.5));
        return s;
    }

    /**
     * The point of {@code robe} nearest {@code x} across at {@code y} up, on its front ({@code side} 1) or back (-1):
     * out of it a little, for what lies on it.
     */
    static V3 on(final Surface robe, final double x, final double y, final int side) {
        List<V3> ring = robe.rings().getFirst();
        double off = Double.POSITIVE_INFINITY;
        for (final List<V3> r : robe.rings()) {
            double h = 0;
            for (final V3 p : r) {
                h += p.y();
            }
            h /= r.size();
            if (Math.abs(h - y) < off) {
                off = Math.abs(h - y);
                ring = r;
            }
        }
        V3 best = ring.getFirst();
        double miss = Double.POSITIVE_INFINITY;
        for (final V3 p : ring) {
            if (p.z() * side > 0 && Math.abs(p.x() - x) < miss) {
                miss = Math.abs(p.x() - x);
                best = p;
            }
        }
        return best.plus(new V3(0, 0, 0.4 * side));
    }

    /** The kasaya's way round the body, each point in a part's frame, as on the right side; round from the shoulder. */
    private static final List<Object[]> KASAYA = List.of(
            new Object[] {Part.CHEST, new V3(-11, 27, 1)},
            new Object[] {Part.CHEST, new V3(-9, 21, 13.5)},
            new Object[] {Part.CHEST, new V3(-1, 11, 16.5)},
            new Object[] {Part.ABDOMEN, new V3(8, 6, 15)},
            new Object[] {Part.PELVIS, new V3(16, 12, 9)},
            new Object[] {Part.PELVIS, new V3(20, 10, -2)},
            new Object[] {Part.PELVIS, new V3(15, 13, -13)},
            new Object[] {Part.ABDOMEN, new V3(4, 8, -15)},
            new Object[] {Part.CHEST, new V3(-6, 12, -15.5)},
            new Object[] {Part.CHEST, new V3(-10, 22, -11)},
            new Object[] {Part.CHEST, new V3(-11, 27, 1)});

    /**
     * What hangs, as {@code rig} stands, in the body's frame: the skirt from the waist, clear of the legs; the kasaya
     * round the body from the left shoulder.
     */
    public List<Surface> hanging(final Rig rig) {
        final double s = proportions.scale();
        final double b = proportions.build();
        final List<Cloth.Leg> legs = new ArrayList<>();
        for (final int side : new int[] {1, -1}) {
            final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part shin = side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part foot = side > 0 ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            legs.add(new Cloth.Leg(rig.frame(thigh).at(), rig.frame(shin).at(), rig.frame(foot).at(), 9 * s * b,
                    6 * s * b, 4.5 * s * b));
        }
        final V3 waist = rig.place(Part.PELVIS, new V3(0, 9, 0.5));
        final double across = 17 * s * b + ease + 1;
        final double deep = 12 * s * b + ease + 1;
        final List<Surface> hung = new ArrayList<>();
        hung.add(Cloth.skirt("skirt", Paint.ROBE, waist, across, deep, hem * s, across * (1 + flare),
                deep * (1 + flare * 1.2), legs, ease + 1.5, 9));
        final List<V3> way = new ArrayList<>();
        for (final Object[] o : KASAYA) {
            way.add(rig.place((Part) o[0], (V3) o[1]));
        }
        hung.addAll(Cloth.band("kasaya", Paint.KASAYA, way, rig.place(Part.ABDOMEN, new V3(0, 10, 0)),
                13 * s, 0.7));
        return hung;
    }
}
