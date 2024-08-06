/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.selfdot.cobblemontrainers

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.ai.BattleAI
import com.cobblemon.mod.common.api.moves.Move
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.moves.categories.DamageCategories
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.battles.*
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*
import kotlin.random.Random

/**
 * AI that tries to choose the best move for the given situations. Based off of the Pokemon Trainer Tournament Simulator Github
 * https://github.com/cRz-Shadows/Pokemon_Trainer_Tournament_Simulator/blob/main/pokemon-showdown/sim/examples/Simulation-test-1.ts#L330
 *
 * @since December 15th 2023
 */
// Define the type for the damage multipliers
typealias TypeEffectivenessMap = Map<String, Map<String, Double>>

fun getDamageMultiplier(attackerType: ElementalType, defenderType: ElementalType): Double {
    return typeEffectiveness[attackerType]?.get(defenderType) ?: 1.0
}

val typeEffectiveness: Map<ElementalType, Map<ElementalType, Double>> = mapOf(
    ElementalTypes.NORMAL to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 0.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.FIRE to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
        ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 2.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 2.0, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.WATER to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 2.0, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 0.5,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 2.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.ELECTRIC to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 2.0, ElementalTypes.ELECTRIC to 0.5, ElementalTypes.GRASS to 0.5,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 0.0, ElementalTypes.FLYING to 2.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.GRASS to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 2.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 0.5,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 2.0, ElementalTypes.FLYING to 0.5,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 0.5, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.ICE to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
        ElementalTypes.ICE to 0.5, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 2.0, ElementalTypes.FLYING to 2.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 2.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.FIGHTING to mapOf(
        ElementalTypes.NORMAL to 2.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 0.5,
        ElementalTypes.PSYCHIC to 0.5, ElementalTypes.BUG to 0.5, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 0.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 2.0, ElementalTypes.STEEL to 2.0, ElementalTypes.FAIRY to 0.5
    ),
    ElementalTypes.POISON to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 0.5, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 0.5, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.0, ElementalTypes.FAIRY to 2.0
    ),
    ElementalTypes.GROUND to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 2.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 2.0, ElementalTypes.GRASS to 0.5,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 2.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 0.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 0.5, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 2.0, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.FLYING to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 0.5, ElementalTypes.GRASS to 2.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 2.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 2.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.PSYCHIC to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 2.0, ElementalTypes.POISON to 2.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 0.5, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 0.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.BUG to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 0.5, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 0.5,
        ElementalTypes.PSYCHIC to 2.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 0.5, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 2.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 0.5
    ),
    ElementalTypes.ROCK to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 2.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 0.5, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 0.5, ElementalTypes.FLYING to 2.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 2.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.GHOST to mapOf(
        ElementalTypes.NORMAL to 0.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 2.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 2.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 0.5, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 1.0
    ),
    ElementalTypes.DRAGON to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 2.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 0.0
    ),
    ElementalTypes.DARK to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 0.5, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 2.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 2.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 0.5, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 0.5
    ),
    ElementalTypes.STEEL to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 0.5, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
        ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 2.0
    ),
    ElementalTypes.FAIRY to mapOf(
        ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
        ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 2.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
        ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 2.0,
        ElementalTypes.DARK to 2.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
    )
)

val multiHitMoves: Map<String, Map<Int, Int>> = mapOf(
    // 2 - 5 hit moves
    "armthrust" to mapOf(2 to 5),
    "barrage" to mapOf(2 to 5),
    "bonerush" to mapOf(2 to 5),
    "bulletseed" to mapOf(2 to 5),
    "cometpunch" to mapOf(2 to 5),
    "doubleslap" to mapOf(2 to 5),
    "furyattack" to mapOf(2 to 5),
    "furyswipes" to mapOf(2 to 5),
    "iciclespear" to mapOf(2 to 5),
    "pinmissile" to mapOf(2 to 5),
    "rockblast" to mapOf(2 to 5),
    "scaleshot" to mapOf(2 to 5),
    "spikecannon" to mapOf(2 to 5),
    "tailslap" to mapOf(2 to 5),
    "watershuriken" to mapOf(2 to 5),

    // fixed hit count
    "bonemerang" to mapOf(2 to 2),
    "doublehit" to mapOf(2 to 2),
    "doubleironbash" to mapOf(2 to 2),
    "doublekick" to mapOf(2 to 2),
    "dragondarts" to mapOf(2 to 2),
    "dualchop" to mapOf(2 to 2),
    "dualwingbeat" to mapOf(2 to 2),
    "geargrind" to mapOf(2 to 2),
    "twinbeam" to mapOf(2 to 2),
    "twineedle" to mapOf(2 to 2),
    "suringstrikes" to mapOf(3 to 3),
    "tripledive" to mapOf(3 to 3),
    "watershuriken" to mapOf(3 to 3),

    // accuracy based multi-hit moves
    "tripleaxel" to mapOf(1 to 3),
    "triplekick" to mapOf(1 to 3),
    "populationbomb" to mapOf(1 to 10)
)

val statusMoves: Map<MoveTemplate?, String> = mapOf(
    Moves.getByName("willowisp") to Statuses.BURN.showdownName,
    Moves.getByName("scald") to Statuses.BURN.showdownName,
    Moves.getByName("scorchingsands") to Statuses.BURN.showdownName,
    Moves.getByName("glare") to Statuses.PARALYSIS.showdownName,
    Moves.getByName("nuzzle") to Statuses.PARALYSIS.showdownName,
    Moves.getByName("stunspore") to Statuses.PARALYSIS.showdownName,
    Moves.getByName("thunderwave") to Statuses.PARALYSIS.showdownName,
    Moves.getByName("Nuzzle") to Statuses.PARALYSIS.showdownName,
    Moves.getByName("darkvoid") to Statuses.SLEEP.showdownName,
    Moves.getByName("hypnosis") to Statuses.SLEEP.showdownName,
    Moves.getByName("lovelykiss") to Statuses.SLEEP.showdownName,
    Moves.getByName("relicsong") to Statuses.SLEEP.showdownName,
    Moves.getByName("sing") to Statuses.SLEEP.showdownName,
    Moves.getByName("sleeppower") to Statuses.SLEEP.showdownName,
    Moves.getByName("spore") to Statuses.SLEEP.showdownName,
    Moves.getByName("yawn") to Statuses.SLEEP.showdownName,
    Moves.getByName("chatter") to "confusion",
    Moves.getByName("confuseray") to "confusion",
    Moves.getByName("dynamicpunch") to "confusion",
    Moves.getByName("flatter") to "confusion",
    Moves.getByName("supersonic") to "confusion",
    Moves.getByName("swagger") to "confusion",
    Moves.getByName("sweetkiss") to "confusion",
    Moves.getByName("teeterdance") to "confusion",
    Moves.getByName("poisongas") to Statuses.POISON.showdownName,
    Moves.getByName("poisonpowder") to Statuses.POISON.showdownName,
    Moves.getByName("toxic") to Statuses.POISON_BADLY.showdownName,
    Moves.getByName("toxicthread") to Statuses.POISON.showdownName,
    Moves.getByName("curse") to "cursed",
    Moves.getByName("leechseed") to "leech"
)

val boostFromMoves: Map<String, Map<Stat, Int>> = mapOf(
    "bellydrum" to mapOf(Stats.ATTACK to 6),
    "bulkup" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1),
    "clangoroussoul" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
    "coil" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.ACCURACY to 1),
    "dragondance" to mapOf(Stats.ATTACK to 1, Stats.SPEED to 1),
    "extremeevoboost" to mapOf(Stats.ATTACK to 2, Stats.DEFENCE to 2, Stats.SPECIAL_ATTACK to 2, Stats.SPECIAL_DEFENCE to 2, Stats.SPEED to 2),
    "clangoroussoulblaze" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
    "filletaway" to mapOf(Stats.ATTACK to 2, Stats.SPECIAL_ATTACK to 2, Stats.SPEED to 2),
    "honeclaws" to mapOf(Stats.ATTACK to 1, Stats.ACCURACY to 1),
    "noretreat" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
    "shellsmash" to mapOf(Stats.ATTACK to 2, Stats.DEFENCE to -1, Stats.SPECIAL_ATTACK to 2, Stats.SPECIAL_DEFENCE to -1, Stats.SPEED to 2),
    "shiftgear" to mapOf(Stats.ATTACK to 1, Stats.SPEED to 2),
    "swordsdance" to mapOf(Stats.ATTACK to 2),
    "tidyup" to mapOf(Stats.ATTACK to 1, Stats.SPEED to 1),
    "victorydance" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPEED to 1),
    "acidarmor" to mapOf(Stats.DEFENCE to 2),
    "barrier" to mapOf(Stats.DEFENCE to 2),
    "cottonguard" to mapOf(Stats.DEFENCE to 3),
    "defensecurl" to mapOf(Stats.DEFENCE to 1),
    "irondefense" to mapOf(Stats.DEFENCE to 2),
    "shelter" to mapOf(Stats.DEFENCE to 2, Stats.EVASION to 1),
    "stockpile" to mapOf(Stats.DEFENCE to 1, Stats.SPECIAL_DEFENCE to 1),
    "stuffcheeks" to mapOf(Stats.DEFENCE to 2),
    "amnesia" to mapOf(Stats.SPECIAL_DEFENCE to 2),
    "calmmind" to mapOf(Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1),
    "geomancy" to mapOf(Stats.SPECIAL_ATTACK to 2, Stats.SPECIAL_DEFENCE to 2, Stats.SPEED to 2),
    "nastyplot" to mapOf(Stats.SPECIAL_ATTACK to 2),
    "quiverdance" to mapOf(Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
    "tailglow" to mapOf(Stats.SPECIAL_ATTACK to 3),
    "takeheart" to mapOf(Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1),
    "agility" to mapOf(Stats.SPEED to 2),
    "autotomize" to mapOf(Stats.SPEED to 2),
    "rockpolish" to mapOf(Stats.SPEED to 2),
    "curse" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPEED to -1),
    "minimize" to mapOf(Stats.EVASION to 2)
)


class ActiveTracker {
    var p1Active: TrackerActor = TrackerActor()
    var p2Active: TrackerActor = TrackerActor()

    // Tracker Actor with a Party of Tracker Pokemon in a Party
    data class TrackerActor(
        var party: MutableList<TrackerPokemon> = mutableListOf(),
        var uuid: String? = null,
        var activePokemon: TrackerPokemon = TrackerPokemon(),
        var nRemainingMons: Int = 0
    )

    // Tracker Pokemon within the Party
    data class TrackerPokemon(
        var pokemon: Pokemon? = null,
        var species: String? = null,
        var availableSwitches: List<Pokemon>? = null,
        var currentHp: Int = 0,
        var currentHpPercent: Double = 0.0,
        var boosts: Map<Stat, Int> = mapOf(), // unused for now
        var atkBoost: Int = 0,
        var defBoost: Int = 0,
        var spaBoost: Int = 0,
        var spdBoost: Int = 0,
        var speBoost: Int = 0,
        var currentAbiltiy: String? = null,
        var currentTypes: MutableList<String>? = null,
        var stats: Map<Stat, Int> = mapOf(),
        var moves: List<Move> = listOf(),
        var sideConditions: Map<String, Any> = mapOf(),
        var firstTurn: Int = 0,
        var protectCount: Int = 0
    )
}

class StrongBattleAI(skill: Int) : BattleAI {

    private val skill = if (skill < 0) 0 else if (skill > 5) 5 else skill
    private val entryHazards = listOf("spikes", "stealthrock", "stickyweb", "toxicspikes")
    private val antiHazardsMoves = listOf("rapidspin", "defog", "tidyup")
    private val antiBoostMoves = listOf("slearsmog","haze")
    private val pivotMoves = listOf("uturn","flipturn", "partingshot", "batonpass", "chillyreception","shedtail", "voltswitch", "teleport")
    private val setupMoves = setOf("tailwind", "trickroom", "auroraveil", "lightscreen", "reflect")
    private val selfRecoveryMoves = listOf("healorder", "milkdrink", "recover", "rest", "roost", "slackoff", "softboiled")
    private val weatherSetupMoves = mapOf(
        "chillyreception" to "Snow",
        "hail" to "Hail",
        "raindance" to "RainDance",
        "sandstorm" to "Sandstorm",
        "snowscape" to "Snow",
        "sunnyday" to "SunnyDay"
    )
    private val speedTierCoefficient = 4.0 //todo set back to 6 to how it was
    private var trickRoomCoefficient = 1.0
    private val typeMatchupWeightConsideration = 2.5 // value of a good or bad type matchup
    private val moveDamageWeightConsideration = 0.8 // value of a good or bad move matchup
    private val antiBoostWeightConsideration = 25 // value of a mon with moves that remove stat boosts
    private val hpWeightConsideration = 0.25 // how much HP difference is a consideration for switchins
    private val hpFractionCoefficient = 0.4 // how much HP differences should be taken into account for switch ins
    private val boostWeightCoefficient = 1 // the amount of boosts considered a baseline to be removed
    private val switchOutMatchupThreshold = 0 // todo change this to get it feeling just right (-7 never switches)
    private val selfKoMoveMatchupThreshold = 0.3
    private val trickRoomThreshold = 85
    private val recoveryMoveThreshold = 0.50
    private val accuracySwitchThreshold = -3
    private val hpSwitchOutThreshold = .3 // percent of HP needed to be considered for switchout
    private val randomProtectChance = 0.3 // percent chance of a protect move being used with 1 turn in between

    // create the active pokemon tracker here
    private val activeTracker = ActiveTracker()

    /*override fun choose(
            activeBattlePokemon: ActiveBattlePokemon,
            moveset: ShowdownMoveset?,
            forceSwitch: Boolean
    ): ShowdownActionResponse {
        if (forceSwitch || activeBattlePokemon.isGone()) {
            val switchTo = activeBattlePokemon.actor.pokemonList.filter { it.canBeSentOut() }.randomOrNull()
                    ?: return DefaultActionResponse() //throw IllegalStateException("Need to switch but no Pokémon to switch to")
            switchTo.willBeSwitchedIn = true
            return SwitchActionResponse(switchTo.uuid)
        }

        if (moveset == null) {
            return PassActionResponse
        }


    }    */

    // get base stats of the pokemon sent in
    fun getBaseStats(pokemon: Pokemon, stat: String): Int {
        return when (stat) {
            "hp" -> pokemon.species.baseStats.getOrDefault(Stats.HP, 0)
            "atk" -> pokemon.species.baseStats.getOrDefault(Stats.ATTACK, 0)
            "spa" -> pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_ATTACK, 0)
            "def" -> pokemon.species.baseStats.getOrDefault(Stats.DEFENCE, 0)
            "spd" -> pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_DEFENCE, 0)
            "spe" -> pokemon.species.baseStats.getOrDefault(Stats.SPEED, 0)
            "total" -> (pokemon.species.baseStats.getOrDefault(Stats.HP, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.ATTACK, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.DEFENCE, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_DEFENCE, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.SPEED, 0))

            else -> 0
        }
    }

    fun getIVs(pokemon: Pokemon, stat: String): Int {
        return when (stat) {
            "hp" -> pokemon.ivs.getOrDefault(Stats.HP)
            "atk" -> pokemon.ivs.getOrDefault(Stats.ATTACK)
            "spa" -> pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK)
            "def" -> pokemon.ivs.getOrDefault(Stats.DEFENCE)
            "spd" -> pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE)
            "spe" -> pokemon.ivs.getOrDefault(Stats.SPEED)
            else -> 0
        }
    }

    fun getEVs(pokemon: Pokemon, stat: String): Int {
        return when (stat) {
            "hp" -> pokemon.evs.getOrDefault(Stats.HP)
            "atk" -> pokemon.evs.getOrDefault(Stats.ATTACK)
            "spa" -> pokemon.evs.getOrDefault(Stats.SPECIAL_ATTACK)
            "def" -> pokemon.evs.getOrDefault(Stats.DEFENCE)
            "spd" -> pokemon.evs.getOrDefault(Stats.SPECIAL_DEFENCE)
            "spe" -> pokemon.evs.getOrDefault(Stats.SPEED)
            else -> 0
        }
    }

    // skill check that will be used for if the AI will make a successful Move decision
    fun checkSkillLevel(skillLevel: Int): Boolean {
        if (skillLevel == 5) {
            return true
        }
        val randomNumber = Random.nextInt(100)
        // Map skill level to the desired probability
        return randomNumber < skillLevel * 20
    }

    // skill check that will be used for if the AI will make a successful Switch Out decision
    fun checkSwitchOutSkill(skillLevel: Int): Boolean {
        // Generate a random number between 0 and 1
        val randomNumber = Random.nextDouble()

        // Determine the chance skill check will succeed based on skill level
        val chance = when (skillLevel) {
            in 0..2 -> 0.0
            3 -> 0.20
            4 -> 0.60
            5 -> 1.00
            else -> 0.0 // if skillLevel is out of expected range
        }

        // Check if the random number is less than the chance
        return randomNumber <= chance
    }

    // skill check that will be used for if the AI will make a successful Use Item decision
    fun checkUseItemSkill(skillLevel: Int): Boolean {
        // Generate a random number between 0 and 1
        val randomNumber = Random.nextDouble()

        // Determine the chance skill check will succeed based on skill level
        val chance = when (skillLevel) {
            in 0..1 -> 0.0
            2 -> 0.25
            3 -> 0.5
            4 -> 0.75
            5 -> 1.00
            else -> 0.0 // if skillLevel is out of expected range
        }

        // Check if the random number is less than the chance
        return randomNumber < chance
    }

    // todo add helper function for sending in move and checking if it will have affect on the pokemon

    // old function definition
    //override fun choose(request: ShowdownActionRequest, active: ActivePokemon, moves: List<MoveChoice>, canDynamax: Boolean, possibleMoves: List<Move>): ShowdownActionResponse {
    override fun choose(activeBattlePokemon: ActiveBattlePokemon, moveset: ShowdownMoveset?, forceSwitch: Boolean): ShowdownActionResponse {
        try {
            // get the current battle and set it as a variable
            val battle = activeBattlePokemon.battle
            val request = activeBattlePokemon.actor.request!! // todo idk if this is the right way to do it
            val p1Actor = battle.side1.actors.first()
            val p2Actor = battle.side2.actors.first()

            val activePlayerBattlePokemon = p1Actor.activePokemon[0].battlePokemon
            val activeNPCBattlePokemon = p2Actor.activePokemon[0].battlePokemon

            val activePlayerBattlePokemonPosBoosts = activePlayerBattlePokemon?.contextManager?.get(BattleContext.Type.BOOST)
            val activeNPCBattlePokemonPosBoosts = activeNPCBattlePokemon?.contextManager?.get(BattleContext.Type.BOOST)
            val activePlayerBattlePokemonNegBoosts = activePlayerBattlePokemon?.contextManager?.get(BattleContext.Type.UNBOOST)
            val activeNPCBattlePokemonNegBoosts = activeNPCBattlePokemon?.contextManager?.get(BattleContext.Type.UNBOOST)

            // positive boosts
            val playerATKPosBoosts = activePlayerBattlePokemonPosBoosts?.count { it.id == "atk" } ?: 0
            val playerDEFPosBoosts = activePlayerBattlePokemonPosBoosts?.count { it.id == "def" } ?: 0
            val playerSPAPosBoosts = activePlayerBattlePokemonPosBoosts?.count { it.id == "spa" } ?: 0
            val playerSPDPosBoosts = activePlayerBattlePokemonPosBoosts?.count { it.id == "spd" } ?: 0
            val playerSPEPosBoosts = activePlayerBattlePokemonPosBoosts?.count { it.id == "spe" } ?: 0

            val npcATKPosBoosts = activeNPCBattlePokemonPosBoosts?.count { it.id == "atk" } ?: 0
            val npcDEFPosBoosts = activeNPCBattlePokemonPosBoosts?.count { it.id == "def" } ?: 0
            val npcSPAPosBoosts = activeNPCBattlePokemonPosBoosts?.count { it.id == "spa" } ?: 0
            val npcSPDPosBoosts = activeNPCBattlePokemonPosBoosts?.count { it.id == "spd" } ?: 0
            val npcSPEPosBoosts = activeNPCBattlePokemonPosBoosts?.count { it.id == "spe" } ?: 0

            // negative boosts
            val playerATKNegBoosts = activePlayerBattlePokemonNegBoosts?.count { it.id == "atk" } ?: 0
            val playerDEFNegBoosts = activePlayerBattlePokemonNegBoosts?.count { it.id == "def" } ?: 0
            val playerSPANegBoosts = activePlayerBattlePokemonNegBoosts?.count { it.id == "spa" } ?: 0
            val playerSPDNegBoosts = activePlayerBattlePokemonNegBoosts?.count { it.id == "spd" } ?: 0
            val playerSPENegBoosts = activePlayerBattlePokemonNegBoosts?.count { it.id == "spe" } ?: 0

            val npcATKNegBoosts = activeNPCBattlePokemonNegBoosts?.count { it.id == "atk" } ?: 0
            val npcDEFNegBoosts = activeNPCBattlePokemonNegBoosts?.count { it.id == "def" } ?: 0
            val npcSPANegBoosts = activeNPCBattlePokemonNegBoosts?.count { it.id == "spa" } ?: 0
            val npcSPDNegBoosts = activeNPCBattlePokemonNegBoosts?.count { it.id == "spd" } ?: 0
            val npcSPENegBoosts = activeNPCBattlePokemonNegBoosts?.count { it.id == "spe" } ?: 0

            // Total Boosts
            val playerATKBoosts = playerATKPosBoosts.minus((playerATKNegBoosts ?: 0)) ?: 0
            val playerDEFBoosts = playerDEFPosBoosts.minus((playerDEFNegBoosts ?: 0)) ?: 0
            val playerSPABoosts = playerSPAPosBoosts.minus((playerSPANegBoosts ?: 0)) ?: 0
            val playerSPDBoosts = playerSPDPosBoosts.minus((playerSPDNegBoosts ?: 0)) ?: 0
            val playerSPEBoosts = playerSPEPosBoosts.minus((playerSPENegBoosts ?: 0)) ?: 0

            val npcATKBoosts = npcATKPosBoosts.minus((npcATKNegBoosts ?: 0))
            val npcDEFBoosts = npcDEFPosBoosts.minus((npcDEFNegBoosts ?: 0))
            val npcSPABoosts = npcSPAPosBoosts.minus((npcSPANegBoosts ?: 0))
            val npcSPDBoosts = npcSPDPosBoosts.minus((npcSPDNegBoosts ?: 0))
            val npcSPEBoosts = npcSPEPosBoosts.minus((npcSPENegBoosts ?: 0))

            // Statuses of the pokemon

            val activePlayerPokemonStatus = activePlayerBattlePokemon?.originalPokemon?.status?.status?.name ?: try {
                activePlayerBattlePokemon?.contextManager?.get(BattleContext.Type.STATUS)?.last()?.id
            } catch (e: Exception) {
                ""
            } ?: ""

            val activePlayerPokemonVolatile = try {
                activePlayerBattlePokemon?.contextManager?.get(BattleContext.Type.VOLATILE)?.last()?.id
            } catch (e: Exception) {
                ""
            } ?: ""

            val activeNPCPokemonStatus = activeNPCBattlePokemon?.originalPokemon?.status?.status?.name ?: try {
                activeNPCBattlePokemon?.contextManager?.get(BattleContext.Type.STATUS)?.last()?.id
            } catch (e: Exception) {
                ""
            } ?: ""

            val activeNPCPokemonVolatile = try {
                activeNPCBattlePokemon?.contextManager?.get(BattleContext.Type.VOLATILE)?.last()?.id
            } catch (e: Exception) {
                ""
            } ?: ""

            //val activeNPCPokemonStatus = activeNPCBattlePokemon?.originalPokemon?.status?.status?.name ?: if (activeNPCBattlePokemon?.contextManager?.get(BattleContext.Type.STATUS).isNullOrEmpty()) "" else activeNPCBattlePokemon?.contextManager?.get(BattleContext.Type.STATUS)?.last()?.id ?: ""

            // Hazards on both sides of the field
            var playerSideHazardsList: MutableList<String> = mutableListOf()
            battle.side1.contextManager.get(BattleContext.Type.HAZARD)?.forEach { playerSideHazardsList.add(it.id) }

            val npcSideHazardsList: MutableList<String> = mutableListOf()
            battle.side2.contextManager.get(BattleContext.Type.HAZARD)?.forEach { npcSideHazardsList.add(it.id) }

            // translate to List
            val playerSideHazards = playerSideHazardsList.toList()
            val npcSideHazards = npcSideHazardsList.toList()

            val canDynamax = false

            if (forceSwitch || activeBattlePokemon.isGone()) {
                if ((forceSwitch && battle.turn == 1) || (activeBattlePokemon.isGone() && battle.turn == 1)) {
                    val switchTo = activeBattlePokemon.actor.pokemonList.filter { it.canBeSentOut() }.randomOrNull()
                        ?: return DefaultActionResponse() //throw IllegalStateException("Need to switch but no Pokémon to switch to")
                    switchTo.willBeSwitchedIn = true
                    return SwitchActionResponse(switchTo.uuid)
                }
                else if ((forceSwitch && battle.turn > 1) || (activeBattlePokemon.isGone() && battle.turn > 1)) {

                    val availableSwitches = activeBattlePokemon.actor.pokemonList.filter { (it.health > 0) && (it.uuid != (activeBattlePokemon.battlePokemon?.uuid ?: true)) } //it.uuid != mon.pokemon!!.uuid && it.health > 0 }

                    // this gets null error after NPC faints
                    val bestEstimation = availableSwitches.maxOfOrNull { estimatePartyMatchup(request, battle, it.effectedPokemon) }

                    val switchTo = availableSwitches.find { estimatePartyMatchup(request, battle, it.effectedPokemon) == bestEstimation }
                        ?: return DefaultActionResponse() //throw IllegalStateException("Need to switch but no Pokémon to switch to")

                    switchTo.willBeSwitchedIn = true

                    switchTo.let {
                        return SwitchActionResponse(it.uuid)

                        //return SwitchActionResponse(switchTo.uuid)

                        /*val availableSwitches = p2Actor.pokemonList.filter { it.health > 0 } //it.uuid != mon.pokemon!!.uuid && it.health > 0 }
                        val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(request, battle, it.effectedPokemon) }
                        *//*availableSwitches.forEach {
                    estimateMatchup(request, battle, it.effectedPokemon)
                }*//*
                val bestMatchup = availableSwitches.find { estimateMatchup(request, battle, it.effectedPokemon) == bestEstimation }
                bestMatchup?.let {
                    return SwitchActionResponse(it.uuid)*/
                    }
                }
            }

            //val (mon, opponent) = getCurrentPlayer(battle)

            updateActiveTracker(activeBattlePokemon, request, battle)

            // sync up the current pokemon that is choosing the moves
            /*val (mon, opponent) = if (activeBattlePokemon.battlePokemon!!.effectedPokemon.uuid == activeTracker.p1Active.party.indexOf(pokemon!!.uuid)) {
                Pair(activeTracker.p1Active, activeTracker.p2Active)
            } else {
                Pair(activeTracker.p2Active, activeTracker.p1Active)
            }*/

            val activeNPCPokemonUUID = activeBattlePokemon.battlePokemon!!.effectedPokemon.uuid
            val activePlayerPokemonUUID = battle.side1.activePokemon[0].battlePokemon?.uuid

            val matchedActiveNPCPokemon = activeTracker.p2Active.party.find {
                it.pokemon?.uuid == activeNPCPokemonUUID }
            val matchedActivePlayerPokemon = activeTracker.p1Active.party.find {
                it.pokemon?.uuid == activePlayerPokemonUUID }

            // todo get to become equal the activeTrackerPokemon set above which for some reason
            // set the active pokemon in the tracker
            activeTracker.p1Active.activePokemon = matchedActivePlayerPokemon!!
            activeTracker.p2Active.activePokemon = matchedActiveNPCPokemon!!

            /*val (mon, opponent) = if (matchedActivePokemon != null) {
                Pair(matchedActivePokemon, activeTracker.p2Active)
            } else {
                Pair(activeTracker.p2Active, matchedActivePokemon)
            }*/

            val mon = matchedActiveNPCPokemon!!
            val opponent = matchedActivePlayerPokemon!!

            // todo WHY WHY WHY does protect fire off twice sometimes still?
            // Update protect count if it's on cooldown and implement a random reduction to the count to not be predictable
            if (mon.protectCount > 0) {
                if (Random.nextDouble() < randomProtectChance) {
                    // 30% chance to decrease by 2
                    mon.protectCount -= 2
                    // Ensure that protectCount does not go below zero
                    if (mon.protectCount < 0) {
                        mon.protectCount = 0
                    }
                } else {
                    // 70% chance to decrease by 1
                    mon.protectCount -= 1
                }
            }

            battle.contextManager.get(BattleContext.Type.WEATHER).isNullOrEmpty()
            // todo make sure changes in weather are detected
            val currentWeather = if (battle.contextManager.get(BattleContext.Type.WEATHER).isNullOrEmpty()) null else battle.contextManager.get(BattleContext.Type.WEATHER)?.iterator()?.next()?.id
            val currentTerrain = if (battle.contextManager.get(BattleContext.Type.TERRAIN).isNullOrEmpty()) null else battle.contextManager.get(BattleContext.Type.TERRAIN)?.iterator()?.next()?.id
            val currentRoom = if (battle.contextManager.get(BattleContext.Type.ROOM).isNullOrEmpty()) null else battle.contextManager.get(BattleContext.Type.ROOM)?.iterator()?.next()?.id

            // change trickRoomCoefficient according to the current room
            if (currentRoom == "trickroom") // todo ALSO consider how many turns of Trick Room are left. If last turn then do not switch out
                trickRoomCoefficient = -1.0
            else
                trickRoomCoefficient = 1.0

            //val currentScreen = if (battle.contextManager.get(BattleContext.Type.SCREEN))
            val allMoves = moveset?.moves?.filterNot { it.pp == 0 || it.disabled }

            // Rough estimation of damage ratio
            val physicalRatio = statEstimationActive(mon, Stats.ATTACK) / statEstimationActive(opponent, Stats.DEFENCE)
            val specialRatio = statEstimationActive(mon, Stats.SPECIAL_ATTACK) / statEstimationActive(opponent, Stats.SPECIAL_DEFENCE)

            // List of all side conditions on each player's side
            val monSideConditionList = mon.sideConditions.keys
            val oppSideConditionList = opponent.sideConditions.keys

            val playerSideTailwindCondition = if (battle.side1.contextManager.get(BattleContext.Type.TAILWIND).isNullOrEmpty()) null else battle.side1.contextManager.get(BattleContext.Type.TAILWIND)?.iterator()?.next()?.id
            val playerSideScreenCondition = if (battle.side1.contextManager.get(BattleContext.Type.SCREEN).isNullOrEmpty()) null else battle.side1.contextManager.get(BattleContext.Type.SCREEN)?.iterator()?.next()?.id
            val playerSideHazardCondition = if (battle.side1.contextManager.get(BattleContext.Type.HAZARD).isNullOrEmpty()) null else battle.side1.contextManager.get(BattleContext.Type.HAZARD)?.iterator()?.next()?.id

            val npcSideTailwindCondition = if (battle.side2.contextManager.get(BattleContext.Type.TAILWIND).isNullOrEmpty()) null else battle.side2.contextManager.get(BattleContext.Type.TAILWIND)?.iterator()?.next()?.id
            val npcSideScreenCondition = if (battle.side2.contextManager.get(BattleContext.Type.SCREEN).isNullOrEmpty()) null else battle.side2.contextManager.get(BattleContext.Type.SCREEN)?.iterator()?.next()?.id
            val npcSideHazardCondition = if (battle.side2.contextManager.get(BattleContext.Type.HAZARD).isNullOrEmpty()) null else battle.side2.contextManager.get(BattleContext.Type.HAZARD)?.iterator()?.next()?.id

            // if a move must be used (like recharge) is in moves list then do that since you have to
            for (move in moveset!!.moves)
                if (move.mustBeUsed())
                    return chooseMove(move, activeBattlePokemon)


            // todo Check for Skill of AI and see if they will make a smart move
            if (!checkSkillLevel(skill)){
                // todo choose random move
                if (moveset == null) {
                    return PassActionResponse
                }
                val move = moveset.moves
                    .filter { it.canBeUsed() }
                    .filter { it.mustBeUsed() || it.target.targetList(activeBattlePokemon)?.isEmpty() != true }
                    .randomOrNull()
                    ?: return MoveActionResponse("struggle")

                return chooseMove(move, activeBattlePokemon)
            }


            // todo update side conditions each turn
            //fun updateSideConditions =

            // todo Assess Damger level of current pokemon based on current HP and matchup pokemon
            // todo Try to find a way to store a list of moves each pokemon in the battle has used so that the AI can learn and decide differently over time
            // todo try to caclulate if it is worth it to use status moves somehow

            // switch out based on current matchup on the field
            if (checkSwitchOutSkill(skill) && shouldSwitchOut(request, battle, activeBattlePokemon )) {
                val availableSwitches = p2Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }
                val availablePlayerSwitches = p1Actor.pokemonList.filter { it.uuid != opponent.pokemon!!.uuid && it.health > 0 }

                // todo try to detect a player switch-in based on if they do that a lot
                // todo if player is in bad matchup against current AI pokemon, and they have switched out before, then they have a chance of switching to a favorable matchup
                // todo make it so that bestEstimation is actually in comparison to that potential pokemon instead and be sure to switch to that instead
                // todo maybe make it random chance to happen the higher the % chance the player likes to switch out AND/OR when the player has revealed more than 3-4 or their party?
                //val bestEstimation =
                /*availableSwitches.forEach {
                    estimateMatchup(request, battle, it.effectedPokemon)
                }*/
                val bestEstimation = if ( 1 == 1 /* todo if player is in bad matchup and switches out a lot and has a better matchup in revealed party */) {
                    // todo make it so that bestEstimation is actually in comparison to that potential pokemon instead and be sure to switch to that instead

                    availableSwitches.maxOfOrNull { estimateMatchup(activeBattlePokemon, request, battle, it.effectedPokemon) }
                } else {
                    availableSwitches.maxOfOrNull { estimateMatchup(activeBattlePokemon, request, battle, it.effectedPokemon) }
                }

                // todo Pivot switches decided here if it wants to switchout anyways
                for (move in moveset!!.moves.filter { !it.disabled }) {
                    if (move.pp > 0 && move.id in pivotMoves /* todo ADD Case for if move will be effective so that it doesn't use useless moves */) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                val bestMatchup = availableSwitches.find { estimateMatchup(activeBattlePokemon, request, battle, it.effectedPokemon) == bestEstimation }
                bestMatchup?.let {
                    return SwitchActionResponse(it.uuid)
                    //Pair("switch ${getPokemonPos(request, it)}", canDynamax)
                }
            }
            mon.firstTurn = 0

            // Decision-making based on move availability and switch-out condition
            if (!moveset?.moves?.isEmpty()!! && !shouldSwitchOut(request, battle, activeBattlePokemon) ||
                (request.side?.pokemon?.count { getHpFraction(it.condition) != 0.0 } == 1 && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) == 1.0)) {
                val nRemainingMons = activeTracker.p2Active.nRemainingMons
                val nOppRemainingMons = activeTracker.p1Active.nRemainingMons

                // Sleep Talk when asleep
                if (activeNPCPokemonStatus == "slp")
                    for (move in moveset.moves.filter { !it.disabled }) {
                        if (move.id == "sleeptalk")
                            return chooseMove(move, activeBattlePokemon)
                    }

                // Fake Out
                allMoves?.firstOrNull { it.pp > 0 && it.id == "fakeout" && mon.firstTurn == 1 && !opponent.pokemon?.types?.contains(ElementalTypes.GHOST)!! }?.let {
                    mon.firstTurn = 0
                    return chooseMove(it, activeBattlePokemon)
                }

                mon.firstTurn = 0


                // Explosion/Self destruct
                allMoves?.firstOrNull {
                    (it.id.equals("explosion") || it.id.equals("selfdestruct"))
                            && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) < selfKoMoveMatchupThreshold
                            && (opponent.currentHp.toDouble() / opponent.pokemon!!.hp.toDouble()) > 0.5
                            && ElementalTypes.GHOST !in opponent.pokemon!!.types
                            && it.pp > 0
                }?.let {
                    return chooseMove(it, activeBattlePokemon)
                }

                // Self recovery moves
                for (move in moveset.moves.filter { !it.disabled }) {
                    if (move.id in selfRecoveryMoves && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) < recoveryMoveThreshold && move.pp > 0) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // Deal with non-weather related field changing effects
                for (move in moveset.moves.filter { !it.disabled }) {
                    val availableSwitches = p2Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }

                    // Tailwind
                    if (move.pp > 0 && move.id == "tailwind" && move.id != npcSideTailwindCondition && p2Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }.size > 2) {
                        return chooseMove(move, activeBattlePokemon)
                    }

                    // Trick room
                    if (move.pp > 0 && move.id == "trickroom" && move.id != currentRoom
                        && availableSwitches.count { statEstimation(it.effectedPokemon, Stats.SPEED) <= trickRoomThreshold } >= 2) {
                        return chooseMove(move, activeBattlePokemon)
                    }

                    // todo find a way to get list of active screens
                    // Aurora veil
                    if (move.pp > 0 && move.id == "auroraveil" && move.id != npcSideScreenCondition
                        && currentWeather in listOf("Hail", "Snow")) {
                        return chooseMove(move, activeBattlePokemon)
                    }

                    // todo find a way to get list of active screens
                    // Light Screen
                    if (move.pp > 0 && move.id == "lightscreen" && move.id != npcSideScreenCondition
                        && getBaseStats(opponent.pokemon!!, "spa") > getBaseStats(opponent.pokemon!!, "atk")
                        && p2Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }.size > 1) {
                        return chooseMove(move, activeBattlePokemon)
                    }

                    // todo find a way to get list of active screens
                    // Reflect
                    if (move.pp > 0 && move.id == "reflect" && move.id != npcSideScreenCondition
                        && getBaseStats(opponent.pokemon!!, "atk") > getBaseStats(opponent.pokemon!!, "spa")
                        && p2Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }.size > 1) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // Entry hazard setup and removal
                for (move in moveset.moves.filter { !it.disabled }) {
                    // Setup
                    if (move.pp > 0 && nOppRemainingMons >= 3 && move.id in entryHazards
                        && playerSideHazards.contains(move.id) != true ) {
                        //&& entryHazards.none { it in oppSideConditionList }) {
                        return chooseMove(move, activeBattlePokemon)
                    }

                    // Removal
                    if (move.pp > 0 && nRemainingMons >= 2 && move.id in antiHazardsMoves
                        && npcSideHazards.isNotEmpty()) {
                        //&& entryHazards.any { it in monSideConditionList }) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // todo stat clearing moves like haze and clearsmog
                for (move in moveset.moves.filter { !it.disabled }) {
                    if (move.id in antiBoostMoves && isBoosted(opponent) && move.pp > 0) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // Court Change
                for (move in moveset.moves.filter { !it.disabled }) {
                    if (move.pp > 0 && move.id == "courtchange"
                        && (!entryHazards.none { it in monSideConditionList }
                                || setOf("tailwind", "lightscreen", "reflect").any { it in oppSideConditionList })
                        && setOf("tailwind", "lightscreen", "reflect").none { it in monSideConditionList }
                        && entryHazards.none { it in oppSideConditionList }) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // todo Check why the hell they still spam heal moves

                // Strength Sap
                for (move in moveset.moves.filter { !it.disabled }) {
                    if (move.id == "strengthsap" && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) < 0.5
                        && getBaseStats(opponent.pokemon!!, "atk") > 80
                        && move.pp > 0) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                //val contextBoost = battle.contextManager.get(BattleContext.Type.BOOST)


                //val pokemonBoost =

                //battle.contextManager.get(BattleContext.Type.WEATHER).isNullOrEmpty()

                // Belly Drum
                for (move in moveset.moves.filter { !it.disabled }) {
                    if (move.id == "bellydrum"
                        && ((mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) > 0.6
                                && mon.pokemon!!.heldItem().item == CobblemonItems.SITRUS_BERRY
                                || (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) > 0.8)
                        && npcATKBoosts < 1 // todo Why does Belly Drum only show up as a single boost to Atk stat
                        && move.pp > 0) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // todo have it not do this unless it is actually helpful for the team
                // Weather setup moves
                for (move in moveset.moves.filter { !it.disabled }) {
                    weatherSetupMoves[move.id]?.let { requiredWeather ->
                        if (move.pp > 0 && currentWeather != requiredWeather.lowercase() &&
                            !(currentWeather == "PrimordialSea" && requiredWeather == "RainDance") &&
                            !(currentWeather == "DesolateLand" && requiredWeather == "SunnyDay")) {
                            return chooseMove(move, activeBattlePokemon)
                        }
                    }
                }

                // todo GET THIS WORKING AS IT PROBABLY SHOULDN'T BE COMMENTED OUT
                // Setup moves
                if (
                    mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble() >= 0.95 &&
                    estimateMatchup(activeBattlePokemon, request, battle) > 0
                ) {
                    for (move in moveset.moves.filter { !it.disabled }) {
                        if (move.pp > 0 && boostFromMoves.contains(move.id) && (getNonZeroStats(move.id).keys.minOf {// todo this can have a null exception with lvl 50 pidgeot with tailwind
                                mon.boosts[it] ?: 0
                            } < 6)) {  // todo something with a lvl 50 pikachu caused this to null exception
                            if (!move.id.equals("curse") || ElementalTypes.GHOST !in mon.pokemon!!.types) {
                                return MoveActionResponse(move.id)
                            }
                        }
                    }
                }
                fun hasMajorStatusImmunity(target: ActiveTracker.TrackerPokemon) : Boolean {
                    // TODO: Need to check for Safeguard and Misty Terrain
                    return listOf("comatose", "purifyingsalt").contains(opponent.pokemon!!.ability.name) &&
                            (currentWeather == "sunny" && opponent.pokemon!!.ability.name == "leafguard");
                }

                // Status Inflicting Moves
                for (move in moveset.moves.filter { !it.disabled }) {
                    //val activeOpponent = opponent.pokemon
                    //activeOpponent?.let {
                    // Make sure the opponent doesn't already have a status condition
                    //if ((it.volatiles.containsKey("curse") || it.status != null) && // todo I removed this because idk why you would need to know if it had curse
                    if (activePlayerPokemonStatus == "" && (opponent.currentHp.toDouble() / opponent.pokemon!!.hp.toDouble()) > 0.3 && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) > 0.5) { // todo make sure this is the right status to use. It might not be

                        val status = (statusMoves.get(Moves.getByName(move.id)))
                        when (status) {
                            "brn" -> {
                                val typing = (activeTracker.p1Active.activePokemon.currentTypes?.contains("Fire") != true)
                                val stats = getBaseStats(opponent.pokemon!!, "atk") > 80
                                val notImmune = !hasMajorStatusImmunity(opponent)
                                val notAbility = !listOf("waterbubble", "waterveil", "flareboost", "guts", "magicguard").contains(opponent.pokemon!!.ability.name)

                                if (typing && stats && notImmune && notAbility) {
                                    return chooseMove(move, activeBattlePokemon)
                                }
                            }

                            "par" -> {
                                val typing = activeTracker.p1Active.activePokemon.currentTypes?.contains("Electric") != true
                                val stats = getBaseStats(opponent.pokemon!!, "spe") > getBaseStats(mon.pokemon!!, "spe")
                                val notImmune = !hasMajorStatusImmunity(opponent)
                                val notAbility = !listOf("limber", "guts").contains(opponent.pokemon!!.ability.name)

                                if (typing && stats && notImmune && notAbility) {
                                    return chooseMove(move, activeBattlePokemon)
                                }
                            }

                            "slp" -> {
                                val typing = activeTracker.p1Active.activePokemon.currentTypes?.contains("Grass") != true
                                val moveID = (move.id.equals("spore") || move.id.equals("sleeppowder"))
                                val notImmune = !hasMajorStatusImmunity(opponent)
                                val notAbility = !listOf("insomnia", "sweetveil").contains(opponent.pokemon!!.ability.name)

                                if (typing && moveID && notImmune && notAbility) {
                                    return chooseMove(move, activeBattlePokemon)
                                }
                            }

                            // todo weight the choice of doing this a bit lesser than the others maybe
                            "confusion" -> if (activePlayerPokemonVolatile != "confusion" && !listOf("owntempo", "oblivious").contains(opponent.pokemon!!.ability.name)) {
                                return chooseMove(move, activeBattlePokemon)
                            }

                            "psn" -> {
                                val typing = activeTracker.p1Active.activePokemon.currentTypes?.contains("Poison") != true && activeTracker.p1Active.activePokemon.currentTypes?.contains("Steel") != true
                                val notImmune = !hasMajorStatusImmunity(opponent)
                                val notAbility = !listOf("immunity", "poisonheal", "guts", "magicguard").contains(opponent.pokemon!!.ability.name)

                                if (typing && notImmune && notAbility) {
                                    return chooseMove(move, activeBattlePokemon)
                                }
                            }

                            "tox" -> { // todo need access to baseType and currentType to go further with this for type changing teams
                                val typing = activeTracker.p1Active.activePokemon.currentTypes?.contains("Poison") != true && activeTracker.p1Active.activePokemon.currentTypes?.contains("Steel") != true
                                val notImmune = !hasMajorStatusImmunity(opponent)
                                val notAbility = !listOf("immunity", "poisonheal", "guts", "magicguard").contains(opponent.pokemon!!.ability.name)

                                if (typing && notImmune && notAbility) {
                                    return chooseMove(move, activeBattlePokemon)
                                }
                            }

                            "cursed" -> if (activeNPCPokemonVolatile != "cursed" && activeTracker.p1Active.activePokemon.currentTypes?.contains("Ghost") != true
                                && !opponent.pokemon!!.ability.name.equals("magicguard")) {
                                return chooseMove(move, activeBattlePokemon)
                            }

                            "leech" -> if (activeNPCPokemonVolatile != "leech" && activeTracker.p1Active.activePokemon.currentTypes?.contains("Grass") != true
                                && !listOf("liquidooze", "magicguard").contains(opponent.pokemon!!.ability.name)) {
                                return chooseMove(move, activeBattlePokemon)
                            }
                        }
                    }
                    //}
                }

                // Accuracy lowering moves // todo seems to get stuck here. Try to check if it is an accuracy lowering move first before entering
                for (move in moveset.moves.filter { !it.disabled }) {
                    if (move.pp > 0 && 1 == 2 && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) == 1.0 && estimateMatchup(activeBattlePokemon, request, battle) > 0 &&
                        (opponent.boosts[Stats.ACCURACY] ?: 0) > accuracySwitchThreshold) {
                        return chooseMove(move, activeBattlePokemon)
                    }
                }

                // Protect style moves
                for (move in moveset.moves.filter { !it.disabled }) {
                    val activeOpponent = opponent.pokemon
                    if (move.pp > 0 && move.id in listOf("protect", "banefulbunker", "obstruct", "craftyshield", "detect", "quickguard", "spikyshield", "silktrap", "kingsshield")) {
                        // Stall out side conditions
                        if ((oppSideConditionList.intersect(setOf("tailwind", "lightscreen", "reflect", "trickroom")).isNotEmpty() &&
                                    monSideConditionList.intersect(setOf("tailwind", "lightscreen", "reflect")).isEmpty()) ||
                            //(activeOpponent?.volatiles?.containsKey("curse") == true || (activeOpponent?.status == null)) && // todo I think this is the wrong status
                            (activeOpponent?.status == null) && // todo I think this is the wrong status
                            mon.protectCount == 0 && opponent.pokemon!!.ability.name != "unseenfist") {
                            mon.protectCount = 3
                            return chooseMove(move, activeBattlePokemon)
                        }
                    }
                }

                // function for calculating the Damage of the move sent in
                fun calculateDamage(move: InBattleMove, mon: ActiveTracker.TrackerPokemon, opponent: ActiveTracker.TrackerPokemon, currentWeather: String?): Double {
                    val moveData = Moves.getByName(move.id)
                    /*var value = moveData!!.power
                    value *= if (moveData.elementalType in mon.pokemon!!.types) 1.5 else 1.0 // STAB
                    value *= if (moveData.damageCategory == DamageCategories.PHYSICAL) physicalRatio else specialRatio
                    //value *= moveData.accuracy // todo look into better way to take accuracy into account
                    value *= expectedHits(Moves.getByName(move.id)!!)
                    value *= moveDamageMultiplier(move.id, opponent.pokemon!!)*/

                    // Attempt at better estimation
                    val movePower = moveData!!.power
                    val pokemonLevel = mon.pokemon!!.level
                    val statRatio = if (moveData.damageCategory == DamageCategories.PHYSICAL) physicalRatio else specialRatio

                    val STAB = when {
                        moveData.elementalType in mon.pokemon!!.types && mon.pokemon!!.ability.name == "adaptability" -> 2.0
                        moveData.elementalType in mon.pokemon!!.types -> 1.5
                        else -> 1.0
                    }
                    val weather = when {
                        // Sunny Weather
                        currentWeather == "sunny" && (moveData.elementalType == ElementalTypes.FIRE || moveData.name == "hydrosteam") -> 1.5
                        currentWeather == "sunny" && moveData.elementalType == ElementalTypes.WATER && moveData.name != "hydrosteam" -> 0.5

                        // Rainy Weather
                        currentWeather == "raining" && moveData.elementalType == ElementalTypes.WATER-> 1.5
                        currentWeather == "raining" && moveData.elementalType == ElementalTypes.FIRE-> 0.5

                        // Add other cases below for weather

                        else -> 1.0
                    }
                    val damageTypeMultiplier = moveDamageMultiplier(move.id, opponent)
                    val burn = when {
                        opponent.pokemon!!.status?.status?.showdownName == "burn" && moveData.damageCategory == DamageCategories.PHYSICAL -> 0.5
                        else -> 1.0
                    }
                    //val hitsExpected = expectedHits(Moves.getByName(move.id)!!) // todo fix this as it has null issues

                    var damage = (((((2 * pokemonLevel) / 5 ) + 2) * movePower * statRatio) / 50 + 2)
                    damage *= weather
                    damage *= STAB
                    damage *= damageTypeMultiplier
                    damage *= burn
                    //damage *= hitsExpected

                    return damage
                }

                // function for finding the most damaging moves in the moveset
                fun mostDamagingMove(selectedMove: InBattleMove, moveset: ShowdownMoveset?, mon: ActiveTracker.TrackerPokemon, opponent: ActiveTracker.TrackerPokemon, currentWeather: String?): Boolean {
                    //val selectedMoveData = Moves.getByName(selectedMove.id)

                    if (moveset != null) {
                        for (move in moveset.moves.filter { !it.disabled && it.id != selectedMove.id}) {

                            if (calculateDamage(move, mon, opponent, currentWeather) > calculateDamage(selectedMove, mon, opponent, currentWeather)) {
                                return false
                            }
                        }

                        return calculateDamage(selectedMove, mon, opponent, currentWeather) > 0
                    }
                    else
                        return false
                    return false
                }

                // Damage dealing moves
                val moveValues = mutableMapOf<InBattleMove, Double>()
                for (move in moveset.moves.filter { !it.disabled }) {
                    val moveData = Moves.getByName(move.id)
                    /**//*var value = moveData!!.power
                value *= if (moveData.elementalType in mon.pokemon!!.types) 1.5 else 1.0 // STAB
                value *= if (moveData.damageCategory == DamageCategories.PHYSICAL) physicalRatio else specialRatio
                //value *= moveData.accuracy // todo look into better way to take accuracy into account
                value *= expectedHits(Moves.getByName(move.id)!!)
                value *= moveDamageMultiplier(move.id, opponent.pokemon!!)*//*

                // Attempt at better estimation
                val movePower = moveData!!.power
                val pokemonLevel = mon.pokemon!!.level
                val statRatio = if (moveData.damageCategory == DamageCategories.PHYSICAL) physicalRatio else specialRatio

                val STAB = when {
                    moveData.elementalType in mon.pokemon!!.types && mon.pokemon!!.ability.name == "adaptability" -> 2.0
                    moveData.elementalType in mon.pokemon!!.types -> 1.5
                    else -> 1.0
                }
                val weather = when {
                    // Sunny Weather
                    currentWeather == "sunny" && (moveData.elementalType == ElementalTypes.FIRE || moveData.name == "hydrosteam") -> 1.5
                    currentWeather == "sunny" && moveData.elementalType == ElementalTypes.WATER && moveData.name != "hydrosteam" -> 0.5

                    // Rainy Weather
                    currentWeather == "raining" && moveData.elementalType == ElementalTypes.WATER-> 1.5
                    currentWeather == "raining" && moveData.elementalType == ElementalTypes.FIRE-> 0.5

                    // Add other cases below for weather

                    else -> 1.0
                }
                val damageTypeMultiplier = moveDamageMultiplier(move.id, opponent)
                val burn = when {
                    opponent.pokemon!!.status?.status?.showdownName == "burn" && moveData.damageCategory == DamageCategories.PHYSICAL -> 0.5
                    else -> 1.0
                }
                //val hitsExpected = expectedHits(Moves.getByName(move.id)!!) // todo fix this as it has null issues

                var damage = (((((2 * pokemonLevel) / 5 ) + 2) * movePower * statRatio) / 50 + 2)
                damage *= weather
                damage *= STAB
                damage *= damageTypeMultiplier
                damage *= burn
                //damage *= hitsExpected*/


                    // calculate initial damage of this move
                    var value = calculateDamage(move, mon, opponent, currentWeather) // set value to be the output of damage to start with


                    // HOW DAMAGE IS ACTUALLY CALCULATED
                    // REFERENCES: https://bulbapedia.bulbagarden.net/wiki/Damage
                    // Damage = (((((2 * pokemon.level) / 5 ) + 2) * move.power * (mon.attackStat / opponent.defenseStat)) / 50 + 2)
                    // Damage *= Targets // 0.75 (0.5 in Battle Royals) if the move has more than one target when the move is executed, and 1 otherwise.
                    // Damage *= PB // 0.25 (0.5 in Generation VI) if the move is the second strike of Parental Bond, and 1 otherwise
                    // Damage *= Weather // 1.5 if a Water-type move is being used during rain or a Fire-type move or Hydro Steam during harsh sunlight, and 0.5 if a Water-type move (besides Hydro Steam) is used during harsh sunlight or a Fire-type move during rain, and 1 otherwise or if any Pokémon on the field have the Ability Cloud Nine or Air Lock.
                    // Damage *= GlaiveRush // 2 if the target used the move Glaive Rush in the previous turn, or 1 otherwise.
                    // Damage *= Critical // 1.5 (2 in Generation V) for a critical hit, and 1 otherwise. Decimals are rounded down to the nearest integer. It is always 1 if the target's Ability is Battle Armor or Shell Armor or if the target is under the effect of Lucky Chant.
                    // Damage *= randomNumber // random number between .85 and 1.00
                    // Damage *= STAB // 1.5 if mon.types is equal to move.type or if it is a combined Pledge move || 2.0 if it has adaptability || Terra gimmick has other rules
                    // Damage *= Type // type damage multipliers || CHeck website for additional rules for some moves
                    // Damage *= Burn // 0.5 if the pokemon is burned, its Ability is not Guts, and the used move is a physical move (other than Facade from Generation VI onward), and 1
                    // Damage *= Other // 1 in most cases, and a different multiplier when specific interactions of moves, Abilities, or items take effect, in this order
                    // Damage *= ZMove // 1 usually OR 0.25 if the move is a Z-Move, Max Move, or G-Max Move being used into a protection move
                    // Damage *= TeraShield // ONLY for Terra raid battles


                    // Handle special cases
                    if (move.id.equals("fakeout")) {
                        value = 0.0
                    }

                    if (move.id.equals("synchronoise")
                        && !(mon.pokemon!!.types.any { it in opponent.pokemon!!.types })) {
                        value = 0.0
                    }

                    // todo last resort: only does damage if all other moves have been used at least once (switchout resets this)

                    // todo focus punch

                    // todo if PP gets lowered to zero does it still try to use it?

                    // todo slack off

                    // todo soak

                    if (move.id.equals("soak"))
                    // if opposing pokemon is steel type or poison type value this higher
                        if (activeTracker.p1Active.activePokemon.currentTypes?.contains("Steel") == true && activeTracker.p1Active.activePokemon.currentTypes?.contains("Poison") == true)
                            value = 200.0 // change this to not be so hardcoded but valued for different circumstances



                    // todo stealth rock. Make list of all active hazards to get referenced

                    val opponentAbility = opponent.pokemon!!.ability
                    if ((opponentAbility.template.name.equals("lightningrod") && moveData!!.elementalType == ElementalTypes.ELECTRIC) ||
                        (opponentAbility.template.name.equals("flashfire") && moveData!!.elementalType == ElementalTypes.FIRE) ||
                        (opponentAbility.template.name.equals("levitate") && moveData!!.elementalType == ElementalTypes.GROUND) ||
                        (opponentAbility.template.name.equals("sapsipper") && moveData!!.elementalType == ElementalTypes.GRASS) ||
                        (opponentAbility.template.name.equals("motordrive") && moveData!!.elementalType == ElementalTypes.ELECTRIC) ||
                        (opponentAbility.template.name.equals("stormdrain") && moveData!!.elementalType == ElementalTypes.WATER) ||
                        (opponentAbility.template.name.equals("voltabsorb") && moveData!!.elementalType == ElementalTypes.ELECTRIC) ||
                        (opponentAbility.template.name.equals("waterabsorb") && moveData!!.elementalType == ElementalTypes.WATER) ||
                        (opponentAbility.template.name.equals("immunity") && moveData!!.elementalType == ElementalTypes.POISON) ||
                        (opponentAbility.template.name.equals("eartheater") && moveData!!.elementalType == ElementalTypes.GROUND) ||
                        (opponentAbility.template.name.equals("suctioncup") && moveData!!.name == "roar" || moveData!!.name == "whirlwind")
                    ) {
                        value = 0.0
                    }

                    // reduce value of Pivot moves if user doesn't want to switchout anyways todo unless maybe it was the only damaging move and needs to
                    if(move.id in pivotMoves && (!shouldSwitchOut(request, battle, activeBattlePokemon) && !mostDamagingMove(move, moveset, mon, opponent, currentWeather)))
                        value = 0.0

                    if (move.pp == 0)
                        value = 0.0

                    moveValues[move] = value
                }

                // uncommment this and try to get it to behave itself. Wants to return no matter what so deal with it later
                val bestMoveValue = moveValues.maxByOrNull { it.value }?.value ?: 0.0
                val bestMove = moveValues.entries.firstOrNull { it.value == bestMoveValue }?.key
                val target = if (bestMove!!.mustBeUsed()) null else bestMove.target.targetList(activeBattlePokemon)
                if (allMoves != null) {
                    if (allMoves.none { it.id == "recharge" || it.id == "struggle" }) {  //"recharge" !in moveValues) {
                        if (target == null) {
                            return MoveActionResponse(bestMove.id)
                        }
                        else {
                            //return MoveActionResponse(getMoveSlot(bestMove, allMoves))//, false) //shouldDynamax(request, canDynamax))
                            val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull()
                                ?: target.random()

                            return MoveActionResponse(bestMove.id, (chosenTarget as ActiveBattlePokemon).getPNX())
                        }
                    } else {
                        if (target == null) {
                            return MoveActionResponse(allMoves.first().id)
                        }
                        else{
                            val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull()
                                ?: target.random()

                            return MoveActionResponse(allMoves.first().id, (chosenTarget as ActiveBattlePokemon).getPNX())
                        }

                        //?: Moves.getByName("struggle")!!.name) //, false) //shouldDynamax(request, canDynamax))
                    }
                }

            }

            // healing wish (dealing with it here because you'd only use it if you should switch out anyway)
            for (move in moveset.moves.filter { !it.disabled }) {
                if (move.id.equals("healingwish") && (mon.currentHp.toDouble() / mon.pokemon!!.hp.toDouble()) < selfKoMoveMatchupThreshold) {
                    return chooseMove(move, activeBattlePokemon)
                }
            }

            // switch out
            if (shouldSwitchOut(request, battle, activeBattlePokemon)) {
                val availableSwitches = p1Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }
                val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(activeBattlePokemon, request, battle, it.effectedPokemon) }
                /*availableSwitches.forEach {
                    estimateMatchup(request, battle, it.effectedPokemon)
                }*/
                val bestMatchup = availableSwitches.find { estimateMatchup(activeBattlePokemon, request, battle, it.effectedPokemon) == bestEstimation }
                bestMatchup?.let {
                    return SwitchActionResponse(it.uuid)
                    //Pair("switch ${getPokemonPos(request, it)}", canDynamax)
                }
            }
            mon.firstTurn = 0

            // otherwise can't find a good option so use a random move
            //return Pair(prng.sample(moves.map { it.choice }), false)
            if (moveset == null) {
                return PassActionResponse
            }
            val move = moveset.moves
                .filter { it.canBeUsed() }
                .filter { it.mustBeUsed() || it.target.targetList(activeBattlePokemon)?.isEmpty() != true }
                .randomOrNull()
                ?: return MoveActionResponse("struggle")

            return chooseMove(move, activeBattlePokemon)
            /*val target = if (move.mustBeUsed()) null else move.target.targetList(activeBattlePokemon)
            return if (target == null) {
                MoveActionResponse(move.id)
            } else {
                // prioritize opponents rather than allies
                val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull() ?: target.random()
                MoveActionResponse(move.id, (chosenTarget as ActiveBattlePokemon).getPNX())
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
            activeBattlePokemon.battle.players.forEach { it.sendMessage(Text.literal(
                Formatting.RED.toString() +
                "An error occurred in the Strong Trainer AI, please report this to the devs."
            )) }
            return PassActionResponse
        }
    }

    // test
    // to estimate party matchup against opposing active pokemon after FAINT
    fun estimatePartyMatchup(request: ShowdownActionRequest, battle: PokemonBattle, nonActiveMon: Pokemon): Double {
        //updateActiveTracker(battle)

        ////nonActiveMon?.let { npcPokemon = it }
        val playerPokemon = activeTracker.p1Active.activePokemon.pokemon
        val npcPokemon = activeTracker.p2Active.activePokemon.pokemon


        var score = 1.0
        //score += bestDamageMultiplier(nonActiveMon, playerPokemon!!) * (1 + typeMatchup(nonActiveMon, playerPokemon) * typeMatchupWeightConsideration) // npcPokemon attacking playerPokemon
        //score -= bestDamageMultiplier(playerPokemon, nonActiveMon) * (1 + typeMatchup(playerPokemon, nonActiveMon) * typeMatchupWeightConsideration) // playerPokemon attacking npcPokemon

        score += (bestDamageMultiplier(nonActiveMon, playerPokemon!!) * moveDamageWeightConsideration) + (typeMatchup(nonActiveMon, playerPokemon) * typeMatchupWeightConsideration) // npcPokemon attacking playerPokemon
        score -= (bestDamageMultiplier(playerPokemon, nonActiveMon) * moveDamageWeightConsideration) + (typeMatchup(playerPokemon, nonActiveMon) * typeMatchupWeightConsideration) // playerPokemon attacking npcPokemon


        // todo make function that determines matchup for attacker type vs defender type to have higher weight to not switch into bad type matchups
        //score *= typeMatchup(nonActiveMon, playerPokemon)
        //score -= typeMatchup(playerPokemon, nonActiveMon)

        // todo consider base stat ratios for switchouts

        if (getBaseStats(nonActiveMon, "spe") > getBaseStats(playerPokemon, "spe")) {
            score += speedTierCoefficient * trickRoomCoefficient
        } else if (getBaseStats(playerPokemon, "spe") > getBaseStats(nonActiveMon, "spe")) {
            score -= speedTierCoefficient * trickRoomCoefficient
        }

        // todo possibly flip these p1 and p2 around as well since p1 is player I think
        if (request.side?.id == "p1") {
            score += if (nonActiveMon != null) (nonActiveMon.currentHealth * hpFractionCoefficient) * hpWeightConsideration
            else (activeTracker.p1Active.activePokemon.currentHp * hpFractionCoefficient) * hpWeightConsideration
            score -= (activeTracker.p2Active.activePokemon.currentHp * hpFractionCoefficient) * hpWeightConsideration
        } else {
            score += if (nonActiveMon != null) (nonActiveMon.currentHealth * hpFractionCoefficient) * hpWeightConsideration
            else (activeTracker.p2Active.activePokemon.currentHp * hpFractionCoefficient) * hpWeightConsideration
            score -= (activeTracker.p1Active.activePokemon.currentHp * hpFractionCoefficient) * hpWeightConsideration
        }

        // add value to a pokemon with stat boost removal moves/abilities/items
        if ((activeTracker.p1Active.activePokemon.atkBoost > 1 || activeTracker.p1Active.activePokemon.spaBoost > 1)
            && (nonActiveMon.moveSet.any { it.name in antiBoostMoves } || nonActiveMon.ability.name == "unaware")) {
            score += antiBoostWeightConsideration
        }

        return score
    }

    // estimate mid-battle switch in value
    fun estimateMatchup(activeBattlePokemon: ActiveBattlePokemon, request: ShowdownActionRequest, battle: PokemonBattle, nonActiveMon: Pokemon? = null): Double {
        updateActiveTracker(activeBattlePokemon, request, battle)
        val battlePokemon = getCurrentPlayer(battle)
        var playerPokemon = battlePokemon.first
        var npcPokemon = battlePokemon.second
        nonActiveMon?.let { npcPokemon = it }

        // todo get count of moves on player side that are PHYSICAL
        // todo get count of moves on player side that are SPECIAL
        // todo Determine if it is a special or physical attacker
        // todo Determine value of matchup based on that attack type against the Defensive stats of the pokemon

        var score = 1.0
        score += (bestDamageMultiplier(npcPokemon, playerPokemon) * moveDamageWeightConsideration) + (typeMatchup(npcPokemon, playerPokemon) * typeMatchupWeightConsideration) // npcPokemon attacking playerPokemon
        score -= (bestDamageMultiplier(playerPokemon, npcPokemon) * moveDamageWeightConsideration) + (typeMatchup(playerPokemon, npcPokemon) * typeMatchupWeightConsideration) // playerPokemon attacking npcPokemon

        //score += bestDamageMultiplier(npcPokemon, playerPokemon) * (1.0 + typeMatchup(npcPokemon, playerPokemon) * typeMatchupWeightConsideration) // npcPokemon attacking playerPokemon
        //score -= bestDamageMultiplier(playerPokemon, npcPokemon) * (1.0 + typeMatchup(playerPokemon, npcPokemon) * typeMatchupWeightConsideration) // playerPokemon attacking npcPokemon


        //score *= typeMatchup(nonActiveMon, playerPokemon)

        if (getBaseStats(npcPokemon, "spe") > getBaseStats(playerPokemon, "spe")) {
            score += speedTierCoefficient * trickRoomCoefficient
        } else if (getBaseStats(playerPokemon, "spe") > getBaseStats(npcPokemon, "spe")) {
            score -= speedTierCoefficient * trickRoomCoefficient
        }

        // HP comparisons
        if (request.side?.id == "p1") {
            score += if (nonActiveMon != null) (nonActiveMon.hp * hpFractionCoefficient) * hpWeightConsideration
            else (activeTracker.p1Active.activePokemon.pokemon!!.hp * hpFractionCoefficient) * hpWeightConsideration
            score -= (activeTracker.p2Active.activePokemon.pokemon!!.hp * hpFractionCoefficient) * hpWeightConsideration
        } else {
            score += if (nonActiveMon != null) (nonActiveMon.hp * hpFractionCoefficient) * hpWeightConsideration
            else (activeTracker.p2Active.activePokemon.pokemon!!.hp * hpFractionCoefficient) * hpWeightConsideration
            score -= (activeTracker.p1Active.activePokemon.pokemon!!.hp * hpFractionCoefficient) * hpWeightConsideration
        }

        // add value to a pokemon with stat boost removal moves/abilities/items
        if ((activeTracker.p1Active.activePokemon.atkBoost > 1 || activeTracker.p1Active.activePokemon.spaBoost > 1)
            && (npcPokemon.moveSet.any { it.name in antiBoostMoves } || npcPokemon.ability.name == "unaware")) {
            score += antiBoostWeightConsideration
        }

        return score
    }

    /*fun estimateMatchupTeamPreview(nonActiveMon: Pokemon, nonActiveOpp: Pokemon): Double {
        val monName = nonActiveMon.species.name
        val oppName = nonActiveOpp.species.name

        var score = 1.0
        score += bestDamageMultiplier(monName, oppName) //todo check what the hell this does
        score -= bestDamageMultiplier(oppName, monName)

        if (getBaseStats(nonActiveMon, "spe") > getBaseStats(nonActiveOpp, "spe")) {
            score += speedTierCoefficient
        } else if (getBaseStats(nonActiveOpp, "spe") > getBaseStats(nonActiveMon, "spe")) {
            score -= speedTierCoefficient
        }

        // Calculate max HP for opponent
        val oppHp = (((2 * getBaseStats(nonActiveOpp, "hp") + getIVs(nonActiveOpp, "hp") + getEVs(nonActiveOpp, "hp") / 4) * nonActiveOpp.level) / 100 + nonActiveOpp.level + 10).toInt()
        score += getCurrentHp(nonActiveMon.condition) * hpFractionCoefficient
        score -= oppHp * hpFractionCoefficient

        return score
    }*/

    fun shouldDynamax(activeBattlePokemon: ActiveBattlePokemon, request: ShowdownActionRequest, battle: PokemonBattle, canDynamax: Boolean): Boolean {
        updateActiveTracker(activeBattlePokemon, request, battle)
        if (canDynamax) {
            //val (mon, opponent) = getCurrentPlayer(battle)

            val mon = activeTracker.p1Active
            val opponent = activeTracker.p2Active

            // if active mon is the last full HP mon
            if (request.side?.pokemon?.count { getHpFraction(it.condition) == 1.0 } == 1 /*&& mon.currentHp == 1*/) {
                return true
            }

            // Matchup advantage and full hp on full hp
            if (estimateMatchup(activeBattlePokemon, request, battle) > 0 && (mon.activePokemon.currentHp.toDouble() / mon.activePokemon.pokemon!!.hp.toDouble()) == 1.0 && (opponent.activePokemon.currentHp.toDouble() / opponent.activePokemon.pokemon!!.hp.toDouble()) == 1.0) {
                return true
            }

            // last pokemon
            if (request.side?.pokemon?.count { getHpFraction(it.condition) != 0.0 } == 1 && (mon.activePokemon.currentHp.toDouble() / mon.activePokemon.pokemon!!.hp.toDouble()) == 1.0) {
                return true
            }
        }
        return false
    }

    fun shouldSwitchOut(request: ShowdownActionRequest, battle: PokemonBattle, activeBattlePokemon: ActiveBattlePokemon): Boolean {
        updateActiveTracker(activeBattlePokemon, request, battle)

        val p1Actor = battle.side1.actors.first()
        val p2Actor = battle.side2.actors.first()

        val (mon, opponent) = if (activeBattlePokemon.battlePokemon!!.effectedPokemon.uuid == activeTracker.p1Active.activePokemon.pokemon!!.uuid) {
            Pair(activeTracker.p1Active, activeTracker.p2Active)
        } else {
            Pair(activeTracker.p2Active, activeTracker.p1Active)
        }

        //val (mon, opponent) = getCurrentPlayer(battle)
        val playerActivePokemon = activeTracker.p1Active

        val npcActivePokemon = activeTracker.p2Active

        //val requestActor = request.side?.
        //val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) != 0.0 }
        //val playerAvailableSwitches =
        val availableSwitches = p2Actor.pokemonList.filter { it.uuid != mon.activePokemon.pokemon!!.uuid && it.health > 0 }

        val currentNPCAbility = request.side?.pokemon!!.first().ability

        val isTrapped = request.active?.any { it ->
            it.trapped ?: false // todo is this the right trapped? Probably not
        }

        // todo add some way to keep track of the player's boosting to see if it needs to switch out to something that can stop it

        // if slower speed stat than the opposing pokemon and HP is less than 20% don't switch out
        if ((npcActivePokemon.activePokemon.currentHp.toDouble() / npcActivePokemon.activePokemon.pokemon!!.hp.toDouble()) < hpSwitchOutThreshold && (npcActivePokemon.activePokemon.pokemon!!.species.baseStats[Stats.SPEED]!! < playerActivePokemon.activePokemon.pokemon!!.species.baseStats[Stats.SPEED]!!)) {
            return false
        }

        // if the npc pokemon was given Truant then switch it out if it is not it's base ability
        if ((!npcActivePokemon.activePokemon.pokemon!!.species.abilities.any { it.template == Abilities.get("truant") } && (currentNPCAbility == "truant")) || (!npcActivePokemon.activePokemon.pokemon!!.species.abilities.any { it.template == Abilities.get("slowstart") } && currentNPCAbility == "slowstart"))
            return true

// maybe use pokemon.species to compare to active pokemon
        // todo add more reasons to switch out
        // If there is a decent switch in and not trapped...
        if (availableSwitches != null) {
            //if (availableSwitches.any { estimateMatchup(request) > 0 } && !request.side?.pokemon.trapped) {
            if (availableSwitches.any { estimateMatchup(activeBattlePokemon, request, battle, it.effectedPokemon) > 0 } && !isTrapped!!) {
                //if (availableSwitches.any { estimateMatchup(request, battle, it.effectedPokemon) > 0 } && !isTrapped!!) {
                // ...and a 'good' reason to switch out
                if ((playerActivePokemon.activePokemon.boosts[Stats.ACCURACY] ?: 0) <= accuracySwitchThreshold) {
                    return true
                }
                if ((playerActivePokemon.activePokemon.boosts[Stats.DEFENCE] ?: 0) <= -3 || (playerActivePokemon.activePokemon.boosts[Stats.SPECIAL_DEFENCE] ?: 0) <= -3) {
                    return true
                }
                if ((playerActivePokemon.activePokemon.boosts[Stats.ATTACK] ?: 0) <= -3 && (playerActivePokemon.activePokemon.stats[Stats.ATTACK]
                        ?: 0) >= (playerActivePokemon.activePokemon.stats[Stats.SPECIAL_ATTACK] ?: 0)) {
                    return true
                }
                if ((playerActivePokemon.activePokemon.boosts[Stats.SPECIAL_ATTACK] ?: 0) <= -3 && (playerActivePokemon.activePokemon.stats[Stats.ATTACK]
                        ?: 0) <= (playerActivePokemon.activePokemon.stats[Stats.SPECIAL_ATTACK] ?: 0)) {
                    return true
                }
                if ((estimateMatchup(activeBattlePokemon, request, battle) < switchOutMatchupThreshold) && (npcActivePokemon.activePokemon.currentHp.toDouble() / npcActivePokemon.activePokemon.pokemon!!.hp.toDouble()) > hpSwitchOutThreshold) {
                    return true
                }
            }
        }
        return false
    }

    fun statEstimationActive(mon: ActiveTracker.TrackerPokemon, stat: Stat): Double {
        val boost = mon.boosts[stat] ?: 0

        val actualBoost = if (boost > 1) {
            (2 + boost) / 2.0
        } else {
            2 / (2.0 - boost)
        }

        val baseStat = getBaseStats(mon.pokemon!!, stat.showdownId) ?: 0
        return ((2 * baseStat + 31) + 5) * actualBoost
    }

    fun statEstimation(mon: Pokemon, stat: Stat): Double {
        //val baseStat = getBaseStats(mon, stat.showdownId) ?: 0
        //val speedStat = ((2.0 * baseStat + 31.0) + 5.0)
        return getBaseStats(mon, stat.showdownId).toDouble() ?: 0.0
    }

    // gets the slot number of the passed-in move
    fun getMoveSlot(move: String, possibleMoves: List<InBattleMove>?): String {
        val bestMoveSlotIndex = possibleMoves?.indexOfFirst { it.id == move }?.plus(1)
        return "move $bestMoveSlotIndex"
    }

    // gets the slot number of the bestMatchup pokemon in the team
    /*fun getPokemonPos(request: ShowdownActionRequest, bestMatchup: Pokemon): Int {
        return request.side?.pokemon?.indexOfFirst {
            it.details == bestMatchup.details && getHpFraction(it.condition) > 0 && !it.active
        } + 1
    }*/

    // returns an approximate number of hits for a given move for estimation purposes
    /*fun expectedHits(move: String): Double {
        val moveData = dex.getMove(move) // todo find equivalent of what this wants
        return when (move) {
            "triplekick", "tripleaxel" -> 1 + 2 * 0.9 + 3 * 0.81
            "populationbomb" -> 7.0
            else -> moveData.multihit?.let { (2 + 3) / 3.0 + (4 + 5) / 6.0 } ?: 1.0
        }
    }*/

    /*fun chooseSwitch(request: ShowdownActionRequest, battle: PokemonBattle, switches: List<SwitchOption>): Int {
        updateActiveTracker(battle)
        val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) > 0 }
        if (availableSwitches!!.isEmpty()) return 1

        val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(request, it) }
        val bestMatchup = availableSwitches.find { estimateMatchup(request, it) == bestEstimation }
        getCurrentPlayer(battle)[0].firstTurn = 1

        return bestMatchup?.let { getPokemonPos(request, it) } ?: 1
    }*/

    /*fun chooseTeamPreview(request: ShowdownActionRequest, battle: PokemonBattle, team: List<AnyObject>): String {
        updateActiveTracker(battle)

        // Uncomment the following line to enable the bot to choose the best mon based on the opponent's team
        // return "team 1"

        val mons = request.side?.pokemon
        val opponentPokemon = request.side.foe.pokemon.map { it.set }
        var bestMon: Pokemon? = null
        var bestAverage: Double? = null

        for (mon in mons) {
            val matchups = opponentPokemon.map { opp -> estimateMatchupTeamPreview(mon, opp) }
            val average = matchups.sum() / matchups.size
            if (bestAverage == null || average > bestAverage) {
                bestMon = mon
                bestAverage = average
            }
        }

        // If you have a pokemon with some setup move that will benefit other pokemon on the team, use that first
        for (mon in mons) {
            for (move in mon.moves) {
                if (weatherSetupMoves.containsKey(move.id) || entryHazards.contains(move.id) ||
                        setupMoves.contains(move.id)) {
                    bestMon = mon
                    break
                }
            }
        }

        getCurrentPlayer(battle)[0].firstTurn = 1
        return "team ${bestMon?.position?.plus(1)}"
    }*/

    // moveID: the move used as a string
    // defender: the activeTracker Pokemon that the move is being used on
    fun moveDamageMultiplier(moveID: String, defender: ActiveTracker.TrackerPokemon): Double {
        val move = Moves.getByName(moveID)
        // repeat the list building for each entry in the list
        var typeList = mutableListOf<ElementalType>()

        defender.currentTypes?.forEach {
            ElementalTypes.get(it.lowercase())?.let { it1 -> typeList.add(it1) }
        }

        val defenderTypes = typeList // set the type list of the current defender
        var multiplier = 1.0

        for (defenderType in defenderTypes)
            multiplier *= (getDamageMultiplier(move!!.elementalType, defenderType) ?: 1.0)

        return multiplier
    }

    // returns the best multiplier of an attacking move in the attacking pokemon's move list to deal with the defending pokemon's typing
    fun bestDamageMultiplier(attacker: Pokemon, defender: Pokemon): Double { // todo copy all to make overload
        //val typeMatchups = JSON.parse(File("../Data/UsefulDatasets/type-chart.json").readText())
        //val atkMoveType = attackMove.type
        val attackerMoves = attacker.moveSet.getMoves()

        val defenderTypes = defender.types

        var multiplier = 1.0
        var bestMultiplier = 1.0

        for (attackerMove in attackerMoves) {
            for (defenderType in defenderTypes) {
                multiplier *= (getDamageMultiplier(attackerMove.type, defenderType) ?: 1.0)
            }

            if (multiplier > bestMultiplier) {
                bestMultiplier = multiplier
            }

            multiplier = 1.0
        }

        return bestMultiplier
    }

    fun typeMatchup (attackingPokemon: Pokemon, defendingPokemon: Pokemon): Double {
        val attackerTypes = attackingPokemon.types
        val defenderTypes = defendingPokemon.types

        var multiplier = 1.0
        //var bestTypeMultiplier = 1.0

        for (atkType in attackerTypes) {
            for (defType in defenderTypes) {
                multiplier *= (getDamageMultiplier(atkType, defType) ?: 1.0)
            }
        }

        /*for (atkType in attackerTypes) {
            for (defType in defenderTypes) {
                multiplier *= (getDamageMultiplier(atkType, defType) ?: 1.0)
            }

            if (multiplier > bestTypeMultiplier) {
                bestTypeMultiplier = multiplier
            }

            multiplier = 1.0
        }*/

        //return bestTypeMultiplier

        return multiplier
    }

    // The move options provided by the simulator have been converted from the name
    // which we're tracking, so we need to convert them back.
    /*private fun fixMove(m: Move): String {
        val id = toID(m.move)
        return when {
            id.startsWith("return") -> "return"
            id.startsWith("frustration") -> "frustration"
            id.startsWith("hiddenpower") -> "hiddenpower"
            else -> id
        }
    }*/

    fun isBoosted(trackerPokemon: ActiveTracker.TrackerPokemon): Boolean {
        if (trackerPokemon.atkBoost > boostWeightCoefficient
            || trackerPokemon.defBoost > boostWeightCoefficient
            || trackerPokemon.spaBoost > boostWeightCoefficient
            || trackerPokemon.spdBoost > boostWeightCoefficient
            || trackerPokemon.speBoost > boostWeightCoefficient)
            return true
        else
            return false
    }

    // returns an approximate number of hits for a given move for estimation purposes
    fun expectedHits(move: MoveTemplate): Int {
        val minMaxHits = multiHitMoves[move.name]
        if (move.name == "triplekick" || move.name == "tripleaxel") {
            //Triple Kick and Triple Axel have an accuracy check for each hit, and also
            //rise in BP for each hit
            return (1 + 2 * 0.9 + 3 * 0.81).toInt()
        }
        if (move.name == "populationbomb") {
            // population bomb hits until it misses, 90% accuracy
            return 7
        }
        if (minMaxHits == null)
        // non multihit move
            return 1
        else if (minMaxHits[0] == minMaxHits[1])
            return minMaxHits[0]!!
        else {
            // It hits 2-5 times
            return (2 + 3) / 3 + (4 + 5) / 6
        }
    }

    private fun getHpFraction(condition: String): Double {
        if (condition == "0 fnt") return 0.0
        val (numerator, denominator) = condition.split('/').map { it.toInt() }
        return numerator.toDouble() / denominator
    }

    /*private fun getCurrentHp(condition: String): Int {
        if (condition == "0 fnt") return 0
        return condition.split('/')[0].toInt()
    }*/

    private fun getNonZeroStats(name: String): Map<Stat, Int> {
        return boostFromMoves[name] ?: emptyMap()
        //boostFromMoves.filterKeys { it == name }.filter { return it.value ?: emptyMap() }
    }

    private fun updateActiveTracker(activeBattlePokemon: ActiveBattlePokemon, request: ShowdownActionRequest, battle: PokemonBattle) {
        val playerActor = activeBattlePokemon.battle.side1.actors.get(0)
        val npcActor = activeBattlePokemon.battle.side2.actors.get(0)

        val playerPosBoostContext = battle.side1.actors.first().activePokemon[0].battlePokemon?.contextManager?.get(BattleContext.Type.BOOST)
        val playerNegBoostContext = battle.side1.actors.first().activePokemon[0].battlePokemon?.contextManager?.get(BattleContext.Type.UNBOOST)

        // I think is the first side pokemon (player)
        val p1 = activeTracker.p1Active
        val pokemon1 = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon

        val playerATKPosBoosts = playerPosBoostContext?.count { it.id == "atk" } ?: 0
        val playerDEFPosBoosts = playerPosBoostContext?.count { it.id == "def" } ?: 0
        val playerSPAPosBoosts = playerPosBoostContext?.count { it.id == "spa" } ?: 0
        val playerSPDPosBoosts = playerPosBoostContext?.count { it.id == "spd" } ?: 0
        val playerSPEPosBoosts = playerPosBoostContext?.count { it.id == "spe" } ?: 0

        val playerATKNegBoosts = playerNegBoostContext?.count { it.id == "atk" } ?: 0
        val playerDEFNegBoosts = playerNegBoostContext?.count { it.id == "def" } ?: 0
        val playerSPANegBoosts = playerNegBoostContext?.count { it.id == "spa" } ?: 0
        val playerSPDNegBoosts = playerNegBoostContext?.count { it.id == "spd" } ?: 0
        val playerSPENegBoosts = playerNegBoostContext?.count { it.id == "spe" } ?: 0

        val p1Boosts = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.statChanges
        val playerSide1 = battle.side1.actors.first()
        val numPlayerPokemon = playerSide1.pokemonList.count()


        /*val lastMajorBattleMessage = if (battle.majorBattleActions.entries.isNotEmpty()) battle.majorBattleActions?.entries?.last()?.value?.rawMessage else ""
        val lastMinorBattleMessage = if (battle.minorBattleActions.entries.isNotEmpty()) battle.minorBattleActions?.entries?.last()?.value?.rawMessage else ""
        val lastBattleState = battle.battleLog
        var currentType: String? = p1.activePokemon.currentPrimaryType
        // test parsing of the Type change
        val typeChangeIndex = lastMinorBattleMessage?.indexOf("typechange|")

        if (typeChangeIndex != -1) {
            // Add the length of "typechange|" to start from the end of this substring
            val startIndex = typeChangeIndex?.plus("typechange|".length)

            // Find the index of the next "|"
            val endIndex = startIndex?.let { lastMinorBattleMessage?.indexOf('|', it).takeIf { it!! >= 0 } }
                    ?: lastMinorBattleMessage?.length

            if (startIndex != null && endIndex != null ) {
                // Extract the substring
                val result = lastMinorBattleMessage.substring(startIndex, endIndex)

                // grab and store the type change
                currentType = ElementalTypes.get(result.lowercase())?.name
                //p1.activePokemon.currentPrimaryType
            }
        }*/
        // todo parse the battle message and grab the elemental typing after the |



        // todo find out how to get stats
        //val p1Stats = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.

        // convert p1Boosts to a regular Map rather than a MutableMap
        //val p1BoostsMap = p1Boosts?.mapKeys { it.key.toString() } ?: mapOf()
        val p1BoostsMap = p1Boosts?.mapKeys { it.key } ?: mapOf()

        // opposing pokemon to the first side pokemon
        val p2 = activeTracker.p2Active


        val pokemon2 = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon
        val p2Boosts = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.statChanges
        val playerSide2 = battle.side2.actors.first()
        val numNPCPokemon = playerSide2.pokemonList.count()

        //val nextAvailablePokemon = playerSide2.pokemonList.filter { it.health != 0 }.first().effectedPokemon.uuid
        // todo make nice function for knowing what is the best switchout

        // convert p2Boosts to a regular Map rather than a MutableMap
        //val p2BoostsMap = p2Boosts?.mapKeys { it.key.toString() } ?: mapOf()
        val p2BoostsMap = p2Boosts?.mapKeys { it.key } ?: mapOf()

        p1.activePokemon = getActiveTrackerPokemon(p1, pokemon1?.uuid)
        p1.activePokemon.pokemon = pokemon1
        p1.activePokemon.species = pokemon1!!.species.name
        p1.activePokemon.currentHp = pokemon1.currentHealth
        p1.activePokemon.currentHpPercent = (pokemon1.currentHealth.toDouble() / pokemon1.hp.toDouble()) // todo this is not syncing. Possibly needs syncActivePokemon called later
        p1.activePokemon.boosts = p1BoostsMap
        p1.activePokemon.atkBoost = playerATKPosBoosts - playerATKNegBoosts
        p1.activePokemon.defBoost = playerDEFPosBoosts - playerDEFNegBoosts
        p1.activePokemon.spaBoost = playerSPAPosBoosts - playerSPANegBoosts
        p1.activePokemon.spdBoost = playerSPDPosBoosts - playerSPDNegBoosts
        p1.activePokemon.speBoost = playerSPEPosBoosts - playerSPENegBoosts
        p1.activePokemon.currentTypes = playerActor.pokemonList
            .find { it.uuid == playerActor.request?.side?.pokemon?.get(0)?.uuid }
            ?.effectedPokemon?.types?.map { it.displayName.string }?.toMutableList()

        //mon.stats = pokemon.stats
        p1.activePokemon.moves = pokemon1.moveSet.getMoves()
        p1.nRemainingMons = battle.side1.actors.sumOf { actor ->
            actor.pokemonList.count { pokemon ->
                pokemon.health != 0
            }
        }

        // if the active pokemon isn't already part of the p1.party then add it to it
        if (p1.party.find { it.pokemon?.uuid == p1.activePokemon.pokemon?.uuid } == null)
            p1.party.add(p1.activePokemon)
        else {
            var partyPokemonIndex = p1.party.indexOfFirst { it.pokemon?.uuid == p1.activePokemon.pokemon?.uuid }
            p1.party[partyPokemonIndex] = p1.activePokemon
        }

        //p1.availableSwitches = playerSide1.pokemonList.filter { it.uuid != p1.pokemon.uuid && it.health > 0 }
        //p1.sideConditions = pokemon.sideConditions   //todo what the hell does this mean

        p2.activePokemon = getActiveTrackerPokemon(p2, pokemon2?.uuid)
        p2.activePokemon.pokemon = pokemon2
        p2.activePokemon.species = pokemon2!!.species.name
        p2.activePokemon.currentHp = pokemon2.currentHealth
        p2.activePokemon.currentHpPercent = (pokemon2.currentHealth.toDouble() / pokemon2.hp.toDouble()) // todo this is not syncing. Possibly needs syncActivePokemon called later
        p2.activePokemon.boosts = p2BoostsMap
        p2.activePokemon.currentTypes = npcActor.pokemonList
            .find { it.uuid == npcActor.request?.side?.pokemon?.get(0)?.uuid }
            ?.effectedPokemon?.types?.map { it.displayName.string }?.toMutableList()
        //mon.stats = pokemon.stats
        p2.activePokemon.moves = pokemon2.moveSet.getMoves()
        p2.nRemainingMons = battle.side2.actors.sumOf { actor ->
            actor.pokemonList.count { pokemon ->
                pokemon.health != 0
            }
        }
        // if the active pokemon isn't already part of the p2.party then add it to it
        if (p2.party.find { it.pokemon?.uuid == p2.activePokemon.pokemon?.uuid } == null)
            p2.party.add(p2.activePokemon)
        else {
            var partyPokemonIndex = p2.party.indexOfFirst { it.pokemon?.uuid == p2.activePokemon.pokemon?.uuid }
            p2.party[partyPokemonIndex] = p2.activePokemon
        }

        //p2.sideConditions = pokemon.sideConditions   //todo what the hell does this mean

    }

    private fun chooseMove(move: InBattleMove, activeBattlePokemon: ActiveBattlePokemon): MoveActionResponse {
        val target = if (move.mustBeUsed()) null else move.target.targetList(activeBattlePokemon)
        if (target == null)
            return MoveActionResponse(move.id)
        else {
            val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull() ?: target.random()
            return MoveActionResponse(move.id, (chosenTarget as ActiveBattlePokemon).getPNX())
        }
    }

    private fun getActiveTrackerPokemon(actor: ActiveTracker.TrackerActor, pokemonUUID: UUID?): ActiveTracker.TrackerPokemon {
        var trackerPokemon = actor.party.find { it.pokemon!!.uuid == pokemonUUID }
        if (trackerPokemon != null)
            return trackerPokemon
        else
            return ActiveTracker.TrackerPokemon()
    }

    private fun getCurrentPlayer(battle: PokemonBattle): Pair<Pokemon, Pokemon> {
        val mon = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon
        val opponent = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon

        //val mon = if (request.side?.id == "p1") activeTracker.p1Active else activeTracker.p2Active
        //val opponent = if (request.side?.id == "p1") activeTracker.p2Active else activeTracker.p1Active

        return Pair(mon!!, opponent!!)
    }


}
