package com.selfdot.cobblemontrainers.util;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class PokemonUtility {

    public static ItemStack pokemonToInfoItem(Pokemon pokemon) {
        String moveOne = pokemon.getMoveSet().getMoves().size() >= 1 ? pokemon.getMoveSet().get(0).getDisplayName().getString() : "Empty";
        String moveTwo = pokemon.getMoveSet().getMoves().size() >= 2 ? pokemon.getMoveSet().get(1).getDisplayName().getString() : "Empty";
        String moveThree = pokemon.getMoveSet().getMoves().size() >= 3 ? pokemon.getMoveSet().get(2).getDisplayName().getString() : "Empty";
        String moveFour = pokemon.getMoveSet().getMoves().size() >= 4 ? pokemon.getMoveSet().get(3).getDisplayName().getString() : "Empty";

        ItemStack itemstack = new ItemBuilder(PokemonItem.from(pokemon, 1))
            .hideAdditional()
            .addLore(new Text[]{
                pokemon.getCaughtBall().item().getName().copy().setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.DARK_GRAY)),
                Text.literal("Gender: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getGender())).formatted(Formatting.WHITE)),
                Text.literal("Level: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getLevel())).formatted(Formatting.WHITE)),
                Text.literal("Nature: ").formatted(Formatting.YELLOW).append(LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).formatted(Formatting.WHITE)),
                Text.literal("Ability: ").formatted(Formatting.GOLD).append(LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).formatted(Formatting.WHITE)),
                Text.literal("IVs: ").formatted(Formatting.LIGHT_PURPLE),
                Text.literal("  HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE))
                    .append(Text.literal("  Atk: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)))
                    .append(Text.literal("  Def: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE))),
                Text.literal("  SpAtk: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE))
                    .append(Text.literal("  SpDef: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)))
                    .append(Text.literal("  Spd: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE))),

                Text.literal("EVs: ").formatted(Formatting.DARK_AQUA),
                Text.literal("  HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE))
                    .append(Text.literal("  Atk: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)))
                    .append(Text.literal("  Def: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE))),
                Text.literal("  SpAtk: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE))
                    .append(Text.literal("  SpDef: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)))
                    .append(Text.literal("  Spd: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE))),
                Text.literal("Moves: ").formatted(Formatting.DARK_GREEN),
                Text.literal(" ").append(Text.literal(moveOne).formatted(Formatting.WHITE)),
                Text.literal(" ").append(Text.literal(moveTwo).formatted(Formatting.WHITE)),
                Text.literal(" ").append(Text.literal(moveThree).formatted(Formatting.WHITE)),
                Text.literal(" ").append(Text.literal(moveFour).formatted(Formatting.WHITE)),
                Text.literal("Form: ").formatted(Formatting.GOLD).append(pokemon.getForm().getName())
            })
            .setCustomName(
                pokemon.getShiny() ?
                    pokemon.getDisplayName().formatted(Formatting.GRAY).append(Text.literal(" ★").formatted(Formatting.GOLD)) :
                    pokemon.getDisplayName().formatted(Formatting.GRAY)
            )
            .build();
        return itemstack;
    }

    public static ItemStack pokemonToItem(Pokemon pokemon) {
        ItemStack itemstack = new ItemBuilder(PokemonItem.from(pokemon, 1))
            .hideAdditional()
            .setCustomName(
                pokemon.getShiny() ?
                    pokemon.getDisplayName().formatted(Formatting.GRAY).append(Text.literal(" ★").formatted(Formatting.GOLD)) :
                    pokemon.getDisplayName().formatted(Formatting.GRAY)
            )
            .build();
        return itemstack;
    }

    public static BattleStartResult startBattle(
        ServerPlayerEntity player,
        Trainer trainer,
        BattleFormat battleFormat
    ) {
        BattleActor playerActor = new PlayerBattleActor(
            player.getUuid(), Cobblemon.INSTANCE.getStorage().getParty(player).toBattleTeam(
            false, true, null
        )
        );
        BattleActor trainerActor = new TrainerBattleActor(
            trainer.getName(), UUID.randomUUID(), trainer.getBattleTeam(), new RandomBattleAI()
        );

        ErroredBattleStart errors = new ErroredBattleStart();

        if (playerActor.getPokemonList().size() < battleFormat.getBattleType().getSlotsPerActor()) {
            errors.getParticipantErrors().get(playerActor).add(BattleStartError.Companion.insufficientPokemon(
                player,
                battleFormat.getBattleType().getSlotsPerActor(),
                playerActor.getPokemonList().size()
            ));
        }

        if (Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayer(player) != null) {
            errors.getParticipantErrors().get(playerActor).add(BattleStartError.Companion.alreadyInBattle(player));
        }

        if (errors.isEmpty()) {
            return new SuccessfulBattleStart(Cobblemon.INSTANCE.getBattleRegistry().startBattle(
                BattleFormat.Companion.getGEN_9_SINGLES(),
                new BattleSide(playerActor),
                new BattleSide(trainerActor)
            ));
        }
        return errors;
    }

}
