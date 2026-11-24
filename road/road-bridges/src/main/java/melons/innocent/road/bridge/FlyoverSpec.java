package melons.innocent.road.bridge;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A flyover, as asked for: a road of two lanes carried on it, each lane so wide, a shoulder so wide beside each; so
 * much room left under it over what it crosses, and what it crosses so wide. In centimetres: lanes 2.75 m to 3.75 m,
 * shoulders up to 3 m; 4.5 m to 7 m of room under it, over a way 10 m to 40 m wide.
 *
 * @param lane      how wide each lane is
 * @param shoulder  how wide each shoulder is
 * @param clearance how much room it leaves under itself
 * @param crossing  how wide what it crosses is
 * @param <C>       whose way it is built
 * @param <E>       when
 */
public record FlyoverSpec<C extends Civilization, E extends Era<? super C>>(double lane, double shoulder,
                                                                            double clearance, double crossing)
        implements Spec<Bridges.Flyover, C, E> {

    public FlyoverSpec {
        if (!(lane >= 275 && lane <= 375) || !(shoulder >= 0 && shoulder <= 300)) {
            throw new IllegalArgumentException("lanes 2.75 m to 3.75 m, shoulders up to 3 m: " + lane + ", "
                    + shoulder);
        }
        if (!(clearance >= 450 && clearance <= 700) || !(crossing >= 1_000 && crossing <= 4_000)) {
            throw new IllegalArgumentException("4.5 m to 7 m of room over a way 10 m to 40 m wide: " + clearance
                    + ", " + crossing);
        }
    }
}
