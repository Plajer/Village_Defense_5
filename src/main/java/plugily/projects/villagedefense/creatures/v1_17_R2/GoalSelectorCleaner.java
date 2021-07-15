/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.creatures.v1_17_R2;

import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.Set;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;

/**
 * Internal helper class
 */
class GoalSelectorCleaner {

  private GoalSelectorCleaner() {
  }

  static void clearSelectors(EntityCreature creature) {
    Set<?> goalD = (Set<?>) CreatureUtils.getPrivateField("d", PathfinderGoalSelector.class, creature.bP);
    goalD.clear();
    Set<?> targetD = (Set<?>) CreatureUtils.getPrivateField("d", PathfinderGoalSelector.class, creature.bQ);
    targetD.clear();
  }

}