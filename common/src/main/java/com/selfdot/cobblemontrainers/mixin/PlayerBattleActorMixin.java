package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(PlayerBattleActor.class)
public abstract class PlayerBattleActorMixin extends BattleActor {

    public PlayerBattleActorMixin(@NotNull UUID uuid, @NotNull List<BattlePokemon> pokemonList) {
        super(uuid, pokemonList);
    }

    @Inject(method = "awardExperience", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectAwardExperience(BattlePokemon battlePokemon, int experience, CallbackInfo ci) {
        if (CobblemonTrainers.INSTANCE.getConfig().isXpDisabled() && getBattle().isPvN()) ci.cancel();
    }

}
