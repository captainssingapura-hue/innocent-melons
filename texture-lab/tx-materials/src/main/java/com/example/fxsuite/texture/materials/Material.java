package com.example.fxsuite.texture.materials;

import com.example.fxsuite.taxonomy.Node;

/**
 * Said of every leaf of the tree of materials ({@link Materials}): something a made thing is made of. A part that
 * needs a material asks for it by its type, so a material that is not in the tree cannot be asked for at all, and
 * one kind cannot be handed over where another is wanted.
 */
public interface Material extends Node {
}
