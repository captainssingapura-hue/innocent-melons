package melons.innocent.road.bridge;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A straight ramp, as asked for: a road one way up it, so many lanes, each so wide, a shoulder so wide either side;
 * climbing from the ground so steeply up to so high; its spans about so long, its piers standing as asked. In
 * centimetres: one lane to three, 3 m to 3.75 m wide, shoulders up to 3 m; up to 4.5 m to 15 m, climbing 2% to 8%;
 * spans 15 m to 40 m.
 *
 * @param lanes    how many lanes
 * @param lane     how wide each lane is
 * @param shoulder how wide each shoulder is
 * @param height   how high its road is at its top, over the ground where it starts
 * @param grade    how steeply it climbs, rise over run
 * @param span     how long its spans are, about
 * @param piers    how its piers stand
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record RampSpec<C extends Civilization, E extends Era<? super C>>(int lanes, double lane, double shoulder,
                                                                         double height, double grade, double span,
                                                                         Piers piers)
        implements Spec<Bridges.Ramp, C, E> {

    public RampSpec {
        if (lanes < 1 || lanes > 3 || !(lane >= 300 && lane <= 375) || !(shoulder >= 0 && shoulder <= 300)) {
            throw new IllegalArgumentException("one lane to three, 3 m to 3.75 m wide, shoulders up to 3 m: " + lanes
                    + ", " + lane + ", " + shoulder);
        }
        if (!(height >= 450 && height <= 1_500) || !(grade >= 0.02 && grade <= 0.08)) {
            throw new IllegalArgumentException("up to 4.5 m to 15 m, climbing 2% to 8%: " + height + ", " + grade);
        }
        if (!(span >= 1_500 && span <= 4_000) || piers == null) {
            throw new IllegalArgumentException("spans 15 m to 40 m, its piers of some kind: " + span + ", " + piers);
        }
    }

    /** How long it is, seen from above. */
    public double length() {
        return height / grade;
    }
}
