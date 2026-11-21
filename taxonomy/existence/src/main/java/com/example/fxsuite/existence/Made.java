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
 * features ({@link Features}). Its departments: the ways along the ground ({@link Highways}); what carries a way over
 * a gap ({@link Bridges}) and through the ground ({@link Tunnels}); the ground reshaped ({@link Earthworks}); and what
 * stands on it ({@link Buildings}). The order they are laid in is each map's to say. Made by a civilization in an era,
 * so each of its leaves is built by one. What can carry traffic is {@link Transit}; a network of it is no part of this
 * tree.
 *
 * <p>What it means is beside this record, in {@code Made.md}; what each department's nodes mean is beside its holder.
 */
public record Made() implements L0 {

    /** The tree, read once from its leaves: each department's. */
    public static final Taxonomy TREE = new Taxonomy("made", new Made(), List.of(
            // the highways: ways, and what they are paved with
            new Highways.Road(), new Highways.Street(), new Highways.Lane(), new Highways.Path(),
            new Highways.Towpath(), new Highways.Ford(), new Highways.Paving(), new Highways.Setts(),
            new Highways.Asphalt(), new Highways.Gravel(),
            // the bridges and the tunnels: a way carried over a gap, or through the ground
            new Bridges.Bridge(), new Bridges.Viaduct(), new Tunnels.Tunnel(),
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
            Stream.of(Made.class, Highways.class, Bridges.class, Tunnels.class, Earthworks.class, Buildings.class)
                    .map(Meanings.INSTANCE::beside)
                    .collect(LinkedHashMap<String, String>::new, Map::putAll, Map::putAll));
}
