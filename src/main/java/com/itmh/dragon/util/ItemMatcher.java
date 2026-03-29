package com.itmh.dragon.util;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ItemMatcher {

    /**
     * Retourne true si l'item correspond à la requête.
     * Vérifie (insensible à la casse, correspondance partielle) :
     * - Nom du matériau
     * - Nom personnalisé (display name)
     * - Lignes de lore
     * - Noms des enchantements
     */
    public static boolean matches(ItemStack item, String query) {
        if (item == null || item.getType().isAir()) return false;

        String q = query.toLowerCase().trim();

        // 1. Nom du matériau
        if (item.getType().name().toLowerCase().contains(q)) return true;

        // 2. Display name, lore et enchantements
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            // Display name (Spigot retourne une String avec codes couleur — on les retire)
            if (meta.hasDisplayName()) {
                String displayName = ChatColor.stripColor(meta.getDisplayName());
                if (displayName != null && displayName.toLowerCase().contains(q)) return true;
            }

            // Lore
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    for (String line : lore) {
                        String plainLine = ChatColor.stripColor(line);
                        if (plainLine != null && plainLine.toLowerCase().contains(q)) return true;
                    }
                }
            }

            // Enchantements
            Map<Enchantment, Integer> enchants = meta.getEnchants();
            for (Enchantment enchant : enchants.keySet()) {
                if (enchant.getKey().getKey().toLowerCase().contains(q)) return true;
            }

            // Livres enchantés
            if (meta instanceof EnchantmentStorageMeta esm) {
                for (Enchantment enchant : esm.getStoredEnchants().keySet()) {
                    if (enchant.getKey().getKey().toLowerCase().contains(q)) return true;
                }
            }
        }

        return false;
    }
}
