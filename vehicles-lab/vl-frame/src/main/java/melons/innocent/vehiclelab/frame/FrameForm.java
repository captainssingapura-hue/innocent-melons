package melons.innocent.vehiclelab.frame;

import com.example.fxsuite.vulkan.form.Ball;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Tube;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A frame's form, as it is made: each rod a round tube as thick as it is, and at each node a ball as thick as the
 * thickest tube meeting there, so where tubes meet at an angle the joint is closed as a weld closes it. In the card's
 * frame: across as x, up as y, along as z.
 *
 * @param frame the frame
 * @param round how many flat sides each tube is round in
 */
public record FrameForm(Frame frame, int round) {

    public FrameForm {
        if (frame == null || round < 3) {
            throw new IllegalArgumentException("a frame, its tubes round in three sides at least: " + round);
        }
    }

    /** Its tubes and its joints, as one mesh. */
    public Mesh mesh() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        for (final Frame.Rod rod : frame.rods()) {
            new Tube(at(frame.nodes().get(rod.from())), at(frame.nodes().get(rod.to())), frame.tube(rod).radius(),
                    round).formed(forming);
        }
        for (int n = 0; n < frame.nodes().size(); n++) {
            final double thickest = frame.rodsAt(n).stream()
                    .mapToDouble(r -> frame.tube(frame.rods().get(r)).radius()).max().orElse(0);
            if (thickest > 0) {
                new Ball(at(frame.nodes().get(n)), thickest, round).formed(forming);
            }
        }
        return forming.formed();
    }

    /** Where {@code node} is, in the card's frame. */
    public Vec3 at(final Frame.Node node) {
        return new Vec3(node.across(), node.up(), node.along());
    }
}
