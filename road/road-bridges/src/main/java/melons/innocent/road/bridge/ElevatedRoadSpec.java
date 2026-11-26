package melons.innocent.road.bridge;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * An elevated road, as asked for: a road one way along it, so many lanes, each so wide, a shoulder so wide either
 * side; up a straight ramp so steep at its start, its piers standing as asked; along a viaduct so long, so high over
 * the ground, on spans about so long; and down a spiral ramp at its far end, so far round its middle, so many turns,
 * held up as asked. Each ramp as its own specification would have it ({@link RampSpec}, {@link SpiralRampSpec}), the
 * spiral climbing the viaduct's height in its turns; the viaduct 50 m to 600 m long, long enough to leave a span clear
 * of the spiral's turns passing under its far end.
 *
 * @param lanes    how many lanes
 * @param lane     how wide each lane is
 * @param shoulder how wide each shoulder is
 * @param height   how high its road is along its viaduct
 * @param grade    how steeply its straight ramp climbs
 * @param length   how long its viaduct is
 * @param span     how long its spans are, about
 * @param piers    how its piers stand
 * @param radius   how far its spiral's line is from its middle
 * @param turns    how many times its spiral goes round
 * @param carried  how its spiral's turns are held up
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record ElevatedRoadSpec<C extends Civilization, E extends Era<? super C>>(int lanes, double lane,
                                                                                 double shoulder, double height,
                                                                                 double grade, double length,
                                                                                 double span, Piers piers,
                                                                                 double radius, double turns,
                                                                                 Carried carried)
        implements Spec<Bridges.ElevatedRoad, C, E> {

    public ElevatedRoadSpec {
        new RampSpec<>(lanes, lane, shoulder, height, grade, span, piers);
        new SpiralRampSpec<>(lanes, lane, shoulder, radius, height / turns, turns, carried);
        if (!(length >= 5_000 && length <= 60_000) || !(length >= radius + span + 1_000)) {
            throw new IllegalArgumentException("a viaduct 50 m to 600 m long, a span clear of its spiral: " + length
                    + ", " + radius + ", " + span);
        }
    }
}
