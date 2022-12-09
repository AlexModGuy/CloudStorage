package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, CloudStorage.MODID);
    public static final RegistryObject<Item> SPAWN_EGG_BADLOON = DEF_REG.register("spawn_egg_badloon", () -> new ForgeSpawnEggItem(CSEntityRegistry.BADLOON, 0XE72929, 0XEEE7E1, new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_BLOVIATOR = DEF_REG.register("spawn_egg_bloviator", () -> new ForgeSpawnEggItem(CSEntityRegistry.BLOVIATOR, 0XDFF3F7, 0X24AFFF, new Item.Properties()));
    public static final RegistryObject<Item> BALLOON_BIT = DEF_REG.register("balloon_bit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GUIDE_BOOK = DEF_REG.register("guide_book", () -> new GuideBookItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BALLOON_INVENTORY = DEF_REG.register("balloon_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BALLOON = DEF_REG.register("balloon", () -> new BalloonItem());
    public static final RegistryObject<Item> ANGRY_CLOUD_IN_A_BOTTLE = DEF_REG.register("angry_cloud_in_a_bottle", () -> new CloudBottleItem(false));
    public static final RegistryObject<Item> HAPPY_CLOUD_IN_A_BOTTLE = DEF_REG.register("happy_cloud_in_a_bottle", () -> new CloudBottleItem(true));
    public static final RegistryObject<Item> BALLOON_BUDDY_INVENTORY = DEF_REG.register("balloon_buddy_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BALLOON_BUDDY = DEF_REG.register("balloon_buddy", () -> new BalloonBuddyItem());
    public static final RegistryObject<Item> BALLOON_ARROW = DEF_REG.register("balloon_arrow", () -> new BalloonArrowItem());
    public static final RegistryObject<Item> PROPELLER_HAT = DEF_REG.register("propeller_hat", () -> new PropellerHatItem());
    public static final RegistryObject<Item> COTTON_CANDY = DEF_REG.register("cotton_candy", () -> new Item(new Item.Properties().food((new FoodProperties.Builder()).nutrition(4).saturationMod(0.15F).fast().build())));
    public static final RegistryObject<Item> CLOUD_BLOWER_INVENTORY = DEF_REG.register("cloud_blower_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLOUD_BLOWER = DEF_REG.register("cloud_blower", () -> new CloudBlowerItem());
    public static final RegistryObject<Item> MUSIC_DISC_DRIFT = DEF_REG.register("music_disc_drift", () -> new RecordItem(14, CSSoundRegistry.MUSIC_DISC_DRIFT, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 146 * 20));

    public static void registerDispenserBehavior(){
        DispenserBlock.registerBehavior(BALLOON.get(), BalloonDispenseBehavior.INSTANCE);
        DispenserBlock.registerBehavior(BALLOON_ARROW.get(), new AbstractProjectileDispenseBehavior() {

            public ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.getLevel();
                Position position = DispenserBlock.getDispensePosition(source);
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                Projectile projectile = this.getProjectile(level, position, stack);
                projectile.shoot((double)direction.getStepX(), (double)((float)direction.getStepY() + 0.1F), (double)direction.getStepZ(), this.getPower(), this.getUncertainty());
                level.addFreshEntity(projectile);
                BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(level);
                balloon.copyPosition(projectile);
                balloon.setDeltaMovement(projectile.getDeltaMovement());
                balloon.setChildId(projectile.getUUID());
                balloon.setBalloonColor(BalloonArrowItem.getBalloonColor(stack));
                balloon.setStringLength(0);
                balloon.setArrow(true);
                level.addFreshEntity(balloon);
                stack.shrink(1);
                return stack;
            }

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                Arrow arrow = new Arrow(worldIn, position.x(), position.y(), position.z());
                arrow.setEffectsFromItem(stackIn);
                arrow.pickup = Arrow.Pickup.ALLOWED;
                return arrow;
            }
        });
    }
}
