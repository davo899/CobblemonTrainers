package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeldItemSelectScreen extends PagedScreen<Item> {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int removeItemIndex;

    public HeldItemSelectScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon), BATTLE_ITEMS, 0);
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        removeItemIndex = slotIndex(3, 4);

        super.initialize(inventory);

        ItemStack heldItemItem = ScreenUtils.withoutAdditional(CobblemonItems.CHOICE_SCARF);
        heldItemItem.setCustomName(Text.literal("Held Item"));
        inventory.setStack(columns / 2, heldItemItem);

        ItemStack removeItemItem = ScreenUtils.withoutAdditional(Items.STICK);
        removeItemItem.setCustomName(Text.literal("Remove Held Item"));
        inventory.setStack(removeItemIndex, removeItemItem);
    }

    @Override
    protected ItemStack toItem(Item item) {
        return new ItemStack(item);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == removeItemIndex) {
            trainerPokemon.setHeldItem(Items.AIR);
            trainer.save();
            switchTo(new TrainerPokemonScreen(trainer, trainerPokemon));
        }
    }

    @Override
    protected void onSelected(Item item, PlayerEntity player) {
        trainerPokemon.setHeldItem(item);
        trainer.save();
        switchTo(new TrainerPokemonScreen(trainer, trainerPokemon));
    }

    @Override
    public String getDisplayName() {
        return "Held Item";
    }

    private static final List<Item> BATTLE_ITEMS = new ArrayList<>() {{
        add(CobblemonItems.ASSAULT_VEST);
        add(CobblemonItems.BIG_ROOT);
        add(CobblemonItems.BLACK_BELT);
        add(CobblemonItems.BLACK_GLASSES);
        add(CobblemonItems.BLACK_SLUDGE);
        add(CobblemonItems.BRIGHT_POWDER);
        add(CobblemonItems.CHARCOAL);
        add(CobblemonItems.CHOICE_BAND);
        add(CobblemonItems.CHOICE_SCARF);
        add(CobblemonItems.CHOICE_SPECS);
        add(CobblemonItems.DEEP_SEA_SCALE);
        add(CobblemonItems.DEEP_SEA_TOOTH);
        add(CobblemonItems.DESTINY_KNOT);
        add(CobblemonItems.DRAGON_FANG);
        add(CobblemonItems.FAIRY_FEATHER);
        add(CobblemonItems.FLAME_ORB);
        add(CobblemonItems.FOCUS_BAND);
        add(CobblemonItems.HARD_STONE);
        add(CobblemonItems.HEAVY_DUTY_BOOTS);
        add(CobblemonItems.KINGS_ROCK);
        add(CobblemonItems.LEFTOVERS);
        add(CobblemonItems.LIFE_ORB);
        add(CobblemonItems.LIGHT_CLAY);
        add(CobblemonItems.MAGNET);
        add(CobblemonItems.MENTAL_HERB);
        add(CobblemonItems.METAL_POWDER);
        add(CobblemonItems.MIRACLE_SEED);
        add(CobblemonItems.MIRROR_HERB);
        add(CobblemonItems.MUSCLE_BAND);
        add(CobblemonItems.MYSTIC_WATER);
        add(CobblemonItems.NEVER_MELT_ICE);
        add(CobblemonItems.POISON_BARB);
        add(CobblemonItems.POWER_ANKLET);
        add(CobblemonItems.POWER_BAND);
        add(CobblemonItems.POWER_BELT);
        add(CobblemonItems.POWER_BRACER);
        add(CobblemonItems.POWER_HERB);
        add(CobblemonItems.POWER_LENS);
        add(CobblemonItems.POWER_WEIGHT);
        add(CobblemonItems.QUICK_CLAW);
        add(CobblemonItems.QUICK_POWDER);
        add(CobblemonItems.RAZOR_CLAW);
        add(CobblemonItems.RAZOR_FANG);
        add(CobblemonItems.ROCKY_HELMET);
        add(CobblemonItems.SAFETY_GOGGLES);
        add(CobblemonItems.SHARP_BEAK);
        add(CobblemonItems.SILK_SCARF);
        add(CobblemonItems.SILVER_POWDER);
        add(CobblemonItems.SOFT_SAND);
        add(CobblemonItems.SPELL_TAG);
        add(CobblemonItems.TOXIC_ORB);
        add(CobblemonItems.TWISTED_SPOON);
        add(CobblemonItems.WHITE_HERB);
        add(CobblemonItems.WISE_GLASSES);

        add(CobblemonItems.ORAN_BERRY);
        add(CobblemonItems.CHERI_BERRY);
        add(CobblemonItems.CHESTO_BERRY);
        add(CobblemonItems.PECHA_BERRY);
        add(CobblemonItems.RAWST_BERRY);
        add(CobblemonItems.ASPEAR_BERRY);
        add(CobblemonItems.PERSIM_BERRY);
        add(CobblemonItems.OCCA_BERRY);
        add(CobblemonItems.PASSHO_BERRY);
        add(CobblemonItems.WACAN_BERRY);
        add(CobblemonItems.RINDO_BERRY);
        add(CobblemonItems.YACHE_BERRY);
        add(CobblemonItems.CHOPLE_BERRY);
        add(CobblemonItems.KEBIA_BERRY);
        add(CobblemonItems.SHUCA_BERRY);
        add(CobblemonItems.COBA_BERRY);
        add(CobblemonItems.PAYAPA_BERRY);
        add(CobblemonItems.TANGA_BERRY);
        add(CobblemonItems.CHARTI_BERRY);
        add(CobblemonItems.KASIB_BERRY);
        add(CobblemonItems.HABAN_BERRY);
        add(CobblemonItems.COLBUR_BERRY);
        add(CobblemonItems.BABIRI_BERRY);
        add(CobblemonItems.CHILAN_BERRY);
        add(CobblemonItems.ROSELI_BERRY);
        add(CobblemonItems.LEPPA_BERRY);
        add(CobblemonItems.LUM_BERRY);
        add(CobblemonItems.FIGY_BERRY);
        add(CobblemonItems.WIKI_BERRY);
        add(CobblemonItems.MAGO_BERRY);
        add(CobblemonItems.AGUAV_BERRY);
        add(CobblemonItems.IAPAPA_BERRY);
        add(CobblemonItems.SITRUS_BERRY);
        add(CobblemonItems.TOUGA_BERRY);
        add(CobblemonItems.ENIGMA_BERRY);
        add(CobblemonItems.KEE_BERRY);
        add(CobblemonItems.MARANGA_BERRY);
        add(CobblemonItems.LIECHI_BERRY);
        add(CobblemonItems.GANLON_BERRY);
        add(CobblemonItems.SALAC_BERRY);
        add(CobblemonItems.PETAYA_BERRY);
        add(CobblemonItems.APICOT_BERRY);
        add(CobblemonItems.LANSAT_BERRY);
        add(CobblemonItems.STARF_BERRY);
        add(CobblemonItems.MICLE_BERRY);
        add(CobblemonItems.CUSTAP_BERRY);
        add(CobblemonItems.JABOCA_BERRY);
        add(CobblemonItems.ROWAP_BERRY);

        sort(Comparator.comparing(item -> item.getName().getString()));
    }};

}
