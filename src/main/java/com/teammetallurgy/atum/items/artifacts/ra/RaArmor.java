package com.teammetallurgy.atum.items.artifacts.ra;

import com.teammetallurgy.atum.api.AtumMats;
import com.teammetallurgy.atum.api.God;
import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.items.artifacts.ArtifactArmor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RaArmor extends ArtifactArmor {

    public RaArmor(EquipmentSlotType slot) {
        super(AtumMats.NEBU_ARMOR, "ra_armor", slot, new Item.Properties().rarity(Rarity.RARE));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public God getGod() {
        return God.RA;
    }

    @Override
    public Item getHelmet() {
        return AtumItems.HALO_OF_RA;
    }

    @Override
    public Item getChestplate() {
        return AtumItems.BODY_OF_RA;
    }

    @Override
    public Item getLeggings() {
        return AtumItems.LEGS_OF_RA;
    }

    @Override
    public Item getBoots() {
        return AtumItems.FEET_OF_RA;
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (hasFullSet(event.getEntityLiving()) && event.getSource().isFireDamage()) {
            event.setAmount(0.0F);
        }
    }
}