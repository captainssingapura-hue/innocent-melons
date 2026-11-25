package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The front subframe, after the current large SUV's: aluminium, cast and pushed through a die, laid out as a U. A
 * main node each side, cast, about on the axle's line, bushed to the body behind it and carrying the lower links'
 * ears, the engine's mount and the steering gear; between the nodes, low and behind, the cross member, extruded; from
 * each node's top, the strut tube, bent forward and up to the front mount, cast, bolted to the side of the body's
 * front rail. Each piece a part of its own, welded to the next; the bushes to come, as joints. The strut tubes are
 * coated black, what they are made of not seen: aluminium, a guess.
 */
final class FrontSubframe {

    /** What its pieces are made of: cast or extruded aluminium, bare; the strut tubes coated. */
    private static final Making CAST = new Making(new Made(Material.ALUMINIUM, true));
    private static final Making COATED = new Making(new Made(Material.ALUMINIUM, false));

    private final Part node;
    private final Part cross;
    private final Part strut;
    private final Part mount;

    /** It, at {@code points}. */
    FrontSubframe(final FrontPoints points) {
        final FrontSubframePieces pieces = new FrontSubframePieces(points);
        final Map<String, Vec3> onNode = new LinkedHashMap<>();
        onNode.put("middle", points.node());
        onNode.put("back mount", points.mountBack());
        onNode.put("cross member", points.crossMember());
        onNode.put("strut tube", points.strutFoot());
        onNode.put("lower front link", points.lowerFrontInner());
        onNode.put("lower rear link", points.lowerRearInner());
        this.node = CAST.part("front node", Group.SUBFRAMES, true, onNode, pieces.node());
        final Map<String, Vec3> onCross = new LinkedHashMap<>();
        onCross.put("node, right", points.crossMember());
        onCross.put("node, left", new Vec3(-points.crossMember().x(), points.crossMember().y(),
                points.crossMember().z()));
        this.cross = CAST.part("front beam", Group.SUBFRAMES, false, onCross, pieces.crossMember());
        final Map<String, Vec3> onStrut = new LinkedHashMap<>();
        onStrut.put("foot", points.strutFoot());
        onStrut.put("head", points.strutHead());
        this.strut = COATED.part("front strut", Group.SUBFRAMES, true, onStrut, pieces.strutTube());
        final Map<String, Vec3> onMount = new LinkedHashMap<>();
        onMount.put("body", points.mountFront());
        onMount.put("strut tube", points.strutHead());
        this.mount = CAST.part("front hook", Group.SUBFRAMES, true, onMount, pieces.frontMount());
    }

    /** How its pieces are joined, to each other and to {@code body}, in the order they are walked. */
    List<Connection> joined(final Part body) {
        return List.of(
                new Connection("front subframe mount, back", body, "subframe mount, back, right", node, "back mount",
                        Joining.BUSHED),
                new Connection("front beam", node, "cross member", cross, "node, right", Joining.WELDED),
                new Connection("strut tube, foot", node, "strut tube", strut, "foot", Joining.WELDED),
                new Connection("strut tube, head", strut, "head", mount, "strut tube", Joining.WELDED),
                new Connection("front subframe mount, front", body, "subframe mount, front, right", mount, "body",
                        Joining.BOLTED));
    }
}
