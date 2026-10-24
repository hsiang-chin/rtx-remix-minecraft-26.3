/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.terrain;

import net.minecraft.world.level.chunk.LevelChunk;

/**
 * A chunk holder's cached entry for the server's per-tick chunk list; see {@code ChunkHolderMixin}.
 *
 * <p>TODO(port-26.3: terrain): 26.3 removed {@code ServerChunkCache.ChunkAndHolder}, so the entry type
 * is opaque until the per-tick chunk list is ported.
 */
public interface ChunkHolderTickEntry {
    Object rtxremix$tickEntry(LevelChunk chunk);
}
