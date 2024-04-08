package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ShowdownInterpreter;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import kotlin.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShowdownInterpreter.class)
public class ShowdownInterpreterMixin {

    @Inject(method = "handleSwitchInstruction", at = @At("TAIL"), remap = false)
    private void injectHandleSwitchInstruction(
        PokemonBattle battle,
        BattleActor battleActor,
        BattleMessage publicMessage,
        BattleMessage privateMessage,
        CallbackInfo ci
    ) {
        Pair<String, String> pnxAndPokemonID = publicMessage.pnxAndUuid(0);
        if (pnxAndPokemonID == null) return;
        BattlePokemon battlePokemon = battle.getBattlePokemon(
            pnxAndPokemonID.component1(), pnxAndPokemonID.component2()
        );
        battlePokemon.setWillBeSwitchedIn(false);
    }

}
