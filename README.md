# Cobblemon Trainers
Adds a trainer battle system to Cobblemon. Intended to be used with an interactable NPC mod.

/trainers add <name> - Creates a new trainer with an empty team\
/trainers setup - Opens GUI to configure trainers' teams\
/trainers battle <name> - Starts a battle with a trainer\
/trainers setmoneyreward <amount> - Sets the money reward for beating the trainer (this runs "/eco give <player> <amount>" so requires an economy setup with that command)\
/trainers remove <name> - Deletes a trainer\
/trainers rename <oldName> <newName> - Renames a trainer (trainer names appear in the battle log)\
/trainers reload - Reloads the trainer data file\

Single quotes can be used to include spaces in the trainer name: /trainers add 'Ash Ketchum'
