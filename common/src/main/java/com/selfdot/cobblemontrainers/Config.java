package com.selfdot.cobblemontrainers;

import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.selfdot.cobblemontrainers.trainer.Generation5AI;
import com.selfdot.cobblemontrainers.util.CommandExecutor;
import lombok.Getter;
import lombok.Setter;

import static com.selfdot.cobblemontrainers.util.CommandExecutor.CONSOLE;

@Getter @Setter
public class Config {

    private boolean xpEnabled = true;
    private CommandExecutor commandExecutor = CONSOLE;
    private int strongAILevel = -1;

    public boolean isXpDisabled() {
        return !xpEnabled;
    }

    public BattleAI getCurrentAI() {
        return strongAILevel == -1 ? new Generation5AI() : new StrongBattleAI(strongAILevel);
    }

}
