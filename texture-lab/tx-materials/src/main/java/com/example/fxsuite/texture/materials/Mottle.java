package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Blurred;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;

import java.util.List;

/**
 * Numbers scattered over cells that fill the space a thing lies in, each cell {@code size} centimetres every way:
 * what a surface with no edges in it is mottled by, turf or skin or bone. Because the cells fill space and are not
 * a sheet laid over the thing, what is made of them is not drawn out on a slope or round a curve. {@code use} tells
 * these numbers from any other scatter over the same cells.
 */
public record Mottle(double size, int use) {

    /** How the cells one above another are told apart, in the one number a scatter takes for how high a cell is. */
    private static final double STOREY = 57;
    private static final double MIDDLE = 0.5;

    /**
     * A number from nothing to one that goes over smoothly from place to place: each corner of a cell has its own,
     * and a point within the cell has something of all eight, most of the nearest. Further off, as a pixel comes
     * to cover several cells, it settles to the middle.
     */
    public Real soft(final Point at, final Real covers) {
        final Real across = eased(at.across().over(size).part());
        final Real up = eased(at.up().over(size).part());
        final Real along = eased(at.along().over(size).part());
        Real[] corners = new Real[8];
        for (int corner = 0; corner < corners.length; corner++) {
            corners[corner] = cell(at, corner & 1, corner >> 1 & 1, corner >> 2 & 1).number();
        }
        // across first, then up, then along: eight corners to four, to two, to one
        for (final Real share : List.of(across, up, along)) {
            final Real[] fewer = new Real[corners.length / 2];
            for (int pair = 0; pair < fewer.length; pair++) {
                fewer[pair] = corners[2 * pair].mixed(corners[2 * pair + 1], share);
            }
            corners = fewer;
        }
        return corners[0].mixed(new Real(MIDDLE), new Blurred(covers, size).share());
    }

    /**
     * The scatter over the cells: the cell a point is in, or the one so many cells further {@code across},
     * {@code up} and {@code along}.
     */
    public Scatter cell(final Point at, final int across, final int up, final int along) {
        return new Scatter(at.across().over(size).floored().plus(new Real(across)),
                at.along().over(size).floored().plus(new Real(along))
                        .plus(at.up().over(size).floored().plus(new Real(up)).times(STOREY)), use, size);
    }

    /** {@code part} of the way across a cell, eased: slow at each side, so nothing shows where cells meet. */
    private Real eased(final Real part) {
        return part.times(part).times(new Real(3).less(part.times(2)));
    }
}
