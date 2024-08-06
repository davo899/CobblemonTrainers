# 1.1.11
- Fixed Forge crash when using Strong AI.

# 1.1.10
- Added Strong battle AI, currently under development for use in official trainers in a future Cobblemon update.
- Added /trainers usestrongai <level> - Sets all trainers to use Strong AI. Level can be 0-5.
  - As the Strong AI is under development, you may encounter bugs/strange behaviour. Reporting these problems will be greatly appreciated.
- Added /trainers usegen5ai - Sets all trainers to use Gen 5 AI.

# 1.1.9
- Fixed shiny trainer Pokémon not appearing shiny.

# 1.1.8
- Fixed incorrect Pokémon data loading order causing some abilities not to be selectable.

# 1.1.7
- Re-re-enabled trainer Pokémon send out animation on Fabric.
- Fixed trainer Pokémon not being recalled at the end of a fight on Forge.
- Fixed trainer Pokémon dropping items on Forge.
- Fixed battles not starting after your first battle on Forge.

# 1.1.6
- Fixed crash when trainer's Pokémon have a status condition.
- Re-enabled trainer Pokémon send out animation.
- Made Fabric Permissions API be included in the Fabric version.

# 1.1.5
- Removed debug command /selfdot.

# 1.1.4
- Added config option "commandExecutor".
  - Set to "CONSOLE" by default (@p and @s will no longer target the player on this setting).
  - Must be set to "PLAYER" on Mohist otherwise the server will crash.
- Added held item to Pokémon item lore in setup menu.

# 1.1.3
- Fixed crash on Forge when trainer tries to send out Pokémon.
- On Forge, the Poké Ball animation no longer plays when a trainer sends out a Pokémon (source of the above crash).

# 1.1.2
- Fixed win/loss commands causing an error on Mohist.
- @p and @s will now target the player in win/loss commands (%player% also still works).

# 1.1.1
- Fixed battle error when trainer uses a recharge move.

# 1.1.0
- Fixed custom Pokémon types causing trainer AI to crash. AI will now assume neutral damage whenever custom types are involved.
- Added "Edit team order" screen in setup menu.
- Added Gender edit button in setup menu.

# 1.0.0
- Made setup menu item names not be in italics.
- Made running just "/trainers battle" open a trainer battle select menu.
- Added form selection when adding new Pokémon, and forms will be preserved when using addpokemon and addfromparty.
- Prevented starting a battle with a trainer that has no Pokémon.

# 0.9.12
- Fixed commands being unusable on singleplayer.
- Fixed trainer using a ditto causing a battle error.
- Fixed trainer attempting to switch to a Pokémon it has previously switched to causing a battle error.

# 0.9.11
- Fixed error on player respawn in some cases when using LuckPerms Forge.

# 0.9.10
- Added trainer defeat requirements, so certain trainers can only be battled if the player has already defeated certain other trainers.
  - /trainers adddefeatrequirement \<trainer\> \<defeatRequirement\>
  - /trainers removedefeatrequirement \<trainer\> \<defeatRequirement\>
- Added /trainers resetwintracker \<player\> \<trainer\>
- Fixed /trainers reload being unavailable after file loading fails.
- Fixed /trainers reload not re-enabling commands when file loading succeeds.
- Made /trainers reload report whether loading succeeded or failed.

# 0.9.9
- Fixed invalid trainer files being cleared of all data.

# 0.9.8
- Added config option to disable XP when battling trainers. (config/trainers/config.json)
- Added /trainers setpartymaximumlevel \<trainer\> \<maxLevel\> to set a maximum level for the player's party to battle a trainer. (Added by NavyBlue)
- Fixed loading invalid nature causing a crash.
- Fixed loading invalid species causing a crash.

# 0.9.7
- Fixed %player% being replaced with server-altered names instead of the player's true username

# 0.9.6
- AI damage calculator now considers the following abilities:
  - Storm Drain
  - Water Absorb
  - Dry Skin
  - Volt Absorb
  - Lightning Rod
  - Motor Drive
  - Earth Eater
  - Well-Baked Body
  - Flash Fire
  - Sap Sipper
  - Wonder Guard
- Fixed /trainers makebattle only being executable by a player

# 0.9.5
- Fixed cursor snapping to centre when switching screens in the setup GUI.
- Changed AI to consider the ability Levitate in its damage calculator.
- Fixed command permissions not acting separately.
- Changed command permissions to:
  - "selfdot.trainers.reload" (/trainers reload)
  - "selfdot.trainers.battle" (/trainers battle)
  - "selfdot.trainers.makebattle" (/trainers makebattle)
  - "selfdot.trainers.edit" (All other commands)
  - "selfdot.op.trainers" will still allow all commands for now, but I recommend switching to using the other permissions.

# 0.9.4c
- Fixed first time loading trainers from before 0.9.4 setting all held items to invalid values.
- Fixed trainer rolling a multi-target killing move causing a battle error.

# 0.9.4b
- Fixed refmap conflicts which prevented the mod from loading with some other mods that don't name their refmaps.

# 0.9.4
- Added Generation 5 based trainer AI.
- Added sending out trainer pokemon.
  - "/trainers makebattle \<player\> \<trainer\>" - If the command executor is a LivingEntity, it will send out the trainer's pokemon.
  - "/trainers makebattle \<player\> \<trainer\> \<trainerEntity\>" - Can use a target selector to select the entity to send out the trainer's pokemon (must be a LivingEntity).
- Added "/trainers addpokemon \<trainer\> \<pokemon\>" to add a pokemon to a trainer's team.
- Added "/trainers addfromparty \<trainer\> \<partySlot\>" to add a pokemon from your party to a trainer's team.
- Added "/trainers add \<name\> \<group\>" to add a new trainer directly to a group.
- Removed additional tooltips from menu items (e.g. disc names).
- Added type, damage category and power tooltips to move icons in menu.
- Fixed HTML escaping in JSON strings ('=' becoming '\\u003d').
- Fixed moves appearing multiple times in move select screen.
- Changed moves to be sorted alphabetically in move select screen.
- Added held items on trainer pokemon.
- Added "/trainers setcooldownseconds \<trainer\> \<cooldownSeconds\>" to set cooldowns for battling trainers.

# 0.9.3b
- Fixed allowing starting a trainer battle with a fainted lead pokemon. Battles now start with the first non-fainted pokemon in the player's party.
- Fixed changing a trainer's group leaving a duplicate file in its old group directory.
- Fixed commands not being disabled when mod is in a corrupt state.
- Added separate permission node "selfdot.trainers.battle" for command /trainers battle

# 0.9.3
- Fixed new trainers not initialising with default data, resulting in a corrupt trainer file.

# 0.9.2
- Changed trainer data to be stored as individual trainer files in group directories. Existing data will be reformatted automatically.
- Added trainer canOnlyBeatOnce property, which will prevent players from battling a trainer that they have already beaten.
- Added TM, egg, evolution, form change and tutor moves to the move select screen.
- Added saving data on setup GUI updates.
- Lowered required permissions for commands from 4 to 2.

# 0.9.1
- Added trainer loss command, command that is run when the player loses to the trainer. Syntax: /trainers \<name\> setlosscommand \<command\>
- Made trainer data be saved on update, so data is not lost on a server crash.
- Added shiny selection

# 0.9.0
- Added nature selection
- Added buttons to scroll 2 pages at a time on paged screens
- Changed paged screens to cycle from first to last and last to first page
- Added button to delete a move from a pokemon's moveset
- Fixed trainer pokemon moves not starting battles with full PP
