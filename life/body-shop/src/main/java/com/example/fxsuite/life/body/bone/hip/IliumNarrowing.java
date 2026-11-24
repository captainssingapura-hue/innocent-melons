package com.example.fxsuite.life.body.bone.hip;

import com.example.fxsuite.life.body.ThinPlate;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.Warp;

import java.util.ArrayList;
import java.util.List;

/**
 * The ilium's crest drawn in as far as men's is: the hip bone was drawn with its crests 30.1 cm apart at their widest,
 * where men's are 27.5 (Musielak and others, 31 men by 3D CT, PeerJ 2019, 7:e6433; and ANSUR II's bicristal breadth,
 * 4,082 soldiers, the same), 26.1 at the drawn figure's height; their anterior superior spines as far apart as men's
 * already, and the hip joints. So space is bent smoothly ({@link ThinPlate}) taking the crest's tubercle in 1.9 cm and
 * the crest behind it in 1.4, holding the spines, the acetabulum, the sacroiliac joint, the ischium and the pubis
 * where they are; what is drawn on the ilium, and what muscles hold on it, bent with it. In the pelvis's frame, as on
 * the right.
 */
public enum IliumNarrowing {
    INSTANCE;

    private final Warp warp;

    IliumNarrowing() {
        final List<V3> from = new ArrayList<>();
        final List<V3> to = new ArrayList<>();
        // held: the spines, the wing's foot, the notch, the hip joint, the sacroiliac joint, the ischium and pubis
        for (final V3 p : List.of(new V3(12.0, 7.0, 5.5), new V3(10.4, 3.4, 4.3), new V3(4.6, 9.5, -8.0),
                new V3(4.8, 6.5, -8.0), new V3(6.4, 2.6, -5.8), new V3(9.3, 2.6, 0.2), HipBone.JOINT,
                new V3(5.3, 6.2, -6.6), new V3(5.2, -2.8, -4.6), new V3(5.6, -8.4, -2.6), new V3(3.6, -8.4, 2.4),
                new V3(1.8, -4.6, 5.6))) {
            from.add(p);
            to.add(p);
        }
        // drawn in: the crest's tubercle, its widest, and the crest behind it
        from.add(new V3(14.2, 11.0, 1.2));
        to.add(new V3(12.33, 11.0, 1.2));
        from.add(new V3(12.6, 13.4, -4.0));
        to.add(new V3(11.2, 13.4, -4.0));
        warp = ThinPlate.INSTANCE.through(from, to);
    }

    /** Where {@code p}, drawn on the hip bone, is with its crest drawn in. */
    public V3 apply(final V3 p) {
        return warp.apply(p);
    }
}
