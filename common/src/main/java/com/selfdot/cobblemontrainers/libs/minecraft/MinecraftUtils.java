package com.selfdot.cobblemontrainers.libs.minecraft;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.util.Formatting.*;

public class MinecraftUtils {

    public static void spawnFireworkExplosion(
        Vec3d position,
        World world,
        DyeColor primaryColor,
        FireworkRocketItem.Type type,
        DyeColor fadeColor
    ) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
        NbtCompound nbtCompound = itemStack2.getOrCreateSubNbt("Explosion");
        nbtCompound.putIntArray("Colors", List.of(primaryColor.getFireworkColor()));
        nbtCompound.putIntArray("FadeColors", List.of(fadeColor.getFireworkColor()));
        nbtCompound.putByte("Type", (byte)type.getId());
        nbtCompound.putByte("Flicker", (byte)1);
        NbtCompound nbtCompound2 = itemStack.getOrCreateSubNbt("Fireworks");
        NbtList nbtList = new NbtList();
        NbtCompound nbtCompound3 = itemStack2.getSubNbt("Explosion");
        if (nbtCompound3 != null) nbtList.add(nbtCompound3);
        if (!nbtList.isEmpty()) nbtCompound2.put("Explosions", nbtList);

        FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
            world,
            position.getX(),
            position.getY(),
            position.getZ(),
            itemStack
        );
        world.spawnEntity(fireworkRocketEntity);
        world.sendEntityStatus(fireworkRocketEntity, (byte)17);
        fireworkRocketEntity.discard();
    }

    public static String colourize(String string) {
        return string
            .replace("&0", String.valueOf(BLACK)).replace("&1", String.valueOf(DARK_BLUE))
            .replace("&2", String.valueOf(DARK_GREEN)).replace("&3", String.valueOf(DARK_AQUA))
            .replace("&4", String.valueOf(DARK_RED)).replace("&5", String.valueOf(DARK_PURPLE))
            .replace("&6", String.valueOf(GOLD)).replace("&7", String.valueOf(GRAY))
            .replace("&8", String.valueOf(DARK_GRAY)).replace("&9", String.valueOf(BLUE))
            .replace("&a", String.valueOf(GREEN)).replace("&b", String.valueOf(AQUA))
            .replace("&c", String.valueOf(RED)).replace("&d", String.valueOf(LIGHT_PURPLE))
            .replace("&e", String.valueOf(YELLOW)).replace("&f", String.valueOf(WHITE))
            .replace("&k", String.valueOf(OBFUSCATED)).replace("&l", String.valueOf(BOLD))
            .replace("&m", String.valueOf(STRIKETHROUGH)).replace("&n", String.valueOf(UNDERLINE))
            .replace("&o", String.valueOf(ITALIC)).replace("&r", String.valueOf(RESET))
            .replace("&A", String.valueOf(GREEN)).replace("&B", String.valueOf(AQUA))
            .replace("&C", String.valueOf(RED)).replace("&D", String.valueOf(LIGHT_PURPLE))
            .replace("&E", String.valueOf(YELLOW)).replace("&F", String.valueOf(WHITE))
            .replace("&K", String.valueOf(OBFUSCATED)).replace("&L", String.valueOf(BOLD))
            .replace("&M", String.valueOf(STRIKETHROUGH)).replace("&N", String.valueOf(UNDERLINE))
            .replace("&O", String.valueOf(ITALIC)).replace("&R", String.valueOf(RESET));

    }

}
