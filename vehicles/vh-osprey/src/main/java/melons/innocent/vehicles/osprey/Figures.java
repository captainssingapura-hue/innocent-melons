package melons.innocent.vehicles.osprey;

/**
 * The Osprey's figures, in centimetres, seconds and degrees. Those marked published are the makers' and the US
 * Navy's (Boeing's V-22 page; the Navy's fact file, "V-22B Osprey tilt-rotor aircraft"), checked on 23 Nov 2026,
 * where sources mostly agree; where they disagree it says which is taken. The rest are the builder's, set to fit
 * them and to look right, from no drawing of the aircraft.
 */
public record Figures() {

    /** Published: its length, 57 ft 4 in. */
    private static final double LENGTH = 1748;
    /** Published: each rotor across, 38 ft 1 in. */
    private static final double ROTOR = 1161;
    /** Published: how wide it is with its rotors turning, 84 ft 7 in; so how far apart its rotors' middles are. */
    private static final double TURNING = 2578;
    /** Published: its wing alone, 45 ft 10 in. */
    private static final double WING = 1397;
    /** Published: how high it stands with its nacelles up, 22 ft 1 in. */
    private static final double HIGH = 673;
    /** Published: each engine's power, an AE 1107C of 6,150 shaft horsepower. */
    private static final double POWER = 6150;
    /**
     * Published, the Navy's: its top speed at the sea's level, 275 knots (one other source gives 351 mph, which is
     * not taken); and its cruise, about 240 knots. In centimetres a second.
     */
    private static final double TOP = 14_147;
    private static final double CRUISE = 12_347;
    /** The builder's, from memory, not checked: the nacelles turn from flying ahead to 97.5 degrees, past upright. */
    private static final double TILTED = 97.5;

    /** Its length, nose to tail. */
    public double length() {
        return LENGTH;
    }

    /** How far its rotor blades reach from their hub. */
    public double rotorReach() {
        return ROTOR / 2;
    }

    /** How far its rotors' middles are from its middle line, either side. */
    public double rotorsApart() {
        return (TURNING - ROTOR) / 2;
    }

    /** How far its wing reaches either side of its middle line. */
    public double wingReach() {
        return WING / 2;
    }

    /** How high it stands, nacelles up, from the ground under its wheels to the top of its spinners. */
    public double high() {
        return HIGH;
    }

    /** Each engine's power, in shaft horsepower. */
    public double power() {
        return POWER;
    }

    /** Its top speed, flying ahead. */
    public double top() {
        return TOP;
    }

    /** Its cruise, flying ahead. */
    public double cruise() {
        return CRUISE;
    }

    /** How far its nacelles turn, from flying ahead (nothing) to most. */
    public double tilted() {
        return TILTED;
    }
}
