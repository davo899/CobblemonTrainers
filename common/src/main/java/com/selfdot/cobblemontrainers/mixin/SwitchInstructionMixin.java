package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.SwitchInstruction;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import kotlin.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwitchInstruction.class)
public abstract class SwitchInstructionMixin {

    @Shadow(remap = false)
    public abstract BattleMessage getPublicMessage();

    @Inject(method = "invoke", at = @At("TAIL"), remap = false)
    private void injectInvoke(PokemonBattle battle, CallbackInfo ci) {
        Pair<String, String> pnxAndPokemonID = getPublicMessage().pnxAndUuid(0);
        if (pnxAndPokemonID == null) return;
        BattlePokemon battlePokemon = battle.getBattlePokemon(
            pnxAndPokemonID.component1(), pnxAndPokemonID.component2()
        );
        battlePokemon.setWillBeSwitchedIn(false);
    }

}
