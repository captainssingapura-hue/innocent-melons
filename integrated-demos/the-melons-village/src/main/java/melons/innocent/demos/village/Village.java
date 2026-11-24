package melons.innocent.demos.village;

import com.example.fxsuite.places.yard.Walled;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Num;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A village along a lane: so many homes, by turns on the lane's left and its right, each a yard walled round with
 * a gateway onto the lane, and in the yard a house that is half a giant watermelon. The lane runs on from where the
 * avatar starts, the way the avatar faces. The same village every time: how big each yard and each melon is, and
 * how each melon is turned, comes from the engine's own scatter, which repeats. Before the village, behind
 * where the avatar starts, is its small lake.
 */
public record Village(int homes) {

    /** How wide the lane is from its middle to a yard's front, and how far along it the first yard's middle is. */
    private static final double LANE = 350;
    private static final double BEGINS = 1100;
    /** How far along the lane from one yard's middle to the next on the same side. */
    private static final double APART = 2000;
    private static final double WALL_HIGH = 190;
    private static final double WALL_THICK = 30;
    private static final double GATE = 300;
    /** How many kinds of wall the yards are walled with, by turns. */
    private static final int WALLINGS = 3;
    private static final int WOOD_CARS = 5;

    /**
     * One home: its yard's wall, the house in the yard, and which kind of wall the yard has (0, 1 or 2).
     */
    public record Home(Walled yard, MelonHouse house, int walling) {
    }

    /** Every home of the village, the first nearest where the avatar starts. */
    public List<Home> homesteads() {
        final List<Home> all = new ArrayList<>();
        for (int home = 0; home < homes; home++) {
            final boolean onLeft = home % 2 == 0;
            final double frontage = 1500 + 300 * Num.hash(home, 1);
            final double deep = 1300 + 200 * Num.hash(home, 2);
            // a yard faces the lane: one on the left faces right, and one on the right, left
            final double turned = onLeft ? -90 : 90;
            final Vec3 middle = new Vec3((onLeft ? -1 : 1) * (LANE + deep / 2), 0, BEGINS + APART * (home / 2));
            final Walled yard = new Walled(middle, turned, frontage, deep, WALL_HIGH, WALL_THICK, GATE, WALL_HIGH);
            // the house a little toward the back of its yard, its length along the yard's front, give or take
            final Vec3 back = new Turned(new Vec3(0, 1, 0), turned).matrix()
                    .clip(new Vec3(0, 0, 0.1 * deep)).shown();
            all.add(new Home(yard, new MelonHouse(middle.plus(back), 20 + 6 * Num.hash(home, 3),
                    turned + 30 * (Num.hash(home, 4) - 0.5)), home % WALLINGS));
        }
        return all;
    }

    /**
     * The small lake before the village: some 120 m by 62 m, about twice the ground the village itself stands on
     * (eight homes: 78 m along the lane by 37 m across it). It lies across the lane's line behind where the avatar
     * starts, its near shore five or six metres off, so the avatar starts between the lake and the village.
     */
    public VillageLake lake() {
        return new VillageLake(new Vec3(0, 0, -3400), 12_000, 6_200);
    }

    /**
     * The car park beside the lake: off the lake's right-hand end as the avatar faces the village, some six metres
     * from its kerb, its aisles running the way the lane does. A hundred cars stand in it.
     */
    public CarPark carPark() {
        return new CarPark(new Vec3(9600, 0, -3400));
    }

    /**
     * The small wood beside the lake: off the lake's left-hand end as the avatar faces the village, across the
     * water from the car park, thirty oaks with no leaves on some fifty metres by forty-five of ground.
     */
    public Grove grove() {
        return new Grove(new Vec3(-9700, 0, -3400), 5000, 4500, 30);
    }

    /**
     * The village's forest: forty thousand trees of the wood's same few, on some 1.27 km by 1.58 km of ground west of
     * the towers, its near edge some 25 m beyond them, about seven metres from tree to tree. Where it lies, how big
     * and how thick are its author's.
     */
    public Grove forest() {
        return new Grove(new Vec3(-79_200, 0, 0), 126_500, 158_100, 40_000);
    }

    /** Its woods: the small wood beside the lake, and the forest. */
    public List<Grove> woods() {
        return List.of(grove(), forest());
    }

    /** Where each house stands, for the one half melon that is drawn once for each. */
    /**
     * Its tall brick towers: two west of the yards on the left of the lane and two south-west of the wood, so that
     * with the sun where the bench's day has it (some 60 degrees up, from the south-west) their shadows fall
     * north-east across the yards and into the wood. Each one's door faces what its shadow falls on. Where they
     * stand and how high are their author's.
     */
    public List<Tower> towers() {
        return List.of(
                new Tower(new Vec3(-2600, 0, 1800), -90, 4500),
                new Tower(new Vec3(-2600, 0, 5800), -90, 5500),
                new Tower(new Vec3(-13400, 0, -6600), 180, 5000),
                new Tower(new Vec3(-9800, 0, -6500), 180, 4000));
    }

    /** The few cars left parked among the wood's trees, clear of their trunks. */
    public List<Parked> woodCars() {
        return grove().parked(WOOD_CARS);
    }

    public List<Mat4> houses() {
        return homesteads().stream().map(home -> home.house().placed()).toList();
    }

    /**
     * Everything of the village that is in the way: every yard's wall, every house, the lake's kerb, every car
     * in the car park, and the trunk of every tree in the woods.
     */
    public Obstacles inTheWay() {
        final List<Block> blocks = new ArrayList<>();
        for (final Home home : homesteads()) {
            blocks.addAll(home.yard().inTheWay());
            blocks.addAll(home.house().inTheWay());
        }
        blocks.addAll(lake().inTheWay());
        blocks.addAll(carPark().inTheWay());
        woods().forEach(wood -> blocks.addAll(wood.inTheWay()));
        towers().forEach(tower -> blocks.addAll(tower.walls().inTheWay()));
        woodCars().forEach(car -> blocks.addAll(car.inTheWay()));
        return new Obstacles(blocks);
    }
}
