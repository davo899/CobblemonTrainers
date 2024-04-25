package com.selfdot.cobblemontrainers.menu;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.libs.cobblemon.CobblemonUtils;
import com.selfdot.libs.minecraft.screen.*;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cobblemon.mod.common.CobblemonItems.*;
import static com.selfdot.libs.cobblemon.CobblemonUtils.*;
import static com.selfdot.libs.minecraft.screen.ItemStackBuilder.itemStack;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

@Slf4j
public class SetupMenu extends Menu<SetupMenu> {

    private static final String GROUPS = "groups";
    private static final String GROUP = "group";
    private static final String TRAINER = "trainer";
    private static final String TEAM_ORDER = "teamOrder";
    private static final String POKEMON = "pokemon";
    private static final String SPECIES = "species";
    private static final String FORMS = "forms";
    private static final String MOVESET = "moveset";
    private static final String MOVES = "moves";
    private static final String ABILITIES = "abilities";
    private static final String EVS = "evs";
    private static final String EV_EDIT = "ev_edit";
    private static final String IVS = "ivs";
    private static final String IV_EDIT = "iv_edit";
    private static final String DELETE_POKEMON = "delete_pokemon";
    private static final String LEVEL = "level";
    private static final String NATURE = "nature";
    private static final String HELD_ITEM = "held_item";

    private String selectedGroup;
    private Trainer selectedTrainer;
    private TrainerPokemon selectedPokemon;
    private int selectedMove;
    private Stats selectedStat;
    private Species selectedSpecies;
    private int selectedSwapIndex = -1;

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
                    this.selectedGroup = group;
                    menu.resetPage();
                    menu.navigate(GROUP);
                }
            )
        );

        viewFactories.put(GROUP, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(GROUPS)
            .withComponent(() -> new ComponentBuilder<SetupMenu>(4, 0, Items.BOOK)
                .withName(selectedGroup)
                .build()
            )
            .paged9x6(
                () -> CobblemonTrainers.INSTANCE.getTrainerRegistry().getAllTrainers().stream()
                    .filter(trainer -> trainer.getGroup().equals(selectedGroup))
                    .toList(),
                trainer -> itemStack(POKE_BALL).withName(trainer.getName()),
                (menu, trainer) -> {
                    this.selectedTrainer = trainer;
                    menu.resetPage();
                    menu.navigate(TRAINER);
                }
            )
        );

        viewFactories.put(TRAINER, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(GROUP)
            .withComponent(() -> new ComponentBuilder<SetupMenu>(4, 0, POKE_BALL)
                .withName(selectedTrainer.getName())
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(7, 4, TIMER_BALL)
                .withName("Edit team order")
                .navigatesTo(TEAM_ORDER)
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                List<BattlePokemon> team = selectedTrainer.getBattleTeam();
                for (int i = 0; i < 6; i++) {
                    int x = 3 + (i % 3);
                    int y = 2 + (i / 3);
                    if (i < team.size()) {
                        int teamSlot = i;
                        components.add(
                            new ComponentBuilder<SetupMenu>(x, y, pokemonInfoItem(team.get(i).getOriginalPokemon()))
                                .withAction(menu -> this.selectedPokemon = selectedTrainer.getTeamSlot(teamSlot))
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
                    components.add(new ComponentBuilder<SetupMenu>(2, 2, POKE_BALL)
                        .withName("New Pokémon")
                        .navigatesTo(SPECIES)
                        .build()
                    );
                }
                return components;
            })
            .build()
        );

        viewFactories.put(TEAM_ORDER, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(TRAINER)
            .withComponent(() -> new ComponentBuilder<SetupMenu>(4, 0, POKE_BALL)
                .withName("Edit Team Order")
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                List<BattlePokemon> team = selectedTrainer.getBattleTeam();
                for (int i = 0; i < 6; i++) {
                    int x = 3 + (i % 3);
                    int y = 2 + (i / 3);
                    if (i < team.size()) {
                        int teamSlot = i;
                        components.add(
                            new ComponentBuilder<SetupMenu>(
                                x, y, selectedSwapIndex == teamSlot ?
                                    itemStack(Items.LIME_STAINED_GLASS_PANE) :
                                    pokemonInfoItem(team.get(i).getOriginalPokemon())
                            )
                                .withAction(menu -> {
                                    if (selectedSwapIndex == -1) {
                                        selectedSwapIndex = teamSlot;
                                    } else {
                                        selectedTrainer.swap(teamSlot, selectedSwapIndex);
                                        selectedSwapIndex = -1;
                                    }
                                    menu.refresh();
                                })
                                .build()
                        );
                    }
                }
                return components;
            })
            .build()
        );

        viewFactories.put(SPECIES, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(TRAINER)
            .paged9x6(
                () -> PokemonSpecies.INSTANCE.getSpecies().stream()
                    .sorted(Comparator.comparingInt(Species::getNationalPokedexNumber)).toList(),
                CobblemonUtils::speciesItem,
                (menu, species) -> {
                    menu.resetPage();
                    if (species.getForms().size() <= 1) {
                        selectedTrainer.addSpecies(species, Set.of());
                        selectedTrainer.save();
                        menu.navigate(TRAINER);
                    } else {
                        selectedSpecies = species;
                        menu.navigate(FORMS);
                    }
                }
            )
        );

        viewFactories.put(FORMS, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(SPECIES)
            .paged9x6(
                () -> selectedSpecies.getForms().stream().toList(),
                form -> speciesItem(selectedSpecies, new HashSet<>(form.getAspects())).withName(form.getName()),
                (menu, form) -> {
                    selectedTrainer.addSpecies(selectedSpecies, new HashSet<>(form.getAspects()));
                    selectedTrainer.save();
                    menu.resetPage();
                    menu.navigate(TRAINER);
                }
            )
        );

        viewFactories.put(POKEMON, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(TRAINER)
            .withComponent(() -> new ComponentBuilder<SetupMenu>(
                4, 0, pokemonInfoItem(selectedPokemon.toPokemon())
            ).build())
            .withComponent(new ComponentBuilder<SetupMenu>(2, 2, Items.MUSIC_DISC_5)
                .withName("Moves")
                .navigatesTo(MOVESET)
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(3, 2, CLOVER_SWEET)
                .withName("Abilities")
                .navigatesTo(ABILITIES)
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(4, 2, FLOWER_SWEET)
                .withName("EVs")
                .navigatesTo(EVS)
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(5, 2, STAR_SWEET)
                .withName("IVs")
                .navigatesTo(IVS)
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(6, 2, Items.BARRIER)
                .withName("Delete Pokémon")
                .navigatesTo(DELETE_POKEMON)
                .build()
            )
            .withComponent(() -> {
                boolean hasItem = !selectedPokemon.getHeldItem().equals(Items.AIR);
                return new ComponentBuilder<SetupMenu>(
                        2, 3, hasItem ? selectedPokemon.getHeldItem() : Items.STICK
                    )
                    .withName(hasItem ?
                        GREEN + selectedPokemon.getHeldItem().getDefaultStack().getName().getString() :
                        RED + "None"
                    )
                    .withAdditional()
                    .navigatesTo(HELD_ITEM)
                    .build();
            })
            .withComponent(new ComponentBuilder<SetupMenu>(3, 3, Items.NETHER_STAR)
                .withName("Toggle Shiny")
                .withAction(menu -> {
                    selectedPokemon.toggleShiny();
                    selectedTrainer.save();
                })
                .navigatesTo(POKEMON)
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(4, 3, WISE_GLASSES)
                .withName("Level")
                .navigatesTo(LEVEL)
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(5, 3, Items.CYAN_DYE)
                .withName("Nature")
                .navigatesTo(NATURE)
                .build()
            )
            .build()
        );

        viewFactories.put(MOVESET, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, Items.MUSIC_DISC_5)
                .withName("Moveset")
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    Move move = selectedPokemon.getMoveset().get(i);
                    int finalI = i;
                    components.add(
                        (move == null ?
                            new ComponentBuilder<SetupMenu>(2 + i + (i / 2), 2, Items.BARRIER).withName("Empty") :
                            new ComponentBuilder<SetupMenu>(2 + i + (i / 2), 2, moveItem(move))
                        )
                        .withAction(menu -> selectedMove = finalI)
                        .navigatesTo(MOVES)
                        .build()
                    );
                }
                return components;
            })
            .build()
        );

        viewFactories.put(MOVES, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(MOVESET)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, Items.MUSIC_DISC_MELLOHI)
                .withName("Moves")
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(3, 4, Items.MUSIC_DISC_CHIRP)
                .withName("Delete Move")
                .withAction(menu -> {
                    selectedPokemon.getMoveset().setMove(selectedMove, null);
                    selectedTrainer.save();
                })
                .navigatesTo(MOVESET)
                .build()
            )
            .paged9x6(
                () -> {
                    Learnset learnset = selectedPokemon.toPokemon().getForm().getMoves();
                    return Stream.of(
                            learnset.getLevelUpMovesUpTo(selectedPokemon.getLevel()),
                            learnset.getTmMoves(),
                            learnset.getEggMoves(),
                            learnset.getEvolutionMoves(),
                            learnset.getFormChangeMoves(),
                            learnset.getTutorMoves()
                        )
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(
                            (moveTemplate) -> moveTemplate.getName() + moveTemplate.getElementalType().getName(),
                            p -> p, (p, q) -> p
                        )).values().stream()
                        .filter(moveTemplate -> selectedPokemon.getMoveset().getMoves().stream().noneMatch(
                            move -> move.getTemplate().equals(moveTemplate)
                        ))
                        .map(MoveTemplate::create)
                        .sorted(Comparator.comparing(Move::getName))
                        .toList();
                },
                CobblemonUtils::moveItem,
                (menu, move) -> {
                    selectedPokemon.getMoveset().setMove(selectedMove, move);
                    selectedTrainer.save();
                    menu.resetPage();
                    menu.navigate(MOVESET);
                }
            )
        );

        viewFactories.put(ABILITIES, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, CLOVER_SWEET)
                .withName("Abilities")
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                List<AbilityTemplate> abilities = new ArrayList<>();
                Pokemon pokemon = selectedPokemon.toPokemon();
                pokemon.getForm().getAbilities()
                    .forEach(potentialAbility -> abilities.add(potentialAbility.getTemplate()));
                int selectedIndex = abilities.indexOf(pokemon.getAbility().getTemplate());
                for (int i = 0; i < abilities.size(); i++) {
                    int finalI = i;
                    components.add(
                        new ComponentBuilder<SetupMenu>(
                            3 + i, 2,
                            i == selectedIndex ? CLOVER_SWEET : CHARCOAL
                        ).withName(Text.translatable(abilities.get(i).getDisplayName()).getString())
                            .withAction(menu -> {
                                selectedPokemon.setAbility(abilities.get(finalI).create(false));
                                selectedTrainer.save();
                            })
                            .navigatesTo(ABILITIES)
                            .build()
                    );
                }
                return components;
            })
            .build()
        );

        viewFactories.put(EVS, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, FLOWER_SWEET)
                .withName("EVs")
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                Stats[] stats = {
                    Stats.HP, Stats.ATTACK, Stats.DEFENCE,
                    Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED,
                };
                for (int i = 0; i < 6; i++) {
                    int finalI = i;
                    components.add(
                        new ComponentBuilder<SetupMenu>(
                            3 + (i % 3), 2 + (i / 3), statVitaminItemStack(stats[i])
                        ).withAction(menu -> selectedStat = stats[finalI])
                        .navigatesTo(EV_EDIT)
                        .build()
                    );
                }
                return components;
            })
            .build()
        );

        viewFactories.put(EV_EDIT, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(EVS)
            .integerEditor(
                () -> selectedPokemon.getEvs().getOrDefault(selectedStat),
                value -> {
                    selectedPokemon.getEvs().add(
                        selectedStat, value - selectedPokemon.getEvs().getOrDefault(selectedStat)
                    );
                    selectedTrainer.save();
                },
                0, 252, 4, 32,
                () -> statVitaminItem(selectedStat),
                () -> selectedStat.getDisplayName().getString()
            ).build()
        );

        viewFactories.put(IVS, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, STAR_SWEET)
                .withName("IVs")
                .build()
            )
            .withComponents(() -> {
                List<Component<SetupMenu>> components = new ArrayList<>();
                Stats[] stats = {
                    Stats.HP, Stats.ATTACK, Stats.DEFENCE,
                    Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED,
                };
                for (int i = 0; i < 6; i++) {
                    int finalI = i;
                    components.add(
                        new ComponentBuilder<SetupMenu>(
                            3 + (i % 3), 2 + (i / 3), statVitaminItemStack(stats[i])
                        ).withAction(menu -> selectedStat = stats[finalI])
                            .navigatesTo(IV_EDIT)
                            .build()
                    );
                }
                return components;
            })
            .build()
        );

        viewFactories.put(IV_EDIT, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(IVS)
            .integerEditor(
                () -> selectedPokemon.getIvs().getOrDefault(selectedStat),
                value -> {
                    selectedPokemon.getIvs().set(selectedStat, value);
                    selectedTrainer.save();
                },
                0, 31, 1, 10,
                () -> statVitaminItem(selectedStat),
                () -> selectedStat.getDisplayName().getString()
            ).build()
        );

        viewFactories.put(DELETE_POKEMON, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 2, Items.LIME_CONCRETE)
                .withName("Confirm Delete Pokémon?")
                .withAction(menu -> {
                    selectedTrainer.removeTrainerPokemon(selectedPokemon);
                    selectedTrainer.save();
                })
                .navigatesTo(TRAINER)
                .build()
            )
            .build()
        );

        viewFactories.put(LEVEL, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .integerEditor(
                () -> selectedPokemon.getLevel(),
                value -> {
                    selectedPokemon.setLevel(value);
                    selectedTrainer.save();
                },
                1, 100, 1, 10,
                () -> WISE_GLASSES,
                () -> "Level"
            ).build()
        );

        viewFactories.put(NATURE, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, Items.CYAN_DYE)
                .withName("Nature")
                .build()
            )
            .paged9x6(
                () -> Natures.INSTANCE.all().stream().toList(),
                CobblemonUtils::natureItem,
                (menu, nature) -> {
                    selectedPokemon.setNature(nature);
                    selectedTrainer.save();
                    menu.resetPage();
                    menu.navigate(POKEMON);
                }
            )
        );

        viewFactories.put(HELD_ITEM, new ViewFactoryBuilder<SetupMenu>()
            .returnsTo(POKEMON)
            .withComponent(new ComponentBuilder<SetupMenu>(4, 0, CobblemonItems.CHOICE_SCARF)
                .withName("Held Item")
                .build()
            )
            .withComponent(new ComponentBuilder<SetupMenu>(3, 4, Items.STICK)
                .withName("Remove Held Item")
                .withAction(menu -> {
                    selectedPokemon.setHeldItem(Items.AIR);
                    selectedTrainer.save();
                })
                .navigatesTo(POKEMON)
                .build()
            )
            .paged9x6(
                () -> BATTLE_ITEMS,
                item -> itemStack(item).withAdditional(),
                (menu, item) -> {
                    selectedPokemon.setHeldItem(item);
                    selectedTrainer.save();
                    menu.resetPage();
                    menu.navigate(POKEMON);
                }
            )
        );
    }

    private static final List<Item> BATTLE_ITEMS = new ArrayList<>() {{
        add(ASSAULT_VEST);
        add(BIG_ROOT);
        add(BLACK_BELT);
        add(BLACK_GLASSES);
        add(BLACK_SLUDGE);
        add(BRIGHT_POWDER);
        add(CHARCOAL);
        add(CHOICE_BAND);
        add(CHOICE_SCARF);
        add(CHOICE_SPECS);
        add(DEEP_SEA_SCALE);
        add(DEEP_SEA_TOOTH);
        add(DESTINY_KNOT);
        add(DRAGON_FANG);
        add(FAIRY_FEATHER);
        add(FLAME_ORB);
        add(FOCUS_BAND);
        add(HARD_STONE);
        add(HEAVY_DUTY_BOOTS);
        add(KINGS_ROCK);
        add(LEFTOVERS);
        add(LIFE_ORB);
        add(LIGHT_CLAY);
        add(MAGNET);
        add(MENTAL_HERB);
        add(METAL_POWDER);
        add(MIRACLE_SEED);
        add(MIRROR_HERB);
        add(MUSCLE_BAND);
        add(MYSTIC_WATER);
        add(NEVER_MELT_ICE);
        add(POISON_BARB);
        add(POWER_ANKLET);
        add(POWER_BAND);
        add(POWER_BELT);
        add(POWER_BRACER);
        add(POWER_HERB);
        add(POWER_LENS);
        add(POWER_WEIGHT);
        add(QUICK_CLAW);
        add(QUICK_POWDER);
        add(RAZOR_CLAW);
        add(RAZOR_FANG);
        add(ROCKY_HELMET);
        add(SAFETY_GOGGLES);
        add(SHARP_BEAK);
        add(SILK_SCARF);
        add(SILVER_POWDER);
        add(SOFT_SAND);
        add(SPELL_TAG);
        add(TOXIC_ORB);
        add(TWISTED_SPOON);
        add(WHITE_HERB);
        add(WISE_GLASSES);

        add(ORAN_BERRY);
        add(CHERI_BERRY);
        add(CHESTO_BERRY);
        add(PECHA_BERRY);
        add(RAWST_BERRY);
        add(ASPEAR_BERRY);
        add(PERSIM_BERRY);
        add(OCCA_BERRY);
        add(PASSHO_BERRY);
        add(WACAN_BERRY);
        add(RINDO_BERRY);
        add(YACHE_BERRY);
        add(CHOPLE_BERRY);
        add(KEBIA_BERRY);
        add(SHUCA_BERRY);
        add(COBA_BERRY);
        add(PAYAPA_BERRY);
        add(TANGA_BERRY);
        add(CHARTI_BERRY);
        add(KASIB_BERRY);
        add(HABAN_BERRY);
        add(COLBUR_BERRY);
        add(BABIRI_BERRY);
        add(CHILAN_BERRY);
        add(ROSELI_BERRY);
        add(LEPPA_BERRY);
        add(LUM_BERRY);
        add(FIGY_BERRY);
        add(WIKI_BERRY);
        add(MAGO_BERRY);
        add(AGUAV_BERRY);
        add(IAPAPA_BERRY);
        add(SITRUS_BERRY);
        add(TOUGA_BERRY);
        add(ENIGMA_BERRY);
        add(KEE_BERRY);
        add(MARANGA_BERRY);
        add(LIECHI_BERRY);
        add(GANLON_BERRY);
        add(SALAC_BERRY);
        add(PETAYA_BERRY);
        add(APICOT_BERRY);
        add(LANSAT_BERRY);
        add(STARF_BERRY);
        add(MICLE_BERRY);
        add(CUSTAP_BERRY);
        add(JABOCA_BERRY);
        add(ROWAP_BERRY);

        sort(Comparator.comparing(item -> item.getName().getString()));
    }};

}
