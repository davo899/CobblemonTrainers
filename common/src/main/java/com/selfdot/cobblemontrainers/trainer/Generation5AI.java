package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.pokemon.status.Statuses;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer;
import com.google.common.collect.Streams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class Generation5AI implements BattleAI {

    private static final Random RANDOM = new Random();
    private static final double SUPER_EFFECTIVE = 2;
    private static final double NOT_VERY_EFFECTIVE = 0.5;
    private static final double IMMUNE = 0;
    private static final Map<ElementalType, Map<ElementalType, Double>> typeChart = new HashMap<>();

    private static double typeEffectiveness(ElementalType attacker, ElementalType defender) {
        if (typeChart.get(defender).containsKey(attacker)) return typeChart.get(defender).get(attacker);
        return 1;
    }

    private static double typeEffectiveness(ElementalType attacker, ElementalType defender, Ability defenderAbility) {
        String defenderAbilityId = defenderAbility.getDisplayName();
        if (attacker.equals(ElementalTypes.INSTANCE.getWATER())) {
            if (
                defenderAbilityId.equals("cobblemon.ability.stormdrain") ||
                defenderAbilityId.equals("cobblemon.ability.waterabsorb") ||
                defenderAbilityId.equals("cobblemon.ability.dryskin")
            ) {
                return 0;
            }
        } else if (attacker.equals(ElementalTypes.INSTANCE.getELECTRIC())) {
            if (
                defenderAbilityId.equals("cobblemon.ability.voltabsorb") ||
                defenderAbilityId.equals("cobblemon.ability.lightningrod") ||
                defenderAbilityId.equals("cobblemon.ability.motordrive")
            ) {
                return 0;
            }
        } else if (attacker.equals(ElementalTypes.INSTANCE.getGROUND())) {
            if (
                defenderAbilityId.equals("cobblemon.ability.levitate") ||
                defenderAbilityId.equals("cobblemon.ability.eartheater")
            ) {
                return 0;
            }
        } else if (attacker.equals(ElementalTypes.INSTANCE.getFIRE())) {
            if (
                defenderAbilityId.equals("cobblemon.ability.wellbakedbody") ||
                defenderAbilityId.equals("cobblemon.ability.flashfire")
            ) {
                return 0;
            }
        } else if (attacker.equals(ElementalTypes.INSTANCE.getGRASS())) {
            if (defenderAbilityId.equals("cobblemon.ability.sapsipper")) {
                return 0;
            }
        }

        double typeEffectiveness = typeEffectiveness(attacker, defender);
        if (
            defenderAbilityId.equals("cobblemon.ability.wonderguard") &&
            typeEffectiveness != SUPER_EFFECTIVE
        ) {
            return 0;
        }

        return typeEffectiveness;
    }

    private static double damage(
        int attackerLevel,
        int attackerEffectiveAttack,
        int defenderEffectiveDefence,
        double movePower,
        ElementalType moveType,
        ElementalType attackerPrimaryType,
        ElementalType attackerSecondaryType,
        ElementalType defenderPrimaryType,
        ElementalType defenderSecondaryType,
        Ability defenderAbility,
        boolean isAttackerBurned,
        boolean isPhysicalMove
    ) {
        double baseDamage = (
            (((2d * attackerLevel) / 5) * movePower * ((double)attackerEffectiveAttack / defenderEffectiveDefence)) / 50
        ) + 2;
        return typeEffectiveness(moveType, defenderPrimaryType, defenderAbility) *
            (defenderSecondaryType == null ? 1 : typeEffectiveness(moveType, defenderSecondaryType, defenderAbility)) *
            (moveType.equals(attackerPrimaryType) || moveType.equals(attackerSecondaryType) ? 1.5 : 1) *
            (isPhysicalMove && isAttackerBurned ? 0.5 : 1) *
            ((RANDOM.nextDouble() * 0.15) + 0.85) *
            baseDamage;
    }

    private static double damage(BattlePokemon attacker, BattlePokemon defender, Move move) {
        String damageCategory = move.getDamageCategory().getName();
        if (damageCategory.equals(DamageCategories.INSTANCE.getSTATUS().getName())) return 0;
        boolean isPhysicalMove = damageCategory.equals(DamageCategories.INSTANCE.getPHYSICAL().getName());
        boolean isAttackerBurned = false;
        PersistentStatusContainer statusContainer = attacker.getEffectedPokemon().getStatus();
        if (statusContainer != null && !statusContainer.isExpired()) {
            isAttackerBurned = statusContainer.getStatus().equals(Statuses.INSTANCE.getBURN());
        }
        return damage(
            attacker.getOriginalPokemon().getLevel(),
            attacker.getOriginalPokemon().getStat(isPhysicalMove ? Stats.ATTACK : Stats.SPECIAL_ATTACK),
            defender.getOriginalPokemon().getStat(isPhysicalMove ? Stats.DEFENCE : Stats.SPECIAL_DEFENCE),
            move.getPower(),
            move.getType(),
            attacker.getOriginalPokemon().getPrimaryType(),
            attacker.getOriginalPokemon().getSecondaryType(),
            defender.getOriginalPokemon().getPrimaryType(),
            defender.getOriginalPokemon().getSecondaryType(),
            defender.getOriginalPokemon().getAbility(),
            isAttackerBurned,
            isPhysicalMove
        );
    }

    private static double powerAndTypeDamage(
        double movePower,
        ElementalType moveType,
        ElementalType defenderPrimaryType,
        ElementalType defenderSecondaryType
    ) {
        return movePower *
            typeEffectiveness(moveType, defenderPrimaryType) *
            (defenderSecondaryType == null ? 1 : typeEffectiveness(moveType, defenderSecondaryType));
    }

    @NotNull
    @Override
    public ShowdownActionResponse choose(
        @NotNull ActiveBattlePokemon activeBattlePokemon,
        @Nullable ShowdownMoveset showdownMoveset,
        boolean mustSwitch
    ) {
        Optional<ActiveBattlePokemon> opponentActiveBattlePokemon = StreamSupport.stream(
                activeBattlePokemon.getAllActivePokemon().spliterator(), false
            )
            .filter(abp -> !abp.isAllied(activeBattlePokemon))
            .findFirst();

        if (mustSwitch || activeBattlePokemon.isGone()) {
            List<BattlePokemon> canSwitchTo = activeBattlePokemon.getActor().getPokemonList().stream()
                .filter(BattlePokemon::canBeSentOut)
                .toList();
            if (canSwitchTo.isEmpty()) return new DefaultActionResponse();
            if (opponentActiveBattlePokemon.isEmpty() || opponentActiveBattlePokemon.get().getBattlePokemon() == null) {
                return new SwitchActionResponse(canSwitchTo.get(RANDOM.nextInt(canSwitchTo.size())).getUuid());
            }
            BattlePokemon opponent = opponentActiveBattlePokemon.get().getBattlePokemon();
            BattlePokemon nextPokemon = canSwitchTo.stream()
                .max(Comparator.comparingDouble(pokemon -> pokemon.getMoveSet().getMoves().stream().map(move ->
                    powerAndTypeDamage(
                        move.getPower(),
                        move.getType(),
                        opponent.getOriginalPokemon().getPrimaryType(),
                        opponent.getOriginalPokemon().getSecondaryType()
                    )
                ).max(Double::compare).orElse(0d))).get();
            nextPokemon.setWillBeSwitchedIn(true);
            return new SwitchActionResponse(nextPokemon.getUuid());
        }

        if (showdownMoveset == null) return PassActionResponse.INSTANCE;
        List<InBattleMove> inBattleMoves = showdownMoveset.moves.stream()
            .filter(InBattleMove::canBeUsed)
            .filter(inBattleMove -> {
                List<Targetable> targetList = inBattleMove.getTarget().getTargetList().invoke(activeBattlePokemon);
                return inBattleMove.mustBeUsed() || targetList == null || !targetList.isEmpty();
            }).toList();

        if (inBattleMoves.isEmpty()) return new MoveActionResponse("struggle", null, null);
        if (opponentActiveBattlePokemon.isEmpty()) {
            return new MoveActionResponse(
                inBattleMoves.get(RANDOM.nextInt(showdownMoveset.moves.size())).id, null, null
            );
        }
        BattlePokemon opponent = opponentActiveBattlePokemon.get().getBattlePokemon();
        if (opponent == null) {
            return new MoveActionResponse(
                inBattleMoves.get(RANDOM.nextInt(showdownMoveset.moves.size())).id, null, null
            );
        }

        Map<InBattleMove, Move> moveMap = new HashMap<>();
        IntStream.range(0, inBattleMoves.size())
            .forEach(i -> moveMap.put(
                inBattleMoves.get(i),
                Moves.INSTANCE.all().stream().filter(move -> move.getName().equals(inBattleMoves.get(i).getId()))
                    .findFirst().get().create()
            ));

        Map<InBattleMove, Double> moveDamages = new HashMap<>();
        inBattleMoves.forEach(inBattleMove -> {
            double dmg = damage(
                activeBattlePokemon.getBattlePokemon(),
                opponent,
                moveMap.get(inBattleMove)
            );
            moveDamages.put(inBattleMove, dmg);
        });

        List<InBattleMove> killingMoves = new ArrayList<>();
        moveDamages.forEach((move, damage) -> { if (damage >= opponent.getHealth()) killingMoves.add(move); });
        InBattleMove move = killingMoves.isEmpty() ?
            Collections.max(moveDamages.entrySet(), Map.Entry.comparingByValue()).getKey() :
            killingMoves.get(RANDOM.nextInt(killingMoves.size()));
        List<Targetable> targets = move.mustBeUsed() ? null : move.getTarget().getTargetList().invoke(activeBattlePokemon);
        return new MoveActionResponse(
            move.id,
            targets == null ? null : opponentActiveBattlePokemon.get().getPNX(),
            null
        );
    }

    public static void initialiseTypeChart() {
        ElementalTypes types = ElementalTypes.INSTANCE;
        ElementalType NORMAL = types.getNORMAL();
        ElementalType FIGHTING = types.getFIGHTING();
        ElementalType FLYING = types.getFLYING();
        ElementalType POISON = types.getPOISON();
        ElementalType GROUND = types.getGROUND();
        ElementalType ROCK = types.getROCK();
        ElementalType BUG = types.getBUG();
        ElementalType GHOST = types.getGHOST();
        ElementalType STEEL = types.getSTEEL();
        ElementalType FIRE = types.getFIRE();
        ElementalType WATER = types.getWATER();
        ElementalType GRASS = types.getGRASS();
        ElementalType ELECTRIC = types.getELECTRIC();
        ElementalType PSYCHIC = types.getPSYCHIC();
        ElementalType ICE = types.getICE();
        ElementalType DRAGON = types.getDRAGON();
        ElementalType DARK = types.getDARK();
        ElementalType FAIRY = types.getFAIRY();

        typeChart.put(NORMAL, Map.of(
            FIGHTING, SUPER_EFFECTIVE,
            GHOST, IMMUNE
        ));
        typeChart.put(FIGHTING, Map.of(
            FLYING, SUPER_EFFECTIVE,
            ROCK, NOT_VERY_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            PSYCHIC, SUPER_EFFECTIVE,
            DARK, NOT_VERY_EFFECTIVE,
            FAIRY, SUPER_EFFECTIVE
        ));
        typeChart.put(FLYING, Map.of(
            FIGHTING, NOT_VERY_EFFECTIVE,
            GROUND, IMMUNE,
            ROCK, SUPER_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE,
            ELECTRIC, SUPER_EFFECTIVE,
            ICE, SUPER_EFFECTIVE
        ));
        typeChart.put(POISON, Map.of(
            FIGHTING, NOT_VERY_EFFECTIVE,
            POISON, NOT_VERY_EFFECTIVE,
            GROUND, SUPER_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE,
            PSYCHIC, SUPER_EFFECTIVE,
            FAIRY, NOT_VERY_EFFECTIVE
        ));
        typeChart.put(GROUND, Map.of(
            POISON, NOT_VERY_EFFECTIVE,
            ROCK, NOT_VERY_EFFECTIVE,
            WATER, SUPER_EFFECTIVE,
            GRASS, SUPER_EFFECTIVE,
            ELECTRIC, IMMUNE,
            ICE, SUPER_EFFECTIVE
        ));
        typeChart.put(ROCK, Map.of(
            NORMAL, NOT_VERY_EFFECTIVE,
            FIGHTING, SUPER_EFFECTIVE,
            FLYING, NOT_VERY_EFFECTIVE,
            POISON, NOT_VERY_EFFECTIVE,
            GROUND, SUPER_EFFECTIVE,
            STEEL, SUPER_EFFECTIVE,
            FIRE, NOT_VERY_EFFECTIVE,
            WATER, SUPER_EFFECTIVE,
            GRASS, SUPER_EFFECTIVE
        ));
        typeChart.put(BUG, Map.of(
            FIGHTING, NOT_VERY_EFFECTIVE,
            FLYING, SUPER_EFFECTIVE,
            GROUND, NOT_VERY_EFFECTIVE,
            ROCK, SUPER_EFFECTIVE,
            FIRE, SUPER_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE
        ));
        typeChart.put(GHOST, Map.of(
            NORMAL, IMMUNE,
            FIGHTING, IMMUNE,
            POISON, NOT_VERY_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            GHOST, SUPER_EFFECTIVE,
            DARK, SUPER_EFFECTIVE
        ));
        Map<ElementalType, Double> steelMap = new HashMap<>(Map.of(
            NORMAL, NOT_VERY_EFFECTIVE,
            FIGHTING, SUPER_EFFECTIVE,
            FLYING, NOT_VERY_EFFECTIVE,
            POISON, IMMUNE,
            GROUND, SUPER_EFFECTIVE,
            ROCK, NOT_VERY_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            STEEL, NOT_VERY_EFFECTIVE,
            FIRE, SUPER_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE
        ));
        steelMap.put(PSYCHIC, NOT_VERY_EFFECTIVE);
        steelMap.put(ICE, NOT_VERY_EFFECTIVE);
        steelMap.put(DRAGON, NOT_VERY_EFFECTIVE);
        steelMap.put(FAIRY, NOT_VERY_EFFECTIVE);
        typeChart.put(STEEL, steelMap);
        typeChart.put(FIRE, Map.of(
            GROUND, SUPER_EFFECTIVE,
            ROCK, SUPER_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            STEEL, NOT_VERY_EFFECTIVE,
            FIRE, NOT_VERY_EFFECTIVE,
            WATER, SUPER_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE,
            ICE, NOT_VERY_EFFECTIVE,
            FAIRY, NOT_VERY_EFFECTIVE
        ));
        typeChart.put(WATER, Map.of(
            STEEL, NOT_VERY_EFFECTIVE,
            FIRE, NOT_VERY_EFFECTIVE,
            WATER, NOT_VERY_EFFECTIVE,
            GRASS, SUPER_EFFECTIVE,
            ELECTRIC, SUPER_EFFECTIVE,
            ICE, NOT_VERY_EFFECTIVE
        ));
        typeChart.put(GRASS, Map.of(
            FLYING, SUPER_EFFECTIVE,
            POISON, SUPER_EFFECTIVE,
            GROUND, NOT_VERY_EFFECTIVE,
            BUG, SUPER_EFFECTIVE,
            FIRE, SUPER_EFFECTIVE,
            WATER, NOT_VERY_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE,
            ELECTRIC, NOT_VERY_EFFECTIVE,
            ICE, SUPER_EFFECTIVE
        ));
        typeChart.put(ELECTRIC, Map.of(
            FLYING, NOT_VERY_EFFECTIVE,
            GROUND, SUPER_EFFECTIVE,
            STEEL, NOT_VERY_EFFECTIVE,
            ELECTRIC, NOT_VERY_EFFECTIVE
        ));
        typeChart.put(PSYCHIC, Map.of(
            FIGHTING, NOT_VERY_EFFECTIVE,
            BUG, SUPER_EFFECTIVE,
            GHOST, SUPER_EFFECTIVE,
            PSYCHIC, NOT_VERY_EFFECTIVE,
            DARK, SUPER_EFFECTIVE
        ));
        typeChart.put(ICE, Map.of(
            FIGHTING, SUPER_EFFECTIVE,
            ROCK, SUPER_EFFECTIVE,
            STEEL, SUPER_EFFECTIVE,
            FIRE, SUPER_EFFECTIVE,
            ICE, NOT_VERY_EFFECTIVE
        ));
        typeChart.put(DRAGON, Map.of(
            FIRE, NOT_VERY_EFFECTIVE,
            WATER, NOT_VERY_EFFECTIVE,
            GRASS, NOT_VERY_EFFECTIVE,
            ELECTRIC, NOT_VERY_EFFECTIVE,
            ICE, SUPER_EFFECTIVE,
            DRAGON, SUPER_EFFECTIVE,
            FAIRY, SUPER_EFFECTIVE
        ));
        typeChart.put(DARK, Map.of(
            FIGHTING, SUPER_EFFECTIVE,
            BUG, SUPER_EFFECTIVE,
            GHOST, NOT_VERY_EFFECTIVE,
            PSYCHIC, IMMUNE,
            DARK, NOT_VERY_EFFECTIVE,
            FAIRY, SUPER_EFFECTIVE
        ));
        typeChart.put(FAIRY, Map.of(
            FIGHTING, NOT_VERY_EFFECTIVE,
            POISON, SUPER_EFFECTIVE,
            BUG, NOT_VERY_EFFECTIVE,
            STEEL, SUPER_EFFECTIVE,
            DRAGON, IMMUNE,
            DARK, NOT_VERY_EFFECTIVE
        ));
    }

}
