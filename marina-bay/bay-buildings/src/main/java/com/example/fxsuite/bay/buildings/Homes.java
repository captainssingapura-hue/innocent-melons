package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Placed;
import com.example.fxsuite.avatar.lifts.HighRiseBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * What the bay's homes towers share, none of whose owners publishes plans with their lengths written on them: each an
 * office tower ({@link OfficeTower}) on the map's outline ({@link Mapped}), its storeys and its height as published, its
 * storeys homes; what no one publishes estimated, the same way for each, and said to be: its lobby's height and its
 * storeys', its lifts, its core.
 *
 * <p>Its core the estimate: two banks of lifts facing each other across one lift lobby, the low zone's on its south,
 * the high zone's on its north; its stairs, its service lift and its risers in a block at its east end. Its skin the
 * office tower's, glass round each storey: the model has no other yet (homes' windows set in a wall, their balconies,
 * are the buildings lab's to add).
 *
 * <p>A storey here is one of the model's: the lobby the first (0), the building's floor 1; floor {@code n} over it the
 * model's {@code n - 1}.
 */
final class Homes {

    private Homes() {
    }

    /** What a storey is: homes; the penthouses at the top. */
    static final String HOMES = "homes";
    static final String PENTHOUSES = "penthouses";
    /** The lift lobby between the two banks, so wide; the block of stairs, service lift and risers, so long across. */
    static final double LIFT_LOBBY = 330;
    static final double SERVICE = 600;

    /** The building's floor numbered {@code n} as the model's storey. */
    static int storey(final int n) {
        return n - 1;
    }

    /**
     * A homes tower: on {@code mapped}'s outline, its way in at the middle of its first edge; floors 2 to {@code top}
     * over a lobby {@code lobby} high, each {@code storey} high; its top {@code height} up, its crown what is over its
     * roof; each floor homes but those {@code said} to be something else; its lifts in two zones, floors 2 to
     * {@code split} and the rest, {@code cars} cars each, at {@code low} and {@code high}; its core's middle at
     * ({@code across}, {@code along}).
     */
    static OfficeTower tower(final String name, final Mapped mapped, final int top, final double lobby,
                             final double storey, final double height, final Map<Integer, String> said,
                             final int split, final int cars, final double low, final double high,
                             final double across, final double along) {
        final List<Double> levels = new ArrayList<>(List.of(0.0));
        final List<String> uses = new ArrayList<>(List.of(OfficeTower.LOBBY));
        for (int n = 2; n <= top; n++) {
            levels.add(lobby + (n - 2) * storey);
            uses.add(said.getOrDefault(n, HOMES));
        }
        levels.add(lobby + (top - 1) * storey);
        final int lowTo = storey(split);
        final int highTo = storey(top);
        final HighRiseBank lowBank = OfficeTower.bank(levels.subList(0, lowTo + 1), 1, lowTo, cars, low);
        final HighRiseBank highBank = OfficeTower.bank(levels.subList(0, highTo + 1), lowTo + 1, highTo, cars, high);
        final Placed probe = new Placed(lowBank, 0, 0, 0);
        final double length = probe.length();
        final double depth = probe.depth();
        final double west = across - (length + SERVICE) / 2;
        final double banksAcross = west + length / 2;
        final List<Placed> banks = List.of(
                new Placed(lowBank, banksAcross, along - LIFT_LOBBY / 2 - depth / 2, 0),
                new Placed(highBank, banksAcross, along + LIFT_LOBBY / 2 + depth / 2, 180));
        final List<double[]> service = List.of(new double[] {west + length, west + length + SERVICE,
            along - LIFT_LOBBY / 2 - depth, along + LIFT_LOBBY / 2 + depth});
        return new OfficeTower(name, mapped.outline(), levels, uses, service, banks, height - levels.getLast(), 0);
    }
}
