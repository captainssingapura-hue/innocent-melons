package melons.innocent.road.bridge.bench;

import javafx.application.Platform;
import melons.innocent.building.bench.BuildingBench;
import melons.innocent.building.bench.Catalogue;
import melons.innocent.building.bench.Inspector;
import melons.innocent.building.bench.Offered;
import melons.innocent.road.bridge.Carried;
import melons.innocent.road.bridge.ElevatedRoadSpec;
import melons.innocent.road.bridge.FlyoverSpec;
import melons.innocent.road.bridge.GenericElevatedRoad;
import melons.innocent.road.bridge.GenericFlyover;
import melons.innocent.road.bridge.GenericRamp;
import melons.innocent.road.bridge.GenericSpiralRamp;
import melons.innocent.road.bridge.GenericViaduct;
import melons.innocent.road.bridge.Piers;
import melons.innocent.road.bridge.RampSpec;
import melons.innocent.road.bridge.SpiralRampSpec;
import melons.innocent.road.bridge.ViaductSpec;

import java.util.List;

/**
 * The bridges bench: the buildings bench's own, offering what carries a road over a gap, on level ground: a viaduct
 * 300 m long carrying an expressway of three lanes each way 10 m up, on spans of 30 m, each pier on two columns; a
 * flyover carrying a road of two lanes over a way 20 m wide, 5.5 m of room left under it; a straight ramp of one
 * lane climbing 5% from the ground up to 8 m, on spans of 20 m; a spiral ramp of two lanes two and a half turns
 * round a core, 16 m out from its middle, climbing 8 m a turn; and the three together, an elevated road of two lanes
 * up a straight ramp climbing 6%, along a viaduct 120 m long 11 m up, and down a spiral ramp of a turn and a half
 * round a core at its far end. Picked in the tree of what is made, under its bridges; its specification set field by
 * field; looked into in Vulkan, and walked round, under and up on the test kit.
 */
public final class BridgesBench {

    private BridgesBench() {
    }

    public static void main(final String[] args) throws InterruptedException {
        final Inspector inspector = new Inspector();
        final Catalogue catalogue = new Catalogue(List.of(
                new Offered(new GenericViaduct(), new ViaductSpec<>(3, 370, 300, 30_000, 1_000, 3_000, Piers.TWIN)),
                new Offered(new GenericFlyover(), new FlyoverSpec<>(350, 100, 550, 2_000)),
                new Offered(new GenericRamp(), new RampSpec<>(1, 350, 100, 800, 0.05, 2_000, Piers.SINGLE)),
                new Offered(new GenericSpiralRamp(), new SpiralRampSpec<>(2, 350, 100, 1_600, 800, 2.5,
                        Carried.CORE)),
                new Offered(new GenericElevatedRoad(), new ElevatedRoadSpec<>(2, 350, 100, 1_100, 0.06, 12_000,
                        3_000, Piers.SINGLE, 1_800, 1.5, Carried.CORE))));
        Platform.startup(() -> new BuildingBench(catalogue, inspector, "Bridges bench").shown());
        inspector.served();
        Platform.exit();
    }
}
