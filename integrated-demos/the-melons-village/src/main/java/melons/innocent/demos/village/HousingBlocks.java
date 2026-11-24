package melons.innocent.demos.village;

import com.example.fxsuite.building.spec.Finish;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.building.spec.Walling;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.building.generic.GenericPointBlock;
import melons.innocent.building.generic.GenericSlabBlock;
import melons.innocent.forms.Tint;

import java.util.List;
import java.util.Optional;

/**
 * The village's blocks of public housing, as the housing board built them, each built by its realizer as the
 * buildings bench builds it. East of the houses beyond the lane's right-hand yards, a slab block twelve storeys high
 * and 134 m long, its length along the lane and its corridors facing it; across the lane's far end, past its last
 * houses, another, ten storeys high, its corridors facing back down the lane to the village; and further east, past
 * the first, three point blocks in a row, sixteen, twenty and twenty-four storeys high, each facing the lane. Where
 * they stand and how high are their author's.
 */
record HousingBlocks() {

    /** How long and how wide a slab block is, its corridor, its first storey and the rest, as the bench has one. */
    private static final double LONG = 13_440;
    private static final double WIDE = 1_205;
    private static final double CORRIDOR = 180;
    private static final double GROUND = 360;
    private static final double STOREY = 280;
    /** How wide a point block's lobby is, each way. */
    private static final double LOBBY = 900;

    /** Each of them, where it stands. */
    List<Built> all() {
        final GenericSlabBlock slab = new GenericSlabBlock();
        final GenericPointBlock point = new GenericPointBlock();
        return List.of(
                new Built(slab.build(new SlabBlockSpec<>(LONG, WIDE, CORRIDOR, 12, GROUND, STOREY,
                        finish("#e8dcc0"))), new Vec3(9000, 0, 7500), -90),
                new Built(slab.build(new SlabBlockSpec<>(LONG, WIDE, CORRIDOR, 10, GROUND, STOREY,
                        finish("#cfdde6"))), new Vec3(0, 0, 16_500), 180),
                new Built(point.build(new PointBlockSpec<>(16, LOBBY)), new Vec3(15_000, 0, 1_500), -90),
                new Built(point.build(new PointBlockSpec<>(20, LOBBY)), new Vec3(15_000, 0, 6_500), -90),
                new Built(point.build(new PointBlockSpec<>(24, LOBBY)), new Vec3(15_000, 0, 11_500), -90));
    }

    /** A slab block's brick walls painted {@code paint}, its frames dark and its glass a grey blue, as the bench's. */
    private Finish finish(final String paint) {
        return new Finish(Walling.BRICK, Optional.of(new Tint(paint)), new Tint("#3c3f44"), new Tint("#6f8fa3"));
    }
}
