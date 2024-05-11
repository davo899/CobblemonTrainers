package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.drop.DropTable;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import kotlin.ranges.IntRange;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DropTable.class)
public abstract class DropTableMixin {

    @Inject(method = "drop", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectDrop(
        LivingEntity entity,
        ServerWorld world,
        Vec3d pos,
        ServerPlayerEntity player,
        IntRange amount,
        CallbackInfo ci
    ) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return;
        if (TrainerPokemon.IS_TRAINER_OWNED.contains(pokemonEntity.getPokemon().getUuid())) ci.cancel();
    }

}
