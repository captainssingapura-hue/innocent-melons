package com.example.fxsuite.building.chinese;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * How the Chinese of the Northern Song built a thing: an implementation whose civilization and era are said once, here.
 *
 * @param <T> the thing it builds
 * @param <S> what it builds from
 */
public interface NorthernSongImpl<T extends Leaf<?>, S extends Spec<T, Chinese, ChineseEras.NorthernSong>>
        extends ThingImpl<T, Chinese, ChineseEras.NorthernSong, S> {

    @Override
    default Chinese civilization() {
        return Chinese.IT;
    }

    @Override
    default ChineseEras.NorthernSong era() {
        return new ChineseEras.NorthernSong();
    }
}
