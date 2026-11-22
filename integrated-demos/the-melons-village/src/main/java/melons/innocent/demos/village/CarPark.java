package melons.innocent.demos.village;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Turns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A car park: a level sheet of concrete, 48 m by 56 m, marked out in bays and with cars standing in them. It is
 * laid out as car parks are: an aisle six metres wide with a row of bays down each side of it, three times over,
 * so that the rows between two aisles stand back to back; twenty bays to a row, each 2.5 m wide and five deep, a
 * hundred and twenty in all. A hundred of them are taken, which ones and by what car being the same every time.
 *
 * <p>{@code stands} is its middle, on the ground. Its aisles run ahead, along the world; its rows of bays lie side
 * by side across it. The concrete is the lab's, with the bays' lines painted on it; a car is the lab's plain car
 * body, in one of the paints, each its own colour. The concrete is in nobody's way, lying as it does a finger's
 * breadth above the ground; the cars are.
 */
public record CarPark(Vec3 stands) {

    private static final double BAY_WIDE = 250;
    private static final double BAY_DEEP = 500;
    private static final double AISLE = 600;
    private static final int BAYS = 20;
    private static final int AISLES = 3;
    /** How far the concrete goes on past the ends of the rows, for cars to turn in. */
    private static final double BEYOND = 300;
    private static final double LINE = 10;
    private static final double THICK = 4;
    private static final double TOP = 1;
    private static final int TAKEN = 100;
    /** How far a car may stand from the middle of its bay, along it and across it. */
    private static final double ALONG = 12;
    private static final double ASKEW = 12;
    private static final long CHANCE = 20261122L;

    /** How far it is across, over its rows and aisles. */
    public double across() {
        return AISLES * (2 * BAY_DEEP + AISLE);
    }

    /** How far it is from end to end, along its aisles. */
    public double deep() {
        return BAYS * BAY_WIDE + 2 * BEYOND;
    }

    /** The sheet of concrete, in its own frame: its middle the middle of the sheet. */
    public Mesh sheet() {
        return new Cuboid(across(), THICK, deep()).formed();
    }

    /** Where the sheet's own middle is in the world: just under the ground, so its top is just over it. */
    public Vec3 sheetStands() {
        return stands.plus(new Vec3(0, TOP - THICK / 2, 0));
    }

    /**
     * What the sheet is surfaced with: the lab's concrete, and over it the bays' lines, white, one between each
     * bay and the next and none across the aisles or past the rows' ends.
     */
    public Surface paved() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final double module = 2 * BAY_DEEP + AISLE;
        // across it, each turn is a row, an aisle and a row: the aisle is the middle of the turn
        final Real aisle = new Turns(at.across().plus(new Real(across() / 2 - BAY_DEEP)).over(module),
                new Real(1 / module)).pulse(AISLE / module, seen.covers());
        // along it, each turn is a bay, with a line begun half its width before the bay does
        final Real lines = new Turns(at.along().plus(new Real(BAYS * BAY_WIDE / 2 + LINE / 2)).over(BAY_WIDE),
                new Real(1 / BAY_WIDE)).pulse(LINE / BAY_WIDE, seen.covers());
        final double ends = BAYS * BAY_WIDE / 2 + LINE / 2;
        final Real within = at.along().past(new Real(-ends)).less(at.along().past(new Real(ends)));
        return new Shades().of(new Materials.Concrete()).marked(new Term("bay lines", new Colour("#ecebe4"),
                lines.times(new Real(1).less(aisle)).times(within), LINE));
    }

    /** The middle of every bay, row by row from the left, each row from its back end forward. */
    public List<Vec3> bays() {
        final List<Vec3> bays = new ArrayList<>();
        for (int row = 0; row < 2 * AISLES; row++) {
            // rows go in pairs about an aisle: the first of a pair before it, the second after
            final double x = -across() / 2 + (row / 2) * (2 * BAY_DEEP + AISLE) + BAY_DEEP / 2
                    + (row % 2) * (BAY_DEEP + AISLE);
            for (int bay = 0; bay < BAYS; bay++) {
                bays.add(stands.plus(new Vec3(x, 0, (bay + 0.5 - BAYS / 2.0) * BAY_WIDE)));
            }
        }
        return bays;
    }

    /**
     * The cars standing in it: a hundred, each in a bay of its own, nose in or tail in, not quite square in the
     * bay's middle, in a livery of its own. Which bays are left empty, and everything else that looks like chance,
     * is the same every time.
     */
    public List<Parked> parked() {
        final Random chance = new Random(CHANCE);
        final List<Vec3> bays = new ArrayList<>(bays());
        final List<Integer> order = new ArrayList<>();
        for (int bay = 0; bay < bays.size(); bay++) {
            order.add(bay);
        }
        Collections.shuffle(order, chance);
        final List<Integer> taken = new ArrayList<>(order.subList(0, TAKEN));
        Collections.sort(taken);
        final List<Parked> parked = new ArrayList<>();
        for (final int bay : taken) {
            final Vec3 off = new Vec3((chance.nextDouble() * 2 - 1) * ALONG, 0, (chance.nextDouble() * 2 - 1) * ASKEW);
            parked.add(new Parked(bays.get(bay).plus(off), chance.nextBoolean(),
                    Livery.values()[chance.nextInt(Livery.values().length)]));
        }
        return parked;
    }

    /** What is in the way: every car, as two boxes. */
    public List<Block> inTheWay() {
        final List<Block> blocks = new ArrayList<>();
        parked().forEach(car -> blocks.addAll(car.inTheWay()));
        return blocks;
    }
}
