package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The rear subframe, after the current large SUV's: a ring of high-strength steel welded from its pieces and bushed to
 * the body at four eyes. Behind the axle, the back cross member, a hydroformed tube, its arms sweeping up and back to
 * the back mounts' sleeves; down each side from it, a side member, a hydroformed tube running down and forward and out
 * round its gooseneck to a front mount's sleeve, low; between the side members ahead, the pressed cross member, its
 * web punched for the differential's front mounts. The differential's back mount through the back cross member's
 * middle. The links' clevises, the upper rear link's and the toe link's on the back cross member, the others on the
 * side members. Each piece a part of its own, welded to the next; the bushes in its eyes to come, as joints.
 */
final class RearSubframe {

    private final RearPoints points;
    private final SubframeLines lines;
    private final Part back;
    private final Part side;
    private final Part front;
    private final Part backSleeve;
    private final Part frontSleeve;
    private final Part diffFront;
    private final Part diffBack;
    private final Map<String, Part> clevises = new LinkedHashMap<>();
    private final Map<String, Vec3> feet = new LinkedHashMap<>();

    /** It, at {@code points}. */
    RearSubframe(final RearPoints points) {
        this.points = points;
        this.lines = new SubframeLines(points);
        final SubframePieces pieces = new SubframePieces(lines);
        final Making making = new Making(new Made(Material.STEEL, false));
        clevis(pieces, "upper front link", lines.sideCourse(), points.upperFrontInner(), points.upperFrontOuter(),
                5.2, 2.6);
        clevis(pieces, "spring link", lines.sideCourse(), points.springInner(), points.springOuter(), 6.5, 3);
        clevis(pieces, "lower front link", lines.sideCourse(), points.lowerFrontInner(), points.lowerFrontOuter(),
                5.6, 2.6);
        clevis(pieces, "upper rear link", lines.backCourse(), points.upperRearInner(), points.upperRearOuter(),
                5.2, 2.6);
        clevis(pieces, "toe link", lines.backCourse(), points.toeInner(), points.toeOuter(), 4.6, 2.4);
        final Map<String, Vec3> onBack = new LinkedHashMap<>();
        onBack.put("differential mount", lines.diffBack());
        both(onBack, "back mount", points.mountBack());
        both(onBack, "side member", lines.sideStart());
        both(onBack, "upper rear link clevis", foot("upper rear link"));
        both(onBack, "toe link clevis", foot("toe link"));
        this.back = making.part("back cross member", Group.SUBFRAMES, false, onBack, pieces.backCrossMember());
        final Map<String, Vec3> onSide = new LinkedHashMap<>();
        onSide.put("back end", lines.sideStart());
        onSide.put("front mount", points.mountFront());
        onSide.put("front cross member", lines.webEnd());
        for (final String link : List.of("upper front link", "spring link", "lower front link")) {
            onSide.put(link + " clevis", foot(link));
        }
        this.side = making.part("side member", Group.SUBFRAMES, true, onSide, pieces.sideMember());
        final Map<String, Vec3> onFront = new LinkedHashMap<>();
        onFront.put("middle", lines.webMiddle());
        both(onFront, "side member", lines.webEnd());
        both(onFront, "differential mount", lines.diffFrontAt());
        this.front = making.part("pressed cross member", Group.SUBFRAMES, false, onFront, pieces.frontCrossMember());
        this.backSleeve = sleeve(making, "back mount sleeve", true, pieces.mountSleeve(points.mountBack()).mesh(),
                points.mountBack());
        this.frontSleeve = sleeve(making, "front mount sleeve", true, pieces.mountSleeve(points.mountFront()).mesh(),
                points.mountFront());
        this.diffFront = sleeve(making, "differential front mount sleeve", true, pieces.diffFront().mesh(),
                lines.diffFrontAt());
        this.diffBack = sleeve(making, "differential back mount sleeve", false, pieces.diffBack().mesh(),
                lines.diffBack());
    }

    /** The clevis {@code link} is bolted into. */
    Part clevis(final String link) {
        return clevises.get(link);
    }

    /** How its pieces are joined, to each other and to {@code body}, in the order they are walked. */
    List<Connection> joined(final Part body) {
        final List<Connection> joined = new ArrayList<>();
        joined.add(new Connection("subframe mount, back", body, "subframe mount, back, right", backSleeve, "centre",
                Joining.BUSHED));
        joined.add(new Connection("back mount sleeve", backSleeve, "centre", back, "back mount, right",
                Joining.WELDED));
        joined.add(new Connection("side member, back", back, "side member, right", side, "back end",
                Joining.WELDED));
        joined.add(new Connection("side member, front", side, "front cross member", front, "side member, right",
                Joining.WELDED));
        joined.add(new Connection("front mount sleeve", side, "front mount", frontSleeve, "centre", Joining.WELDED));
        joined.add(new Connection("subframe mount, front", body, "subframe mount, front, right", frontSleeve,
                "centre", Joining.BUSHED));
        joined.add(new Connection("differential front mount sleeve", front, "differential mount, right", diffFront,
                "centre", Joining.WELDED));
        joined.add(new Connection("differential back mount sleeve", back, "differential mount", diffBack, "centre",
                Joining.WELDED));
        for (final Map.Entry<String, Part> c : clevises.entrySet()) {
            final boolean behind = List.of("upper rear link", "toe link").contains(c.getKey());
            joined.add(new Connection(c.getKey() + " clevis", behind ? back : side, c.getKey() + " clevis"
                    + (behind ? ", right" : ""), c.getValue(), "foot", Joining.WELDED));
        }
        return joined;
    }

    private void clevis(final SubframePieces pieces, final String link, final Course course, final Vec3 inner,
                        final Vec3 outer, final double gap, final double tip) {
        final Clevis made = pieces.clevis(course, inner, outer, gap, tip);
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("foot", made.foot());
        named.put("pivot", inner);
        feet.put(link, made.foot());
        clevises.put(link, new Making(new Made(Material.STEEL, false)).part(link + " clevis", Group.SUBFRAMES, true,
                named, made.mesh()));
    }

    private Vec3 foot(final String link) {
        return feet.get(link);
    }

    private Part sleeve(final Making making, final String name, final boolean paired,
                        final Mesh form, final Vec3 at) {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("centre", at);
        return making.part(name, Group.SUBFRAMES, paired, named, form);
    }

    /** {@code name} on the right at {@code at}, and its mirror on the left. */
    private void both(final Map<String, Vec3> named, final String name, final Vec3 at) {
        named.put(name + ", right", at);
        named.put(name + ", left", new Vec3(-at.x(), at.y(), at.z()));
    }
}
