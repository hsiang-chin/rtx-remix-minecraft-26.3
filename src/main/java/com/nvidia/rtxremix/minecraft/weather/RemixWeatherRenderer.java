/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.weather;

import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws rain and snow with GL directly onto the composited Remix colour image,
 * depth-testing against the interop depth texture so weather is occluded by
 * path-traced geometry.
 *
 * <p>Render thread only, and after Remix has presented its image into GL: the
 * renderer owns its GL buffers and reads the interop textures the frame just
 * produced.
 */
public final class RemixWeatherRenderer {
    private static final float NEAR_PLANE = 0.05f;
    private static final float FAR_PLANE = 1000.0f;
    private static final Identifier RAIN_TEXTURE =
            Identifier.withDefaultNamespace("textures/environment/rain.png");

    private final List<DeferredDraw> draws = new ArrayList<>(2);
    private Matrix4f projection;
    private Matrix4f modelView;
    private int capturedQuadCount;
    private int lastDrawCount;
    private int lastQuadCount;

    public static void registerShaders() {
        // TODO(port-26.3: particles): the rain refraction shader was a core shader
        // registered through CoreShaderRegistrationCallback, which 26.3 removed.
    }

    public void beginCaptureFrame() {
        closePendingDraws();
        capturedQuadCount = 0;
        lastDrawCount = 0;
        lastQuadCount = 0;
    }

    public void updateCamera(Camera camera, float fovYDegrees, float aspect) {
        Quaternionf cameraRotation = camera.rotation().conjugate(new Quaternionf());
        modelView = new Matrix4f().rotation(cameraRotation);
        projection = new Matrix4f().perspective(
                (float) Math.toRadians(fovYDegrees),
                aspect,
                NEAR_PLANE,
                FAR_PLANE);
    }

    public void capture(MeshData buffer, Identifier texture) {
        if (texture == null) {
            buffer.close();
            throw new IllegalStateException("Weather draw did not bind a texture");
        }
        capturedQuadCount += buffer.drawState().vertexCount() / 4;
        draws.add(new DeferredDraw(buffer, texture, texture.equals(RAIN_TEXTURE)));
    }

    public void render(int finalColorTexture, int depthTexture) {
        if (draws.isEmpty()) {
            return;
        }
        if (depthTexture == 0) {
            closePendingDraws();
            throw new IllegalStateException("Remix depth texture is unavailable");
        }
        if (finalColorTexture == 0) {
            closePendingDraws();
            throw new IllegalStateException("Remix final-color texture is unavailable");
        }
        if (projection == null || modelView == null) {
            closePendingDraws();
            throw new IllegalStateException("Weather camera state was not initialized");
        }

        // TODO(port-26.3: particles): the deferred rain and snow draw went through the
        // RenderSystem shader state, ShaderInstance and the lightmap texture, none of
        // which exist in 26.3. The captured meshes are counted and released, not drawn.
        // The depth-seeding pass that made weather depth-test against the Remix depth
        // texture went with it and has to be rebuilt on the 26.3 render-pass API.
        lastDrawCount = draws.size();
        lastQuadCount = capturedQuadCount;
        closePendingDraws();
    }

    private void closePendingDraws() {
        for (DeferredDraw draw : draws) {
            if (draw != null) {
                draw.buffer().close();
            }
        }
        draws.clear();
    }

    public int getLastDrawCount() {
        return lastDrawCount;
    }

    public int getLastQuadCount() {
        return lastQuadCount;
    }

    public void cleanup() {
        closePendingDraws();
    }

    private record DeferredDraw(MeshData buffer, Identifier texture, boolean rain) {
    }
}
