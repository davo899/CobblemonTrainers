# Cobblemon Trainers
Adds a trainer battle system to Cobblemon. Intended to be used with an interactable NPC mod.

/trainers add \<name\> - Creates a new trainer with an empty team\
/trainers setup - Opens GUI to configure trainers' teams\
/trainers battle \<name\> - Starts a battle with a trainer\
/trainers setwincommand \<command\> - Sets the command run when the trainer is defeated. All occurences of the string %player% will be replaced with the winning player's name.\
/trainers setgroup \<groupName\> - Groups the trainer under the given group name in the setup GUI\
/trainers remove \<name\> - Deletes a trainer\
/trainers rename \<oldName\> \<newName\> - Renames a trainer (trainer names appear in the battle log)\
/trainers reload - Reloads the trainer data file (invalid teams can be setup by directly editing the trainer data file "config/trainers/trainers.json", be warned Cobblemon may react in weird ways)

Single quotes can be used to include spaces in the trainer name: /trainers add 'Ash Ketchum'
