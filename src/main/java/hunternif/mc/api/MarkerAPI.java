package hunternif.mc.api;

import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API for putting custom markers to the atlases. Set the textures on the
 * client side, put markers into atlases on the server side.
 *
 * @author Hunternif
 */
public interface MarkerAPI {

    /**
     * Put a marker in the specified Atlas instance at specified block
     * coordinates. Call this method per one marker either on the server or
     * on the client.
     * <p>
     * If calling this method on the client, the player must carry the atlas
     * in his inventory, to prevent griefing!
     * </p>
     *
     * @param world
     * @param visibleAhead whether the marker should appear visible even if
     *                     the player hasn't yet discovered that area.
     * @param atlasID      the ID of the atlas you want to put marker in. Equal
     *                     to ItemStack damage for ItemAtlas.
     * @param marker       name of your custom marker type.
     * @param label        text label to be displayed on mouseover.
     * @param x            block coordinate
     * @param z            block coordinate
     * @return returns the marker. null if failed or client
     */
    @Nullable
    Marker putMarker(@NotNull World world, boolean visibleAhead, int atlasID, Identifier marker, Text label, int x, int z);

    /**
     * Put a marker in all atlases in the world at specified block coordinates.
     * Server side only!
     *
     * @param world
     * @param visibleAhead whether the marker should appear visible even if
     *                     the player hasn't yet discovered that area.
     * @param marker       name of your custom marker type.
     * @param label        text label to be displayed on mouseover.
     * @param x            block coordinate
     * @param z            block coordinate
     * @return returns the marker. null if failed or client
     */
    @Nullable
    Marker putGlobalMarker(@NotNull World world, boolean visibleAhead, Identifier marker, Text label, int x, int z);

    /**
     * Delete a marker from an atlas.
     * <p>
     * If calling this method on the client,
     * the player must carry the atlas in his inventory, to prevent griefing!
     * </p>
     *
     * @param world
     * @param atlasID
     * @param markerID
     */
    void deleteMarker(@NotNull World world, int atlasID, int markerID);

    /**
     * Delete a global marker from all atlases. Server side only!
     *
     * @param world
     * @param markerID
     */
    void deleteGlobalMarker(@NotNull World world, int markerID);
}
