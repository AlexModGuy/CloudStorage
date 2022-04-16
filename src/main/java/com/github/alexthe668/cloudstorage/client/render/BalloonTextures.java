package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BalloonTextures {
    public static final ResourceLocation STRING_TIE = new ResourceLocation("cloudstorage:textures/entity/balloon/balloon_string.png");

    public static final ResourceLocation BALLOON = new ResourceLocation("cloudstorage:textures/entity/balloon/balloon.png");
    public static final ResourceLocation BALLOON_SHEEN = new ResourceLocation("cloudstorage:textures/entity/balloon/balloon_sheen.png");
    public static final ResourceLocation POPPED = new ResourceLocation("cloudstorage:textures/entity/balloon/balloon_popped.png");
    public static final ResourceLocation LIGHTNING = new ResourceLocation("cloudstorage:textures/entity/balloon/balloon_lightning.png");
    public static final ResourceLocation LOOT = new ResourceLocation("cloudstorage:textures/entity/balloon/loot_balloon.png");
    private static final Map<BalloonFace, ResourceLocation> FACE_TEXTURES = new HashMap<>();

    public static ResourceLocation getTextureForFace(BalloonFace face) {
        if (FACE_TEXTURES.containsKey(face)) {
            return FACE_TEXTURES.get(face);
        } else {
            ResourceLocation res = new ResourceLocation("cloudstorage:textures/entity/balloon/balloon_face_" + face.getName() + ".png");
            FACE_TEXTURES.put(face, res);
            return res;
        }
    }
}
