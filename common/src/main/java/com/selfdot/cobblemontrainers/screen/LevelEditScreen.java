package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
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
        ItemStack itemStack = ScreenUtils.withoutAdditional(CobblemonItems.WISE_GLASSES);
        itemStack.setCustomName(Text.literal("Level"));
        inventory.setStack(columns / 2, itemStack);

        setMinSlot = (columns * 2) + (columns / 2) - 3;
        decreaseHighSlot = setMinSlot + 1;
        decreaseLowSlot = decreaseHighSlot + 1;
        int infoSlot = decreaseLowSlot + 1;
        increaseLowSlot = infoSlot + 1;
        increaseHighSlot = increaseLowSlot + 1;
        setMaxSlot = increaseHighSlot + 1;

        itemStack = ScreenUtils.withoutAdditional(Items.RED_DYE);
        itemStack.setCustomName(Text.literal("Set to " + MIN));
        inventory.setStack(setMinSlot, itemStack);
        itemStack = ScreenUtils.withoutAdditional(Items.RED_DYE);
        itemStack.setCustomName(Text.literal("-" + EDIT_HIGH));
        inventory.setStack(decreaseHighSlot, itemStack);
        itemStack = ScreenUtils.withoutAdditional(Items.RED_DYE);
        itemStack.setCustomName(Text.literal("-" + EDIT_LOW));
        inventory.setStack(decreaseLowSlot, itemStack);
        infoItem = ScreenUtils.withoutAdditional(CobblemonItems.WISE_GLASSES);
        updateInfoItem();
        inventory.setStack(infoSlot, infoItem);
        itemStack = ScreenUtils.withoutAdditional(Items.GREEN_DYE);
        itemStack.setCustomName(Text.literal("+" + EDIT_LOW));
        inventory.setStack(increaseLowSlot, itemStack);
        itemStack = ScreenUtils.withoutAdditional(Items.GREEN_DYE);
        itemStack.setCustomName(Text.literal("+" + EDIT_HIGH));
        inventory.setStack(increaseHighSlot, itemStack);
        itemStack = ScreenUtils.withoutAdditional(Items.GREEN_DYE);
        itemStack.setCustomName(Text.literal("Set to " + MAX));
        inventory.setStack(setMaxSlot, itemStack);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == setMinSlot) {
            trainerPokemon.setLevel(MIN);
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == decreaseHighSlot) {
            trainerPokemon.setLevel(Math.max(trainerPokemon.getLevel() - EDIT_HIGH, MIN));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == decreaseLowSlot) {
            trainerPokemon.setLevel(Math.max(trainerPokemon.getLevel() - EDIT_LOW, MIN));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == increaseLowSlot) {
            trainerPokemon.setLevel(Math.min(trainerPokemon.getLevel() + EDIT_LOW, MAX));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == increaseHighSlot) {
            trainerPokemon.setLevel(Math.min(trainerPokemon.getLevel() + EDIT_HIGH, MAX));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == setMaxSlot) {
            trainerPokemon.setLevel(MAX);
            trainer.save();
            updateInfoItem();
        }
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
