package hunternif.mc.impl.atlas.mixinhooks;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.item.AtlasItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class CartographyTableHooks {
    public static void onTake(PlayerEntity player, ItemStack map, ItemStack atlas) {
    	if (player.getEntityWorld().isRemote()) {
            return;
        }
        if (map.getItem() == Items.FILLED_MAP) {
        	MapData mapState = FilledMapItem.getMapData(map, player.getEntityWorld());
            if (mapState != null) {
                mapState.mapDecorations.forEach((key, icon) -> {
                    int i = 1 << mapState.scale;

                    int x = (int) ((int) (icon.getX() - 0.5f) / 2f) * i + mapState.xCenter;
                    int z = (int) ((int) (icon.getY() - 0.5f) / 2f) * i + mapState.zCenter;
                    
                    ResourceLocation type = null;
                    ITextComponent label = null;

                    if (icon.getType() == MapDecoration.Type.RED_X) {
                    	type = AntiqueAtlasMod.id("red_x_small");
                        label = new TranslationTextComponent("gui.antiqueatlas.marker.treasure");
                    } else if (icon.getType() == MapDecoration.Type.MONUMENT) {
                    	type = AntiqueAtlasMod.id("monument");
                        label = new TranslationTextComponent("gui.antiqueatlas.marker.monument");
                    } else if (icon.getType() == MapDecoration.Type.MANSION) {
                    	type = AntiqueAtlasMod.id("mansion");
                        label = new TranslationTextComponent("gui.antiqueatlas.marker.mansion");
                    }

                    if (type != null) {
                        AtlasAPI.getMarkerAPI().putMarker(player.getEntityWorld(), true, AtlasItem.getAtlasID(atlas), type, label, x, z);
                    }
                });
            }
        }
    }
}