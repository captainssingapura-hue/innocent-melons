package melons.innocent.glm.engine;

/**
 * An engine's figures, the one modular design's, in centimetres: what it is called; how many banks of cylinders it
 * has, one in line or two in a V, and the angle between them, or two lying flat either side of the crank, opposed, a
 * boxer's, at 180 degrees; how many cylinders to a bank, in a V twin one; each one's bore and its piston's stroke;
 * how far apart the cylinders are along the crank, and how far one bank is set back from the other, so a V's
 * connecting rods sit side
 * by side on the crank's pins, or a boxer's each on its own pin between the other bank's; how long a connecting rod
 * is, centre to centre; and how far a piston's pin is under its crown. From these the block is cast: how high its
 * deck stands over the crank, how long it is, and where each cylinder is.
 *
 * @param name    what it is called
 * @param banks   how many banks: 1 in line, 2 in a V or a boxer
 * @param vee     the angle between the banks, in degrees; nought in line, 180 in a boxer
 * @param perBank how many cylinders to a bank
 * @param bore    a cylinder's bore
 * @param stroke  a piston's stroke
 * @param spacing how far apart the cylinders are along the crank
 * @param offset  how far the second bank is set back from the first
 * @param rod     a connecting rod's length, centre to centre
 * @param crown   how far a piston's pin is under its crown
 */
public record EngineFigures(String name, int banks, double vee, int perBank, double bore, double stroke,
                            double spacing, double offset, double rod, double crown) {

    /** How far past the end cylinders the block reaches, beyond half a spacing. */
    private static final double END = 2.2;

    public EngineFigures {
        if (banks == 1 && vee != 0 || banks == 2 && (vee < 45 || vee > 120) && vee != 180) {
            throw new IllegalArgumentException(name + ": in line, a V of 45 to 120 degrees, or a boxer, not " + vee);
        }
        final boolean twin = banks == 2 && vee != 180 && perBank == 1;
        if (banks < 1 || banks > 2 || !twin && perBank < (vee == 180 ? 2 : 3) || perBank > (vee == 180 ? 3 : 6)) {
            throw new IllegalArgumentException(name + ": one or two banks of three to six cylinders, a boxer's two or "
                    + "three, or a V twin's one");
        }
        if (bore <= 0 || stroke <= 0 || spacing <= bore + 0.3 || rod <= stroke || crown <= 0 || offset < 0) {
            throw new IllegalArgumentException(name + ": a bore and stroke that fit the spacing and the rod");
        }
    }

    /** Whether its banks lie flat, opposed either side of the crank, each piston on its own pin: a boxer. */
    public boolean boxer() {
        return banks == 2 && vee == 180;
    }

    /**
     * Whether it is a V twin, a cylinder to each bank, as a motorcycle's is: its crankcase cast apart from its
     * cylinders' barrels, bolted on it.
     */
    public boolean twin() {
        return banks == 2 && !boxer() && perBank == 1;
    }

    /** How many cylinders, in all. */
    public int cylinders() {
        return banks * perBank;
    }

    /** How much the cylinders sweep, in cubic centimetres. */
    public double displacement() {
        return cylinders() * Math.PI * bore * bore / 4 * stroke;
    }

    /** How high the deck stands over the crank's axis: half the stroke, the rod and the crown. */
    public double deck() {
        return stroke / 2 + rod + crown;
    }

    /** How far past each end cylinder's middle the block reaches. */
    public double margin() {
        return spacing / 2 + END;
    }

    /** How long the block is, along the crank. */
    public double length() {
        return 2 * margin() + (perBank - 1) * spacing + (banks - 1) * offset;
    }

    /** How far along the crank the {@code k}th cylinder of bank {@code bank} stands, from the block's front. */
    public double along(final int bank, final int k) {
        return margin() + k * spacing + bank * offset;
    }

    /**
     * How far bank {@code bank} leans from upright, toward +x, in degrees: in line, not at all; in a V, half the V each
     * way, the first bank to the right seen from behind, as its makers number them, where +x is to the left; in a
     * boxer, lying flat each way.
     */
    public double lean(final int bank) {
        return banks == 1 ? 0 : (bank == 0 ? -vee / 2 : vee / 2);
    }
}
