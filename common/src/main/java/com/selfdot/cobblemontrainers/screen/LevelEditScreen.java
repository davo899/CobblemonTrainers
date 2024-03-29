package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class LevelEditScreen extends Screen {

    private static final int EDIT_LOW = 1;
    private static final int EDIT_HIGH = 10;
    private static final int MIN = 1;
    private static final int MAX = 100;
    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private ItemStack infoItem;
    private int setMinSlot;
    private int decreaseHighSlot;
    private int decreaseLowSlot;
    private int increaseLowSlot;
    private int increaseHighSlot;
    private int setMaxSlot;

    public LevelEditScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        setSlot(inventory, columns / 2, CobblemonItems.WISE_GLASSES, "Level");

        setMinSlot = (columns * 2) + (columns / 2) - 3;
        decreaseHighSlot = setMinSlot + 1;
        decreaseLowSlot = decreaseHighSlot + 1;
        increaseLowSlot = decreaseLowSlot + 2;
        increaseHighSlot = increaseLowSlot + 1;
        setMaxSlot = increaseHighSlot + 1;

        infoItem = ScreenUtils.withoutAdditional(CobblemonItems.WISE_GLASSES);
        updateInfoItem();
        inventory.setStack(decreaseLowSlot + 1, infoItem);

        setSlot(inventory, setMinSlot, Items.RED_DYE, "Set to " + MIN);
        setSlot(inventory, decreaseHighSlot, Items.RED_DYE, "-" + EDIT_HIGH);
        setSlot(inventory, decreaseLowSlot, Items.RED_DYE, "-" + EDIT_LOW);
        setSlot(inventory, increaseLowSlot, Items.GREEN_DYE, "+" + EDIT_LOW);
        setSlot(inventory, increaseHighSlot, Items.GREEN_DYE, "+" + EDIT_HIGH);
        setSlot(inventory, setMaxSlot, Items.GREEN_DYE, "Set to " + MAX);
    }

    private void setLevel(int level) {
        trainerPokemon.setLevel(level);
        trainer.save();
        updateInfoItem();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if      (slotIndex == setMinSlot)       setLevel(MIN);
        else if (slotIndex == decreaseHighSlot) setLevel(Math.max(trainerPokemon.getLevel() - EDIT_HIGH, MIN));
        else if (slotIndex == decreaseLowSlot)  setLevel(Math.max(trainerPokemon.getLevel() - EDIT_LOW, MIN));
        else if (slotIndex == increaseLowSlot)  setLevel(Math.min(trainerPokemon.getLevel() + EDIT_LOW, MAX));
        else if (slotIndex == increaseHighSlot) setLevel(Math.min(trainerPokemon.getLevel() + EDIT_HIGH, MAX));
        else if (slotIndex == setMaxSlot)       setLevel(MAX);
    }

    @Override
    public String getDisplayName() {
        return "Level";
    }

    private void updateInfoItem() {
        infoItem.setCustomName(
            Text.literal("Current Level: " + trainerPokemon.getLevel())
        );
    }

}
