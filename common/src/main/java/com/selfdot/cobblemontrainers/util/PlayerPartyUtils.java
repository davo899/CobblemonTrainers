package com.selfdot.cobblemontrainers.util;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PlayerPartyUtils {
    public static boolean isUnderPartyMaximumLevel(ServerPlayerEntity player, Trainer trainer) {
        try {
            assertValidPartyMaximumLevel(trainer);
            assertNotEmptyPlayerParty(player);
            return isAllPokemonUnderPartyMaximumLevel(player, trainer);
        } catch (PartyMaximumLevelNotValidException e) {
            player.sendMessage((Text.literal(
                    "Trainer does not have valid partyMaximumLevel. " +
                    "Please check your config file")));
            return true;
        } catch (PartyEmptyException e) {
            player.sendMessage((Text.literal(
                    "You cannot start battle with empty party")));
            return false;
        }
    }

    private static void assertValidPartyMaximumLevel(Trainer trainer) throws PartyMaximumLevelNotValidException {
        if(!hasValidPartyMaximumLevel(trainer)) {
            throw(new PartyMaximumLevelNotValidException(trainer));
        }
    }

    private static boolean hasValidPartyMaximumLevel(Trainer trainer) {
        int partyMaximumLevel = trainer.getPartyMaximumLevel();
        return partyMaximumLevel > 0 && partyMaximumLevel <= 100;
    }

    private static void assertNotEmptyPlayerParty(ServerPlayerEntity player) throws PartyEmptyException {
        if(getPlayerParty(player).size() == 0) {
            throw(new PartyEmptyException(player));
        }
    }

    private static boolean isAllPokemonUnderPartyMaximumLevel(ServerPlayerEntity player, Trainer trainer) {
        int partyMaximumLevel = trainer.getPartyMaximumLevel();
        List<Integer> partyLevels = getPartyLevels(player);
        return partyLevels.stream().allMatch(level -> level <= partyMaximumLevel);
    }

    private static List<Integer> getPartyLevels(ServerPlayerEntity player) {
        PlayerPartyStore party = getPlayerParty(player);
        List<Integer> levels = new ArrayList<>();
        for(Pokemon pokemon : party) {
            levels.add(pokemon.getLevel());
        }
        return levels;
    }

    private static PlayerPartyStore getPlayerParty(ServerPlayerEntity player) {
        return Cobblemon.INSTANCE.getStorage().getParty(player);
    }

    public static class PartyMaximumLevelNotValidException extends Exception {
        PartyMaximumLevelNotValidException(Trainer trainer) {

        }
    }

    public static class PartyEmptyException extends Exception {
        PartyEmptyException(ServerPlayerEntity player) {

        }
    }
}