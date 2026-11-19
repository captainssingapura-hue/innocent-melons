package com.example.fxsuite.bay.world;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.bay.buildings.Mbfc;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The bay project's own office: a few floors of the financial centre's first tower, the building it builds, kept for it.
 * A placeholder as yet: the floors named and reserved, nothing on them but what the tower has; to be furnished, its
 * team's rooms laid out, when the project comes to it. Walked to, it is the bay's standing scenario: the office is
 * fine as anywhere in the bay must be, at the bay's frame rate.
 *
 * @param tower what tower it is in, as the tower is described
 * @param from  its first floor, as the tower numbers its floors
 * @param to    its last floor
 */
public record ProjectOffice(OfficeTower tower, int from, int to) {

    /** The project's office: floors 26 to 28 of MBFC Tower 1, office floors of the tower's high zone, under its plant floor. */
    public static final ProjectOffice BAY = new ProjectOffice(Mbfc.tower1(), 26, 28);

    public ProjectOffice {
        if (from > to) {
            throw new IllegalArgumentException("floors from one to one at or over it");
        }
    }

    /** Its floors as the tower's storeys count them, from the lobby, 0. */
    public List<Integer> storeys() {
        return IntStream.rangeClosed(from, to).map(Mbfc::storey).boxed().toList();
    }

    /** What it is, in a line: where, and that it is held. */
    public String said() {
        return tower.name() + ", floors " + from + " to " + to + ": the project's office, reserved, to be furnished";
    }
}
