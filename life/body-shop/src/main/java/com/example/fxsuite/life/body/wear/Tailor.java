package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Set;
import java.util.function.Predicate;

/**
 * What is worn made to fit a body as it is held to be bound, over what is beneath it ({@link Beneath}): the marks the
 * knit is made to ({@link Legwear.Marks}), found on the body, the crotch under the lowest of it between the legs, the
 * turn at the ankle across its front, clear of it, the toes their furthest; and each garment cut from the knit
 * ({@link Sheet#cut}). Tights from the waist, a little lower in front, to the toes; briefs low on the hips, each leg's
 * opening slanting up from the crotch to the side of the hip, lower behind than before, the crotch between.
 */
public record Tailor() implements StatelessFunctionalObject {

    public static final Tailor INSTANCE = new Tailor();

    /** How far out from what is beneath each is made: as thick as its knit. */
    private static final double TIGHTS = 0.35;
    private static final double BRIEFS = 0.5;

    /** Where the knit is made to on the body {@code bind} holds over {@code b}. */
    public Legwear.Marks marks(final Beneath b, final Rig bind) {
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        final V3 knee = bind.frame(Part.RIGHT_SHIN).at();
        final V3 ankle = bind.frame(Part.RIGHT_FOOT).at();
        final double crotch = lowest(b, Set.of(Part.PELVIS, Part.RIGHT_THIGH, Part.LEFT_THIGH),
                p -> Math.abs(p.x()) < 2.5 && p.y() > knee.y()) - 0.3;
        final double front = furthest(b, Set.of(Part.RIGHT_SHIN),
                p -> p.x() > 0 && p.y() > ankle.y() - 2 && p.y() < ankle.y() + 8, V3.Z) + 1.5;
        final double instep = Math.max(ankle.y() + 3, furthest(b, Set.of(Part.RIGHT_FOOT, Part.RIGHT_TOES),
                p -> p.x() > 0 && p.z() > front, V3.Y) + 1.5);
        final double toes = furthest(b, Set.of(Part.RIGHT_FOOT, Part.RIGHT_TOES), p -> p.x() > 0, V3.Z);
        return new Legwear.Marks(crotch, hip.y() + 16, new V3(0, instep, front), toes);
    }

    /** Tights over {@code b}, where {@code bind} holds the body: from the waist to the toes. */
    public Garment tights(final Beneath b, final Rig bind, final String colour) {
        final Legwear.Marks m = marks(b, bind);
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        final Sheet knit = Legwear.INSTANCE.sheet(b, bind, m, TIGHTS);
        return new Garment("Tights", colour, knit.cut(new V3(0, hip.y() + 12, 0), new V3(0, -1, -0.12).unit())
                .flesh(bind));
    }

    /** Briefs over {@code b}, where {@code bind} holds the body: low on the hips, cut away up the sides of the legs. */
    public Garment briefs(final Beneath b, final Rig bind, final String colour) {
        final Legwear.Marks m = marks(b, bind);
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        Sheet s = Legwear.INSTANCE.sheet(b, bind, m, BRIEFS)
                .cut(new V3(0, hip.y() + 6, 0), new V3(0, -1, -0.1).unit());
        for (final int side : new int[] {1, -1}) {
            final V3 top = new V3(side * (hip.x() + 9), hip.y() + 1, 0);
            final V3 crotch = new V3(side * 2.5, m.crotch() - 1.5, 0);
            // the opening's plane: through the side of the hip and the crotch, lower behind than before
            final V3 n = crotch.minus(top).cross(new V3(0, 0.25, 1)).unit();
            s = s.cut(top, side > 0 ? n : n.times(-1));
        }
        return new Garment("Briefs", colour, s.flesh(bind));
    }

    /** How low {@code b} goes where {@code where} says, of the points going with {@code parts}. */
    private double lowest(final Beneath b, final Set<Part> parts, final Predicate<V3> where) {
        return -furthest(b, parts, where, V3.Y.times(-1));
    }

    /** How far {@code b} goes along {@code way}, where {@code where} says, of the points going with {@code parts}. */
    private double furthest(final Beneath b, final Set<Part> parts, final Predicate<V3> where, final V3 way) {
        double best = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < b.size(); i++) {
            final V3 p = b.point(i);
            if (parts.contains(b.main()[i]) && where.test(p)) {
                best = Math.max(best, p.dot(way));
            }
        }
        return best;
    }
}
