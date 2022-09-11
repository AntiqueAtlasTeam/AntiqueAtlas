package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.util.Identifier;


/**
 * Just a collection of Identifiers used when tiles are referenced from code
 *
 * @author Hunternif
 */
public class TileIdMap {
    public static final Identifier
            // Village:
            TILE_VILLAGE_LIBRARY = AntiqueAtlasMod.id("npc_village_library"),
            TILE_VILLAGE_SMITHY = AntiqueAtlasMod.id("npc_village_smithy"),
            TILE_VILLAGE_L_HOUSE = AntiqueAtlasMod.id("npc_village_l_house"),
            TILE_VILLAGE_FARMLAND_SMALL = AntiqueAtlasMod.id("npc_village_farmland_small"),
            TILE_VILLAGE_FARMLAND_LARGE = AntiqueAtlasMod.id("npc_village_farmland_large"),
            TILE_VILLAGE_WELL = AntiqueAtlasMod.id("npc_village_well"),
            TILE_VILLAGE_TORCH = AntiqueAtlasMod.id("npc_village_torch"),
            TILE_VILLAGE_PATH_X = AntiqueAtlasMod.id("npc_village_path_x"),
            TILE_VILLAGE_PATH_Z = AntiqueAtlasMod.id("npc_village_path_z"),
            TILE_VILLAGE_HUT = AntiqueAtlasMod.id("npc_village_hut"),
            TILE_VILLAGE_SMALL_HOUSE = AntiqueAtlasMod.id("npc_village_small_house"),
            TILE_VILLAGE_BUTCHERS_SHOP = AntiqueAtlasMod.id("npc_village_butchers_shop"),
            TILE_VILLAGE_CHURCH = AntiqueAtlasMod.id("npc_village_church"),

            TILE_RAVINE = AntiqueAtlasMod.id("ravine"),
            SWAMP_WATER = AntiqueAtlasMod.id("swamp_water"),

            // Overworld stuff:
            RUINED_PORTAL = AntiqueAtlasMod.id("ruined_portal"),

            // Nether & Nether Fortress:
            TILE_LAVA = AntiqueAtlasMod.id("lava"),
            TILE_LAVA_SHORE = AntiqueAtlasMod.id("lava_shore"),
            NETHER_FORTRESS_BRIDGE_CROSSING = AntiqueAtlasMod.id("nether_bridge"),
            NETHER_BRIDGE_X = AntiqueAtlasMod.id("nether_bridge_x"),
            NETHER_BRIDGE_Z = AntiqueAtlasMod.id("nether_bridge_z"),
            NETHER_BRIDGE_END_X = AntiqueAtlasMod.id("nether_bridge_end_x"),
            NETHER_BRIDGE_END_Z = AntiqueAtlasMod.id("nether_bridge_end_z"),
            NETHER_FORTRESS_BRIDGE_SMALL_CROSSING = AntiqueAtlasMod.id("nether_bridge_gate"),
            NETHER_FORTRESS_BRIDGE_STAIRS = AntiqueAtlasMod.id("nether_tower"),
            NETHER_FORTRESS_WALL = AntiqueAtlasMod.id("nether_wall"),
            NETHER_FORTRESS_EXIT = AntiqueAtlasMod.id("nether_hall"),
            NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM = AntiqueAtlasMod.id("nether_fort_stairs"),
            NETHER_FORTRESS_BRIDGE_PLATFORM = AntiqueAtlasMod.id("nether_throne");
}
