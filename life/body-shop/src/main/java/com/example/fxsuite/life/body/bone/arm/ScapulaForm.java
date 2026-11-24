package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.bone.form.BoneForm;
import com.example.fxsuite.life.body.bone.form.BonePiece;
import com.example.fxsuite.life.body.bone.form.BonePiece.Knob;
import com.example.fxsuite.life.body.bone.form.BonePiece.Pit;
import com.example.fxsuite.life.body.bone.form.BonePiece.Ridge;
import com.example.fxsuite.life.body.bone.form.BonePiece.Sheet;
import com.example.fxsuite.life.body.bone.form.Placed;
import com.example.fxsuite.math.form.Course;
import com.example.fxsuite.math.form.Form;
import com.example.fxsuite.math.form.FormBuilder;
import com.example.fxsuite.math.graph.Point.Spatial;
import melons.innocent.math.curves.CurveImpl;
import melons.innocent.math.curves.Fold;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;

import java.util.ArrayList;
import java.util.List;

/**
 * The right shoulder blade as a form, in its own frame, centimetres: its origin the middle of the glenoid's face;
 * {@code x} out along the blade toward the glenoid, {@code y} up the blade, {@code z} out of the blade's back. Its
 * landmarks set to a man of the drawn figure's height (adult men's measures scaled by 0.95): 14.3 cm from superior to
 * inferior angle (Chaijaroonkhanarak 2019, Garzón-Alfaro 2024); 10.0 cm from the spine's root to the glenoid
 * (Garzón-Alfaro 2024); the spine 12.5 cm from its root to the acromion's lateral edge, standing off the blade 0.7 cm
 * a centimetre from its root, 1.8 cm four along and 4.4 at the spinoglenoid notch, its acromion 4.6 by 2.2 by 0.85 cm
 * (von Schroeder 2001); the glenoid 3.4 by 2.6 cm. Its borders, crest and coracoid are ridges, its fossae and the
 * spine sheets through faces, its tubercles knobs, and the glenoid hollowed round the humerus's head.
 */
public final class ScapulaForm {

    /** How far the humerus's head's middle is from the glenoid's face, cm: the head's radius. */
    private static final double HEAD = 2.5;
    /**
     * How it stands at rest, men standing (Matsumura 2020, upright CT): its blade turned 29 degrees from the body's
     * front, its glenoid turned up 10, its top tipped forward 10.
     */
    private static final double TURNED = 29;
    private static final double UPWARD = 10;
    private static final double TIPPED = 10;

    /** Out of the blade's back. */
    private final Spatial back = new Spatial(0, 0, 1);
    /** Up the blade. */
    private final Spatial up = new Spatial(0, 1, 0);
    /** Out toward the glenoid. */
    private final Spatial out = new Spatial(1, 0, 0);

    /** Its landmarks, edges and faces. */
    public Form form() {
        final FormBuilder b = new FormBuilder("scapula");
        b.landmark("glenoid", 0, 0, 0).landmark("neck", -1.4, 0.2, 0.3)
                .landmark("supraglenoid tubercle", -0.3, 1.9, 0).landmark("infraglenoid tubercle", -0.9, -2.3, 0.1)
                .landmark("coracoid base", -1.6, 2.5, -0.3).landmark("coracoid knee", -1.3, 4.0, -1.6)
                .landmark("coracoid tip", 0.9, 3.4, -4.3).landmark("suprascapular notch", -4.0, 3.2, 0)
                .landmark("superior angle", -9.3, 4.8, 0.3).landmark("root of the spine", -9.95, 0.5, 0.4)
                .landmark("inferior angle", -7.7, -9.4, 0.3).landmark("lateral border", -3.9, -5.4, 0.2)
                .landmark("spinoglenoid notch", -1.9, 1.3, 0.6).landmark("crest's root", -9.6, 0.8, 1.1)
                .landmark("crest", -6.0, 1.9, 2.2).landmark("acromion's base", -0.4, 3.6, 2.6)
                .landmark("acromial angle", 2.2, 3.4, 3.4).landmark("acromion's tip", 2.6, 3.9, -1.2)
                .landmark("clavicular facet", 1.0, 4.1, -0.9);
        // the blade's borders, the spine and the acromion round their faces; the glenoid and the coracoid
        b.path("superior angle", "suprascapular notch", "coracoid base", "spinoglenoid notch", "root of the spine",
                "superior angle");
        b.edge("root of the spine", "inferior angle", new Course.Bow(new Spatial(-0.3, 0, 0), 2))
                .edge("inferior angle", "lateral border", new Course.Bow(new Spatial(0.3, -0.2, 0), 2))
                .edge("lateral border", "infraglenoid tubercle", new Course.Bow(new Spatial(0.3, 0, 0), 2))
                .path("infraglenoid tubercle", "neck", "spinoglenoid notch")
                .path("spinoglenoid notch", "acromion's base", "crest", "crest's root", "root of the spine")
                .path("acromion's base", "acromial angle", "acromion's tip", "clavicular facet", "acromion's base")
                .path("neck", "glenoid", "supraglenoid tubercle", "coracoid base", "coracoid knee", "coracoid tip");
        b.face("supraspinous", List.of(List.of("superior angle", "suprascapular notch", "coracoid base"),
                List.of("coracoid base", "spinoglenoid notch"), List.of("spinoglenoid notch", "root of the spine"),
                List.of("root of the spine", "superior angle")));
        b.face("infraspinous", List.of(List.of("root of the spine", "inferior angle"),
                List.of("inferior angle", "lateral border", "infraglenoid tubercle"),
                List.of("infraglenoid tubercle", "neck", "spinoglenoid notch"),
                List.of("spinoglenoid notch", "root of the spine")));
        b.face("spine", List.of(List.of("root of the spine", "spinoglenoid notch"),
                List.of("spinoglenoid notch", "acromion's base"), List.of("acromion's base", "crest", "crest's root"),
                List.of("crest's root", "root of the spine")));
        b.face("acromion", List.of(List.of("acromion's base", "acromial angle"),
                List.of("acromial angle", "acromion's tip"), List.of("acromion's tip", "clavicular facet"),
                List.of("clavicular facet", "acromion's base")));
        return b.made();
    }

    /** It as a bone: its form, and what it says about it. */
    public BoneForm bone() {
        final List<BonePiece> p = new ArrayList<>();
        p.add(new Sheet("supraspinous fossa", "supraspinous", 0.35, 0, back, 8, 6, 0.3));
        p.add(new Sheet("infraspinous fossa", "infraspinous", 0.3, 0.6, back, 10, 10, 0.3));
        p.add(new Ridge("medial border", List.of("superior angle", "root of the spine", "inferior angle"),
                profile(0.55, 0.5, 0.65), profile(0.5, 0.45, 0.55), back, 0.3));
        p.add(new Ridge("lateral border", List.of("infraglenoid tubercle", "lateral border", "inferior angle"),
                profile(1.3, 1.0, 0.7), profile(1.0, 0.8, 0.6), back, 0.4));
        p.add(new Ridge("superior border", List.of("superior angle", "suprascapular notch", "coracoid base"),
                profile(0.35, 0.3, 0.6), profile(0.35, 0.3, 0.5), back, 0.25));
        p.add(new Sheet("spine", "spine", 0.45, 0, up, 8, 5, 0.35));
        p.add(new Ridge("spine's crest", List.of("crest's root", "crest", "acromion's base", "acromial angle"),
                profile(0.6, 1.0, 1.3), profile(0.5, 0.55, 0.7), up, 0.35));
        p.add(new Sheet("acromion", "acromion", 0.85, 0.25, up, 5, 5, 0.4));
        p.add(new Knob("acromial angle", "acromial angle", new Spatial(0, 0, 0), back, up, 0.55, 0.5, 0.55, 0.3));
        p.add(new Ridge("coracoid", List.of("coracoid base", "coracoid knee", "coracoid tip"),
                profile(1.5, 1.2, 0.95), profile(1.1, 0.9, 0.8), up, 0.45));
        p.add(new Knob("glenoid rim", "glenoid", new Spatial(-0.5, 0, 0), out, up, 0.85, 1.75, 1.35, 0.5));
        p.add(new Ridge("neck", List.of("supraglenoid tubercle", "glenoid", "neck", "infraglenoid tubercle"),
                profile(1.2, 2.2, 1.3), profile(1.0, 1.4, 1.0), back, 0.5));
        p.add(new Knob("supraglenoid tubercle", "supraglenoid tubercle", new Spatial(0, 0, 0), up, out,
                0.45, 0.4, 0.4, 0.3));
        p.add(new Knob("infraglenoid tubercle", "infraglenoid tubercle", new Spatial(0, 0, 0), up, out,
                0.6, 0.45, 0.45, 0.3));
        p.add(new Pit("glenoid cavity", "glenoid", new Spatial(HEAD, 0, 0), HEAD, 0.25));
        return new BoneForm(form(), p);
    }

    /**
     * Where it stands in the body at rest, on the right: its glenoid's face round the humerus's head at the shoulder
     * joint, facing out along the blade; the blade turned from the body's front, its glenoid turned up and its top
     * tipped forward as men's stand. The body's frame: {@code x} out to the right, {@code y} up, {@code z} ahead.
     */
    public Placed placed() {
        final double turned = Math.toRadians(TURNED);
        final double upward = Math.toRadians(UPWARD);
        final double tipped = Math.toRadians(TIPPED);
        // the blade in the body: out along it, ahead as it goes; its back square to that, behind
        final Spatial along = new Spatial(Math.cos(turned), 0, Math.sin(turned));
        final Spatial behind = new Spatial(Math.sin(turned), 0, -Math.cos(turned));
        final Spatial rising = new Spatial(0, 1, 0);
        // turned up about the blade's back: the glenoid up, the inferior angle out
        final Spatial out1 = along.times(Math.cos(upward)).plus(rising.times(Math.sin(upward)));
        final Spatial up1 = rising.times(Math.cos(upward)).minus(along.times(Math.sin(upward)));
        // tipped forward about the way out along it: the top ahead, the back looking up
        final Spatial up2 = up1.times(Math.cos(tipped)).minus(behind.times(Math.sin(tipped)));
        final Spatial back2 = behind.times(Math.cos(tipped)).plus(up1.times(Math.sin(tipped)));
        final Spatial joint = new Spatial(Scapula.JOINT.x(), Scapula.JOINT.y(), Scapula.JOINT.z());
        return new Placed(joint.minus(out1.times(HEAD)), out1, up2, back2);
    }

    /** A width or a depth: {@code start} at a ridge's first end, {@code middle} halfway, {@code end} at its last. */
    private CurveImpl<?> profile(final double start, final double middle, final double end) {
        return new Fold(new Knots(List.of(new Knot(0, start), new Knot(0.5, middle), new Knot(1, end))));
    }
}
