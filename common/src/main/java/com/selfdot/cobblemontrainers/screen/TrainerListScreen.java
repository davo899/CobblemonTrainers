package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

public class TrainerListScreen extends PagedScreen<Trainer> {

    public TrainerListScreen(String trainerGroup) {
        super(
            new TrainerGroupScreen(),
            CobblemonTrainers.INSTANCE.getTRAINER_REGISTRY().getAllTrainers().stream()
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
        ItemStack itemStack = ScreenUtils.withoutAdditional(CobblemonItems.POKE_BALL);
        itemStack.setCustomName(Text.literal(trainer.getName()));
        return itemStack;
    }

    @Override
    protected void onSelected(Trainer trainer, PlayerEntity player) {
        switchTo(new TrainerTeamScreen(trainer));
    }

}
