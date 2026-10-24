/*
 * SPDX-FileCopyrightText: Copyright (c) 2026 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.nvidia.rtxremix.minecraft.entity;

import com.nvidia.rtxremix.minecraft.block.CopperBeaconBlockEntity;
import com.nvidia.rtxremix.minecraft.block.CopperBeaconPose;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nvidia.rtxremix.minecraft.RtxRemixCommonMod;
import org.joml.Quaternionf;

/** Renders the independently rotating yoke and lamp head above the static mount. */
public final class CopperBeaconBlockEntityRenderer
        implements BlockEntityRenderer<CopperBeaconBlockEntity, BlockEntityRenderState> {

    public static final ModelLayerLocation BODY_MODEL_LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(RtxRemixCommonMod.MOD_ID, "copper_beacon"), "body");
    public static final ModelLayerLocation LENS_MODEL_LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(RtxRemixCommonMod.MOD_ID, "copper_beacon"), "lens");
    static final Identifier LENS_SPRITE_ID =
            Identifier.fromNamespaceAndPath(RtxRemixCommonMod.MOD_ID, "block/copper_beacon_lens");
    static final Identifier UNLIT_LENS_SPRITE_ID =
            Identifier.fromNamespaceAndPath(RtxRemixCommonMod.MOD_ID, "block/copper_beacon_lens_unlit");
    private static final Material LENS_TEXTURE = new Material(LENS_SPRITE_ID);
    private static final Material UNLIT_LENS_TEXTURE = new Material(UNLIT_LENS_SPRITE_ID);

    private final ModelPart yoke;
    private final ModelPart head;
    private final ModelPart lens;
    private final CopperBeaconPose.Pose pose = new CopperBeaconPose.Pose();

    public CopperBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart bodyRoot = context.bakeLayer(BODY_MODEL_LAYER);
        yoke = bodyRoot.getChild("yoke");
        head = yoke.getChild("head");
        lens = context.bakeLayer(LENS_MODEL_LAYER).getChild("lens");
    }

    public static LayerDefinition getBodyTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();
        PartDefinition yokeData = root.addOrReplaceChild(
                "yoke",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0f, -6.0f, -1.0f, 3.0f, 2.0f, 3.0f)
                        .texOffs(0, 0).addBox(0.0f, -6.0f, -1.0f, 3.0f, 2.0f, 3.0f)
                        .texOffs(0, 0).addBox(-5.0f, -5.0f, -1.0f, 2.0f, 8.0f, 3.0f)
                        .texOffs(0, 0).addBox(3.0f, -5.0f, -1.0f, 2.0f, 8.0f, 3.0f),
                PartPose.offset(8.0f, 10.0f, 8.0f));
        yokeData.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0f, -4.0f, -3.0f, 4.0f, 8.0f, 1.0f)
                        .texOffs(0, 0).addBox(0.0f, -4.0f, -3.0f, 4.0f, 8.0f, 1.0f)
                        .texOffs(0, 0).addBox(-4.0f, -4.0f, 3.0f, 4.0f, 8.0f, 1.0f)
                        .texOffs(0, 0).addBox(0.0f, -4.0f, 3.0f, 4.0f, 8.0f, 1.0f)
                        .texOffs(0, 0).addBox(-4.0f, -3.0f, -2.0f, 1.0f, 6.0f, 5.0f)
                        .texOffs(0, 0).addBox(3.0f, -3.0f, -2.0f, 1.0f, 6.0f, 5.0f)
                        .texOffs(0, 0).addBox(-3.0f, -4.0f, -2.0f, 3.0f, 1.0f, 5.0f)
                        .texOffs(0, 0).addBox(0.0f, -4.0f, -2.0f, 3.0f, 1.0f, 5.0f)
                        .texOffs(0, 0).addBox(-3.0f, 3.0f, -2.0f, 3.0f, 1.0f, 5.0f)
                        .texOffs(0, 0).addBox(0.0f, 3.0f, -2.0f, 3.0f, 1.0f, 5.0f)
                        .texOffs(0, 0).addBox(-6.0f, -1.0f, -1.0f, 2.0f, 2.0f, 3.0f)
                        .texOffs(0, 0).addBox(4.0f, -1.0f, -1.0f, 2.0f, 2.0f, 3.0f)
                        .texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 4.0f, 1.0f, 1.0f)
                        .texOffs(0, 0).addBox(0.0f, -4.0f, -4.0f, 4.0f, 1.0f, 1.0f)
                        .texOffs(0, 0).addBox(-4.0f, 3.0f, -4.0f, 4.0f, 1.0f, 1.0f)
                        .texOffs(0, 0).addBox(0.0f, 3.0f, -4.0f, 4.0f, 1.0f, 1.0f)
                        .texOffs(0, 0).addBox(-4.0f, -3.0f, -4.0f, 1.0f, 6.0f, 1.0f)
                        .texOffs(0, 0).addBox(3.0f, -3.0f, -4.0f, 1.0f, 6.0f, 1.0f),
                PartPose.ZERO);
        return LayerDefinition.create(modelData, 16, 16);
    }

    public static LayerDefinition getLensTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();
        root.addOrReplaceChild(
                "lens",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0f, -3.0f, -4.01f, 6.0f, 6.0f, 0.0f),
                PartPose.offset(8.0f, 10.0f, 8.0f));
        return LayerDefinition.create(modelData, 6, 6);
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(
            BlockEntityRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera) {
        // TODO(port-26.3: entities): 26.3 splits block entity drawing into extractRenderState
        // and submit, with model parts handed to the SubmitNodeCollector. The articulated
        // draw (mounting rotation, applyArticulation, copper UV strip, tinted lens) has to
        // be re-expressed as submitModelPart calls over a state that carries the animation
        // angles, so nothing is submitted yet. The pose maths below is unchanged.
    }

    static Quaternionf mountingRotation(Direction upDirection, Direction forwardDirection) {
        return CopperBeaconPose.mountingRotation(
                upDirection, forwardDirection, new Quaternionf());
    }

    static void applyArticulation(
            ModelPart yoke,
            ModelPart head,
            ModelPart lens,
            CopperBeaconPose.Pose pose) {
        yoke.yRot = pose.modelYawRadians();
        yoke.xRot = 0.0f;
        head.yRot = 0.0f;
        head.xRot = pose.modelPitchRadians();
        lens.yRot = pose.modelYawRadians();
        lens.xRot = pose.modelPitchRadians();
    }

    static Material lensTexture(boolean active, float intensityMultiplier) {
        return active && intensityMultiplier > 0.0f
                ? LENS_TEXTURE
                : UNLIT_LENS_TEXTURE;
    }

    /**
     * Keeps resource-pack details along the texture edges while excluding all
     * four corners, where stylized copper textures commonly place rivets.
     */
    static float copperTextureV(float v) {
        return 0.25f + v * 0.5f;
    }
}
