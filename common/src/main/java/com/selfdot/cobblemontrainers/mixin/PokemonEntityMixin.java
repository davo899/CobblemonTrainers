package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public abstract class PokemonEntityMixin extends LivingEntity {

    protected PokemonEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private boolean cobblemonTrainers$isTrainerOwned() {
        return TrainerPokemon.IS_TRAINER_OWNED.contains(getPokemon().getUuid());
    }

    @Shadow
    public abstract Pokemon getPokemon();

    @Inject(method = "shouldSave", at = @At("HEAD"), cancellable = true)
    private void injectShouldSave(CallbackInfoReturnable<Boolean> cir) {
        if (cobblemonTrainers$isTrainerOwned()) cir.setReturnValue(false);
    }

    @Inject(method = "updatePostDeath", at = @At("HEAD"))
    private void injectUpdatePostDeath(CallbackInfo ci) {
        if (deathTime != 59 || getWorld().isClient) return;
        if (
            cobblemonTrainers$isTrainerOwned()// &&
            //getWorld().getGameRules().getBoolean(CobblemonGameRules.DO_POKEMON_LOOT)
        ) {
            TrainerPokemon.MUST_REENABLE_LOOT_GAMERULE.add(getPokemon().getUuid());
            //getWorld().getGameRules().get(CobblemonGameRules.DO_POKEMON_LOOT).set(false, getServer());
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void injectRemove(RemovalReason reason, CallbackInfo ci) {
        TrainerPokemon.IS_TRAINER_OWNED.remove(getPokemon().getUuid());
        if (TrainerPokemon.MUST_REENABLE_LOOT_GAMERULE.contains(getPokemon().getUuid())) {
            TrainerPokemon.MUST_REENABLE_LOOT_GAMERULE.remove(getPokemon().getUuid());
            //getWorld().getGameRules().get(CobblemonGameRules.DO_POKEMON_LOOT).set(true, getServer());
        }
    }

}
