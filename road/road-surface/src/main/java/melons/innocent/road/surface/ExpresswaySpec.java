package melons.innocent.road.surface;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * An expressway's surface, as asked for: its two carriageways either side of a barrier down its middle, each so many
 * lanes, each so wide, and a shoulder so wide along its outer edge; a straight stretch of it so long. In centimetres:
 * two lanes each way to five, 3.5 m to 3.75 m wide, shoulders 1.8 m to 3.5 m, the stretch 10 m to 1 km.
 *
 * @param lanes    how many lanes each way
 * @param lane     how wide each lane is
 * @param shoulder how wide each outer shoulder is
 * @param length   how long a stretch of it
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record ExpresswaySpec<C extends Civilization, E extends Era<? super C>>(int lanes, double lane,
                                                                               double shoulder, double length)
        implements Spec<Highways.Expressway, C, E> {

    public ExpresswaySpec {
        if (lanes < 2 || lanes > 5 || !(lane >= 350 && lane <= 375)) {
            throw new IllegalArgumentException("two to five lanes each way, 3.5 m to 3.75 m wide: " + lanes + ", "
                    + lane);
        }
        if (!(shoulder >= 180 && shoulder <= 350)) {
            throw new IllegalArgumentException("shoulders 1.8 m to 3.5 m, not " + shoulder);
        }
        if (!(length >= 1_000 && length <= 100_000)) {
            throw new IllegalArgumentException("a stretch 10 m to 1 km long, not " + length);
        }
    }
}
