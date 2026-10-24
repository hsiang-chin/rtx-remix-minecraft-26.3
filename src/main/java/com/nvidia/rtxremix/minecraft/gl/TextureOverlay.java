/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.gl;

import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Draws the Remix output texture as a full-screen, fully opaque quad on
 * Minecraft's main framebuffer, after the world and first-person model have
 * been submitted and before the screen overlays and HUD.
 *
 * <p>Render thread only. It goes through {@code RenderSystem} and
 * {@code Tesselator} rather than raw GL so the surrounding vanilla render
 * state stays valid.
 */
public class TextureOverlay {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TextureOverlay.class);
    
    private final Minecraft client;
    private boolean warnedUnported;
    
    public TextureOverlay() {
        this.client = Minecraft.getInstance();
        LOGGER.info("TextureOverlay initialized");
    }
    
    /**
     * Renders a texture as a full-screen overlay.
     * 
     * @param textureId The GL texture ID to render as full-screen overlay
     */
    public void render(int textureId) {
        if (textureId <= 0) {
            return;
        }
        
        if (client == null || client.getWindow() == null) {
            return;
        }
        
        try {
            renderTextureOverlay(textureId);
        } catch (Exception e) {
            LOGGER.error("Error rendering texture overlay", e);
        }
    }
    
    private void renderTextureOverlay(int texId) {
        // TODO(port-26.3: window): the immediate-mode RenderSystem/Tesselator blit this
        // used is gone in 26.3, where drawing goes through render pipelines and the
        // render-state extraction pass. The presentation of the Remix texture has to
        // be rebuilt on that path; until then nothing is drawn.
        if (!warnedUnported) {
            warnedUnported = true;
            LOGGER.warn("TextureOverlay is not ported to Minecraft 26.3; the Remix image is not presented");
        }
    }

    /**
     * Cleans up resources.
     */
    public void shutdown() {
        LOGGER.info("TextureOverlay shutdown");
    }
}
