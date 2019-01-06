/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.creatures.upgrades;

/**
 * @author Plajer
 * <p>
 * Created at 14.10.2018
 */
public class Upgrade {

  private String name;
  @Deprecated
  private String[] description;
  private String metadataAccess;

  public Upgrade(String name, String[] description, String metadataAccess) {
    this.name = name;
    //we shouldn't store mutable reference
    this.description = description;
    this.metadataAccess = metadataAccess;
  }

  public String getName() {
    return name;
  }

  @Deprecated
  public String[] getDescription() {
    return description;
  }

  public String getMetadataAccess() {
    return metadataAccess;
  }

}
