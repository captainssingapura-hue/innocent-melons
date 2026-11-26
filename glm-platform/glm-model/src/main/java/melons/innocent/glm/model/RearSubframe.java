package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The rear subframe, after the current large SUV's: a ring of high-strength steel welded from its pieces and bushed to
 * the body at four eyes, fitted as its workshop manual has it. Ahead of the axle, the tube cross member, hydroformed,
 * the differential's front mount through its middle and its arms sweeping up and forward to the front mounts' sleeves,
 * bushed to the body on hydraulic bushes; down each side from it, a side member, hydroformed, running down and back
 * and out round its gooseneck to a back mount's sleeve, low; between the side members behind, the pressed cross
 * member, its web punched for the differential's two rear mounts. The links' clevises each on the tube nearest its
 * pivot: the upper links' and the toe link's high, ahead, the lower links' under the side members. Each piece a part
 * of its own, welded to the next; the bushes in its eyes to come, as joints.
 */
final class RearSubframe {

    private static final Making STEEL = new Making(new Made(Material.STEEL, false));

    private final SubframeLines lines;
    private final Part tube;
    private final Part side;
    private final Part pressed;
    private final Part frontSleeve;
    private final Part backSleeve;
    private final Part diffFront;
    private final Part diffRear;
    private final Map<String, Part> clevises = new LinkedHashMap<>();
    private final Map<String, Vec3> feet = new LinkedHashMap<>();
    private final Map<String, Boolean> onTube = new LinkedHashMap<>();

    /** It, at {@code points}. */
    RearSubframe(final RearPoints points) {
        this.lines = new SubframeLines(points);
        final SubframePieces pieces = new SubframePieces(lines);
        clevis(pieces, "upper front link", points.upperFrontInner(), points.upperFrontOuter(), 5.2, 2.6);
        clevis(pieces, "upper rear link", points.upperRearInner(), points.upperRearOuter(), 5.2, 2.6);
        clevis(pieces, "toe link", points.toeInner(), points.toeOuter(), 4.6, 2.4);
        clevis(pieces, "lower front link", points.lowerFrontInner(), points.lowerFrontOuter(), 5.6, 2.6);
        clevis(pieces, "spring link", points.springInner(), points.springOuter(), 6.5, 3);
        final Map<String, Vec3> onTubeMember = new LinkedHashMap<>();
        onTubeMember.put("differential mount", lines.diffTube());
        both(onTubeMember, "front mount", points.mountFront());
        both(onTubeMember, "side member", lines.sideStart());
        final Map<String, Vec3> onSide = new LinkedHashMap<>();
        onSide.put("front end", lines.sideStart());
        onSide.put("back mount", points.mountBack());
        onSide.put("pressed cross member", lines.webEnd());
        for (final String link : clevises.keySet()) {
            if (onTube.get(link)) {
                both(onTubeMember, link + " clevis", feet.get(link));
            } else {
                onSide.put(link + " clevis", feet.get(link));
            }
        }
        this.tube = STEEL.part("tube cross member", Group.SUBFRAMES, false, onTubeMember, pieces.tubeCrossMember());
        this.side = STEEL.part("side member", Group.SUBFRAMES, true, onSide, pieces.sideMember());
        final Map<String, Vec3> onPressed = new LinkedHashMap<>();
        onPressed.put("middle", lines.webMiddle());
        both(onPressed, "side member", lines.webEnd());
        both(onPressed, "differential mount", lines.diffPlateAt());
        this.pressed = STEEL.part("pressed cross member", Group.SUBFRAMES, false, onPressed,
                pieces.pressedCrossMember());
        this.frontSleeve = sleeve("front mount sleeve", true, pieces.mountSleeve(points.mountFront()).mesh(),
                points.mountFront());
        this.backSleeve = sleeve("back mount sleeve", true, pieces.mountSleeve(points.mountBack()).mesh(),
                points.mountBack());
        this.diffFront = sleeve("differential front mount sleeve", false, pieces.diffTube().mesh(), lines.diffTube());
        this.diffRear = sleeve("differential rear mount sleeve", true, pieces.diffPlate().mesh(),
                lines.diffPlateAt());
    }

    /** The clevis {@code link} is bolted into. */
    Part clevis(final String link) {
        return clevises.get(link);
    }

    /** How its pieces are joined, to each other and to {@code body}, in the order they are walked. */
    List<Connection> joined(final Part body) {
        final List<Connection> joined = new ArrayList<>();
        joined.add(new Connection("subframe mount, front", body, "subframe mount, front, right", frontSleeve,
                "centre", Joining.BUSHED));
        joined.add(new Connection("front mount sleeve", frontSleeve, "centre", tube, "front mount, right",
                Joining.WELDED));
        joined.add(new Connection("side member, front", tube, "side member, right", side, "front end",
                Joining.WELDED));
        joined.add(new Connection("side member, back", side, "pressed cross member", pressed, "side member, right",
                Joining.WELDED));
        joined.add(new Connection("back mount sleeve", side, "back mount", backSleeve, "centre", Joining.WELDED));
        joined.add(new Connection("subframe mount, back", body, "subframe mount, back, right", backSleeve, "centre",
                Joining.BUSHED));
        joined.add(new Connection("differential front mount sleeve", tube, "differential mount", diffFront, "centre",
                Joining.WELDED));
        joined.add(new Connection("differential rear mount sleeve", pressed, "differential mount, right", diffRear,
                "centre", Joining.WELDED));
        for (final Map.Entry<String, Part> c : clevises.entrySet()) {
            final boolean high = onTube.get(c.getKey());
            joined.add(new Connection(c.getKey() + " clevis", high ? tube : side, c.getKey() + " clevis"
                    + (high ? ", right" : ""), c.getValue(), "foot", Joining.WELDED));
        }
        return joined;
    }

    /** The clevis for {@code link}, welded to whichever tube is nearer its pivot. */
    private void clevis(final SubframePieces pieces, final String link, final Vec3 inner, final Vec3 outer,
                        final double gap, final double tip) {
        final Course tubeLine = lines.tubeCourse();
        final Course sideLine = lines.sideCourse();
        final boolean high = tubeLine.nearest(inner).minus(inner).length()
                < sideLine.nearest(inner).minus(inner).length();
        final Clevis made = pieces.clevis(high ? tubeLine : sideLine, inner, outer, gap, tip);
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("foot", made.foot());
        named.put("pivot", inner);
        feet.put(link, made.foot());
        onTube.put(link, high);
        clevises.put(link, STEEL.part(link + " clevis", Group.SUBFRAMES, true, named, made.mesh()));
    }

    private Part sleeve(final String name, final boolean paired, final Mesh form, final Vec3 at) {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("centre", at);
        return STEEL.part(name, Group.SUBFRAMES, paired, named, form);
    }

    /** {@code name} on the right at {@code at}, and its mirror on the left. */
    private void both(final Map<String, Vec3> named, final String name, final Vec3 at) {
        named.put(name + ", right", at);
        named.put(name + ", left", new Vec3(-at.x(), at.y(), at.z()));
    }
}
