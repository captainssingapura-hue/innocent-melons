package melons.innocent.building.container;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A container house, as asked for: its containers so long; its lower two raised so high over the ground, so far apart
 * end to end, a deck between them; its lower two finished in one finish, its upper one in another, and its frame,
 * pillars, stairs and railings in a third. In centimetres: raised 1 m to 4 m, 4.5 m to 7 m apart.
 *
 * @param size   how long its containers are
 * @param raised how high its lower containers' undersides are over the ground
 * @param gap    how far apart its lower containers' inner ends are
 * @param lower  what its lower containers are finished in
 * @param upper  what its upper container is finished in
 * @param frame  what its frame, pillars, stairs and railings are finished in
 * @param <C>    whose way it is built
 * @param <E>    when
 */
public record ContainerHouseSpec<C extends Civilization, E extends Era<? super C>>(Size size, double raised,
                                                                                   double gap, Finish lower,
                                                                                   Finish upper, Finish frame)
        implements Spec<Buildings.House, C, E> {

    public ContainerHouseSpec {
        if (size == null || lower == null || upper == null || frame == null) {
            throw new IllegalArgumentException("containers of some size, each part finished some way");
        }
        if (!(raised >= 100 && raised <= 400) || !(gap >= 450 && gap <= 700)) {
            throw new IllegalArgumentException("raised 1 m to 4 m, 4.5 m to 7 m apart: " + raised + ", " + gap);
        }
    }
}
