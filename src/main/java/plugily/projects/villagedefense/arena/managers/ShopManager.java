/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.arena.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.Arena;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

  private final String defaultGolemItemName;
  private final String defaultWolfItemName;

  private final Main plugin;
  private final FileConfiguration config;
  private final Arena arena;
  private NormalFastInv gui;
  private Consumer<Player> openMenuConsumer;

  public ShopManager(Arena arena) {
    plugin = arena.getPlugin();
    config = ConfigUtils.getConfig(plugin, "arenas");
    this.arena = arena;

    defaultGolemItemName = plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_GOLEM_ITEM");
    defaultWolfItemName = plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_WOLF_ITEM");

    if(config.isSet("instances." + arena.getId() + ".shop")) {
      registerShop();
    }
    openMenuConsumer = player -> {
      if(plugin.getArenaRegistry().getArena(player) == null) {
        return;
      }
      if(gui == null) {
        player.sendMessage(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_NOT_DEFINED"));
        return;
      }
      gui.open(player);
    };
  }

  public NormalFastInv getShop() {
    return gui;
  }

  public void setShop(NormalFastInv gui) {
    this.gui = gui;
  }

  public void setOpenMenuConsumer(@NotNull Consumer<Player> openMenuConsumer) {
    this.openMenuConsumer = openMenuConsumer;
  }

  /**
   * Default name of golem spawn item from language.yml
   *
   * @return the default golem item name
   */
  public String getDefaultGolemItemName() {
    return defaultGolemItemName;
  }

  /**
   * Default name of wolf spawn item from language.yml
   *
   * @return the default wolf item name
   */
  public String getDefaultWolfItemName() {
    return defaultWolfItemName;
  }

  public void openShop(Player player) {
    if(openMenuConsumer != null) {
      openMenuConsumer.accept(player);
    }
  }

  private void registerShop() {
    if(!validateShop()) {
      return;
    }
    ItemStack[] contents = ((Chest) LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".shop"))
        .getBlock().getState()).getInventory().getContents();
    gui = new NormalFastInv(plugin.getBukkitHelper().serializeInt(contents.length), plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_GUI"));
    gui.addClickHandler(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    for(int slot = 0; slot < contents.length; slot++) {
      ItemStack itemStack = contents[slot];
      if(itemStack == null || itemStack.getType() == Material.REDSTONE_BLOCK) {
        continue;
      }

      String costString = "";
      ItemMeta meta = itemStack.getItemMeta();
      //seek for item price
      if(meta != null && meta.hasLore()) {
        for(String s : ComplementAccessor.getComplement().getLore(meta)) {
          if(s.contains(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY")) || s.contains("orbs")) {
            costString = ChatColor.stripColor(s).replaceAll("&[0-9a-zA-Z]", "").replaceAll("[^0-9]", "");
            break;
          }
        }
      }

      int cost;
      try {
        cost = Integer.parseInt(costString);
      } catch(NumberFormatException e) {
        plugin.getDebugger().debug(Level.WARNING, "No price set for shop item in arena {0} skipping item!", arena.getId());
        continue;
      }

      gui.setItem(slot, itemStack, event -> {
        Player player = (Player) event.getWhoClicked();

        if(!arena.getPlayers().contains(player)) {
          return;
        }

        User user = plugin.getUserManager().getUser(player);
        int orbs = user.getStat("ORBS");

        if(cost > orbs) {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_NOT_ENOUGH_CURRENCY"));
          return;
        }

        if(ItemUtils.isItemStackNamed(itemStack)) {
          String name = ComplementAccessor.getComplement().getDisplayName(itemStack.getItemMeta());
          if(name.contains(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_GOLEM_ITEM"))
              || name.contains(defaultGolemItemName)) {
            arena.spawnGolem(arena.getStartLocation(), player);
            adjustOrbs(user, cost);
            return;
          }
          if(name.contains(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_WOLF_ITEM"))
              || name.contains(defaultWolfItemName)) {
            arena.spawnWolf(arena.getStartLocation(), player);
            adjustOrbs(user, cost);
            return;
          }
        }

        ItemStack stack = itemStack.clone();
        ItemMeta itemMeta = stack.getItemMeta();

        if(itemMeta != null) {
          if(itemMeta.hasLore()) {
            ComplementAccessor.getComplement().setLore(itemMeta, ComplementAccessor.getComplement().getLore(itemMeta).stream().filter(lore ->
                    !lore.contains(plugin.getChatManager().colorMessage("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY")))
                .collect(Collectors.toList()));
          }

          stack.setItemMeta(itemMeta);
        }

        player.getInventory().addItem(stack);
        adjustOrbs(user, cost);
      });
    }
  }

  private void adjustOrbs(User user, int cost) {
    user.addStat("ORBS", -cost);
    arena.changeArenaOptionBy("TOTAL_ORBS_SPENT", cost);
  }

  private boolean validateShop() {
    String shop = config.getString("instances." + arena.getId() + ".shop", "");
    if(!shop.contains(",")) {
      plugin.getDebugger().debug(Level.WARNING, "There is no shop for arena {0}! Aborting registering shop!", arena.getId());
      return false;
    }
    Location location = LocationSerializer.getLocation(shop);
    if(location.getWorld() == null || !(location.getBlock().getState() instanceof Chest)) {
      plugin.getDebugger().debug(Level.WARNING, "Shop failed to load, invalid location for location {0}", LocationSerializer.locationToString(location));
      return false;
    }
    return true;
  }

}
