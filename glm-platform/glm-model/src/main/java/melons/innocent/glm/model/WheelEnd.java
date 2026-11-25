package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.Map;

/**
 * The points a corner's wheel end is made to, on the right, in the platform's frame, front or rear alike: the wheel's
 * middle; where the hub turns in the carrier, where the brake's disc sits on it and where the wheel is bolted to it;
 * where the caliper is bolted to the carrier; and where each link meets the carrier, by name.
 */
interface WheelEnd {

    /** What the corner's parts are called before what they are: nothing at the rear, "front " at the front. */
    String called();

    /** The wheel's middle. */
    Vec3 wheel();

    /** Where the hub turns in the carrier. */
    Vec3 hub();

    /** Where the brake's disc sits on the hub. */
    Vec3 disc();

    /** Where the wheel is bolted to the hub. */
    Vec3 hubFace();

    /** Where the caliper is bolted to the carrier. */
    Vec3 caliper();

    /** Where each link meets the carrier, by the name the carrier calls it. */
    Map<String, Vec3> carried();

    /**
     * The way each link's joint at the carrier points, by the same names: up or down, a ball pin's stud drawn into a
     * boss; level, a bush's bolt through a fork.
     */
    Map<String, Vec3> axes();

    /** Whether the carrier is forged, slim; or cast, stouter. */
    boolean forged();
}
