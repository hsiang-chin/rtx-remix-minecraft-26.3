/*
 * SPDX-FileCopyrightText: Copyright (c) 2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.mixin;

import net.minecraft.client.particle.ParticleResources;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Sprite providers keyed by particle type id. 26.3 moved them from the particle
 * engine into {@link ParticleResources}. The value class is private in vanilla,
 * so callers read it through the public {@code SpriteSet} interface it implements.
 *
 * <p>TODO(port-26.3: particles): not registered in the mixin list yet.
 */
@Mixin(ParticleResources.class)
public interface ParticleResourcesAccessor {
    @Accessor("spriteSets")
    Map<Identifier, ?> getSpriteSets();
}
