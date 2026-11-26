package melons.innocent.doors.swing;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * One part of a door as it is drawn: all of it that is of one material, told one colour, and moves one way, as one
 * mesh. The mesh is in the door's own frame, the leaf shut: the middle of the doorway's floor where nothing is, across
 * the opening rightward as seen from the front, up, and through the wall away from the front.
 *
 * @param name  what it is, in words
 * @param of    the lab's material it is made of
 * @param tint  the colour it is told, for a material that takes its colour from the thing
 * @param moves whether it is fixed or turns with the leaf
 * @param mesh  its mesh
 */
public record DoorPart(String name, Material of, Vec3 tint, Moves moves, Mesh mesh) {
}
