# 0.9.10
- Added trainer defeat requirements, so certain trainers can only be battled if the player has already defeated certain other trainers.
  - /trainers adddefeatrequirement \<trainer\> \<defeatRequirement\>
  - /trainers removedefeatrequirement \<trainer\> \<defeatRequirement\>
- Added /trainers resetwintracker \<player\> \<trainer\>
- Fixed /trainers reload being unavailable after file loading fails.

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
