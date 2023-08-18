package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

public class TrainerGroupScreen extends PagedScreen<String> {

    public TrainerGroupScreen() {
        super(
            TrainerRegistry.getInstance().getAllTrainers().stream()
                .map(Trainer::getGroup)
                .distinct()
                .collect(Collectors.toList()),
            0
        );
    }

    @Override
    protected ItemStack toItem(String trainerGroup) {
        ItemStack itemStack = new ItemStack(Items.BOOK);
        itemStack.setCustomName(Text.literal(trainerGroup));
        return itemStack;
    }

    @Override
    protected void onSelected(String trainerGroup, PlayerEntity player) {
        player.openHandledScreen(new TrainerSetupHandlerFactory(new TrainerListScreen(trainerGroup)));
    }

    @Override
    public String getDisplayName() {
        return "Groups";
    }

}
