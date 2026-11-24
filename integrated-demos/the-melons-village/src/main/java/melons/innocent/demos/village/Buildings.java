package melons.innocent.demos.village;

import com.example.fxsuite.building.spec.HouseSpec;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.building.container.ContainerHouse;
import melons.innocent.building.container.ContainerHouseSpec;
import melons.innocent.building.container.Finish;
import melons.innocent.building.container.Size;
import melons.innocent.building.generic.GenericHouse;

import java.util.List;

/**
 * The village's buildings of the buildings' own, each built by its realizer as the benches build it, and set down
 * where its author has it. East of the lane, past its right-hand yards, each facing the lane: a house of forty-foot
 * containers, cream under gun metal; a smaller one of twenty-foot containers, timber under titanium on charcoal
 * steel; and a house of concrete, two storeys. West of the lane, past its last yards, facing it: a house of concrete
 * three storeys high. A building's front, toward along in its own frame, turned minus ninety degrees faces the
 * lane from its right, and turned ninety from its left.
 */
record Buildings() {

    /** Each of them, where it stands. */
    List<Built> all() {
        final ContainerHouse containers = new ContainerHouse();
        final GenericHouse house = new GenericHouse();
        return List.of(
                new Built(containers.build(new ContainerHouseSpec<>(Size.FORTY, 250, 500, Finish.CREAM,
                        Finish.GUN_METAL, Finish.GUN_METAL)), new Vec3(4200, 0, 2600), -90),
                new Built(containers.build(new ContainerHouseSpec<>(Size.TWENTY, 200, 450, Finish.TIMBER,
                        Finish.TITANIUM, Finish.CHARCOAL)), new Vec3(4200, 0, 6200), -90),
                new Built(house.build(new HouseSpec<>(900, 700, 2, 300, 3)), new Vec3(3600, 0, 9000), -90),
                new Built(house.build(new HouseSpec<>(1100, 800, 3, 300, 4)), new Vec3(-3200, 0, 9200), 90));
    }
}
