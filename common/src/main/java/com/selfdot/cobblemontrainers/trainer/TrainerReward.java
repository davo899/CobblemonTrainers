package com.selfdot.cobblemontrainers.trainer;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.economy.EconomyAccount;
import com.selfdot.cobblemontrainers.economy.NullEconomyAccount;
import com.selfdot.cobblemontrainers.economy.OctoEconomyAccount;
import com.selfdot.cobblemontrainers.economy.exceptions.EconomyNotExistException;
import com.selfdot.cobblemontrainers.economy.exceptions.PlayerAccountNotExistException;
import com.selfdot.cobblemontrainers.economy.exceptions.TrainerRewardNotExistException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TrainerReward {
    public static void giveMoneyRewardToPlayer(ServerPlayerEntity player, Trainer trainer) {
        try {
            assertExistTrainerMoneyReward(trainer);
            assertExistPlayerAccount(player);
            increasePlayerAccountBalanceByTrainerMoneyReward(player, trainer);
            sendMessagePlayerBalanceIncrease(player, trainer);
            logMessagePlayerBalanceIncrease(player, trainer);
        } catch (TrainerRewardNotExistException e) {
            logMessageNotExistTrainerReward(trainer);
        } catch (PlayerAccountNotExistException e) {
            sendMessageNotExistPlayerAccount(player);
            logMessageNotExistPlayerAccount(player);
        }
    }

    private static void increasePlayerAccountBalanceByTrainerMoneyReward(ServerPlayerEntity player, Trainer trainer) {
        long moneyReward = trainer.getMoneyReward();
        EconomyAccount account = getPlayerAccount(player);
        account.addBalance(moneyReward);
    }

    private static void sendMessagePlayerBalanceIncrease(ServerPlayerEntity player, Trainer trainer) {
        long moneyReward = trainer.getMoneyReward();
        String message = String.format("Congratulation! You received $%,d as reward", moneyReward);
        player.sendMessage(Text.literal(message));
    }

    private static void logMessagePlayerBalanceIncrease(ServerPlayerEntity player, Trainer trainer) {
        long moneyReward = trainer.getMoneyReward();
        String message = String.format("Gave $%,d to %s", moneyReward, player.getGameProfile().getName());
        CobblemonTrainers.INSTANCE.getLogger().atInfo().log(message);
    }

    private static void sendMessageNotExistPlayerAccount(ServerPlayerEntity player) {
        String message = String.format("Failed to give reward. You do not have an account");
        player.sendMessage(Text.literal(message));
    }

    private static void logMessageNotExistPlayerAccount(ServerPlayerEntity player) {
        String message = String.format("Failed to give reward to %s", player.getGameProfile().getName());
        CobblemonTrainers.INSTANCE.getLogger().atInfo().log(message);
    }

    private static void logMessageNotExistTrainerReward(Trainer trainer) {
        String message = String.format("Reward does not exist for Trainer %s", trainer.getName());
        CobblemonTrainers.INSTANCE.getLogger().atDebug().log(message);
    }

    private static void assertExistTrainerMoneyReward(Trainer trainer) throws TrainerRewardNotExistException {
        long moneyReward = trainer.getMoneyReward();
        if(isZeroMoneyReward(moneyReward) || isInvalidMoneyReward(moneyReward)) {
            throw(new TrainerRewardNotExistException());
        }
    }

    private static boolean isZeroMoneyReward(long moneyReward) {
        return moneyReward == 0;
    }

    private static boolean isInvalidMoneyReward(long moneyReward) {
        return moneyReward < 0;
    }

    private static void assertExistPlayerAccount(ServerPlayerEntity player) throws PlayerAccountNotExistException {
        getPlayerAccount(player).assertExist();
    }

    private static EconomyAccount getPlayerAccount(ServerPlayerEntity player) {
        try {
            return(new OctoEconomyAccount(player));
        } catch (EconomyNotExistException e) {
            return(new NullEconomyAccount(player));
        }
    }
}
