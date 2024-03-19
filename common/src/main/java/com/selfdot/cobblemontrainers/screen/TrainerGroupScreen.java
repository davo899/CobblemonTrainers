package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

public class TrainerGroupScreen extends PagedScreen<String> {

    public TrainerGroupScreen() {
        super(
            CobblemonTrainers.INSTANCE.getTRAINER_REGISTRY().getAllTrainers().stream()
                .map(Trainer::getGroup)
                .distinct()
                .collect(Collectors.toList()),
            0
        );
    }

    @Override
    protected ItemStack toItem(String trainerGroup) {
        ItemStack itemStack = ScreenUtils.withoutAdditional(Items.BOOK);
        itemStack.setCustomName(Text.literal(trainerGroup));
        return itemStack;
    }

    @Override
    protected void onSelected(String trainerGroup, PlayerEntity player) {
        switchTo(new TrainerListScreen(trainerGroup));
    }

    @Override
    public String getDisplayName() {
        return "Groups";
    }

}
