package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * NTUC Centre, One Marina Boulevard: DP Architects', its owner (the Singapore Labour Foundation) publishing no plans of
 * it, so on the map's outline. Published: 32 storeys, 162.8 m (the Skyscraper Center; Wikipedia says 110 m to its
 * roof); its ceilings 3 m; a lower zone of meeting rooms and an auditorium. Its floors 4.2 m apart, its lifts three
 * zones of six in a slim core: estimates.
 */
public final class NtucCentre {

    private NtucCentre() {
    }

    /**
     * NTUC Centre as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Marina Boulevard.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-813.14, -417.22}, {-792.53, -423.25}, {-780.60, -383.42}, {-785.77, -381.91}, {-785.32, -380.40},
            {-800.43, -375.96}, {-800.90, -377.51}, {-802.06, -377.17}, {-806.61, -392.32}, {-823.59, -387.36},
            {-821.33, -379.81}, {-842.63, -373.57}, {-848.69, -393.80}, {-849.41, -393.58}, {-851.27, -399.77},
            {-850.51, -400.00}, {-852.25, -405.76}, {-830.86, -412.03}, {-830.10, -409.49}, {-812.38, -414.69}});

    /**
     * NTUC Centre, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 32, 2, 420);
        return Offices.slim("NTUC Centre", MAPPED, levels, 16_280, Offices.uses(2, 4, "auditorium and meeting rooms"),
                List.of(new Offices.Zone(5, 14, 6, 400), new Offices.Zone(15, 23, 6, 600), new Offices.Zone(24, 32, 6,
                700)), -278, -410, List.of());
    }
}
