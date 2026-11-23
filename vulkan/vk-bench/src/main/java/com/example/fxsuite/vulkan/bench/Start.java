package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Body;

/**
 * Where a run's avatar stands at first, and which way it faces, as a run is told it: {@code x,z,yaw}, metres across
 * and along the world and degrees round (0 along the world, 90 toward its right). Blank, the world's middle, facing
 * along the world. What cannot be read is refused, saying why.
 */
public record Start(String said) {

    private static final double METRE = 100;

    /** The avatar standing so, as the engine has a body. */
    public Body body() {
        if (said.isBlank()) {
            return Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0);
        }
        final String[] numbers = said.split(",");
        if (numbers.length != 3) {
            throw new IllegalArgumentException("a start is so far across, so far along, in metres, and which way"
                    + " round, in degrees: " + said);
        }
        return Body.standing(new com.example.fxsuite.avatar.sim.Vec3(Double.parseDouble(numbers[0].trim()) * METRE,
                0, Double.parseDouble(numbers[1].trim()) * METRE), Double.parseDouble(numbers[2].trim()));
    }
}
