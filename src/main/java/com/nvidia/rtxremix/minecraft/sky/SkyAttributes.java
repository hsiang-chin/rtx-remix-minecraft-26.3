/*
 * SPDX-FileCopyrightText: Copyright (c) 2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import org.joml.Vector4fc;

/**
 * Reads the sky values that Minecraft 26.3 publishes as environment attributes,
 * which replaced the {@code ClientLevel} sky getters and
 * {@code DimensionSpecialEffects} that the sky, cloud and light code used.
 *
 * <p>Every value is sampled at the main camera, as vanilla's own sky extraction
 * does, so it is only meaningful on the render thread.
 */
public final class SkyAttributes {
    private SkyAttributes() {}

    /** Which kind of sky a dimension draws; the 26.3 successor of {@code DimensionSpecialEffects.SkyType}. */
    public enum SkyKind {
        /** A sun, a moon and stars on a time-driven atmosphere. */
        NORMAL,
        /** The End's constant dark dome. */
        END,
        /** No sky at all, as in the Nether. */
        NONE
    }

    /**
     * The sky kind of a world.
     *
     * <p>TODO(port-26.3: sky): 26.3 no longer names a sky type per dimension, so this
     * derives it from the dimension itself. A modded dimension that drew a different
     * sky than its sky light implies will be classified wrongly until the sky
     * subsystem is ported against the environment attributes.
     */
    public static SkyKind kindOf(ClientLevel world) {
        if (world.dimension() == Level.END) {
            return SkyKind.END;
        }
        return world.dimensionType().hasSkyLight() ? SkyKind.NORMAL : SkyKind.NONE;
    }

    /** The sun's angle as a fraction of a turn, the value {@code ClientLevel.getTimeOfDay} returned. */
    public static float sunAngleTurns(float tickDelta) {
        return sample(EnvironmentAttributes.SUN_ANGLE, tickDelta) / 360.0f;
    }

    /** How strongly the stars show, from 0 to 1. */
    public static float starBrightness(float tickDelta) {
        return sample(EnvironmentAttributes.STAR_BRIGHTNESS, tickDelta);
    }

    /** The moon phase index, 0 for the full moon. */
    public static int moonPhaseIndex(float tickDelta) {
        return sample(EnvironmentAttributes.MOON_PHASE, tickDelta).index();
    }

    /** The cloud layer's height in blocks. */
    public static float cloudHeight(float tickDelta) {
        return sample(EnvironmentAttributes.CLOUD_HEIGHT, tickDelta);
    }

    /**
     * The cloud tint as RGBA floats.
     *
     * <p>TODO(port-26.3: sky): the pre-26.3 {@code ClientLevel.getCloudColor} also darkened
     * the tint with rain and thunder; whether the attribute already includes that has not
     * been checked in game.
     */
    public static Vector4fc cloudColor(float tickDelta) {
        return sample(EnvironmentAttributes.CLOUD_COLOR, tickDelta);
    }

    private static <T> T sample(EnvironmentAttribute<T> attribute, float tickDelta) {
        return Minecraft.getInstance().gameRenderer.mainCamera()
                .attributeProbe().getValue(attribute, tickDelta);
    }
}
