package melons.innocent.core.walk;

import melons.innocent.core.step.Simulated;

/**
 * A walker, as it is described: what the core holds of it that no tick changes. All in centimetres and centimetres
 * a second.
 *
 * @param name   what it is called
 * @param wide   how wide it is, across and along alike
 * @param tall   how tall it is, standing
 * @param walks  how fast it walks
 * @param runs   how fast it runs
 * @param jumps  how fast it leaves the ground when it jumps: the same wherever it is, so it rises higher where
 *               the world pulls less
 * @param steps  how high a ledge it steps up onto, and how far down it keeps its footing
 * @param climbs how steep a slope it climbs at most: rise over run
 */
public record Walker(String name, double wide, double tall, double walks, double runs, double jumps, double steps,
                     double climbs) implements Simulated {

    /**
     * A figure of a man's size, 1.88 m: walking and running as the old engine's avatar did, to begin with;
     * jumping as a person does, half a metre on the Earth; stepping up twenty centimetres; climbing to 45 degrees.
     */
    public Walker(final String name) {
        this(name, 64, 188, 320, 760, 300, 20, 1);
    }
}
