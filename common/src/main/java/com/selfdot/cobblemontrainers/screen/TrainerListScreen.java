package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class TrainerListScreen extends PagedScreen<Trainer> {

    public TrainerListScreen(String trainerGroup) {
        super(
            new TrainerGroupScreen(),
            TrainerRegistry.getInstance().getAllTrainers().stream()
                .filter(trainer -> trainer.getGroup().equals(trainerGroup))
                .collect(Collectors.toList()),
            0
        );
    }

    @Override
    public String getDisplayName() {
        return "Trainers";
    }

    @Override
    protected ItemStack toItem(Trainer trainer) {
        ItemStack itemStack = new ItemStack(CobblemonItems.POKE_BALL);
        itemStack.setCustomName(Text.literal(trainer.getName()));
        return itemStack;
    }

    @Override
    protected void onSelected(Trainer trainer, PlayerEntity player) {
        player.openHandledScreen(new TrainerSetupHandlerFactory(new TrainerTeamScreen(trainer)));
    }

}
