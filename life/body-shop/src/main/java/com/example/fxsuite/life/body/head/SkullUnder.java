package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Reshaped;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullLandmarks;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * The skull fitted under a clay head ({@link ClayHead}), the head its own and the skull to go in it: the head set on
 * the skull first, its eyes in the skull's orbits as men's are measured, each orbit's opening about its eye, the eye a
 * little behind its rim, {@link #BEHIND}; then each landmark the skin tells of where the bone lies under it: a line
 * from the skull's middle, {@link #MIDDLE}, or the mouth, {@link #MOUTH}, for the jaws', out through where men's
 * measures put the landmark, to the head's skin, its ears left off; the bone as far in from there as the soft over it
 * is thick in men ({@link #depth}); each orbit's opening about its eye. The measures shaping the rest
 * ({@link SkullLandmarks}).
 */
public record SkullUnder() implements StatelessFunctionalObject {

    public static final SkullUnder INSTANCE = new SkullUnder();

    /** The skull's middle, in the head's frame, cm; how far behind its orbit's rim an eye's middle lies, cm. */
    private static final V3 MIDDLE = new V3(0, 6.5, 0.5);
    /** Inside the mouth, where the jaws' landmarks are seen from, cm. */
    private static final V3 MOUTH = new V3(0, -1.5, 2.0);
    private static final double BEHIND = 0.2;
    /** How far out a line is followed for the skin, and in steps how long, cm. */
    private static final double REACH = 20;
    private static final double STEP = 0.02;

    /**
     * Where the head is set on the skull, moved from where its frame has it; how many times as big along each way it
     * is made, a man's size; where its skin asks the bone to lie.
     */
    public record Fit(V3 headAt, V3 size, Map<Landmark, V3> asked) {
    }

    /**
     * The skull under the clay head made from the reference head at {@code reference}, the head first made the size
     * of a man's ({@link HeadSize}).
     */
    public Fit fit(final Path reference) throws IOException {
        final ReferenceHead r = ReferenceReading.INSTANCE.of(reference);
        final Map<Landmark, V3> men = SkullLandmarks.INSTANCE.targets();
        final Field head = ClayHead.INSTANCE.field(r, EnumSet.of(Piece.NOSE, Piece.MOUTH, Piece.EYES));
        final Field.Shape skin = Field.Compound.of(head.elements());
        final V3 size = HeadSize.INSTANCE.of(skin, r.rightEye().middle());
        final V3 eye = r.rightEye().middle().times(size);
        final V3 headAt = at(eye);
        final Map<Landmark, V3> asked = under(new Reshaped(skin, V3.ZERO, V3.ZERO, Rot.NONE, size, 0), headAt, men);
        final V3 across = new V3(eye.x() - opening(men).x(), 0, 0);
        for (final Landmark l : orbit()) {
            asked.put(l, men.get(l).plus(across));
        }
        return new Fit(headAt, size, asked);
    }

    /**
     * Where a head is set on the skull, moved from where its frame has it, its right eye's middle at {@code eye}: its
     * eyes in the skull's orbits as men's are measured, each a little behind its orbit's rim, {@link #BEHIND}.
     */
    public V3 at(final V3 eye) {
        final V3 opening = opening(SkullLandmarks.INSTANCE.targets());
        return new V3(0, opening.y() - eye.y(), opening.z() - BEHIND - eye.z());
    }

    /** The middle of the right orbit's opening, where {@code men} has its rim. */
    private V3 opening(final Map<Landmark, V3> men) {
        V3 opening = V3.ZERO;
        for (final Landmark l : orbit()) {
            opening = opening.plus(men.get(l).times(1.0 / orbit().size()));
        }
        return opening;
    }

    /** The landmarks round an orbit's rim. */
    private List<Landmark> orbit() {
        return List.of(Landmark.DACRYON, Landmark.ECTOCONCHION, Landmark.ORBIT_TOP, Landmark.ORBIT_FOOT);
    }

    /**
     * Where the skin of {@code head}, set at {@code at}, asks each landmark it tells of to lie: on the line from the
     * skull's middle out through where {@code men} has it, as far in from the skin as the soft over it is thick.
     */
    public Map<Landmark, V3> under(final Field.Shape head, final V3 at, final Map<Landmark, V3> men) {
        final Map<Landmark, V3> asked = new EnumMap<>(Landmark.class);
        for (final Landmark l : Landmark.values()) {
            final OptionalDouble d = depth(l);
            if (d.isPresent()) {
                final V3 from = jaw(l) ? MOUTH : MIDDLE;
                final V3 way = men.get(l).minus(from).unit();
                skin(head, at, from, way).ifPresent(s -> asked.put(l, s.minus(way.times(d.getAsDouble()))));
            }
        }
        return asked;
    }

    /** Whether {@code l} is on the jaws, its line from the mouth, the skull's middle too high to see it from. */
    private boolean jaw(final Landmark l) {
        return l == Landmark.PROSTHION || l == Landmark.INFRADENTALE || l == Landmark.GNATHION
                || l == Landmark.GONION;
    }

    /**
     * Where a line from {@code from} out along {@code way} first goes from in {@code head}, set at {@code at}, to out
     * of it: its skin there, not the neck's cut further on, the line under the chin coming back into it; nor where it
     * starts, the head's deep inside perhaps taken for out of it. None, if it never does: out through the neck's cut.
     */
    private Optional<V3> skin(final Field.Shape head, final V3 at, final V3 from, final V3 way) {
        boolean was = false;
        for (double t = 0; t < REACH; t += STEP) {
            final V3 p = from.plus(way.times(t)).minus(at);
            final boolean in = head.at(p.x(), p.y(), p.z()) < 0;
            if (was && !in) {
                return Optional.of(from.plus(way.times(t)));
            }
            was = in;
        }
        return Optional.empty();
    }

    /**
     * How thick the soft over landmark {@code l} is, cm, as Hona and Stephan found it over 140 years' studies, men's
     * and women's together, a man's less than a millimetre more (Int J Legal Med 2024, 138:519, table 3): glabella,
     * nasion, the vertex over bregma, opisthocranion, the lips over prosthion and infradentale, gnathion, zygion,
     * gonion; none where they did not measure, the vault's sides and back, or where the skin does not lie over it,
     * the orbits and the nose.
     */
    public OptionalDouble depth(final Landmark l) {
        return switch (l) {
            case GLABELLA -> OptionalDouble.of(0.55);
            case NASION -> OptionalDouble.of(0.60);
            case BREGMA -> OptionalDouble.of(0.50);
            case OPISTHOCRANION -> OptionalDouble.of(0.65);
            case PROSTHION -> OptionalDouble.of(1.20);
            case INFRADENTALE -> OptionalDouble.of(1.35);
            case GNATHION -> OptionalDouble.of(0.80);
            case ZYGION -> OptionalDouble.of(0.75);
            case GONION -> OptionalDouble.of(1.30);
            default -> OptionalDouble.empty();
        };
    }
}
