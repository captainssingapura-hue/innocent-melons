package melons.innocent.road.bridge.bench;

import javafx.application.Platform;
import melons.innocent.building.bench.BuildingBench;
import melons.innocent.building.bench.Catalogue;
import melons.innocent.building.bench.Inspector;
import melons.innocent.building.bench.Offered;
import melons.innocent.road.bridge.FlyoverSpec;
import melons.innocent.road.bridge.GenericFlyover;
import melons.innocent.road.bridge.GenericViaduct;
import melons.innocent.road.bridge.Piers;
import melons.innocent.road.bridge.ViaductSpec;

import java.util.List;

/**
 * The bridges bench: the buildings bench's own, offering what carries a road over a gap, on level ground: a viaduct
 * 300 m long carrying an expressway of three lanes each way 10 m up, on spans of 30 m, each pier on two columns; a
 * flyover carrying a road of two lanes over a way 20 m wide, 5.5 m of room left under it. Picked in the tree of what
 * is made, under its bridges; its specification set field by field; looked into in Vulkan, and walked round and under
 * on the test kit.
 */
public final class BridgesBench {

    private BridgesBench() {
    }

    public static void main(final String[] args) throws InterruptedException {
        final Inspector inspector = new Inspector();
        final Catalogue catalogue = new Catalogue(List.of(
                new Offered(new GenericViaduct(), new ViaductSpec<>(3, 370, 300, 30_000, 1_000, 3_000, Piers.TWIN)),
                new Offered(new GenericFlyover(), new FlyoverSpec<>(350, 100, 550, 2_000))));
        Platform.startup(() -> new BuildingBench(catalogue, inspector, "Bridges bench").shown());
        inspector.served();
        Platform.exit();
    }
}
