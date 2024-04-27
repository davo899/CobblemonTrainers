package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ShowdownInterpreter;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShowdownInterpreter.class)
public class ShowdownInterpreterMixin {

    @Inject(method = "handleSwitchInstruction", at = @At("TAIL"), remap = false)
    private void injectHandleSwitchInstruction(
        PokemonBattle battle, BattleActor battleActor, String publicMessage, String privateMessage, CallbackInfo ci
    ) {
        String pnx = publicMessage.split("\\|")[2].split(":")[0];
        BattlePokemon battlePokemon = battle.getActorAndActiveSlotFromPNX(pnx).getSecond().getBattlePokemon();
        if (battlePokemon == null) return;
        battlePokemon.setWillBeSwitchedIn(false);
    }

}
