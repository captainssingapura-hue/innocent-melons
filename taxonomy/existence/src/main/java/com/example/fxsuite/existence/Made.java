package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The root of what people have made on the land: the made layer, laid over the base terrain ({@link Terrains}) and its
 * natural features ({@link Features}). In three parts, each laid over those before it: the ways and what carries them
 * ({@link Networks}); the ground reshaped for what will stand on it ({@link Earthworks}); and what stands on it
 * ({@link Buildings}). Made by a civilization in an era, so each of its leaves is built by one.
 *
 * <p>What it means is beside this record, in {@code Made.md}; what each part's nodes mean is beside its holder.
 */
public record Made() implements L0 {

    /** The tree, read once from its leaves: each part's, in the order the parts are laid. */
    public static final Taxonomy TREE = new Taxonomy("made", new Made(), List.of(
            // the networks: ways, waterways, what carries them, what they are paved with
            new Networks.Road(), new Networks.Street(), new Networks.Lane(), new Networks.Path(),
            new Networks.Towpath(), new Networks.Canal(), new Networks.Bridge(), new Networks.Viaduct(),
            new Networks.Tunnel(), new Networks.Ford(), new Networks.Paving(), new Networks.Setts(),
            new Networks.Asphalt(), new Networks.Gravel(),
            // the earthworks: the ground levelled, raised, cut, faced, cultivated
            new Earthworks.Pad(), new Earthworks.Terrace(), new Earthworks.Reclaimed(), new Earthworks.Embankment(),
            new Earthworks.Dike(), new Earthworks.Mound(), new Earthworks.CityWall(), new Earthworks.Cutting(),
            new Earthworks.Ditch(), new Earthworks.Moat(), new Earthworks.Revetment(), new Earthworks.Seawall(),
            new Earthworks.Field(), new Earthworks.Paddy(),
            // the buildings: what stands on the ground
            new Buildings.House(), new Buildings.Farmhouse(), new Buildings.CourtyardHouse(),
            new Buildings.HomesTower(), new Buildings.ShopHouse(), new Buildings.WineHouse(), new Buildings.TeaHouse(),
            new Buildings.Stall(), new Buildings.Warehouse(), new Buildings.FestiveGateway(),
            new Buildings.OfficeTower(), new Buildings.HotelTower(), new Buildings.MixedTower(), new Buildings.Temple(),
            new Buildings.Church(), new Buildings.Pavilion(), new Buildings.Courthouse(), new Buildings.Quay(),
            new Buildings.GateTower()),
            Stream.of(Made.class, Networks.class, Earthworks.class, Buildings.class).map(Meanings.INSTANCE::beside)
                    .collect(LinkedHashMap<String, String>::new, Map::putAll, Map::putAll));
}
