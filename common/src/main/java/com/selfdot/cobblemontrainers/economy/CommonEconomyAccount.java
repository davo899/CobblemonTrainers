package com.selfdot.cobblemontrainers.economy;

import com.selfdot.cobblemontrainers.economy.EconomyAccount;
import com.selfdot.cobblemontrainers.economy.exceptions.EconomyNotExistException;
import com.selfdot.cobblemontrainers.economy.exceptions.PlayerAccountNotExistException;
import eu.pb4.common.economy.api.CommonEconomy;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CommonEconomyAccount implements EconomyAccount {

    private final eu.pb4.common.economy.api.EconomyAccount account;

    public CommonEconomyAccount(ServerPlayerEntity player) throws EconomyNotExistException {
        try {
            // Not sure whether this is the correct way to make Common Economy identifier
            Identifier identifier = new Identifier("commoneconomy", "account");
            account = CommonEconomy.getAccount(player, identifier);
        } catch (NoClassDefFoundError e) {
            throw(new EconomyNotExistException());
        }
    }

    @Override
    public long getBalance() {
        return account.balance();
    }

    @Override
    public void addBalance(long amount) {
        account.increaseBalance(amount);
    }

    @Override
    public void removeBalance(long amount) {
        account.decreaseBalance(amount);
    }

    @Override
    public void assertExist() throws PlayerAccountNotExistException {
        if(account == null) {
            throw(new PlayerAccountNotExistException());
        }
    }
}
