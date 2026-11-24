package melons.innocent.road.surface.bench;

import javafx.application.Platform;
import melons.innocent.building.bench.BuildingBench;
import melons.innocent.building.bench.Catalogue;
import melons.innocent.building.bench.Inspector;
import melons.innocent.building.bench.Offered;
import melons.innocent.road.surface.ExpresswaySpec;
import melons.innocent.road.surface.ExpresswaySurface;
import melons.innocent.road.surface.LaneSpec;
import melons.innocent.road.surface.LaneSurface;
import melons.innocent.road.surface.RoadSpec;
import melons.innocent.road.surface.RoadSurface;
import melons.innocent.road.surface.StreetSpec;
import melons.innocent.road.surface.StreetSurface;

import java.util.List;

/**
 * The road surfaces bench: the buildings bench's own, offering what is on a road and nothing under it, a straight
 * stretch of each kind of way 100 m long on level ground: a road of two lanes and its shoulders; an expressway of three
 * lanes each way either side of its barrier; a street of a lane each way between its kerbs; a country lane. Picked in
 * the tree of what is made, under its highways; its specification set field by field; looked into in Vulkan, and
 * walked or driven along on the test kit, started at its far end looking down it.
 */
public final class RoadSurfaceBench {

    private RoadSurfaceBench() {
    }

    public static void main(final String[] args) throws InterruptedException {
        final Inspector inspector = new Inspector();
        final Catalogue catalogue = new Catalogue(List.of(
                new Offered(new RoadSurface(), new RoadSpec<>(350, 100, 10_000)),
                new Offered(new ExpresswaySurface(), new ExpresswaySpec<>(3, 370, 300, 10_000)),
                new Offered(new StreetSurface(), new StreetSpec<>(1, 325, 10_000)),
                new Offered(new LaneSurface(), new LaneSpec<>(300, 10_000))));
        Platform.startup(() -> new BuildingBench(catalogue, inspector, "Road surfaces bench").shown());
        inspector.served();
        Platform.exit();
    }
}
