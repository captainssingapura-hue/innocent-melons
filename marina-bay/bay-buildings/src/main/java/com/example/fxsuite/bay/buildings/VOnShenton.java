package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * V on Shenton, its homes tower: UNStudio's, its developer (UIC) publishing no lengths, so on the map's two slabs taken
 * as one, the gap between them with them. Published: 54 storeys, 237 m (the Skyscraper Center); 510 homes; its sky
 * gardens on floors 8, 24 and 34, the last two its sky lobbies; its penthouses on 53 and 54. Its floors 4 m apart, its
 * lifts two zones of four: estimates. Its office tower, 23 storeys, is not here.
 */
public final class VOnShenton {

    private VOnShenton() {
    }

    /**
     * V on Shenton as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Shenton Lane.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1027.23, -1096.57}, {-1011.19, -1038.41}, {-1035.22, -1031.94}, {-1049.69, -1084.38},
            {-1044.06, -1085.90}, {-1045.63, -1091.61}});

    /**
     * V on Shenton, as above.
     */
    public static OfficeTower tower() {
        return Homes.tower("V on Shenton", MAPPED, 54, 600, 400, 23_700, Offices.uses(8, 8, "sky garden", 24, 24,
                "sky garden and lobby", 34, 34, "sky garden and lobby", 53, 54, Homes.PENTHOUSES), 34, 4, 400, 600,
                -884, 28);
    }
}
