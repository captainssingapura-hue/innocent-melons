package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Someone ({@link Person}) with what they wear on ({@link Wear}), each made to their body, inner first, each in its own
 * colours. What is fitted to the body's contour ({@link Garment}): robes and shirts, each over the one under it, looser
 * than it by a little at least; a robe's collar laid on it and its skirt hung from its waist, clear of the legs as thick
 * as the body has them. What is close on a part, moving with it: leggings, shoes, boots, a robe's sleeve below the
 * elbow. What is laid on whatever is fitted under it: sashes, belts. Their skin drawn where nothing covers it.
 */
public final class Dressed {

    /** Bodies' outlines and garments fitted to them already: working them out is slow. */
    private static final Map<String, Envelope> OUTLINES = new ConcurrentHashMap<>();
    private static final Map<String, Garment> FITTED = new ConcurrentHashMap<>();
    /** How loose what lies under a sash or a belt is taken to be, with nothing fitted: the skin, just off it. */
    private static final double NEXT_TO_SKIN = 0.4;
    /** How much looser at least a fitted garment is than the one under it, cm. */
    private static final double OVER = 0.6;

    /** A garment fitted to the body: what it is, how loose as worn, and as made. */
    private record Fit(Wear piece, double ease, Garment garment) {
    }

    private final Person person;
    private final List<Wear> wear;
    /** Each fitted garment, by where it is worn; the skin's, for what is laid on it with nothing fitted. */
    private final Map<Integer, Fit> fits = new java.util.HashMap<>();
    private final Garment skin;
    /** How thick the legs are, as the body has them: at the hip, the knee and the ankle, cm. */
    private final double[] legs;

    public Dressed(final Person person) {
        this.person = person;
        this.wear = person.outfit().wear();
        final Proportions body = person.body();
        final Envelope outline = OUTLINES.computeIfAbsent(body.said(), k -> new Envelope(body));
        double under = 0;
        for (int i = 0; i < wear.size(); i++) {
            final double ease = switch (wear.get(i)) {
                case Wear.Robe r -> r.ease();
                case Wear.Shirt s -> s.ease();
                case Wear.Undershirt u -> u.ease();
                default -> -1;
            };
            if (ease >= 0) {
                final double worn = Math.max(ease, under + (under > 0 ? OVER : 0));
                fits.put(i, new Fit(wear.get(i), worn, fitted(body, outline, worn)));
                under = worn;
            }
        }
        skin = fitted(body, outline, NEXT_TO_SKIN);
        legs = legs(outline);
    }

    private static Garment fitted(final Proportions body, final Envelope outline, final double ease) {
        return FITTED.computeIfAbsent(body.said() + " " + ease, k -> Garment.robe(outline, ease, Paint.ROBE));
    }

    public Person person() {
        return person;
    }

    /** The parts a robe covers, of the skin and what is close on it; a shirt's; leggings'. */
    private static final Set<Part> ROBED = EnumSet.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE,
            Part.LEFT_GIRDLE, Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM, Part.RIGHT_THIGH, Part.LEFT_THIGH);
    private static final Set<Part> SHIRTED = EnumSet.of(Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE, Part.LEFT_GIRDLE,
            Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM);
    private static final Set<Part> UNDERSHIRTED = EnumSet.of(Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE,
            Part.LEFT_GIRDLE);
    private static final Set<Part> LEGGED = EnumSet.of(Part.PELVIS, Part.RIGHT_THIGH, Part.LEFT_THIGH, Part.RIGHT_SHIN,
            Part.LEFT_SHIN);

    private static boolean forearm(final Part p) {
        return p == Part.RIGHT_FOREARM || p == Part.LEFT_FOREARM;
    }

    /** Whether something worn at {@code from} or over it hides {@code p}: a garment fitted over it, a sleeve on it. */
    private boolean hidden(final Part p, final int from) {
        for (int i = from; i < wear.size(); i++) {
            final boolean covers = switch (wear.get(i)) {
                case Wear.Robe r -> ROBED.contains(p) || forearm(p);
                case Wear.Shirt s -> SHIRTED.contains(p) || forearm(p) && s.longSleeves();
                case Wear.Undershirt u -> UNDERSHIRTED.contains(p);
                default -> false;
            };
            if (covers) {
                return true;
            }
        }
        return false;
    }

    /**
     * What is drawn of {@code p}, close on it, in its own frame as on the right side, layer by layer: its skin where
     * nothing covers it; leggings, shoes and boots, and a sleeve below the elbow, where nothing over them hides them.
     * Nothing where a garment fitted over it covers it ({@link #hanging}).
     */
    public List<Puppet.Layer> drawn(final Part p) {
        final List<Surface> bare = Skin.shapes(p, Muscles.Bulge.REST, Puppet.TWIST, Skin.HUMAN);
        final List<Puppet.Layer> layers = new ArrayList<>();
        final boolean underLeggings = wear.stream().anyMatch(w -> w instanceof Wear.Leggings) && LEGGED.contains(p);
        final boolean underShoes = wear.stream().anyMatch(w -> w instanceof Wear.Footwear) && foot(p);
        // the hips under a pair of underwear: under its cloth, not through it
        final boolean underPants = p == Part.PELVIS && wear.stream().anyMatch(w -> w instanceof Wear.Briefs
                || w instanceof Wear.BoxerBriefs || w instanceof Wear.Panties);
        if (!hidden(p, 0) && !underLeggings && !underShoes && !underPants) {
            layers.add(new Puppet.Layer(bare, Palette.DEFAULT.with(Paint.SKIN, person.skin())));
        }
        for (int i = 0; i < wear.size(); i++) {
            if (hidden(p, i + 1)) {
                continue;
            }
            final List<Surface> s = new ArrayList<>();
            final Wear w = wear.get(i);
            final Fit fit = fits.get(i);
            switch (w) {
                case Wear.Leggings l -> {
                    if (LEGGED.contains(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 0.8, "legging", Paint.LEGGINGS)));
                    }
                }
                case Wear.Shoes sh -> {
                    if (foot(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 0.6, "shoe", Paint.CLOTH_SHOES)));
                    }
                }
                case Wear.Boots b -> {
                    if (foot(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 1, "boot", Paint.LEATHER)));
                    } else if (p == Part.RIGHT_SHIN || p == Part.LEFT_SHIN) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 1.4, "boot", Paint.LEATHER, 1 - b.height(), 1)));
                    }
                }
                case Wear.Robe r -> {
                    if (forearm(p) && r.wideSleeves()) {
                        // the sleeve a wide bell from the elbow, its hem short of the wrist
                        s.addAll(Cloth.bell("sleeve", Paint.ROBE, Paint.TRIM, new V3(0, 3, 0), new V3(0, -22, 0.5),
                                5 + fit.ease(), 13 + fit.ease()));
                    } else if (forearm(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, fit.ease() * 0.6, "sleeve", Paint.ROBE)));
                    }
                }
                case Wear.Shirt sh -> {
                    if (forearm(p) && sh.longSleeves()) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, fit.ease() * 0.6, "sleeve", Paint.ROBE)));
                    }
                }



                default -> {
                }
            }
            if (!s.isEmpty()) {
                layers.add(new Puppet.Layer(s, colours(w)));
            }
        }
        return layers;
    }

    /** How close underwear is on the skin, cm. */
    private static final double UNDER = 0.4;

    /**
     * A pair of underwear as {@code rig} carries it: fitted round the hips and the tops of the thighs as one, from the
     * crotch up {@code rise} of the way to the top of the hips; down each thigh {@code down} of the way to the knee, if
     * any; its legs cut up at the sides by {@code cut}, cm; a waistband round its top.
     */
    private List<Surface> pants(final double rise, final double down, final double cut, final Rig rig) {
        final Proportions body = person.body();
        final Garment pants = FITTED.computeIfAbsent(body.said() + " pants " + rise + " " + down + " " + cut,
                k -> Garment.pants(OUTLINES.get(body.said()), UNDER, rise, down, cut, Paint.UNDERWEAR));
        final List<Surface> s = new ArrayList<>(pants.at(rig));
        final double[][] round = new double[25][];
        for (int k = 0; k < round.length; k++) {
            round[k] = new double[] {1, 90 - k * 15.0};
        }
        s.addAll(Cloth.band("waistband", Paint.TRIM, laid(pants, round, 0.2, rig), rig.place(Part.PELVIS, V3.ZERO),
                2.2 * body.scale(), 0.5));
        return s;
    }

    /**
     * A bra laid on {@code on}: a band round the ribs under the chest; a cup on each side of the chest, standing out as
     * deep as it is set; a strap from the top of each cup up over its shoulder and down the back to the band.
     */
    private static List<Surface> bra(final Garment on, final Wear.Bra bra, final Rig rig) {
        final List<Surface> s = new ArrayList<>();
        final double[][] band = new double[25][];
        for (int k = 0; k < band.length; k++) {
            band[k] = new double[] {0.5, 90 - k * 15.0};
        }
        final List<V3> round = new ArrayList<>();
        for (final double[] b : band) {
            round.add(on.at(on.onHeight(b[0], b[1], 0.3), rig));
        }
        final V3 middle = rig.place(Part.CHEST, V3.ZERO);
        s.addAll(Cloth.band("bra band", Paint.UNDERWEAR, round, middle, bra.band(), 0.5));
        for (final int side : new int[] {1, -1}) {
            // the cup, on the chest a little to its side, standing out from it
            final double front = side > 0 ? 55 : 125;
            final V3 at = on.at(on.onHeight(0.62, front, 0), rig);
            final V3 out = at.minus(new V3(middle.x(), at.y(), middle.z())).unit();
            s.add(Shapes.ellipsoid("cup", Paint.UNDERWEAR, at, 6, 5.5,
                    Math.max(1, bra.cups() * 0.9), 18, 10));
            // the strap, from the cup's top over the shoulder to the band behind
            final double[][] way = side > 0
                    ? new double[][] {{0.72, 62}, {0.88, 55}, {0.97, 40}, {0.9, -40}, {0.7, -55}, {0.52, -60}}
                    : new double[][] {{0.72, 118}, {0.88, 125}, {0.97, 140}, {0.9, -140}, {0.7, -125}, {0.52, -120}};
            final List<V3> strap = new ArrayList<>();
            for (final double[] w : way) {
                strap.add(on.at(on.onHeight(w[0], w[1], 0.35), rig));
            }
            s.addAll(Cloth.band("bra strap", Paint.UNDERWEAR, strap, middle, 1.4, 0.4));
        }
        return s;
    }

    private static boolean foot(final Part p) {
        return p == Part.RIGHT_FOOT || p == Part.LEFT_FOOT || p == Part.RIGHT_TOES || p == Part.LEFT_TOES;
    }

    /** {@code w}'s colours, each where its shapes are coloured. */
    private static Palette colours(final Wear w) {
        return switch (w) {
            case Wear.Robe r -> Palette.DEFAULT.with(Paint.ROBE, r.colour()).with(Paint.TRIM, r.trim());
            case Wear.Shirt s -> Palette.DEFAULT.with(Paint.ROBE, s.colour());
            case Wear.Undershirt u -> Palette.DEFAULT.with(Paint.UNDERWEAR, u.colour()).with(Paint.ROBE, u.colour());
            case Wear.Bra b -> Palette.DEFAULT.with(Paint.UNDERWEAR, b.colour());
            case Wear.Briefs b -> Palette.DEFAULT.with(Paint.UNDERWEAR, b.colour()).with(Paint.TRIM, b.waistband());
            case Wear.BoxerBriefs b -> Palette.DEFAULT.with(Paint.UNDERWEAR, b.colour()).with(Paint.TRIM, b.waistband());
            case Wear.Panties b -> Palette.DEFAULT.with(Paint.UNDERWEAR, b.colour()).with(Paint.TRIM, b.waistband());
            case Wear.Leggings l -> Palette.DEFAULT.with(Paint.LEGGINGS, l.colour());
            case Wear.Shoes s -> Palette.DEFAULT.with(Paint.CLOTH_SHOES, s.colour());
            case Wear.Boots b -> Palette.DEFAULT.with(Paint.LEATHER, b.colour());
            case Wear.Sash s -> Palette.DEFAULT.with(Paint.KASAYA, s.colour());
            case Wear.Belt b -> Palette.DEFAULT.with(Paint.LEATHER, b.colour());
        };
    }

    /**
     * A robe's crossed collar: round the back of the neck, its left lapel crossing over to the right under the right
     * arm, the right's edge short under it. Each point up from the waist to the neck, and round from the right.
     */
    private static final double[][] COLLAR = {{1, 75}, {1, 30}, {1, -30}, {1, -90}, {1, -150}, {1, 150}, {1, 110},
        {0.9, 95}, {0.8, 72}, {0.7, 48}, {0.62, 25}};
    private static final double[][] UNDER_COLLAR = {{1, 72}, {0.92, 84}};
    /** A sash's way: from over the left shoulder down across the chest to the right hip, round, and back up. */
    private static final double[][] SASH = {{0.94, 180}, {0.86, 140}, {0.66, 100}, {0.42, 60}, {0.2, 25},
        {0.06, 0}, {0.12, -40}, {0.4, -100}, {0.66, -140}, {0.86, -170}, {0.94, 180}};
    /** A belt's way: round the waist, a little up from the garment's bottom. */
    private static final double[][] BELT = belt();

    private static double[][] belt() {
        final double[][] way = new double[25][];
        for (int k = 0; k < way.length; k++) {
            way[k] = new double[] {0.07, 90 - k * 15.0};
        }
        return way;
    }

    /** The points {@code way} on {@code on}, {@code off} out from it, as {@code rig} carries them. */
    private static List<V3> laid(final Garment on, final double[][] way, final double off, final Rig rig) {
        final List<V3> out = new ArrayList<>();
        for (final double[] w : way) {
            out.add(on.at(on.on(w[0], w[1], off), rig));
        }
        return out;
    }

    /** The outermost garment fitted under what is worn at {@code at}; the skin's, if none. */
    private Garment under(final int at) {
        for (int i = at - 1; i >= 0; i--) {
            final Fit f = fits.get(i);
            if (f != null) {
                return f.garment();
            }
        }
        return skin;
    }

    /**
     * What is worked out afresh as {@code rig} stands, in the body's frame, layer by layer: each garment fitted to the
     * body's contour; a robe's collar laid on it and its skirt hung from its waist, clear of the legs; sashes and belts
     * laid on whatever is fitted under them.
     */
    public List<Puppet.Layer> hanging(final Rig rig) {
        final double s = person.body().scale();
        final List<Puppet.Layer> layers = new ArrayList<>();
        final V3 middle = rig.place(Part.CHEST, V3.ZERO);
        final V3 waistline = rig.place(Part.ABDOMEN, new V3(0, 0, 0));
        for (int i = 0; i < wear.size(); i++) {
            final Wear w = wear.get(i);
            final Fit fit = fits.get(i);
            final List<Surface> hung = new ArrayList<>();
            switch (w) {
                case Wear.Robe r -> {
                    final Garment g = fit.garment();
                    hung.addAll(g.at(rig));
                    hung.addAll(Cloth.band("collar", Paint.TRIM, laid(g, COLLAR, 0.3, rig), middle, 3.6 * s, 0.7));
                    hung.addAll(Cloth.band("under collar", Paint.TRIM, laid(g, UNDER_COLLAR, 0.15, rig), middle,
                            3 * s, 0.5));
                    // the skirt carrying on from the robe's bottom edge as it is now, its outline, point for point
                    final List<V3> waist = g.pieces().getFirst().rings().getFirst().stream().map(p -> g.at(p, rig))
                            .toList();
                    hung.add(Cloth.skirt("skirt", Paint.ROBE, waist, r.hem() * s, r.flare(), legs(rig),
                            fit.ease() + 1.5, 9));
                }
                case Wear.Shirt sh -> hung.addAll(fit.garment().at(rig));
                case Wear.Undershirt u -> hung.add(fit.garment().bodyAt(rig));
                case Wear.Bra bra -> hung.addAll(bra(under(i), bra, rig));
                case Wear.Briefs b -> hung.addAll(pants(b.rise(), 0, 6, rig));
                case Wear.BoxerBriefs b -> hung.addAll(pants(b.rise(), b.length(), 0, rig));
                case Wear.Panties b -> hung.addAll(pants(b.rise(), 0, 10, rig));
                case Wear.Sash sash -> hung.addAll(Cloth.band("sash", Paint.KASAYA, laid(under(i), SASH, 0.6, rig),
                        rig.place(Part.ABDOMEN, new V3(0, 10, 0)), sash.wide() * s, 0.7));
                case Wear.Belt b -> hung.addAll(Cloth.band("belt", Paint.LEATHER, laid(under(i), BELT, 0.4, rig),
                        waistline, b.wide() * s, 0.6));
                default -> {
                }
            }
            if (!hung.isEmpty()) {
                layers.add(new Puppet.Layer(hung, colours(w)));
            }
        }
        return layers;
    }

    /** The legs as a skirt sees them, as {@code rig} has them, as thick as the body has them. */
    private List<Cloth.Leg> legs(final Rig rig) {
        final List<Cloth.Leg> out = new ArrayList<>();
        for (final int side : new int[] {1, -1}) {
            final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part shin = side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part foot = side > 0 ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            out.add(new Cloth.Leg(rig.frame(thigh).at(), rig.frame(shin).at(), rig.frame(foot).at(), legs[0], legs[1],
                    legs[2]));
        }
        return out;
    }

    /** How thick the right leg is, as the body has it at rest: at the top of the thigh, the knee, the ankle. */
    private static double[] legs(final Envelope outline) {
        final Rig rest = outline.rest();
        final V3 hip = rest.frame(Part.RIGHT_THIGH).at();
        final V3 knee = rest.frame(Part.RIGHT_SHIN).at();
        final V3 ankle = rest.frame(Part.RIGHT_FOOT).at();
        final V3 thigh = knee.minus(hip);
        final V3 shin = ankle.minus(knee);
        return new double[] {
            outline.thickness(hip.plus(thigh.times(0.15)), thigh, EnumSet.of(Part.RIGHT_THIGH)),
            outline.thickness(knee, shin, EnumSet.of(Part.RIGHT_THIGH, Part.RIGHT_SHIN)),
            outline.thickness(ankle.minus(shin.times(0.08)), shin, EnumSet.of(Part.RIGHT_SHIN))};
    }
}
