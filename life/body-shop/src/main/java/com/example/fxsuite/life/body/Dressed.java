package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Someone ({@link Person}) with what they wear on ({@link Wear}), each made to their body, inner first, each in its own
 * colours.
 * <ul>
 *   <li>Fitted to the body's contour ({@link Garment}), each over the one under it, looser than it by a little at
 *       least: robes, shirts, undershirts, jackets, hoodies, jerseys; a robe's collar laid on it and its skirt hung from
 *       its waist, clear of the legs as thick as the body has them. And, round the hips and down the legs, underwear,
 *       trousers and shorts.</li>
 *   <li>Close on a part, moving with it: leggings, socks, shoes, boots, trainers; a sleeve below the elbow; hair, a cap,
 *       a helmet.</li>
 *   <li>Laid on whatever is fitted under it: sashes, belts, a bra, an apron, a badge on its lanyard.</li>
 * </ul>
 * Their skin drawn where nothing covers it.
 */
public final class Dressed {

    /** Bodies' outlines and garments fitted to them already: working them out is slow. */
    private static final Map<String, Envelope> OUTLINES = new ConcurrentHashMap<>();
    private static final Map<String, Garment> FITTED = new ConcurrentHashMap<>();
    /** How loose what lies under a sash or a belt is taken to be, with nothing fitted: the skin, just off it. */
    private static final double NEXT_TO_SKIN = 0.4;
    /** How much looser at least a fitted garment is than the one under it, cm. */
    private static final double OVER = 0.6;
    /** How close underwear is on the skin, cm. */
    private static final double UNDER = 0.4;

    /** A garment fitted to the body: what it is, how loose as worn, and as made. */
    private record Fit(Wear piece, double ease, Garment garment) {
    }

    private final Person person;
    private final List<Wear> wear;
    private final Envelope outline;
    /** Each fitted garment, by where it is worn; the skin's, for what is laid on it with nothing fitted. */
    private final Map<Integer, Fit> fits = new java.util.HashMap<>();
    private final Garment skin;
    /** How thick the legs are, as the body has them: at the hip, the knee and the ankle, cm. */
    private final double[] legs;

    public Dressed(final Person person) {
        this.person = person;
        this.wear = person.outfit().wear();
        final Proportions body = person.body();
        outline = OUTLINES.computeIfAbsent(body.said(), k -> new Envelope(body));
        double under = 0;
        for (int i = 0; i < wear.size(); i++) {
            final Wear w = wear.get(i);
            final double ease = switch (w) {
                case Wear.Robe r -> r.ease();
                case Wear.Shirt s -> s.ease();
                case Wear.Undershirt u -> u.ease();
                case Wear.Jacket j -> j.ease();
                case Wear.Hoodie h -> h.ease();
                case Wear.Jersey j -> j.ease();
                default -> -1;
            };
            // how low its body comes, from the hip joints: a robe's from its waist, a jacket's below the hips
            final double bottom = switch (w) {
                case Wear.Jacket j -> -j.hem();
                case Wear.Hoodie h -> -3;
                case Wear.Shirt s -> 3;
                case Wear.Jersey j -> 2;
                default -> 6;
            };
            if (ease >= 0) {
                final double worn = Math.max(ease, under + (under > 0 ? OVER : 0));
                fits.put(i, new Fit(w, worn, fitted(worn, bottom)));
                under = worn;
            }
        }
        skin = fitted(NEXT_TO_SKIN, 6);
        legs = legs(outline);
    }

    private Garment fitted(final double ease, final double bottom) {
        return FITTED.computeIfAbsent(person.body().said() + " top " + ease + " " + bottom,
                k -> Garment.robe(outline, ease, Paint.ROBE, bottom));
    }

    public Person person() {
        return person;
    }

    /** The parts a robe covers, of the skin and what is close on it; a shirt's and the like; an undershirt's; leggings'. */
    private static final Set<Part> ROBED = EnumSet.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE,
            Part.LEFT_GIRDLE, Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM, Part.RIGHT_THIGH, Part.LEFT_THIGH);
    private static final Set<Part> SHIRTED = EnumSet.of(Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE, Part.LEFT_GIRDLE,
            Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM);
    private static final Set<Part> UNDERSHIRTED = EnumSet.of(Part.ABDOMEN, Part.CHEST, Part.RIGHT_GIRDLE,
            Part.LEFT_GIRDLE);
    private static final Set<Part> LEGGED = EnumSet.of(Part.PELVIS, Part.RIGHT_THIGH, Part.LEFT_THIGH, Part.RIGHT_SHIN,
            Part.LEFT_SHIN);
    private static final Set<Part> TROUSERED = EnumSet.of(Part.PELVIS, Part.RIGHT_THIGH, Part.LEFT_THIGH);

    private static boolean forearm(final Part p) {
        return p == Part.RIGHT_FOREARM || p == Part.LEFT_FOREARM;
    }

    private static boolean shin(final Part p) {
        return p == Part.RIGHT_SHIN || p == Part.LEFT_SHIN;
    }

    private static boolean foot(final Part p) {
        return p == Part.RIGHT_FOOT || p == Part.LEFT_FOOT || p == Part.RIGHT_TOES || p == Part.LEFT_TOES;
    }

    /** Whether something worn at {@code from} or over it hides {@code p}: a garment fitted over it, a sleeve on it. */
    private boolean hidden(final Part p, final int from) {
        for (int i = from; i < wear.size(); i++) {
            final boolean covers = switch (wear.get(i)) {
                case Wear.Robe r -> ROBED.contains(p) || forearm(p);
                case Wear.Shirt s -> SHIRTED.contains(p) || forearm(p) && s.longSleeves();
                case Wear.Undershirt u -> UNDERSHIRTED.contains(p);
                case Wear.Jacket j -> SHIRTED.contains(p) || forearm(p);
                case Wear.Hoodie h -> SHIRTED.contains(p) || forearm(p);
                case Wear.Jersey j -> SHIRTED.contains(p);
                case Wear.Trousers t -> TROUSERED.contains(p);
                case Wear.Shorts s -> p == Part.PELVIS;
                case Wear.Briefs b -> p == Part.PELVIS;
                case Wear.BoxerBriefs b -> p == Part.PELVIS;
                case Wear.Panties b -> p == Part.PELVIS;
                case Wear.Shoes s -> foot(p);
                case Wear.Boots b -> foot(p);
                case Wear.Trainers t -> foot(p);
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
     * nothing covers it; leggings, socks and footwear, a sleeve below the elbow, hair and what is worn on the head,
     * where nothing over them hides them. Nothing where a garment fitted over it covers it ({@link #hanging}).
     */
    public List<Puppet.Layer> drawn(final Part p) {
        final List<Surface> bare = Skin.shapes(p, Muscles.Bulge.REST, Puppet.TWIST, Skin.HUMAN);
        final List<Puppet.Layer> layers = new ArrayList<>();
        final boolean underLeggings = wear.stream().anyMatch(w -> w instanceof Wear.Leggings) && LEGGED.contains(p);
        if (!hidden(p, 0) && !underLeggings) {
            // a shin under a sock or a boot: its skin only above it, so it does not show through
            double covered = 1;
            for (final Wear w : wear) {
                if (shin(p) && w instanceof Wear.Socks so) {
                    covered = Math.min(covered, 1 - so.height());
                } else if (shin(p) && w instanceof Wear.Boots b) {
                    covered = Math.min(covered, 1 - b.height());
                }
            }
            final double above = covered + 0.04;
            final List<Surface> shown = above >= 1 ? bare
                    : bare.stream().map(k -> Cloth.fitted(k, 0, k.name(), Paint.SKIN, 0, above)).toList();
            layers.add(new Puppet.Layer(shown, Palette.DEFAULT.with(Paint.SKIN, person.skin())));
        }
        for (int i = 0; i < wear.size(); i++) {
            if (hidden(p, i + 1)) {
                continue;
            }
            final Wear w = wear.get(i);
            final Fit fit = fits.get(i);
            final List<Surface> s = new ArrayList<>();
            switch (w) {
                case Wear.Leggings l -> {
                    if (LEGGED.contains(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 0.8, "legging", Paint.LEGGINGS)));
                    }
                }
                case Wear.Socks so -> {
                    if (shin(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 1.2, "sock", Paint.LEGGINGS, 1 - so.height(), 1)));
                    } else if (foot(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 0.5, "sock", Paint.LEGGINGS)));
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
                    } else if (shin(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 1.4, "boot", Paint.LEATHER, 1 - b.height(), 1)));
                    }
                }
                case Wear.Trainers t -> {
                    if (foot(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, 0.9, "trainer", Paint.CLOTH_SHOES)));
                        bare.forEach(k -> s.add(sole(k)));
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
                case Wear.Jacket j -> {
                    if (forearm(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, fit.ease() * 0.6, "sleeve", Paint.ROBE)));
                    }
                }
                case Wear.Hoodie h -> {
                    if (forearm(p)) {
                        bare.forEach(k -> s.add(Cloth.fitted(k, fit.ease() * 0.6, "sleeve", Paint.ROBE)));
                    }
                }
                case Wear.ShortHair hair -> {
                    if (p == Part.HEAD) {
                        s.add(Cloth.fitted(bare.getFirst(), hair.thick(), "hair", Paint.HAIR, HAIRLINE, 1));
                    }
                }
                case Wear.LongHair hair -> {
                    if (p == Part.HEAD) {
                        s.add(Cloth.fitted(bare.getFirst(), 1.2, "hair", Paint.HAIR, HAIRLINE, 1));
                        s.add(fall(bare.getFirst(), hair.length()));
                    }
                }
                case Wear.Bun hair -> {
                    if (p == Part.HEAD) {
                        s.add(Cloth.fitted(bare.getFirst(), 0.7, "hair", Paint.HAIR, HAIRLINE, 1));
                        s.add(bun(bare.getFirst()));
                    }
                }
                case Wear.Cap c -> {
                    if (p == Part.HEAD) {
                        s.add(Cloth.fitted(bare.getFirst(), 1.8, "cap", Paint.ROBE, 0.68, 1));
                        s.add(peak(bare.getFirst()));
                    }
                }
                case Wear.Helmet h -> {
                    if (p == Part.HEAD) {
                        s.add(helmet(bare.getFirst()));
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

    /** Where hair starts up the head, as a share of the way from under the chin to the crown. */
    private static final double HAIRLINE = 0.6;

    /** A ring of {@code head} at {@code share} of the way up it: its middle, and how far out it reaches to the front and back. */
    private static double[] ring(final Surface head, final double share) {
        final List<V3> ring = head.rings().get((int) Math.round(share * (head.rings().size() - 1)));
        double y = 0;
        double front = -Double.MAX_VALUE;
        double back = Double.MAX_VALUE;
        double wide = 0;
        for (final V3 v : ring) {
            y += v.y() / ring.size();
            front = Math.max(front, v.z());
            back = Math.min(back, v.z());
            wide = Math.max(wide, Math.abs(v.x()));
        }
        return new double[] {y, front, back, wide};
    }

    /** Long hair falling from the back of {@code head}, {@code length} below it, as broad as the head behind. */
    private static Surface fall(final Surface head, final double length) {
        final double[] at = ring(head, 0.62);
        final double[] low = ring(head, 0.2);
        return Shapes.capsule("long hair", Paint.HAIR, new V3(0, at[0], at[2] + 2.5),
                new V3(0, low[0] - length, low[2] - 1.5), new double[] {at[3] * 0.9, at[3] * 0.8, at[3] * 0.6}, 0.35,
                0.5, 20);
    }

    /** A bun at the back of {@code head}, high. */
    private static Surface bun(final Surface head) {
        final double[] at = ring(head, 0.8);
        return Shapes.ellipsoid("bun", Paint.HAIR, new V3(0, at[0], at[2] - 2.5), 4.5, 4, 3.5, 16, 10);
    }

    /** A cap's peak, out over the brow of {@code head}. */
    private static Surface peak(final Surface head) {
        final double[] at = ring(head, 0.7);
        return Shapes.ellipsoid("peak", Paint.ROBE, new V3(0, at[0], at[1] + 3.5), at[3] * 0.85, 0.5, 6.5, 20, 6);
    }

    /** How thick a trainer's sole, cm. */
    private static final double SOLE = 1.8;

    /**
     * A trainer's sole under {@code skin}, a foot's or its toes': the foot's own outline, a little wider, flattened to
     * a slab under it as thick as a sole.
     */
    private static Surface sole(final Surface skin) {
        final double low = skin.lowest();
        return Cloth.fitted(skin, 1.1, "sole", Paint.SOLE).moved(v -> new V3(v.x(), Math.min(v.y(), low + SOLE - 0.9),
                v.z()));
    }

    /** A helmet over {@code head}: a rounded shell down to the ears, out from the head by its padding. */
    private static Surface helmet(final Surface head) {
        final double[] at = ring(head, 0.62);
        final double crown = head.end().y();
        final double pad = 2.2;
        return Shapes.dome("helmet", Paint.HELMET, new V3(0, at[0], (at[1] + at[2]) / 2), at[3] + pad,
                crown - at[0] + pad, (at[1] - at[2]) / 2 + pad, -30, 28, 12);
    }

    /** {@code w}'s colours, each where its shapes are coloured. */
    private static Palette colours(final Wear w) {
        final Palette p = Palette.DEFAULT;
        return switch (w) {
            case Wear.Robe r -> p.with(Paint.ROBE, r.colour()).with(Paint.TRIM, r.trim());
            case Wear.Shirt s -> p.with(Paint.ROBE, s.colour());
            case Wear.Jacket j -> p.with(Paint.ROBE, j.colour()).with(Paint.TRIM, Outfit.darker(j.colour(), 0.55));
            case Wear.Hoodie h -> p.with(Paint.ROBE, h.colour()).with(Paint.TRIM, Outfit.darker(h.colour(), 0.75));
            case Wear.Jersey j -> p.with(Paint.ROBE, j.colour()).with(Paint.TRIM, j.trim());
            case Wear.Undershirt u -> p.with(Paint.UNDERWEAR, u.colour()).with(Paint.ROBE, u.colour());
            case Wear.Bra b -> p.with(Paint.UNDERWEAR, b.colour());
            case Wear.Briefs b -> p.with(Paint.UNDERWEAR, b.colour()).with(Paint.TRIM, b.waistband());
            case Wear.BoxerBriefs b -> p.with(Paint.UNDERWEAR, b.colour()).with(Paint.TRIM, b.waistband());
            case Wear.Panties b -> p.with(Paint.UNDERWEAR, b.colour()).with(Paint.TRIM, b.waistband());
            case Wear.Leggings l -> p.with(Paint.LEGGINGS, l.colour());
            case Wear.Trousers t -> p.with(Paint.UNDERWEAR, t.colour()).with(Paint.TRIM, Outfit.darker(t.colour(), 0.7));
            case Wear.Shorts s -> p.with(Paint.UNDERWEAR, s.colour()).with(Paint.TRIM, Outfit.darker(s.colour(), 0.7));
            case Wear.Socks s -> p.with(Paint.LEGGINGS, s.colour());
            case Wear.Shoes s -> p.with(Paint.CLOTH_SHOES, s.colour());
            case Wear.Boots b -> p.with(Paint.LEATHER, b.colour());
            case Wear.Trainers t -> p.with(Paint.CLOTH_SHOES, t.colour()).with(Paint.SOLE, t.sole());
            case Wear.Sash s -> p.with(Paint.KASAYA, s.colour());
            case Wear.Belt b -> p.with(Paint.LEATHER, b.colour());
            case Wear.Apron a -> p.with(Paint.ROBE, a.colour());
            case Wear.Cap c -> p.with(Paint.ROBE, c.colour());
            case Wear.Helmet h -> p.with(Paint.HELMET, h.colour());
            case Wear.Badge b -> p.with(Paint.CARD, b.card()).with(Paint.STRAP, b.cord());
            case Wear.ShortHair h -> p.with(Paint.HAIR, h.colour());
            case Wear.LongHair h -> p.with(Paint.HAIR, h.colour());
            case Wear.Bun h -> p.with(Paint.HAIR, h.colour());
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

    /** All the way round, {@code up} of the way from the bottom of a fitted garment to its neck. */
    private static double[][] round(final double up) {
        final double[][] way = new double[25][];
        for (int k = 0; k < way.length; k++) {
            way[k] = new double[] {up, 90 - k * 15.0};
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

    /** As {@link #laid}, but each point {@code up} of the way by height. */
    private static List<V3> laidHigh(final Garment on, final double[][] way, final double off, final Rig rig) {
        final List<V3> out = new ArrayList<>();
        for (final double[] w : way) {
            out.add(on.at(on.onHeight(w[0], w[1], off), rig));
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
     * body's contour, and what goes with it; what is worn round the hips and down the legs; what is laid on whatever is
     * fitted under it.
     */
    public List<Puppet.Layer> hanging(final Rig rig) {
        final double s = person.body().scale();
        final List<Puppet.Layer> layers = new ArrayList<>();
        final V3 middle = rig.place(Part.CHEST, V3.ZERO);
        final V3 waistline = rig.place(Part.ABDOMEN, V3.ZERO);
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
                case Wear.Jacket j -> {
                    final Garment g = fit.garment();
                    hung.addAll(g.at(rig));
                    // its fastening down the front, darker
                    hung.addAll(Cloth.band("fastening", Paint.TRIM, laid(g, new double[][] {{1, 90}, {0.5, 90},
                        {0, 90}}, 0.25, rig), middle, 1.2 * s, 0.4));
                }
                case Wear.Hoodie h -> {
                    final Garment g = fit.garment();
                    hung.addAll(g.at(rig));
                    // the hood down behind the neck; the pocket across the belly
                    final V3 hood = g.at(g.on(0.98, -90, 3.5), rig);
                    hung.add(Shapes.ellipsoid("hood", Paint.ROBE, hood, 12 * s, 6 * s, 5 * s, 18, 10));
                    hung.addAll(Cloth.band("pocket", Paint.TRIM, laidHigh(g, new double[][] {{0.33, 90}, {0.14, 90}},
                            0.5, rig), waistline, 22 * s, 0.8));
                }
                case Wear.Jersey j -> {
                    final Garment g = fit.garment();
                    hung.addAll(g.at(rig));
                    hung.addAll(Cloth.band("collar", Paint.TRIM, laid(g, round(1), 0.2, rig), middle, 2.4 * s, 0.4));
                }
                case Wear.Sash sash -> hung.addAll(Cloth.band("sash", Paint.KASAYA, laid(under(i), SASH, 0.6, rig),
                        rig.place(Part.ABDOMEN, new V3(0, 10, 0)), sash.wide() * s, 0.7));
                case Wear.Belt b -> hung.addAll(Cloth.band("belt", Paint.LEATHER, laid(under(i), round(0.07), 0.4, rig),
                        waistline, b.wide() * s, 0.6));
                case Wear.Bra bra -> hung.addAll(bra(under(i), bra, rig));
                case Wear.Briefs b -> hung.addAll(pants(UNDER, b.rise(), 0, 6, false, rig, Paint.UNDERWEAR));
                case Wear.BoxerBriefs b -> hung.addAll(pants(UNDER, b.rise(), b.length(), 0, false, rig,
                        Paint.UNDERWEAR));
                case Wear.Panties b -> hung.addAll(pants(UNDER, b.rise(), 0, 10, false, rig, Paint.UNDERWEAR));
                case Wear.Trousers t -> hung.addAll(pants(t.ease(), 0.85, 1 + t.length(), 0, true, rig,
                        Paint.UNDERWEAR));
                case Wear.Shorts sh -> hung.addAll(pants(sh.ease(), 0.85, sh.length(), 0, false, rig,
                        Paint.UNDERWEAR));
                case Wear.Apron a -> hung.addAll(apron(under(i), a, rig));
                case Wear.Badge b -> hung.addAll(badge(under(i), rig));
                default -> {
                }
            }
            if (!hung.isEmpty()) {
                layers.add(new Puppet.Layer(hung, colours(w)));
            }
        }
        return layers;
    }

    /**
     * A pair of pants as {@code rig} carries it, {@code ease} loose: fitted round the hips and the tops of the thighs as
     * one, from the crotch up {@code rise} of the way to the top of the hips; down the legs {@code down} (past one, on
     * down the shins), hanging straight below the knee if {@code straight}; its legs cut up at the sides by
     * {@code cut}, cm; a waistband round its top.
     */
    private List<Surface> pants(final double ease, final double rise, final double down, final double cut,
                                final boolean straight, final Rig rig, final Paint paint) {
        final Proportions body = person.body();
        final Garment pants = FITTED.computeIfAbsent(body.said() + " pants " + ease + " " + rise + " " + down + " "
                + cut + " " + straight, k -> Garment.pants(outline, ease, rise, down, cut, straight, paint));
        final List<Surface> s = new ArrayList<>(pants.at(rig));
        s.addAll(Cloth.band("waistband", Paint.TRIM, laid(pants, round(1), 0.2, rig), rig.place(Part.PELVIS, V3.ZERO),
                2.2 * body.scale(), 0.5));
        return s;
    }

    /**
     * A bra laid on {@code on}: a band round the ribs under the chest; a cup on each side of the chest, standing out as
     * deep as it is set; a strap from the top of each cup up over its shoulder and down the back to the band.
     */
    private static List<Surface> bra(final Garment on, final Wear.Bra bra, final Rig rig) {
        final List<Surface> s = new ArrayList<>();
        final V3 middle = rig.place(Part.CHEST, V3.ZERO);
        s.addAll(Cloth.band("bra band", Paint.UNDERWEAR, laidHigh(on, round(0.5), 0.3, rig), middle, bra.band(),
                0.5));
        for (final int side : new int[] {1, -1}) {
            // the cup, on the chest a little to its side
            final V3 at = on.at(on.onHeight(0.62, side > 0 ? 55 : 125, 0), rig);
            s.add(Shapes.ellipsoid("cup", Paint.UNDERWEAR, at, 6, 5.5, Math.max(1, bra.cups() * 0.9), 18, 10));
            // the strap, from the cup's top over the shoulder to the band behind
            final double[][] way = side > 0
                    ? new double[][] {{0.72, 62}, {0.88, 55}, {0.97, 40}, {0.9, -40}, {0.7, -55}, {0.52, -60}}
                    : new double[][] {{0.72, 118}, {0.88, 125}, {0.97, 140}, {0.9, -140}, {0.7, -125}, {0.52, -120}};
            s.addAll(Cloth.band("bra strap", Paint.UNDERWEAR, laidHigh(on, way, 0.35, rig), middle, 1.4, 0.4));
        }
        return s;
    }

    /**
     * An apron laid on {@code on}: tied round the waist; hanging from it before the legs, as long as it is; a bib up the
     * chest to below the neck, on a strap round the neck, if it has one.
     */
    private List<Surface> apron(final Garment on, final Wear.Apron apron, final Rig rig) {
        final double s = person.body().scale();
        final List<Surface> out = new ArrayList<>();
        final V3 waistline = rig.place(Part.ABDOMEN, V3.ZERO);
        out.addAll(Cloth.band("apron ties", Paint.ROBE, laid(on, round(0.05), 0.5, rig), waistline, 2 * s, 0.4));
        // the skirt of it: from the waist in front straight down, out a little from the legs
        final V3 top = on.at(on.on(0.05, 90, 1), rig);
        final V3 down = top.plus(new V3(0, -apron.length() * s, 4 * s));
        out.addAll(Cloth.band("apron", Paint.ROBE, List.of(top, top.plus(new V3(0, -6 * s, 1.5 * s)), down),
                top.minus(new V3(0, 0, 20)), 40 * s, 0.4));
        if (apron.bib()) {
            final List<V3> bib = laidHigh(on, new double[][] {{0.12, 90}, {0.55, 90}, {0.78, 90}}, 0.6, rig);
            out.addAll(Cloth.band("bib", Paint.ROBE, bib, rig.place(Part.CHEST, V3.ZERO), 24 * s, 0.4));
            out.addAll(Cloth.band("neck strap", Paint.ROBE, laidHigh(on, new double[][] {{0.78, 70}, {0.98, 40},
                {1, -90}, {0.98, 140}, {0.78, 110}}, 0.6, rig), rig.place(Part.CHEST, V3.ZERO), 2 * s, 0.4));
        }
        return out;
    }

    /** A badge laid on {@code on}: a card at the chest on a cord round the neck. */
    private List<Surface> badge(final Garment on, final Rig rig) {
        final double s = person.body().scale();
        final List<Surface> out = new ArrayList<>();
        final V3 middle = rig.place(Part.CHEST, V3.ZERO);
        out.addAll(Cloth.band("cord", Paint.STRAP, laidHigh(on, new double[][] {{0.62, 80}, {0.8, 60}, {1, 40},
            {1, -90}, {1, 140}, {0.8, 120}, {0.62, 100}}, 0.4, rig), middle, 1.2 * s, 0.3));
        final V3 card = on.at(on.onHeight(0.55, 90, 1), rig);
        out.add(Shapes.box("card", Paint.CARD, card, 7 * s, 10 * s, 0.4));
        return out;
    }

    /** The legs as a skirt sees them, as {@code rig} has them, as thick as the body has them. */
    private List<Cloth.Leg> legs(final Rig rig) {
        final List<Cloth.Leg> out = new ArrayList<>();
        for (final int side : new int[] {1, -1}) {
            final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part shinPart = side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part footPart = side > 0 ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            out.add(new Cloth.Leg(rig.frame(thigh).at(), rig.frame(shinPart).at(), rig.frame(footPart).at(), legs[0],
                    legs[1], legs[2]));
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
