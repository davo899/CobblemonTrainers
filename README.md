# Cobblemon Trainers
Adds a trainer battle system to Cobblemon. Intended to be used with an interactable NPC mod.

- **/trainers add \<name\> [\<group\>]** - Creates a new trainer with an empty team. Can add to a specified group.
- **/trainers setup** - Opens GUI to configure trainers' teams
- **/trainers battle \<trainer\>** - Starts a battle with a trainer
- **/trainers setwincommand \<trainer\> \<command\>** - Sets the command run when the trainer is defeated. All occurrences of the string %player% will be replaced with the winning player's name.
- **/trainers setlosscommand \<trainer\> \<command\>** - Sets the command run when the player is defeated by the trainer. All occurrences of the string %player% will be replaced with the winning player's name.
- **/trainers setgroup \<trainer\> \<group\>** - Groups the trainer under the given group name in the setup GUI
- **/trainers remove \<trainer\>** - Deletes a trainer
- **/trainers rename \<oldName\> \<newName\>** - Renames a trainer (trainer names appear in the battle log)
- **/trainers reload** - Reloads the trainer data file (invalid teams can be setup by directly editing the trainer data file under "config/trainers/", but be warned Cobblemon may react in weird ways)
- **/trainers makebattle \<player\> \<trainer\> [\<trainerEntity\>]** - Starts a battle between a player and a trainer
- **/trainers setcanonlybeatonce \<trainer\> true/false** - Sets whether a trainer can only be beaten once
- **/trainers addpokemon \<trainer\> \<pokemon\>** - Adds a pokemon to a trainer's team
- **/trainers addfromparty \<trainer\> \<partySlot\>** - Adds a pokemon from your party to a trainer's team
- **/trainers setcooldownseconds \<trainer\> \<cooldownSeconds\>** - Sets the cooldown for battling the trainer

Single quotes can be used to include spaces in the trainer name or commands: /trainers add 'Ash Ketchum'\
To include single quotes within a string, use \\': 'say \\'hello\\'' becomes say 'hello'

All commands require permission level 2, or their corresponding permission node:
- "selfdot.trainers.battle" - /trainers battle \<trainerName\>
- "selfdot.op.trainers" - All other commands
