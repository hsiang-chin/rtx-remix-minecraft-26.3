/*
 * SPDX-FileCopyrightText: Copyright (c) 2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Queue;

/**
 * The live particles of one {@link ParticleGroup}. 26.3 keeps them per group
 * instead of per render type in the engine, so the particle capture reads each
 * group's queue through this accessor.
 *
 * <p>TODO(port-26.3: particles): not registered in the mixin list yet.
 */
@Mixin(ParticleGroup.class)
public interface ParticleGroupAccessor {
    @Accessor("particles")
    Queue<Particle> getParticles();
}
