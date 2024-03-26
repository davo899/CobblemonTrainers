package com.selfdot.cobblemontrainers.economy;

import com.selfdot.cobblemontrainers.economy.exceptions.PlayerAccountNotExistException;
import net.minecraft.server.network.ServerPlayerEntity;

public class NullEconomyAccount implements EconomyAccount {
    public NullEconomyAccount(ServerPlayerEntity player) {

    }

    @Override
    public long getBalance() {
        return 0;
    }

    @Override
    public void addBalance(long amount) {

    }

    @Override
    public void removeBalance(long amount) {

    }

    @Override
    public void assertExist() throws PlayerAccountNotExistException {
        throw(new PlayerAccountNotExistException());
    }
}
