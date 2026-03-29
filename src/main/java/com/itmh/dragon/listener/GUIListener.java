package com.itmh.dragon.listener;

import com.itmh.dragon.gui.ResultsGUI;
import com.itmh.dragon.gui.ResultsGUISession;
import com.itmh.dragon.model.ItemSearchResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Spigot : getTitle() retourne une String (avec les codes couleur)
        String title = event.getView().getTitle();

        if (!title.contains(ResultsGUI.GUI_TITLE_MARKER)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        List<ItemSearchResult> results = ResultsGUISession.getResults(player);
        String query = ResultsGUISession.getQuery(player);
        int page = ResultsGUISession.getPage(player);

        if (results == null || query == null) return;

        int slot = event.getSlot();
        int totalPages = (int) Math.ceil(results.size() / 45.0);

        if (slot == 45 && page > 0) {
            ResultsGUI.open(player, results, query, page - 1);
        }

        if (slot == 53 && page < totalPages - 1) {
            ResultsGUI.open(player, results, query, page + 1);
        }
    }
}
