package melons.innocent.glm.engine;

import java.util.List;

/**
 * The range the one modular design is cast in, after the current large German group's engines: the inline four and
 * five after its 2.0 and 2.5 litre turbocharged engines, 82.5 by 92.8 mm, their cylinders 88 mm apart, their rods
 * 144 mm long; and the V6 after its 3.0 litre turbocharged V6, 84.5 by 89 mm in a 90 degree V, the V8 and V12 the same
 * cylinder with a bank grown to four and to six, the V12's V closed to 60 degrees to run evenly. The V engines'
 * spacing, bank offset, rods and crowns are estimates; and all are cast in aluminium, as not all of the originals are.
 * And the boxers, after the group's sports cars' flat engines, the four after their 2.0 litre and the six after their
 * 3.0 litre, which share one cylinder, 91 by 76.4 mm, 118 mm apart, as all their boxers are; one bank set back half
 * that from the other, so each rod has its own pin between two main bearings; their rods 127 mm long, as the
 * flat sixes' with that stroke have had, and their crowns estimates.
 */
public record EngineRange() {

    /** The inline four. */
    public EngineFigures l4() {
        return inline("L4", 4);
    }

    /** The flat four. */
    public EngineFigures b4() {
        return boxer("B4", 2);
    }

    /** The flat six. */
    public EngineFigures b6() {
        return boxer("B6", 3);
    }

    /** The inline five. */
    public EngineFigures l5() {
        return inline("L5", 5);
    }

    /** The V6. */
    public EngineFigures v6() {
        return vee("V6", 3, 90);
    }

    /** The V8. */
    public EngineFigures v8() {
        return vee("V8", 4, 90);
    }

    /** The V12. */
    public EngineFigures v12() {
        return vee("V12", 6, 60);
    }

    /** Every one of them, smallest first. */
    public List<EngineFigures> all() {
        return List.of(l4(), b4(), l5(), v6(), b6(), v8(), v12());
    }

    private EngineFigures boxer(final String name, final int perBank) {
        return new EngineFigures(name, 2, 180, perBank, 9.1, 7.64, 11.8, 5.9, 12.7, 3.0);
    }

    private EngineFigures inline(final String name, final int cylinders) {
        return new EngineFigures(name, 1, 0, cylinders, 8.25, 9.28, 8.8, 0, 14.4, 3.1);
    }

    private EngineFigures vee(final String name, final int perBank, final double angle) {
        return new EngineFigures(name, 2, angle, perBank, 8.45, 8.9, 9.3, 1.8, 15.4, 3.0);
    }
}
