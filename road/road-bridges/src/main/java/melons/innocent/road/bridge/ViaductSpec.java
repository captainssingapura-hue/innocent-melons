package melons.innocent.road.bridge;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A viaduct, as asked for: an expressway carried on it, so many lanes each way, each so wide, a shoulder so wide along
 * each carriageway's outer edge; so long, its road so high over the ground under its middle; its spans about so long,
 * its piers standing as asked. In centimetres: two lanes each way to five, 3.5 m to 3.75 m wide, shoulders 1.8 m to
 * 3.5 m; 50 m to 1 km long, 6 m to 40 m high, spans 20 m to 60 m.
 *
 * @param lanes    how many lanes each way
 * @param lane     how wide each lane is
 * @param shoulder how wide each outer shoulder is
 * @param length   how long it is
 * @param height   how high its road is over the ground under its middle
 * @param span     how long its spans are, about
 * @param piers    how its piers stand
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record ViaductSpec<C extends Civilization, E extends Era<? super C>>(int lanes, double lane, double shoulder,
                                                                            double length, double height, double span,
                                                                            Piers piers)
        implements Spec<Bridges.Viaduct, C, E> {

    public ViaductSpec {
        if (lanes < 2 || lanes > 5 || !(lane >= 350 && lane <= 375) || !(shoulder >= 180 && shoulder <= 350)) {
            throw new IllegalArgumentException("two to five lanes each way, 3.5 m to 3.75 m wide, shoulders 1.8 m to"
                    + " 3.5 m: " + lanes + ", " + lane + ", " + shoulder);
        }
        if (!(length >= 5_000 && length <= 100_000) || !(height >= 600 && height <= 4_000)) {
            throw new IllegalArgumentException("50 m to 1 km long, 6 m to 40 m high: " + length + ", " + height);
        }
        if (!(span >= 2_000 && span <= 6_000) || piers == null) {
            throw new IllegalArgumentException("spans 20 m to 60 m, its piers of some kind: " + span + ", " + piers);
        }
    }
}
