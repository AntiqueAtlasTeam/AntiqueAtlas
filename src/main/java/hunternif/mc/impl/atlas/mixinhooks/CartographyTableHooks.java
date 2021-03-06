package hunternif.mc.impl.atlas.mixinhooks;

import hunternif.mc.api.Markers;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class CartographyTableHooks {
    public static void onTake(PlayerEntity player, ItemStack map, ItemStack atlas) {
        if (map.getItem() == Items.FILLED_MAP) {
        	MapData mapState = FilledMapItem.getMapData(map, player.getEntityWorld());
            if (mapState != null) {
                mapState.mapDecorations.forEach((key, icon) -> {
                    int i = 1 << mapState.scale;

                    int x = (int) ((int) (icon.getX() - 0.5f) / 2f) * i + mapState.xCenter;
                    int z = (int) ((int) (icon.getY() - 0.5f) / 2f) * i + mapState.zCenter;

                    if (icon.getType() == MapDecoration.Type.RED_X) {
                        MarkerType type = MarkerType.REGISTRY.getOrDefault(AntiqueAtlasMod.id("red_x_small"));
                        if (!player.getEntityWorld().isRemote()) {
                            Markers.API.putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, new TranslationTextComponent("gui.antiqueatlas.marker.treasure"), x, z);
                        }
                    } else if (icon.getType() == MapDecoration.Type.MONUMENT) {
                        MarkerType type = MarkerType.REGISTRY.getOrDefault(AntiqueAtlasMod.id("monument"));
                        if (!player.getEntityWorld().isRemote()) {
                            Markers.API.putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, new TranslationTextComponent("gui.antiqueatlas.marker.monument"), x, z);
                        }
                    } else if (icon.getType() == MapDecoration.Type.MANSION) {
                        MarkerType type = MarkerType.REGISTRY.getOrDefault(AntiqueAtlasMod.id("mansion"));
                        if (!player.getEntityWorld().isRemote()) {
                            Markers.API.putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, new TranslationTextComponent("gui.antiqueatlas.marker.mansion"), x, z);
                        }
                    }
                });
            }
        }
    }
}