package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TrainerListScreen extends PagedScreen<Trainer> {

    public TrainerListScreen() {
        super(TrainerRegistry.getInstance().getAllTrainers().stream().toList(), 0);
    }

    @Override
    public String getDisplayName() {
        return "Trainers";
    }

    @Override
    protected ItemStack toItem(Trainer trainer) {
        ItemStack itemStack = new ItemStack(CobblemonItems.POKE_BALL.get());
        itemStack.setCustomName(Text.literal(trainer.getName()));
        return itemStack;
    }

    @Override
    protected void onSelected(Trainer trainer, PlayerEntity player) {
        player.openHandledScreen(new TrainerSetupHandlerFactory(new TrainerTeamScreen(trainer)));
    }

}
