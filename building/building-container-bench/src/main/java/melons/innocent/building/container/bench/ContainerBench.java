package melons.innocent.building.container.bench;

import javafx.application.Platform;
import melons.innocent.building.bench.BuildingBench;
import melons.innocent.building.bench.Catalogue;
import melons.innocent.building.bench.Inspector;
import melons.innocent.building.bench.Offered;
import melons.innocent.building.container.ContainerHouse;
import melons.innocent.building.container.ContainerHouseSpec;
import melons.innocent.building.container.Finish;
import melons.innocent.building.container.Size;

import java.util.List;

/**
 * The container houses bench: the buildings bench's own, offering a house of three forty-foot containers, the lower
 * two raised 2.5 m and 5 m apart, painted cream, the upper one across them in gun metal, its frame in gun metal too.
 * Picked in the tree of what is made, under its residences; its specification set field by field, its finishes
 * chosen; looked into in Vulkan, and walked round and up on the test kit.
 */
public final class ContainerBench {

    private ContainerBench() {
    }

    public static void main(final String[] args) throws InterruptedException {
        final Inspector inspector = new Inspector();
        final Catalogue catalogue = new Catalogue(List.of(new Offered(new ContainerHouse(),
                new ContainerHouseSpec<>(Size.FORTY, 250, 500, Finish.CREAM, Finish.GUN_METAL, Finish.GUN_METAL))));
        Platform.startup(() -> new BuildingBench(catalogue, inspector, "Container houses bench").shown());
        inspector.served();
        Platform.exit();
    }
}
