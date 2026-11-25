package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.frame.Frame;
import melons.innocent.vehiclelab.frame.FrameForm;
import melons.innocent.vehiclelab.frame.Framing;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The rear subframe, a frame of steel tube carrying the five links each side: a side member each side, down from its
 * front mount on the body to a lower rail under the axle and up again to its back mount; towers up from it ahead of
 * the axle and behind, to the upper links' pivots; the lower links' and the toe link's pivots on brackets off the
 * rail; and tubes across at the rails and the back mounts. Its points, both sides: its mounts to the body, and each
 * link's pivot, named as the link is.
 *
 * @param points the rear axle's points
 */
record RearSubframe(RearPoints points) {

    /** The subframe's tube, and how many sides it is round in. */
    private static final Frame.Tube TUBE = new Frame.Tube(2.8, 0.3);
    private static final int ROUND = 12;

    /** Its frame of tubes, where it stands on the platform. */
    Frame frame() {
        return new Framing("the rear subframe", TUBE)
                .pair("body mount, front", points.mountFront()).pair("body mount, back", points.mountBack())
                .pair("rail, front", points.railFront()).pair("rail, back", points.railBack())
                .pair("upper front link", points.upperFrontInner()).pair("upper rear link", points.upperRearInner())
                .pair("spring link", points.springInner()).pair("lower front link", points.lowerFrontInner())
                .pair("toe link", points.toeInner())
                // the side members, down from the front mount, along the rail, and up to the back mount
                .rods("body mount, front", "rail, front").rods("rail, front", "rail, back")
                .rods("rail, back", "body mount, back")
                // the towers to the upper links' pivots
                .rods("upper front link", "body mount, front").rods("upper front link", "rail, front")
                .rods("upper rear link", "body mount, back").rods("upper rear link", "rail, back")
                // the lower links' and the toe link's brackets
                .rods("spring link", "rail, front").rods("spring link", "rail, back")
                .rods("lower front link", "rail, front").rods("toe link", "rail, back")
                .rods("toe link", "upper rear link")
                // tubes across
                .across("rail, front").across("rail, back").across("body mount, back").across("upper front link")
                .framed();
    }

    /** It. */
    Part part() {
        final Frame frame = frame();
        final Map<String, Vec3> named = new LinkedHashMap<>();
        frame.nodes().forEach(n -> named.put(n.name(), new Vec3(n.across(), n.up(), n.along())));
        return new Making().part("rear subframe", Group.SUBFRAMES, false, named, new FrameForm(frame, ROUND).mesh());
    }
}
