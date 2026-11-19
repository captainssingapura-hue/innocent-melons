package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Sites;
import com.example.fxsuite.avatar.build.Town;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.worlds.Meadow;

import java.util.ArrayList;
import java.util.List;

/**
 * The campus, the virtual headquarters: laid on the meadow bare of its village's houses
 * ({@link Meadow#pieces(boolean)}), its land, its river and bridge, its well, its trees, its church on its hill as they
 * are, its roads one road from the highway over the bridge, past the well, winding on up to the church; east of the
 * green, its door to the well, its main office ({@link MainOffice}), a hut of the greatest luxury; and in a ring round
 * that, its main shared departments ({@link Department}), each in a hut of luxury of its own.
 */
public final class Campus {

    /** The keys its buildings are drawn by: clear of every other world's. */
    public static final int KEYS = 2_000_000;

    /** The key its main office is drawn by; its departments' after it, in their order. */
    public static final int MAIN = KEYS + 1_000;

    /** The first key its labs' libraries are drawn by ({@link LabHut}), in their order. */
    public static final int LABS = MAIN + 100;

    /** Its base: the meadow bare of its village's houses. */
    static final boolean HOUSED = false;

    private Campus() {
    }

    /**
     * Its land: the meadow's, bare of its village, as its roads leave it; its quarters' plots laid on it, each in its
     * own cover ({@link Quarters}), the land's shape as it was. Worked out once, when first wanted.
     */
    public static Land land() {
        return Laid.LAND;
    }

    private static final class Laid {
        static final Land LAND = Meadow.land(HOUSED).with(Quarters.layers());
    }

    /** Department {@code number}'s key: after its main office's, in its order. */
    static int key(final Department department) {
        return MAIN + department.number();
    }

    /**
     * Its buildings, told to the drawing: its main office's key drawn as it is, each department's as its own is, each
     * lab's library's as its own is.
     */
    public static void register() {
        Sites.register(MAIN, MAIN + 1 + Department.ALL.size(),
                k -> k == 0 ? MainOffice.HUT.solids() : Department.ALL.get(k - 1).hut().solids());
        Sites.register(LABS, LABS + LabHut.ALL.size(), k -> LabHut.ALL.get(k).hut().solids());
    }

    /**
     * What stands on it: the meadow's pieces, bare of its village's houses; its main office; its departments; its labs'
     * libraries.
     */
    public static List<Course.Piece> pieces() {
        final List<Course.Piece> pieces = new ArrayList<>(Meadow.pieces(HOUSED));
        pieces.add(Town.building(MainOffice.HUT.solids(), MainOffice.place(), MAIN, MainOffice.HUT.shape(), 1));
        for (final Department d : Department.ALL) {
            pieces.add(Town.building(d.hut().solids(), d.place(), key(d), d.hut().shape(), 1));
        }
        for (int k = 0; k < LabHut.ALL.size(); k++) {
            final LabHut lab = LabHut.ALL.get(k);
            pieces.add(Town.building(lab.hut().solids(), lab.place(), LABS + k, lab.hut().shape(), 1));
        }
        return pieces;
    }
}
