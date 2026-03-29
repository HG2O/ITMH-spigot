package com.itmh.dragon.gui;

import com.itmh.dragon.model.ItemSearchResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ResultsGUI {

    private static final int PAGE_SIZE = 45;

    // Titre de base reconnu par GUIListener
    public static final String GUI_TITLE_MARKER = "Recherche";

    public static void open(Player player, List<ItemSearchResult> results, String query, int page) {
        int totalPages = Math.max(1, (int) Math.ceil(results.size() / (double) PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        String title = ChatColor.DARK_AQUA + "🔍 Recherche : "
                + ChatColor.WHITE + "\"" + query + "\""
                + ChatColor.GRAY + " — " + results.size() + " résultat(s)";

        Inventory gui = Bukkit.createInventory(null, 54, title);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, results.size());

        for (int i = start; i < end; i++) {
            gui.setItem(i - start, buildResultItem(results.get(i), i + 1));
        }

        fillNavigationBar(gui, page, totalPages, query, results.size());

        player.openInventory(gui);
        ResultsGUISession.store(player, results, query, page);
    }

    private static ItemStack buildResultItem(ItemSearchResult result, int index) {
        ItemStack display = result.getItem().clone();
        ItemMeta meta = display.getItemMeta();

        String locationIcon = switch (result.getLocationType()) {
            case PLAYER_INVENTORY  -> "🎒";
            case PLAYER_ENDERCHEST -> "🟣";
            case CHEST             -> "📦";
            case BARREL            -> "🛢";
            case SHULKER_BOX       -> "🟪";
            case OTHER_CONTAINER   -> "🗃";
        };

        String locationLabel = switch (result.getLocationType()) {
            case PLAYER_INVENTORY  -> "Inventaire";
            case PLAYER_ENDERCHEST -> "Ender Chest";
            case CHEST             -> "Coffre";
            case BARREL            -> "Tonneau";
            case SHULKER_BOX       -> "Shulker Box";
            case OTHER_CONTAINER   -> "Conteneur";
        };

        meta.setDisplayName(ChatColor.YELLOW + "#" + index + " "
                + ChatColor.AQUA + locationIcon + " " + locationLabel);

        // Récupère le lore existant (chaînes colorées), ou liste vide
        List<String> lore = new ArrayList<>(meta.hasLore() && meta.getLore() != null
                ? meta.getLore()
                : List.of());

        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.GRAY + "📍 " + ChatColor.WHITE + result.getOwnerOrCoords());
        lore.add(ChatColor.GRAY + "🔢 Quantité : " + ChatColor.WHITE + result.getCount());

        if (result.getLocation() != null) {
            lore.add(ChatColor.GRAY + "🌍 Monde : " + ChatColor.WHITE
                    + result.getLocation().getWorld().getName());
        }

        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
    }

    private static void fillNavigationBar(Inventory gui, int page, int totalPages, String query, int total) {
        ItemStack separator = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta sepMeta = separator.getItemMeta();
        sepMeta.setDisplayName(" ");
        separator.setItemMeta(sepMeta);
        for (int i = 45; i < 54; i++) gui.setItem(i, separator);

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.GREEN + "◀ Page précédente");
            prevMeta.setLore(List.of(ChatColor.GRAY + "Page " + page + "/" + totalPages));
            prev.setItemMeta(prevMeta);
            gui.setItem(45, prev);
        }

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.YELLOW + "Page " + (page + 1) + " / " + totalPages);
        infoMeta.setLore(List.of(
                ChatColor.GRAY + String.valueOf(total) + " résultat(s)",
                ChatColor.DARK_GRAY + "Recherche : \"" + query + "\""
        ));
        info.setItemMeta(infoMeta);
        gui.setItem(49, info);

        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GREEN + "Page suivante ▶");
            nextMeta.setLore(List.of(ChatColor.GRAY + "Page " + (page + 2) + "/" + totalPages));
            next.setItemMeta(nextMeta);
            gui.setItem(53, next);
        }
    }
}
