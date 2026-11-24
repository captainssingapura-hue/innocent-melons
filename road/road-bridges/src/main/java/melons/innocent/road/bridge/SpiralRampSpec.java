package melons.innocent.road.bridge;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A spiral ramp, as asked for: a road one way round it, so many lanes, each so wide, a shoulder so wide either side;
 * its line so far round its middle, climbing from the ground so much each turn, so many turns; its turns held up as
 * asked. In centimetres: one lane or two, 3 m to 3.75 m wide, shoulders up to 1.5 m; 14 m to 50 m round, climbing 7 m
 * to 12 m a turn, never steeper than {@link #STEEPEST}; half a turn to four.
 *
 * @param lanes    how many lanes
 * @param lane     how wide each lane is
 * @param shoulder how wide each shoulder is
 * @param radius   how far its line is from its middle
 * @param rise     how much it climbs each turn
 * @param turns    how many times it goes round
 * @param carried  how its turns are held up
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record SpiralRampSpec<C extends Civilization, E extends Era<? super C>>(int lanes, double lane,
                                                                               double shoulder, double radius,
                                                                               double rise, double turns,
                                                                               Carried carried)
        implements Spec<Bridges.SpiralRamp, C, E> {

    /** How steeply it may climb, rise over run, along its line. */
    public static final double STEEPEST = 0.08;

    public SpiralRampSpec {
        if (lanes < 1 || lanes > 2 || !(lane >= 300 && lane <= 375) || !(shoulder >= 0 && shoulder <= 150)) {
            throw new IllegalArgumentException("one lane or two, 3 m to 3.75 m wide, shoulders up to 1.5 m: " + lanes
                    + ", " + lane + ", " + shoulder);
        }
        if (!(radius >= 1_400 && radius <= 5_000) || !(rise >= 700 && rise <= 1_200)
                || !(rise / (2 * Math.PI * radius) <= STEEPEST)) {
            throw new IllegalArgumentException("14 m to 50 m round, climbing 7 m to 12 m a turn, no steeper than 8%: "
                    + radius + ", " + rise);
        }
        if (!(turns >= 0.5 && turns <= 4) || carried == null) {
            throw new IllegalArgumentException("half a turn to four, held up some way: " + turns + ", " + carried);
        }
    }
}
