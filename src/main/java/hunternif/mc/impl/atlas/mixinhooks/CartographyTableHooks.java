package hunternif.mc.impl.atlas.mixinhooks;

import hunternif.mc.api.Markers;
import hunternif.mc.api.client.MarkerRegistry;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CartographyTableHooks {
    public static void onTakeItem(PlayerEntity player, ItemStack map, ItemStack atlas) {
        if (map.getItem() == Items.FILLED_MAP) {
            MapState mapState = FilledMapItem.getMapState(map, player.getEntityWorld());
            if (mapState != null) {
                mapState.icons.forEach((key, icon) -> {
                    int i = 1 << mapState.scale;

                    int x = (int) ((int) (icon.getX() - 0.5f) / 2f) * i + mapState.xCenter;
                    int z = (int) ((int) (icon.getZ() - 0.5f) / 2f) * i + mapState.zCenter;

                    if (icon.getType() == MapIcon.Type.RED_X) {
                        Identifier type = AntiqueAtlasMod.id("red_x_small");
                        if (!player.getEntityWorld().isClient()) {
                            Markers.API.putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, new TranslatableText("gui.antiqueatlas.marker.treasure"), x, z);
                        }
                    } else if (icon.getType() == MapIcon.Type.MONUMENT) {
                        Identifier type = AntiqueAtlasMod.id("monument");
                        if (!player.getEntityWorld().isClient()) {
                            Markers.API.putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, new TranslatableText("gui.antiqueatlas.marker.monument"), x, z);
                        }
                    } else if (icon.getType() == MapIcon.Type.MANSION) {
                        Identifier type = AntiqueAtlasMod.id("mansion");
                        if (!player.getEntityWorld().isClient()) {
                            Markers.API.putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, new TranslatableText("gui.antiqueatlas.marker.mansion"), x, z);
                        }
                    }
                });
            }
        }
    }
}
