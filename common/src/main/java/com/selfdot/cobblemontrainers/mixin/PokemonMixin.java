package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Pokemon.class)
public abstract class PokemonMixin {

    @Shadow
    public abstract UUID getUuid();

    @Inject(method = "sendOut", at = @At("RETURN"), remap = false)
    private void injectSendOut(
        ServerWorld level,
        Vec3d position,
        Function1<? super PokemonEntity, Unit> mutation,
        CallbackInfoReturnable<PokemonEntity> cir
    ) {
        if (TrainerPokemon.IS_TRAINER_OWNED.contains(getUuid())) {
            cir.getReturnValue().setOwnerUuid(UUID.randomUUID());
            //event.getPokemonEntity().getUnbattleable().set(true);
        }
    }

}
