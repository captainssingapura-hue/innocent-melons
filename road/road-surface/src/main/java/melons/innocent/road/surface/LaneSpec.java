package melons.innocent.road.surface;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A country lane's surface, as asked for: one lane, shared both ways, so wide, nothing painted on it; a straight
 * stretch of it so long. In centimetres: 2.5 m to 4 m wide, the stretch 10 m to 1 km.
 *
 * @param wide   how wide it is
 * @param length how long a stretch of it
 * @param <C>    whose way it is built
 * @param <E>    when
 */
public record LaneSpec<C extends Civilization, E extends Era<? super C>>(double wide, double length)
        implements Spec<Highways.Lane, C, E> {

    public LaneSpec {
        if (!(wide >= 250 && wide <= 400)) {
            throw new IllegalArgumentException("a lane 2.5 m to 4 m wide, not " + wide);
        }
        if (!(length >= 1_000 && length <= 100_000)) {
            throw new IllegalArgumentException("a stretch 10 m to 1 km long, not " + length);
        }
    }
}
