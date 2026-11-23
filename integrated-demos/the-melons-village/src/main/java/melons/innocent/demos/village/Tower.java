package melons.innocent.demos.village;

import com.example.fxsuite.places.yard.Walled;
import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.texture.stone.CoursedStone;
import com.example.fxsuite.texture.stone.Footed;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Surface;

/**
 * A tall brick tower with nothing inside it: four walls round a hollow core, open to the sky, standing with its
 * middle at {@code middle}, turned {@code turned} degrees, so many centimetres {@code high}. It is a square six
 * metres outside, its walls 60 cm thick, laid in common red brick on a base of dressed stone four metres high, and in
 * the stone a doorway to go in by, 1.4 m wide and 2.6 m high, with the wall carried over it. As a walled yard is, it
 * is the walls the drawing and the collision are both made from, so what is drawn is what is in the way. Its sizes
 * are their author's.
 */
public record Tower(Vec3 middle, double turned, double high) {

    /** How wide it is outside, each way, and how thick its walls are. */
    private static final double SIDE = 600;
    private static final double THICK = 60;
    /** Its doorway, how wide and how high, and how high its base of stone stands. */
    private static final double DOOR_WIDE = 140;
    private static final double DOOR_HIGH = 260;
    private static final double BASE = 400;

    public Tower {
        if (!(high > BASE)) {
            throw new IllegalArgumentException("a tower stands higher than its base of stone: " + high);
        }
    }

    /** Its four walls, the doorway in the front. */
    public Walled walls() {
        return new Walled(middle, turned, SIDE, SIDE, high, THICK, DOOR_WIDE, DOOR_HIGH);
    }

    /** What its walls are laid of: common red brick on dressed stone, as high as the base. */
    public Surface laid() {
        return new Footed(Brickwork.COMMON_RED.wall(), new CoursedStone().wall().surface(), BASE).surface();
    }

    /** How long its shadow is on level ground with the sun {@code high} degrees up, in centimetres. */
    public double shadow(final double sunHigh) {
        return high / Math.tan(Math.toRadians(sunHigh));
    }
}
