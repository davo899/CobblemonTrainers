package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.libs.minecraft.screen.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.selfdot.libs.cobblemon.CobblemonUtils.pokemonInfoItem;
import static com.selfdot.libs.minecraft.screen.ItemStackBuilder.itemStack;

public class SetupMenu extends Menu<SetupMenu> {

    private static final String GROUPS = "groups";
    private static final String GROUP = "group";
    private static final String TRAINER = "trainer";
    private static final String POKEMON = "pokemon";
    private static final String SPECIES = "species";
    private static final String MOVES = "moves";
    private static final String ABILITIES = "abilities";
    private static final String EVS = "evs";
    private static final String IVS = "ivs";
    private static final String DELETE_POKEMON = "delete_pokemon";
    private static final String LEVEL = "level";
    private static final String NATURE = "nature";
    private static final String HELD_ITEM = "held_item";

    private String trainerGroup;
    private Trainer trainer;
    private TrainerPokemon trainerPokemon;

    public SetupMenu(PlayerEntity player) {
        super("Trainer Setup", player, MenuSize.SIZE_9x6, GROUPS);
        setBordered(true);
    }

    @Override
    protected void registerViewFactories(Map<String, ViewFactory<SetupMenu>> viewFactories) {
        viewFactories.put(GROUPS, new ViewFactoryBuilder<SetupMenu>()
            .paged9x6(
                () -> CobblemonTrainers.INSTANCE.getTrainerRegistry().getAllTrainers().stream()
                    .map(Trainer::getGroup)
                    .distinct()
                    .toList(),
                group -> itemStack(Items.BOOK).withName(group),
                (menu, group) -> {
                    this.trainerGroup = group;
                    menu.navigate(GROUP);
                }
            )
        );
        viewFactories.put(GROUP, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(GROUPS)
            .withComponent(() -> new ComponentBuilder<SetupMenu>(4, 0, Items.BOOK)
                .withName(trainerGroup)
                .build()
            )
            .paged9x6(
                () -> CobblemonTrainers.INSTANCE.getTrainerRegistry().getAllTrainers().stream()
                    .filter(trainer -> trainer.getGroup().equals(trainerGroup))
                    .toList(),
                trainer -> itemStack(CobblemonItems.POKE_BALL).withName(trainer.getName()),
                (menu, trainer) -> {
                    this.trainer = trainer;
                    menu.navigate(TRAINER);
                }
            )
        );
        viewFactories.put(TRAINER, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(GROUP)
            .withComponent(() -> new ComponentBuilder<SetupMenu>(4, 0, CobblemonItems.POKE_BALL)
                .withName(trainer.getName())
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                List<BattlePokemon> team = trainer.getBattleTeam();
                for (int i = 0; i < 6; i++) {
                    int x = 3 + (i % 3);
                    int y = 2 + (i / 3);
                    if (i < team.size()) {
                        int teamSlot = i;
                        components.add(
                            new ComponentBuilder<SetupMenu>(x, y, pokemonInfoItem(team.get(i).getOriginalPokemon()))
                                .withAction(menu -> this.trainerPokemon = trainer.getTeamSlot(teamSlot))
                                .navigatesTo(POKEMON)
                                .build()
                        );
                    } else {
                        components.add(new ComponentBuilder<SetupMenu>(x, y, Items.BEDROCK)
                            .withName("Empty")
                            .build()
                        );
                    }
                }
                if (team.size() < 6) {
                    components.add(new ComponentBuilder<SetupMenu>(2, 2, CobblemonItems.POKE_BALL)
                        .withName("New Pokémon")
                        .navigatesTo(SPECIES)
                        .build()
                    );
                }
                return components;
            })
            .build()
        );
    }

}
