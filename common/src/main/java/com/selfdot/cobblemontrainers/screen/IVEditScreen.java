package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
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

public class IVEditScreen extends Screen {

    private static final int EDIT_LOW = 1;
    private static final int EDIT_HIGH = 5;
    private static final int MIN = 0;
    private static final int MAX = 31;
    private final IVs ivs;
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

    public IVEditScreen(Stats stat, Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new IVSelectScreen(trainer, trainerPokemon));
        this.stat = stat;
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
        this.ivs = trainerPokemon.getIvs();
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

        if (slotIndex == setMinSlot) {
            ivs.set(stat, MIN);
            updateInfoItem();
        } else if (slotIndex == decreaseHighSlot) {
            ivs.set(stat, Math.max(ivs.get(stat) - EDIT_HIGH, MIN));
            updateInfoItem();
        } else if (slotIndex == decreaseLowSlot) {
            ivs.set(stat, Math.max(ivs.get(stat) - EDIT_LOW, MIN));
            updateInfoItem();
        } else if (slotIndex == increaseLowSlot) {
            ivs.set(stat, Math.min(ivs.get(stat) + EDIT_LOW, MAX));
            updateInfoItem();
        } else if (slotIndex == increaseHighSlot) {
            ivs.set(stat, Math.min(ivs.get(stat) + EDIT_HIGH, MAX));
            updateInfoItem();
        } else if (slotIndex == setMaxSlot) {
            ivs.set(stat, MAX);
            updateInfoItem();
        }
    }

    @Override
    public String getDisplayName() {
        return stat.getDisplayName().getString();
    }

    private void updateInfoItem() {
        infoItem.setCustomName(
            Text.literal("Current " + stat.getDisplayName().getString() + ": " + ivs.get(stat))
        );
    }

}
