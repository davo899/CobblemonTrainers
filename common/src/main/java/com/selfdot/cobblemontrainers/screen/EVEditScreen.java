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
    private ItemStack infoItem;
    private int setMinSlot;
    private int decreaseHighSlot;
    private int decreaseLowSlot;
    private int increaseLowSlot;
    private int increaseHighSlot;
    private int setMaxSlot;

    public EVEditScreen(Stats stat, Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new EVSelectScreen(trainer, trainerPokemon));
        this.stat = stat;
        this.trainer = trainer;
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
        increaseLowSlot = decreaseLowSlot + 2;
        increaseHighSlot = increaseLowSlot + 1;
        setMaxSlot = increaseHighSlot + 1;

        infoItem = ScreenUtils.statVitaminItem(stat);
        updateInfoItem();
        inventory.setStack(decreaseLowSlot + 1, infoItem);

        setSlot(inventory, setMinSlot, Items.RED_DYE, "Set to " + MIN);
        setSlot(inventory, decreaseHighSlot, Items.RED_DYE, "-" + EDIT_HIGH);
        setSlot(inventory, decreaseLowSlot, Items.RED_DYE, "-" + EDIT_LOW);
        setSlot(inventory, increaseLowSlot, Items.GREEN_DYE, "+" + EDIT_LOW);
        setSlot(inventory, increaseHighSlot, Items.GREEN_DYE, "+" + EDIT_HIGH);
        setSlot(inventory, setMaxSlot, Items.GREEN_DYE, "Set to " + MAX);
    }

    private void setEv(int ev) {
        evs.set(stat, ev);
        trainer.save();
        updateInfoItem();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        int currentTotal = 0;
        for (Stats stats : ScreenUtils.STATS) currentTotal += evs.getOrDefault(stats);

        if (slotIndex == setMinSlot) {
            setEv(MIN);
        } else if (slotIndex == decreaseHighSlot) {
            setEv(Math.max(evs.getOrDefault(stat) - EDIT_HIGH, MIN));
        } else if (slotIndex == decreaseLowSlot) {
            setEv(Math.max(evs.getOrDefault(stat) - EDIT_LOW, MIN));
        } else if (slotIndex == increaseLowSlot && currentTotal + EDIT_LOW <= EVs.MAX_TOTAL_VALUE) {
            setEv(Math.min(evs.getOrDefault(stat) + EDIT_LOW, MAX));
        } else if (slotIndex == increaseHighSlot && currentTotal + EDIT_HIGH <= EVs.MAX_TOTAL_VALUE) {
            setEv(Math.min(evs.getOrDefault(stat) + EDIT_HIGH, MAX));
        } else if (slotIndex == setMaxSlot && currentTotal - evs.getOrDefault(stat) + MAX <= EVs.MAX_TOTAL_VALUE) {
            setEv(MAX);
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
