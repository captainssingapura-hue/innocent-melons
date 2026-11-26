package melons.innocent.demos.forestroad;

import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.bench.Walk;
import melons.innocent.glm.testdrive.GlmDrive;
import melons.innocent.plants.walk.RoadForestPlace;

import java.io.IOException;

/**
 * The retro saloon, built on the GLM platform, driven down a country road through a forest without end: the plants
 * lab's road and forest ({@link RoadForestPlace}), made on ahead as far as it is driven, and the GLM test drive's car
 * on it ({@link GlmDrive}), driven as it is there, W the throttle, S the brake, A and D the wheel, F its lights, G its
 * indicators. It starts two hundred metres back down the straight the road begins with, before it wanders, in the
 * right-hand lane, headed along it. As the walks' runs say, with their sky, scripts, snapshots and films; and as the
 * forest's and the car's say, the engine in it, say, by {@code -Dglm.engine=}.
 */
public final class ForestRoadDrive {

    private static final String TITLE = "The retro saloon on a road through a forest without end";
    /** How far right of the road's middle line its right-hand lane's middle is, a lane being three metres wide. */
    private static final double LANE = 150;
    /** How far back along the road it starts: down the straight, with room to get up to speed before the bends. */
    private static final double BACK = 20_000;

    public static void main(final String[] args) throws IOException {
        new ForestRoadDrive().run();
    }

    private void run() throws IOException {
        final GlmDrive car = new GlmDrive(true);
        new Walk(TITLE, RoadForestPlace::new, car.roamer(new Place(LANE, -BACK, car.chassis().ride(), 0))).run();
    }
}
