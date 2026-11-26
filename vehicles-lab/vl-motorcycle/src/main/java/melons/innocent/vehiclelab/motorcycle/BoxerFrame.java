package melons.innocent.vehiclelab.motorcycle;

import melons.innocent.vehiclelab.frame.Frame;
import melons.innocent.vehiclelab.frame.Framing;

/**
 * The boxer's frame, a double loop of tube as a big boxer cruiser's is, its tubes bent where it bends, each bend a
 * node: its steering head stood high, raked 32 degrees, so the forks trail as a cruiser's do; from it twin top tubes,
 * curving down and back under the tank to where the low seat begins, and there bending down into a loop behind the
 * gearbox, down to the swing arm's pivot, the seat on the bend and nothing under it but its own body; from the bend
 * a stay each side, slanting down and back to just ahead of the back axle, so its back looks rigid, as an old
 * frame's did; two down tubes from the head, ahead of the cylinders, curving down into a cradle under the engine,
 * low between the wheels, rising behind it to the pivot; tubes across at each pair; and the swing arm, back from the
 * pivot to the back axle either side of the back wheel, the right arm the thicker, the shaft's housing. The frame
 * 28 mm tube; the head tube and the swing arm heavier. In centimetres: across, along from between the wheels, up
 * from the ground.
 */
record BoxerFrame() {

    /** The frame's tube; the head tube; the pivot's tube across; the swing arm's left arm and its right. */
    private static final Frame.Tube TUBE = new Frame.Tube(1.4, 0.2);
    private static final Frame.Tube HEAD = new Frame.Tube(2.2, 0.3);
    private static final Frame.Tube PIVOT = new Frame.Tube(1.6, 0.4);
    private static final Frame.Tube ARM = new Frame.Tube(2.4, 0.3);
    private static final Frame.Tube SHAFT = new Frame.Tube(3.2, 0.35);

    /** It. */
    Frame frame() {
        return new Framing("the boxer's frame", TUBE)
                .node("head tube, top", 0, 48, 86).node("head tube, foot", 0, 58, 70)
                // the top tubes, curving down under the tank, and the loop behind the gearbox
                .pair("top tube, front", 7, 36, 82).pair("top tube, middle", 9, 8, 72).pair("seat bend", 10, -20, 63)
                .pair("loop", 11, -30, 52).pair("swing arm pivot", 12, -32, 36, Frame.Kind.HINGE)
                // the stays and the back axle
                .pair("stay foot", 13, -78, 38).pair("back axle", 13, -85, 32)
                // the down tubes and the cradle under the engine
                .pair("down tube", 10, 50, 40).pair("down tube, foot", 12, 46, 20).pair("cradle, front", 12, 38, 11)
                .pair("cradle, back", 12, -24, 11)
                // the head tube, for the forks
                .rod("head tube, top", "head tube, foot", HEAD)
                .rods("top tube, front", "head tube, top").rods("top tube, middle", "top tube, front")
                .rods("seat bend", "top tube, middle").rods("loop", "seat bend").rods("swing arm pivot", "loop")
                // the stays, from the seat's bend down to the back axle
                .rods("stay foot", "seat bend").rods("stay foot", "back axle")
                .rods("down tube", "head tube, foot").rods("down tube, foot", "down tube")
                .rods("cradle, front", "down tube, foot").rods("cradle, back", "cradle, front")
                .rods("swing arm pivot", "cradle, back")
                // tubes across, at each pair
                .across("top tube, middle").across("seat bend")
                .across("down tube, foot").across("cradle, front").across("cradle, back")
                .across("swing arm pivot", PIVOT)
                // the swing arm, either side of the back wheel: the right arm the shaft's housing
                .rod("swing arm pivot, left", "back axle, left", ARM)
                .rod("swing arm pivot, right", "back axle, right", SHAFT)
                .framed();
    }
}
