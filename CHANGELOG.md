# 0.9.4
- Added Generation 5 based trainer AI.
- Added sending out trainer's Pokémon with "/trainers makebattle <player> <trainer> <trainerEntity>"
- Added "/trainers addpokemon <trainer> <pokemon>" to add a pokemon to a trainer's team
- Added "/trainers addfromparty <trainer> <partySlot>" to add a pokemon from your party to a trainer's team
- Added "/trainers add <name> <group>" to add a new trainer directly to a group
- Removed additional tooltips from menu items (e.g. disc names)

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
