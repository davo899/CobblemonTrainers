package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.selfdot.cobblemontrainers.trainer.EntityBackerTrainerBattleActor;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PokemonBattle.class)
public abstract class PokemonBattleMixin {

    @Shadow(remap = false)
    public abstract Iterable<BattleActor> getActors();

    @Inject(method = "end", at = @At("HEAD"), remap = false)
    private void injectEnd(CallbackInfo ci) {
        getActors().forEach(actor -> {
            if (actor instanceof EntityBackerTrainerBattleActor trainerActor) {
                List<ActiveBattlePokemon> activeBattlePokemonList = trainerActor.getActivePokemon();
                if (activeBattlePokemonList.isEmpty()) return;
                BattlePokemon battlePokemon = activeBattlePokemonList.get(0).getBattlePokemon();
                if (battlePokemon == null) return;
                PokemonEntity pokemonEntity = battlePokemon.getEntity();
                if (pokemonEntity == null) return;
                pokemonEntity.recallWithAnimation();
            }

            actor.getPlayerUUIDs().forEach(PokemonUtility.IN_TRAINER_BATTLE::remove);
        });
    }

}
