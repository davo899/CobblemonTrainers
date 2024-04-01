package com.selfdot.cobblemontrainers.util;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.battle.TrainerMaximumLevelError;
import com.selfdot.cobblemontrainers.battle.TrainersNotDefeatedError;
import com.selfdot.cobblemontrainers.trainer.EntityBackerTrainerBattleActor;
import com.selfdot.cobblemontrainers.trainer.Generation5AI;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerBattleListener;
import kotlin.Unit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.stream.Collectors;

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

    private static BattleStartResult createTrainerBattle(
        ServerPlayerEntity player,
        Trainer trainer,
        LivingEntity trainerEntity,
        BattleFormat battleFormat
    ) {
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        UUID leadingPokemon = null;
        for (Pokemon pokemon : party) {
            if (!pokemon.isFainted()) {
                leadingPokemon = pokemon.getUuid();
                break;
            }
        }

        BattleActor playerActor = new PlayerBattleActor(
            player.getUuid(), party.toBattleTeam(false, true, leadingPokemon)
        );
        BattleActor trainerActor = trainerEntity == null ?
            new TrainerBattleActor(
                trainer.getName(), UUID.randomUUID(), trainer.getBattleTeam(), new Generation5AI()
            ) :
            new EntityBackerTrainerBattleActor(
                trainer.getName(), trainerEntity, UUID.randomUUID(), trainer.getBattleTeam(), new Generation5AI()
            );

        ErroredBattleStart errors = new ErroredBattleStart();
        Set<BattleStartError> playerErrors = errors.getParticipantErrors().get(playerActor);

        for (Pokemon pokemon : party) {
            if (pokemon.getLevel() > trainer.getPartyMaximumLevel()) {
                playerErrors.add(new TrainerMaximumLevelError(trainer.getPartyMaximumLevel()));
                break;
            }
        }

        List<String> notDefeatedTrainers = trainer.getDefeatRequiredTrainers().stream()
            .filter(mustDefeat -> !CobblemonTrainers.INSTANCE.getTRAINER_WIN_TRACKER().hasBeaten(player, mustDefeat))
            .toList();
        if (!notDefeatedTrainers.isEmpty()) playerErrors.add(new TrainersNotDefeatedError(notDefeatedTrainers));

        if (playerActor.getPokemonList().size() < battleFormat.getBattleType().getSlotsPerActor()) {
            playerErrors.add(BattleStartError.Companion.insufficientPokemon(
                player,
                battleFormat.getBattleType().getSlotsPerActor(),
                playerActor.getPokemonList().size()
            ));
        }

        if (Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayer(player) != null) {
            playerErrors.add(BattleStartError.Companion.alreadyInBattle(player));
        }

        if (errors.isEmpty()) {
            return Cobblemon.INSTANCE.getBattleRegistry().startBattle(
                BattleFormat.Companion.getGEN_9_SINGLES(),
                new BattleSide(playerActor),
                new BattleSide(trainerActor),
                false
            );
        }
        return errors;
    }

    public static final Set<UUID> IN_TRAINER_BATTLE = new HashSet<>();

    public static void startTrainerBattle(ServerPlayerEntity player, Trainer trainer, LivingEntity trainerEntity) {
        if (IN_TRAINER_BATTLE.contains(player.getUuid())) return;
        if (trainer.canOnlyBeatOnce() &&
            CobblemonTrainers.INSTANCE.getTRAINER_WIN_TRACKER().hasBeaten(player, trainer)
        ) {
            player.sendMessage(Text.literal(Formatting.RED + "You have already beaten this trainer!"));
            return;
        }

        long cooldownMillis = CobblemonTrainers.INSTANCE.getTRAINER_COOLDOWN_TRACKER()
            .remainingCooldownMillis(player, trainer);
        if (cooldownMillis > 0) {
            player.sendMessage(Text.literal(
                Formatting.RED + "You can't battle this trainer for another " +
                    (cooldownMillis / 1000) + " seconds"
            ));
            return;
        }

        PokemonUtility.createTrainerBattle(player, trainer, trainerEntity, BattleFormat.Companion.getGEN_9_SINGLES())
            .ifErrored(error -> {
                error.sendTo(player, t -> t);
                return Unit.INSTANCE;
            })
            .ifSuccessful(battle -> {
                CobblemonTrainers.INSTANCE.getTRAINER_COOLDOWN_TRACKER().onBattleStart(player, trainer);
                TrainerBattleListener.getInstance().addOnBattleVictory(battle, trainer);
                TrainerBattleListener.getInstance().addOnBattleLoss(battle, trainer.getLossCommand());
                IN_TRAINER_BATTLE.add(player.getUuid());
                return Unit.INSTANCE;
            });
    }

}
