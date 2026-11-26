package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.frame.Frame;
import melons.innocent.vehiclelab.frame.FrameForm;

import java.util.ArrayList;
import java.util.List;

/**
 * The boxer, as the avatar rides it, made as such a motorcycle is: its frame of tubes ({@link BoxerFrame}); its
 * engine round and low in it, a cylinder lying flat out to either side ({@link Engine}); its wheels, each a tyre round
 * a rim on spokes from a hub ({@link Wheels}); its tank, its seat, its fenders and its tail lamp ({@link Bodywork});
 * its forks, bars and headlamp, which the bars turn ({@link FrontEnd}); and its pipes and footrests ({@link Pipes}).
 * Its sizes the avatar's: 1.73 m between its axles, its wheels 64 cm round. In centimetres, in its own frame: across
 * as x, up from the ground as y, along as z, ahead its front; its middle between its wheels.
 */
public record Boxer() {

    /** How many flat sides a round part is round in. */
    static final int ROUND = 24;
    /** How far its wheels reach from their axles to the ground. */
    public static final double WHEEL = 32;
    /** Its axles, the front and the back. */
    public static final Vec3 FRONT_AXLE = new Vec3(0, WHEEL, 88);
    public static final Vec3 BACK_AXLE = new Vec3(0, WHEEL, -85);
    /** Its head tube's top and foot: the line its forks turn about. */
    public static final Vec3 HEAD_TOP = new Vec3(0, 86, 48);
    public static final Vec3 HEAD_FOOT = new Vec3(0, 70, 58);

    /** Its frame of tubes. */
    public Frame frame() {
        return new BoxerFrame().frame();
    }

    /** The way the line its forks turn about runs, down from the head tube's top. */
    public Vec3 steering() {
        return HEAD_FOOT.minus(HEAD_TOP).unit();
    }

    /** Every part of it. */
    public List<Part> parts() {
        final Shapes shapes = new Shapes(ROUND);
        final List<Part> parts = new ArrayList<>();
        parts.add(new Part("frame", Part.Slot.FRAME, Part.Moves.BODY, new FrameForm(frame(), ROUND / 2).mesh()));
        parts.addAll(new Engine(shapes).parts());
        parts.addAll(new Wheels(shapes).parts());
        parts.addAll(new Bodywork(shapes).parts());
        parts.addAll(new FrontEnd(shapes, steering()).parts());
        parts.addAll(new Pipes(shapes).parts());
        return parts;
    }
}
