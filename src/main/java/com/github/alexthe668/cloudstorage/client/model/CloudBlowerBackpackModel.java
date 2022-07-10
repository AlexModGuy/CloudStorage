package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public class CloudBlowerBackpackModel extends AdvancedEntityModel {
	private final AdvancedModelBox brace1;
	private final AdvancedModelBox brace2;
	private final AdvancedModelBox pack;

	public CloudBlowerBackpackModel() {
		texHeight = 64;
		texWidth = 64;

		brace1 = new AdvancedModelBox(this);
		brace1.setRotationPoint(0.0F, 24.0F, 0.0F);
		brace1.setTextureOffset(12, 0).addBox(4.0F, -13.0F, 10.0F, 2.0F, 6.0F, 0.0F, 0.0F, false);
		brace1.setTextureOffset(7, 9).addBox(4.0F, -7.0F, 5.0F, 2.0F, 0.0F, 5.0F, 0.0F, false);
		brace1.setTextureOffset(7, 14).addBox(4.0F, -13.0F, 5.0F, 2.0F, 0.0F, 5.0F, 0.0F, false);

		brace2 = new AdvancedModelBox(this);
		brace2.setRotationPoint(-8.0F, 16.0F, 8.0F);
		brace2.setTextureOffset(12, 0).addBox(2.0F, -5.0F, 2.0F, 2.0F, 6.0F, 0.0F, 0.0F, false);
		brace2.setTextureOffset(7, 9).addBox(2.0F, 1.0F, -3.0F, 2.0F, 0.0F, 5.0F, 0.0F, false);
		brace2.setTextureOffset(7, 14).addBox(2.0F, -5.0F, -3.0F, 2.0F, 0.0F, 5.0F, 0.0F, false);

		pack = new AdvancedModelBox(this);
		pack.setRotationPoint(0.0F, 24.0F, 0.0F);
		pack.setTextureOffset(4, 33).addBox(-7.0F, -15.0F, -6.0F, 14.0F, 15.0F, 11.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(brace1, brace2, pack);
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(brace1, brace2, pack);
	}

	@Override
	public void setupAnim(Entity entity, float v, float v1, float v2, float v3, float v4) {
		this.resetToDefaultPose();
	}
}