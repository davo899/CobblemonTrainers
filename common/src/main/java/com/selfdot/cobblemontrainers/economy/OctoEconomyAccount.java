package com.selfdot.cobblemontrainers.economy;

import com.epherical.octoecon.OctoEconomy;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.user.UniqueUser;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.economy.EconomyAccount;
import com.selfdot.cobblemontrainers.economy.exceptions.EconomyNotExistException;
import com.selfdot.cobblemontrainers.economy.exceptions.PlayerAccountNotExistException;
import net.minecraft.server.network.ServerPlayerEntity;

public class OctoEconomyAccount implements EconomyAccount {
    private final UniqueUser account;
    private final Currency currency;

    public OctoEconomyAccount(ServerPlayerEntity player) throws EconomyNotExistException {
        try {
            Economy economy = OctoEconomy.getInstance().getCurrentEconomy();
            account = economy.getOrCreatePlayerAccount(player.getUuid());
            currency = economy.getDefaultCurrency();
        } catch (NoClassDefFoundError e) {
            logMessageFailedToLoadOctoEconomy();
            throw(new EconomyNotExistException());
        }
    }

    private void logMessageFailedToLoadOctoEconomy() {
        String message = String.format("Failed to load OctoEconomy");
        CobblemonTrainers.INSTANCE.getLogger().atDebug().log(message);
    }

    @Override
    public long getBalance() {
        double balanceInDouble = account.getBalance(currency);
        return doubleToLong(balanceInDouble);
    }

    @Override
    public void addBalance(long amount) {
        double amountInDouble = longToDouble(amount);
        account.depositMoney(currency, amountInDouble, "addBalance");
    }

    @Override
    public void removeBalance(long amount) {
        double amountInDouble = longToDouble(amount);
        account.withdrawMoney(currency, amountInDouble, "removeBalance");
    }

    private double longToDouble(long value) {
        return(Long.valueOf(value).doubleValue());
    }

    private long doubleToLong(double value) {
        return(Double.valueOf(value).longValue());
    }

    @Override
    public void assertExist() throws PlayerAccountNotExistException {
        if(account == null) {
            throw(new PlayerAccountNotExistException());
        }
    }
}
