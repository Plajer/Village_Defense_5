package plugily.projects.villagedefense.creatures.v1_8_R3.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class VillagerBusterSpawner implements SimpleEnemySpawner {
  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5) {
      return 1D / 3;
    }
    if(wave >= 15) {
      return 1D / 8;
    }
    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5) {
      return spawnAmount / 4;
    }
    if(wave >= 15) {
      return spawnAmount - 13;
    }
    return 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 || (wave >= 15 && !arena.getVillagers().isEmpty());
  }

  @Override
  public Creature spawn(Location location) {
    Creature villagerBuster = CreatureUtils.getCreatureInitializer().spawnVillagerBuster(location);
    villagerBuster.getEquipment().setHelmet(new ItemStack(Material.TNT));
    villagerBuster.getEquipment().setHelmetDropChance(0.0F);
    VersionUtils.setItemInHandDropChance(villagerBuster, 0F);
    villagerBuster.getEquipment().setBoots(XMaterial.LEATHER_BOOTS.parseItem());
    villagerBuster.getEquipment().setLeggings(XMaterial.LEATHER_LEGGINGS.parseItem());
    villagerBuster.getEquipment().setChestplate(XMaterial.LEATHER_CHESTPLATE.parseItem());
    return villagerBuster;
  }

  @Override
  public String getName() {
    return "VillagerBuster";
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}