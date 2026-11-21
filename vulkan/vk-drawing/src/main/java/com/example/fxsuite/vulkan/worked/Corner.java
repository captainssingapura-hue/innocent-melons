package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A corner of a triangle as it falls on the picture, or a point between corners: where on the thing it is, the way
 * the surface faces there, where it is as the camera sees it; and where it falls, in pixels across and down, how
 * deep, and how far ahead of the camera.
 */
record Corner(Vec3 at, Vec3 faces, Vec3 from, double x, double y, double depth, double ahead) {
}
