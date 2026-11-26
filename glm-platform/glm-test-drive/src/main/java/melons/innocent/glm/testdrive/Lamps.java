package melons.innocent.glm.testdrive;

import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.Optional;

/**
 * The car's lamps as it is driven, lit as a driver and the car itself would light them, each lit lamp glowing in its
 * own light: the brake lamps bright red while the brake is pressed with the car rolling ahead, or the throttle with
 * it rolling back; the reversing lamps white while it rolls back; the headlamps and the tail lamps, dim red, while F
 * has put them on, till F puts them off again, F coming as the engine tells it, in the step it would take off or land
 * in; and the indicators flashing amber, G stepping them from off to the
 * left, the right, both as hazard lights, and off again, at a flash and a half a second. Told each step what the
 * driver did and how fast the car goes; asked, of each part, the light it glows in, if it is a lamp that is lit.
 */
final class Lamps {

    /** The ways the indicators may be flashing. */
    enum Indicating {
        OFF, LEFT, RIGHT, HAZARD;

        /** The next, as G steps them. */
        Indicating next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    /** How fast the car must roll, in centimetres a second, to count as rolling one way or the other. */
    private static final double ROLLING = 5;
    /** How long one flash and the dark after it last, in seconds. */
    private static final double FLASH = 2.0 / 3;
    private static final Coat HEADLAMP = new Coat(Coat.Kind.LAMP, "#fff3d8");
    private static final Coat BRAKING = new Coat(Coat.Kind.LAMP, "#ff2216");
    private static final Coat TAIL = new Coat(Coat.Kind.LAMP, "#8a120c");
    private static final Coat REVERSING = new Coat(Coat.Kind.LAMP, "#fffaf2");
    private static final Coat INDICATING = new Coat(Coat.Kind.LAMP, "#ffa01c");

    private boolean lights;
    private Indicating indicating = Indicating.OFF;
    private boolean braking;
    private boolean reversing;
    private double clock;

    /**
     * Told a step: what the driver did, {@code told}; the pedal they asked for, {@code pedal}, the throttle more than
     * nought and the brake less; how fast the car rolls ahead, {@code ahead}, in centimetres a second; and how long
     * the step is, {@code seconds}.
     */
    void told(final Step told, final double pedal, final double ahead, final double seconds) {
        if (told.flies() || told.pressed().contains("F")) {
            lights = !lights;
        }
        if (told.pressed().contains("G")) {
            indicating = indicating.next();
        }
        braking = pedal < 0 && ahead > ROLLING || pedal > 0 && ahead < -ROLLING;
        reversing = ahead < -ROLLING;
        clock = indicating == Indicating.OFF ? 0 : clock + seconds;
    }

    /** The light {@code part} glows in, if it is a lamp that is lit; nothing, if it is lit by nothing but the day. */
    Optional<Coat> lit(final PartAt part) {
        return Optional.ofNullable(switch (part.part().name()) {
            case "headlamp" -> lights ? HEADLAMP : null;
            case "tail lamp" -> braking ? BRAKING : lights ? TAIL : null;
            case "reversing lamp" -> reversing ? REVERSING : null;
            case "indicator", "rear indicator" -> flashing(part.side()) ? INDICATING : null;
            default -> null;
        });
    }

    /** Whether the indicators on {@code side} are alight just now. */
    boolean flashing(final Side side) {
        final boolean asked = indicating == Indicating.HAZARD || indicating == Indicating.LEFT && side == Side.LEFT
                || indicating == Indicating.RIGHT && side == Side.RIGHT;
        return asked && clock % FLASH < FLASH / 2;
    }

    /** What it says over the picture: whether the lights are on and which way it is indicating. */
    String said() {
        return (lights ? "LIGHTS" : "") + (indicating == Indicating.OFF ? "" : (lights ? "  " : "")
                + (indicating == Indicating.HAZARD ? "HAZARD" : "INDICATE " + indicating.name()));
    }

    /** Whether the brake lamps are lit. */
    boolean braking() {
        return braking;
    }
}
