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

    /** Robes fitted already, by the body they fit and how loose: fitting one is slow. */
    private static final java.util.Map<String, Garment> FITTED = new java.util.concurrent.ConcurrentHashMap<>();

    /** Its robe, from the waist to the neck and down to the elbows, fitted to its body's contour. */
    public Garment robe() {
        return FITTED.computeIfAbsent(proportions.said() + " " + ease, k -> Garment.robe(proportions, ease, Paint.ROBE));
    }

    /**
     * What is drawn of {@code p}, fitted to it, in its own frame as on the right side: its skin where it shows, its
     * wide sleeve, its leggings and shoes; nothing where its robe covers it ({@link #robe}).
     */
    public List<Surface> drawn(final Part p) {
        final List<Surface> skin = Skin.shapes(p, Muscles.Bulge.REST, Puppet.TWIST, Skin.HUMAN);
        final List<Surface> s = new ArrayList<>();
        switch (p) {
            case HEAD, NECK, RIGHT_HAND, LEFT_HAND -> s.addAll(skin);
            // the sleeve a wide bell from the elbow, its hem short of the wrist
            case RIGHT_FOREARM, LEFT_FOREARM -> s.addAll(Cloth.bell("sleeve", Paint.ROBE, Paint.TRIM,
                    new V3(0, 3, 0), new V3(0, -22, 0.5), 5 + ease, 13 + ease));
            case RIGHT_SHIN, LEFT_SHIN -> skin.forEach(k -> s.add(Cloth.fitted(k, 0.8, "legging", Paint.LEGGINGS)));
            case RIGHT_FOOT, LEFT_FOOT, RIGHT_TOES, LEFT_TOES ->
                    skin.forEach(k -> s.add(Cloth.fitted(k, 0.6, "shoe", Paint.CLOTH_SHOES)));
            // under the robe, out of sight: not drawn
            default -> {
            }
        }
        return s;
    }

    /**
     * The crossed collar, on the robe: round the back of the neck, its left lapel crossing over to the right under the
     * right arm, the right's edge short under it. Each point up from the waist to the neck, and round from the right.
     */
    private static final double[][] COLLAR = {{1, 75}, {1, 30}, {1, -30}, {1, -90}, {1, -150}, {1, 150}, {1, 110},
        {0.9, 95}, {0.8, 72}, {0.7, 48}, {0.62, 25}};
    private static final double[][] UNDER_COLLAR = {{1, 72}, {0.92, 84}};
    /** The kasaya's way on the robe: from over the left shoulder down across the chest to the right hip, round, back. */
    private static final double[][] KASAYA = {{0.94, 180}, {0.86, 140}, {0.66, 100}, {0.42, 60}, {0.2, 25},
        {0.06, 0}, {0.12, -40}, {0.4, -100}, {0.66, -140}, {0.86, -170}, {0.94, 180}};

    /** The points {@code way} on the robe, {@code off} out from it, as {@code rig} carries them. */
    private List<V3> laid(final double[][] way, final double off, final Rig rig) {
        final Garment robe = robe();
        final List<V3> out = new ArrayList<>();
        for (final double[] w : way) {
            out.add(robe.at(robe.on(w[0], w[1], off), rig));
        }
        return out;
    }

    /**
     * What is worked out afresh as {@code rig} stands, in the body's frame: the robe fitted to its contour, its body and
     * sleeves; its collar and the kasaya laid on it; the skirt hanging from the waist, clear of the legs.
     */
    public List<Surface> hanging(final Rig rig) {
        final double s = proportions.scale();
        final double b = proportions.build();
        final List<Surface> hung = new ArrayList<>(robe().at(rig));
        final V3 middle = rig.place(Part.CHEST, new V3(0, 0, 0));
        hung.addAll(Cloth.band("collar", Paint.TRIM, laid(COLLAR, 0.3, rig), middle, 3.6 * s, 0.7));
        hung.addAll(Cloth.band("under collar", Paint.TRIM, laid(UNDER_COLLAR, 0.15, rig), middle, 3 * s, 0.5));
        hung.addAll(Cloth.band("kasaya", Paint.KASAYA, laid(KASAYA, 0.6, rig), rig.place(Part.ABDOMEN,
                new V3(0, 10, 0)), 13 * s, 0.7));
        final List<Cloth.Leg> legs = new ArrayList<>();
        for (final int side : new int[] {1, -1}) {
            final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part shin = side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part foot = side > 0 ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            legs.add(new Cloth.Leg(rig.frame(thigh).at(), rig.frame(shin).at(), rig.frame(foot).at(), 9 * s * b,
                    6 * s * b, 4.5 * s * b));
        }
        // the skirt carrying on from the robe's bottom edge as it is now, its outline, point for point
        final Garment robe = robe();
        final List<V3> waist = robe.pieces().getFirst().rings().getFirst().stream().map(p -> robe.at(p, rig)).toList();
        hung.add(Cloth.skirt("skirt", Paint.ROBE, waist, hem * s, flare, legs, ease + 1.5, 9));
        return hung;
    }
}
