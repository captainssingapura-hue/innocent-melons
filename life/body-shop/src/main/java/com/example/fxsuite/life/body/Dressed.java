package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Someone ({@link Person}) with their clothes on: each piece of their outfit ({@link Wear}) made to their body. A robe
 * fitted to the body's contour and moving with it ({@link Garment}), its collar laid on it, its skirt carrying on from
 * its waist and clear of the legs, measured as the body has them; a sash laid on what is under it; leggings and shoes
 * close on the skin. Their skin drawn where nothing covers it. What is close on a part moves with the part
 * ({@link #drawn}); what is fitted or hangs is worked out as the body moves ({@link #hanging}).
 */
public final class Dressed {

    /** Bodies' outlines and robes fitted to them already: working them out is slow. */
    private static final Map<String, Envelope> OUTLINES = new ConcurrentHashMap<>();
    private static final Map<String, Garment> FITTED = new ConcurrentHashMap<>();
    /** How loose what lies under a sash is taken to be, with no robe: the skin, just off it. */
    private static final double NEXT_TO_SKIN = 0.4;

    private final Person person;
    private final Outfit outfit;
    private final Wear.Robe robe;
    private final Garment fitted;
    /** How thick the legs are, as the body has them: at the hip, the knee and the ankle, cm. */
    private final double[] legs;

    public Dressed(final Person person) {
        this.person = person;
        this.outfit = person.outfit();
        this.robe = outfit.piece(Wear.Robe.class).orElse(null);
        final Proportions body = person.body();
        final Envelope outline = OUTLINES.computeIfAbsent(body.said(), k -> new Envelope(body));
        final double ease = robe != null ? robe.ease() : NEXT_TO_SKIN;
        fitted = FITTED.computeIfAbsent(body.said() + " " + ease, k -> Garment.robe(outline, ease, Paint.ROBE));
        legs = legs(outline);
    }

    public Person person() {
        return person;
    }

    /** Its colours: its skin's, and each piece's. */
    public Palette palette() {
        Palette p = Palette.DEFAULT.with(Paint.SKIN, person.skin());
        if (robe != null) {
            p = p.with(Paint.ROBE, robe.colour()).with(Paint.TRIM, robe.trim());
        }
        for (final Wear w : outfit.wear()) {
            p = switch (w) {
                case Wear.Sash s -> p.with(Paint.KASAYA, s.colour());
                case Wear.Leggings l -> p.with(Paint.LEGGINGS, l.colour());
                case Wear.Shoes s -> p.with(Paint.CLOTH_SHOES, s.colour());
                case Wear.Robe r -> p;
            };
        }
        return p;
    }

    /** The parts a robe covers, of the skin and what is close on it. */
    private static final Set<Part> ROBED = EnumSet.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE,
            Part.LEFT_GIRDLE, Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM, Part.RIGHT_THIGH, Part.LEFT_THIGH);

    /**
     * What is drawn of {@code p}, close on it, in its own frame as on the right side: its skin where nothing covers it;
     * a robe's sleeve below the elbow; leggings; shoes. Nothing where a robe covers it ({@link #hanging}).
     */
    public List<Surface> drawn(final Part p) {
        final List<Surface> skin = Skin.shapes(p, Muscles.Bulge.REST, Puppet.TWIST, Skin.HUMAN);
        final boolean leggings = outfit.piece(Wear.Leggings.class).isPresent();
        final boolean shoes = outfit.piece(Wear.Shoes.class).isPresent();
        final List<Surface> s = new ArrayList<>();
        if (robe != null && ROBED.contains(p)) {
            return s;
        }
        switch (p) {
            case RIGHT_FOREARM, LEFT_FOREARM -> {
                if (robe != null && robe.wideSleeves()) {
                    // the sleeve a wide bell from the elbow, its hem short of the wrist
                    s.addAll(Cloth.bell("sleeve", Paint.ROBE, Paint.TRIM, new V3(0, 3, 0), new V3(0, -22, 0.5),
                            5 + robe.ease(), 13 + robe.ease()));
                } else if (robe != null) {
                    skin.forEach(k -> s.add(Cloth.fitted(k, robe.ease() * 0.6, "sleeve", Paint.ROBE)));
                } else {
                    s.addAll(skin);
                }
            }
            case RIGHT_THIGH, LEFT_THIGH, RIGHT_SHIN, LEFT_SHIN -> {
                if (leggings) {
                    skin.forEach(k -> s.add(Cloth.fitted(k, 0.8, "legging", Paint.LEGGINGS)));
                } else {
                    s.addAll(skin);
                }
            }
            case RIGHT_FOOT, LEFT_FOOT, RIGHT_TOES, LEFT_TOES -> {
                if (shoes) {
                    skin.forEach(k -> s.add(Cloth.fitted(k, 0.6, "shoe", Paint.CLOTH_SHOES)));
                } else {
                    s.addAll(skin);
                }
            }
            default -> s.addAll(skin);
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
    /** A sash's way: from over the left shoulder down across the chest to the right hip, round, and back up. */
    private static final double[][] SASH = {{0.94, 180}, {0.86, 140}, {0.66, 100}, {0.42, 60}, {0.2, 25},
        {0.06, 0}, {0.12, -40}, {0.4, -100}, {0.66, -140}, {0.86, -170}, {0.94, 180}};

    /** The points {@code way} on what is fitted, {@code off} out from it, as {@code rig} carries them. */
    private List<V3> laid(final double[][] way, final double off, final Rig rig) {
        final List<V3> out = new ArrayList<>();
        for (final double[] w : way) {
            out.add(fitted.at(fitted.on(w[0], w[1], off), rig));
        }
        return out;
    }

    /**
     * What is worked out afresh as {@code rig} stands, in the body's frame: a robe fitted to the body's contour, its
     * collar laid on it, its skirt hanging from its waist clear of the legs; a sash laid on whatever is under it.
     */
    public List<Surface> hanging(final Rig rig) {
        final double s = person.body().scale();
        final List<Surface> hung = new ArrayList<>();
        final V3 middle = rig.place(Part.CHEST, V3.ZERO);
        if (robe != null) {
            hung.addAll(fitted.at(rig));
            hung.addAll(Cloth.band("collar", Paint.TRIM, laid(COLLAR, 0.3, rig), middle, 3.6 * s, 0.7));
            hung.addAll(Cloth.band("under collar", Paint.TRIM, laid(UNDER_COLLAR, 0.15, rig), middle, 3 * s, 0.5));
            final List<Cloth.Leg> legs = new ArrayList<>();
            for (final int side : new int[] {1, -1}) {
                final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
                final Part shin = side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
                final Part foot = side > 0 ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
                legs.add(new Cloth.Leg(rig.frame(thigh).at(), rig.frame(shin).at(), rig.frame(foot).at(),
                        this.legs[0], this.legs[1], this.legs[2]));
            }
            // the skirt carrying on from the robe's bottom edge as it is now, its outline, point for point
            final List<V3> waist = fitted.pieces().getFirst().rings().getFirst().stream()
                    .map(p -> fitted.at(p, rig)).toList();
            hung.add(Cloth.skirt("skirt", Paint.ROBE, waist, robe.hem() * s, robe.flare(), legs, robe.ease() + 1.5,
                    9));
        }
        outfit.piece(Wear.Sash.class).ifPresent(sash -> hung.addAll(Cloth.band("sash", Paint.KASAYA,
                laid(SASH, 0.6, rig), rig.place(Part.ABDOMEN, new V3(0, 10, 0)), sash.wide() * s, 0.7)));
        return hung;
    }

    /** How thick the right leg is, as the body has it at rest: at the top of the thigh, the knee, the ankle. */
    private static double[] legs(final Envelope outline) {
        final Rig rest = outline.rest();
        final V3 hip = rest.frame(Part.RIGHT_THIGH).at();
        final V3 knee = rest.frame(Part.RIGHT_SHIN).at();
        final V3 ankle = rest.frame(Part.RIGHT_FOOT).at();
        final V3 thigh = knee.minus(hip);
        final V3 shin = ankle.minus(knee);
        final Set<Part> leg = EnumSet.of(Part.RIGHT_THIGH, Part.RIGHT_SHIN);
        return new double[] {
            outline.thickness(hip.plus(thigh.times(0.15)), thigh, EnumSet.of(Part.RIGHT_THIGH)),
            outline.thickness(knee, shin, leg),
            outline.thickness(ankle.minus(shin.times(0.08)), shin, EnumSet.of(Part.RIGHT_SHIN))};
    }
}
