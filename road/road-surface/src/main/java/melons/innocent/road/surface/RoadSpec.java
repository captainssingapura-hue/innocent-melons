package melons.innocent.road.surface;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A road's surface, as asked for: two lanes, one each way, so wide, a shoulder so wide beside each, none if nought; a
 * straight stretch of it so long. In centimetres: lanes 2.75 m to 3.75 m, shoulders up to 3 m, the stretch 10 m to
 * 1 km.
 *
 * @param lane     how wide each lane is
 * @param shoulder how wide each shoulder is
 * @param length   how long a stretch of it
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record RoadSpec<C extends Civilization, E extends Era<? super C>>(double lane, double shoulder, double length)
        implements Spec<Highways.Road, C, E> {

    public RoadSpec {
        if (!(lane >= 275 && lane <= 375) || !(shoulder >= 0 && shoulder <= 300)) {
            throw new IllegalArgumentException("lanes 2.75 m to 3.75 m, shoulders up to 3 m: " + lane + ", "
                    + shoulder);
        }
        if (!(length >= 1_000 && length <= 100_000)) {
            throw new IllegalArgumentException("a stretch 10 m to 1 km long, not " + length);
        }
    }
}
