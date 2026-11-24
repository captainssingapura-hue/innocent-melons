package melons.innocent.road.surface;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A street's surface, as asked for: so many lanes each way, each so wide, between its kerbs; a straight stretch of it
 * so long. In centimetres: one lane each way to three, 2.75 m to 3.5 m wide, the stretch 10 m to 1 km.
 *
 * @param lanes  how many lanes each way
 * @param lane   how wide each lane is
 * @param length how long a stretch of it
 * @param <C>    whose way it is built
 * @param <E>    when
 */
public record StreetSpec<C extends Civilization, E extends Era<? super C>>(int lanes, double lane, double length)
        implements Spec<Highways.Street, C, E> {

    public StreetSpec {
        if (lanes < 1 || lanes > 3 || !(lane >= 275 && lane <= 350)) {
            throw new IllegalArgumentException("one to three lanes each way, 2.75 m to 3.5 m wide: " + lanes + ", "
                    + lane);
        }
        if (!(length >= 1_000 && length <= 100_000)) {
            throw new IllegalArgumentException("a stretch 10 m to 1 km long, not " + length);
        }
    }
}
