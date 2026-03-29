package com.itmh.dragon.command;

import com.itmh.dragon.ItemFinderPlugin;
import com.itmh.dragon.gui.ResultsGUI;
import com.itmh.dragon.model.ItemSearchResult;
import com.itmh.dragon.search.SearchEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FindItemCommand implements CommandExecutor, TabCompleter {

    private final ItemFinderPlugin plugin;
    private final SearchEngine searchEngine;

    public FindItemCommand(ItemFinderPlugin plugin) {
        this.plugin = plugin;
        this.searchEngine = new SearchEngine(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }

        if (!player.hasPermission("itemfinder.use")) {
            player.sendMessage(ChatColor.RED + "❌ Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage : /finditem <nom ou matériau>");
            return true;
        }

        String query = String.join(" ", args);

        player.sendMessage(ChatColor.AQUA + "🔍 Recherche de " + ChatColor.WHITE + "\"" + query + "\""
                + ChatColor.AQUA + " en cours...");

        // Étape 1 : collecter les snapshots sur le main thread (accès TileEntities obligatoire)
        List<SearchEngine.InventorySnapshot> snapshots = searchEngine.collectSnapshots();

        // Étape 2 : matching en async (aucun accès Bukkit, juste du traitement de données)
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<ItemSearchResult> results = searchEngine.search(snapshots, query);

            // Étape 3 : retour sur le main thread pour ouvrir l'inventaire
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (results.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "❌ Aucun item trouvé pour "
                            + ChatColor.WHITE + "\"" + query + "\"");
                    return;
                }

                player.sendMessage(ChatColor.GREEN + "✅ " + ChatColor.WHITE
                        + results.size() + " résultat(s) trouvé(s) !");

                ResultsGUI.open(player, results, query, 0);
            });
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "diamond_sword", "netherite_sword", "diamond_pickaxe",
                    "netherite_pickaxe", "enchanted", "sharpness", "protection"
            );
        }
        return List.of();
    }
}