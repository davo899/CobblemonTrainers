package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TrainerPokemonScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int movesSlot;
    private int abilitiesSlot;
    private int evsSlot;
    private int ivsSlot;
    private int deleteSlot;
    private int levelSlot;
    private int natureSlot;
    private int shinySlot;
    private int heldItemSlot;

    public TrainerPokemonScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerTeamScreen(trainer));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        inventory.setStack(columns / 2, PokemonUtility.pokemonToInfoItem(trainerPokemon.toPokemon()));

        movesSlot = (columns * 2) + (columns / 2) - 2;
        abilitiesSlot = movesSlot + 1;
        evsSlot = abilitiesSlot + 1;
        ivsSlot = evsSlot + 1;
        deleteSlot = ivsSlot + 1;

        levelSlot = evsSlot + columns;
        natureSlot = levelSlot + 1;
        shinySlot = levelSlot - 1;
        heldItemSlot = levelSlot - 2;

        setSlot(inventory, movesSlot, Items.MUSIC_DISC_5, "Moves");
        setSlot(inventory, abilitiesSlot, CobblemonItems.CLOVER_SWEET, "Abilities");
        setSlot(inventory, evsSlot, CobblemonItems.FLOWER_SWEET, "EVs");
        setSlot(inventory, ivsSlot, CobblemonItems.STAR_SWEET, "IVs");
        setSlot(inventory, deleteSlot, Items.BARRIER, "Delete Pokémon");
        setSlot(inventory, shinySlot, Items.NETHER_STAR, "Toggle Shiny");
        setSlot(inventory, levelSlot, CobblemonItems.WISE_GLASSES, "Level");
        setSlot(inventory, natureSlot, Items.CYAN_DYE, "Nature");

        Item heldItem = trainerPokemon.getHeldItem();
        ItemStack heldItemItemStack = new ItemStack(heldItem.equals(Items.AIR) ? Items.STICK : heldItem);
        heldItemItemStack.setCustomName(Text.literal("Held Item"));
        ScreenUtils.addLore(heldItemItemStack, new Text[]{
            Text.literal(heldItem.equals(Items.AIR) ?
                Formatting.RED + "None" :
                Formatting.GREEN + heldItem.getDefaultStack().getName().getString()
            )
        });
        inventory.setStack(heldItemSlot, heldItemItemStack);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == movesSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new PokemonMovesetScreen(trainer, trainerPokemon)));
        } else if (slotIndex == abilitiesSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new AbilitySelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == evsSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new EVSelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == ivsSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new IVSelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == deleteSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new ConfirmDeletePokemonScreen(trainer, trainerPokemon)));
        } else if (slotIndex == levelSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new LevelEditScreen(trainer, trainerPokemon)));
        } else if (slotIndex == natureSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new NatureSelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == shinySlot) {
            trainerPokemon.toggleShiny();
            trainer.save();
            player.openHandledScreen(new TrainerSetupHandlerFactory(this));
        } else if (slotIndex == heldItemSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new HeldItemSelectScreen(trainer, trainerPokemon)));
        }
    }

    @Override
    public String getDisplayName() {
        return trainerPokemon.getName();
    }

}
