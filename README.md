![image](https://media.forgecdn.net/attachments/description/959503/description_399370d4-14fe-4f47-941f-7a0e2b11def1.png)

Cobblemon Trainers introduces a trainer battle system to Cobblemon, designed to seamlessly integrate with interactable NPC mods.\
These trainers feature a custom AI inspired by Generation 5's trainer AI, ensuring engaging and challenging battles.

- Customize trainer teams extensively, including Moves, IVs, EVs, Abilities, Level, and Nature, offering diverse and challenging battles.
- Access any Pokémon available on your server, including those from datapacks.
- Efficiently organize trainers into groups for streamlined navigation within the setup interface.
- Define win commands for trainers, enabling post-battle rewards such as economy-based incentives.

**Commands Overview:**


- **/trainers add \<name\> [\<group\>]**: Establish a new trainer with an empty team, optionally assigning them to a specific group.
- **/trainers setup**: Access the GUI to configure trainers' teams.
- **/trainers setwincommand \<trainer\> \<command\>**: Specify the command executed upon defeating a trainer, with automatic player name substitution (%player%).
- **/trainers setlosscommand \<trainer\> \<command\>**: Define the command executed when a player is defeated by a trainer, with automatic player name substitution (%player%).
- **/trainers setgroup \<trainer\> \<group\>**: Group trainers under specified names in the setup GUI for organizational purposes.
- **/trainers remove \<trainer\>**: Delete a trainer from the system.
- **/trainers rename \<oldName\> \<newName\>**: Modify a trainer's name (reflects in battle logs).
- **/trainers setcanonlybeatonce \<trainer\> true/false**: Toggle whether a trainer can only be defeated once.
- **/trainers addpokemon \<trainer\> \<pokemon\>**: Add a Pokémon to a trainer's team via command similar to /pokegive.
- **/trainers addfromparty \<trainer\> \<partySlot\>**: Add a Pokémon from the player's party to a trainer's team.
- **/trainers setcooldownseconds \<trainer\> \<cooldownSeconds\>**: Set the cooldown duration for battling the trainer.
- **/trainers setpartymaximumlevel \<trainer\> \<maxLevel\>**: Set a maximum level for the player's party to battle a trainer.
- **/trainers adddefeatrequirement \<trainer\> \<defeatRequirement\>**: Makes it so the given trainer cannot be battled unless the player has defeated a certain trainer.
- **/trainers removedefeatrequirement \<trainer\> \<defeatRequirement\>**: Removes a defeat requirement.
- **/trainers resetwintracker \<player\> \<trainer\>**: Clears whether the given player has defeated the given trainer in the past.
- **/trainers battle \<trainer\>**: Initiate a battle with a trainer.
- **/trainers makebattle \<player\> \<trainer\> [\<trainerEntity\>]**: Commence a battle between a player and a trainer.
- **/trainers reload**: Refresh the trainer data file; caution advised for manual edits.

Utilize single quotes for trainer names or commands with spaces. For instance: /trainers add 'Ash Ketchum'\
To include single quotes within a string, utilize \': 'say \'hello\'' becomes say 'hello'

All commands require permission level 2 or their corresponding permission nodes:
- "selfdot.trainers.reload" - /trainers reload
- "selfdot.trainers.battle" - /trainers battle \<trainer\>
- "selfdot.trainers.makebattle" - /trainers makebattle \<player\> \<trainer\>
- "selfdot.trainers.edit" - All other commands.

**Config Options:**

- **xpEnabled**: Controls whether player Pokémon will gain XP from battling trainers.

Graphics by twsparklecat

[![image](https://media.forgecdn.net/attachments/description/959503/description_ec38fa43-4312-4aea-b11d-849dbdd062b1.png)](https://discord.gg/y8K2HYDBuX)