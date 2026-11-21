package com.example.fxsuite.building.chinese;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

/**
 * A festive gateway of the Northern Song (彩楼欢门): the decorated scaffold of poles a wine house stood before its
 * door, as the Qingming scroll draws the one before the wine house by the Rainbow Bridge. What one is built from,
 * centimetres.
 *
 * @param wide how wide it is across the door it stands before
 * @param deep how deep it is, front to back
 * @param high how high its middle poles stand
 */
public record FestiveGate(double wide, double deep, double high)
        implements Spec<Buildings.FestiveGateway, Chinese, ChineseEras.NorthernSong> {

    public FestiveGate {
        if (!(wide >= 400) || !(deep >= 100) || !(high >= 400)) {
            throw new IllegalArgumentException("a gateway 4 m wide or more, a metre deep or more, 4 m high or more");
        }
    }
}
