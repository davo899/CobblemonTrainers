package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class EVEditScreen extends Screen {

    private static final int EDIT_LOW = 4;
    private static final int EDIT_HIGH = 32;
    private static final int MIN = 0;
    private static final int MAX = 252;
    private final EVs evs;
    private final Stats stat;
    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private ItemStack infoItem;
    private int setMinSlot;
    private int decreaseHighSlot;
    private int decreaseLowSlot;
    private int infoSlot;
    private int increaseLowSlot;
    private int increaseHighSlot;
    private int setMaxSlot;

    public EVEditScreen(Stats stat, Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new EVSelectScreen(trainer, trainerPokemon));
        this.stat = stat;
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
        this.evs = trainerPokemon.getEvs();
    }

    @Override
    public void initialize(Inventory inventory) {
        ItemStack itemStack = ScreenUtils.statVitaminItem(stat);
        itemStack.setCustomName(stat.getDisplayName());
        inventory.setStack(columns / 2, itemStack);

        setMinSlot = (columns * 2) + (columns / 2) - 3;
        decreaseHighSlot = setMinSlot + 1;
        decreaseLowSlot = decreaseHighSlot + 1;
        infoSlot = decreaseLowSlot + 1;
        increaseLowSlot = infoSlot + 1;
        increaseHighSlot = increaseLowSlot + 1;
        setMaxSlot = increaseHighSlot + 1;

        itemStack = new ItemStack(Items.RED_DYE);
        itemStack.setCustomName(Text.literal("Set to " + MIN));
        inventory.setStack(setMinSlot, itemStack);
        itemStack = new ItemStack(Items.RED_DYE);
        itemStack.setCustomName(Text.literal("-" + EDIT_HIGH));
        inventory.setStack(decreaseHighSlot, itemStack);
        itemStack = new ItemStack(Items.RED_DYE);
        itemStack.setCustomName(Text.literal("-" + EDIT_LOW));
        inventory.setStack(decreaseLowSlot, itemStack);
        infoItem = ScreenUtils.statVitaminItem(stat);
        updateInfoItem();
        inventory.setStack(infoSlot, infoItem);
        itemStack = new ItemStack(Items.GREEN_DYE);
        itemStack.setCustomName(Text.literal("+" + EDIT_LOW));
        inventory.setStack(increaseLowSlot, itemStack);
        itemStack = new ItemStack(Items.GREEN_DYE);
        itemStack.setCustomName(Text.literal("+" + EDIT_HIGH));
        inventory.setStack(increaseHighSlot, itemStack);
        itemStack = new ItemStack(Items.GREEN_DYE);
        itemStack.setCustomName(Text.literal("Set to " + MAX));
        inventory.setStack(setMaxSlot, itemStack);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        int currentTotal = 0;
        for (Stats stats : ScreenUtils.STATS) currentTotal += evs.getOrDefault(stats);

        if (slotIndex == setMinSlot) {
            evs.set(stat, MIN);
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == decreaseHighSlot) {
            evs.set(stat, Math.max(evs.getOrDefault(stat) - EDIT_HIGH, MIN));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == decreaseLowSlot) {
            evs.set(stat, Math.max(evs.getOrDefault(stat) - EDIT_LOW, MIN));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == increaseLowSlot && currentTotal + EDIT_LOW <= EVs.MAX_TOTAL_VALUE) {
            evs.set(stat, Math.min(evs.getOrDefault(stat) + EDIT_LOW, MAX));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == increaseHighSlot && currentTotal + EDIT_HIGH <= EVs.MAX_TOTAL_VALUE) {
            evs.set(stat, Math.min(evs.getOrDefault(stat) + EDIT_HIGH, MAX));
            trainer.save();
            updateInfoItem();
        } else if (slotIndex == setMaxSlot && currentTotal - evs.getOrDefault(stat) + MAX <= EVs.MAX_TOTAL_VALUE) {
            evs.set(stat, MAX);
            trainer.save();
            updateInfoItem();
        }
    }

    @Override
    public String getDisplayName() {
        return stat.getDisplayName().getString();
    }

    private void updateInfoItem() {
        infoItem.setCustomName(
            Text.literal("Current " + stat.getDisplayName().getString() + ": " + evs.getOrDefault(stat))
        );
    }

}
