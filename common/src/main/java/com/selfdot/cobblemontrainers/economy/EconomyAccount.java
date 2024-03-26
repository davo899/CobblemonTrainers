package com.selfdot.cobblemontrainers.economy;

import com.selfdot.cobblemontrainers.economy.exceptions.PlayerAccountNotExistException;

public interface EconomyAccount {
    public long getBalance();
    public void addBalance(long amount);
    public void removeBalance(long amount);
    public void assertExist() throws PlayerAccountNotExistException;
}
