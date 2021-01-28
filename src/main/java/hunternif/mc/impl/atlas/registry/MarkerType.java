package hunternif.mc.impl.atlas.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Lifecycle;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.client.texture.Texture;
import hunternif.mc.impl.atlas.util.BitMatrix;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;

public class MarkerType {
	public static final RegistryKey<Registry<MarkerType>> KEY = RegistryKey.ofRegistry(AntiqueAtlasMod.id("marker"));
	public static final DefaultedRegistry<MarkerType> REGISTRY = new DefaultedRegistry<>(AntiqueAtlasMod.id("red_x_small").toString(),
			KEY,
			Lifecycle.experimental());

	private Identifier[] icons;
	private BitMatrix[] iconPixels;
	private int[] iconSizes = null;

	private int viewSize = 2;
	private int clipMin = -1000;
	private int clipMax = 1000;

	private boolean alwaysShow = false;
	private boolean isTile = false;
	private boolean isTechnical = false;

	private double centerX = 0.5;
	private double centerY = 0.5;

	private boolean isFromJson = false;

	private final JSONData data = new JSONData(this);

	public MarkerType(Identifier... icons) {
		this.icons = icons;
	}

	public static void register(Identifier location, MarkerType type) {
		type.initMips();
		if (REGISTRY.containsId(location)) {
			int id = REGISTRY.getRawId(REGISTRY.get(location));
			REGISTRY.set(id, RegistryKey.of(KEY, location), type, Lifecycle.stable());
		} else {
			REGISTRY.add(RegistryKey.of(KEY, location), type, Lifecycle.stable());
		}
	}

	public boolean isTechnical() {
		return isTechnical;
	}

	/**
	 * Whether the marker should be hidden
	 */
	public boolean shouldHide(boolean isHidingMarkers, int scaleIndex) {
		return shouldClip(scaleIndex) || (!alwaysShow && isHidingMarkers);
	}

	/**
	 * Whether the marker should hide due to the scale clipping
	 */
    private boolean shouldClip(int scaleIndex) {
		return !(scaleIndex >= clipMin && scaleIndex <= clipMax);
	}

	/**
	 * If the cursor is currently hovering over the marker
	 *
	 * @param x
	 *            The X position in the marker (0-1 is the bounding box of the
	 *            render, though it may be outside that range)
	 * @param y
	 *            The Y position in the marker (0-1 is the bounding box of the
	 *            render, though it may be outside that range)
	 */
	public boolean shouldHover(double x, double y) {
		if(isTechnical() || x > 1 || x < 0 || y > 1 || y < 0)
			return false;
		if(iconPixels == null || iconPixels.length == 0 || iconIndex < 0 )
			return true;
		int iconX = (int)(iconPixels[iconIndex].getWidth()*x);
		int iconY = (int)(iconPixels[iconIndex].getHeight()*y);

		return iconPixels[iconIndex].get(iconX, iconY);
	}

	/**
	 * The size of the icon, in chunks
	 */
    private int viewSize() {
		return viewSize;
	}

	/**
	 * Whether the marker is a tile, and as such should scale with the map
	 */
    private boolean isTile() {
		return isTile;
	}

	/**
	 * The X position (0-1) of the icon that should be at the marker location
	 */
    private double getCenterX() {
		return centerX;
	}

	/**
	 * The Y position (0-1) of the icon that should be at the marker location
	 */
    private double getCenterY() {
		return centerY;
	}

	/**
	 * Get the icon for the marker
	 */
	public Identifier getIcon() {
		return icons.length == 0 || iconIndex < 0 ? TextureManager.MISSING_IDENTIFIER : icons[iconIndex];
	}

	public ITexture getTexture() {
		if (icons.length == 0 || iconIndex < 0) return null;
		return new Texture(getIcon(), iconSizes[iconIndex], iconSizes[iconIndex]);
	}

	public Identifier[] getAllIcons() {
		return icons;
	}

	private int iconIndex = 0;

	public void calculateMip(double scale, double mapScale, double screenScale) {
		int size = (int) (16 * scale * viewSize());
		if (isTile) {
			size *= mapScale;
		}

		if (icons.length > 1) {
			int smallestSide = (int) (size);

			int closestValue = Integer.MAX_VALUE;
			int closestIndex = -1;
			for (int i = 0; i < iconSizes.length; i++) {
				if (iconSizes[i] < closestValue && iconSizes[i] >= smallestSide) {
					closestValue = iconSizes[i];
					closestIndex = i;
				}
			}
			if (closestIndex > 0) {
				iconIndex = closestIndex;
			}
		}
	}

	public void resetMip() {
		iconIndex = 0;
	}

	public MarkerRenderInfo getRenderInfo(double scale, double mapScale, double screenScale) {
		boolean isTile = isTile();

		int size = (int) (16 * scale * viewSize());
		if (isTile) {
			size *= mapScale;
		}
		int x = -(int) (size * getCenterX());
		int y = -(int) (size * getCenterY());

		return new MarkerRenderInfo(getTexture(), x, y, size, size);
	}

	@Environment(EnvType.CLIENT)
	public void initMips() {
		iconSizes = new int[icons.length];
		iconPixels = new BitMatrix[icons.length];
		int ALPHA_THRESHOLD = 8;
		for (int i = 0; i < icons.length; i++) {
			iconSizes[i] = -1;
			if (icons[i] == null) {
				Log.warn("Marker %s -- Texture location is null at index %d!", MarkerType.REGISTRY.getId(this).toString(), i);
			}

			Resource iresource = null;
			NativeImage bufferedimage = null;

			try {
				iresource = MinecraftClient.getInstance().getResourceManager().getResource(icons[i]);
				bufferedimage = NativeImage.read(iresource.getInputStream());
				iconSizes[i] = Math.min(bufferedimage.getWidth(), bufferedimage.getHeight());
				BitMatrix matrix = new BitMatrix(bufferedimage.getWidth(), bufferedimage.getHeight(), false);

				for (int x = 0; x < bufferedimage.getWidth(); x++) {
					for (int y = 0; y < bufferedimage.getHeight(); y++) {

						int color = bufferedimage.getPixelColor(x, y);
						int alpha = (color >> 24) & 0xff;

						if(alpha >= ALPHA_THRESHOLD) {
							matrix.set(x, y, true);

							// sides
							matrix.set(x-1, y, true);
							matrix.set(x+1, y, true);
							matrix.set(x, y-1, true);
							matrix.set(x, y+1, true);

							// corners
							matrix.set(x+1, y+1, true);
							matrix.set(x-1, y-1, true);
							matrix.set(x+1, y-1, true);
							matrix.set(x-1, y+1, true);
						}
					}
				}

				iconPixels[i] = matrix;
			} catch (IOException e) {
				Log.warn(e, "Marker %s -- Error getting texture size data for index %d - %s",
								MarkerType.REGISTRY.getId(this).toString(), i, icons[i].toString());
			} finally {
				if (bufferedimage != null) {
					bufferedimage.close();
				}
				IOUtils.closeQuietly(iresource);
			}
		}
	}

	/* Setters */

	public MarkerType setSize(int value) {
		this.viewSize = value;
		return this;
	}

	public MarkerType setIsTile(boolean value) {
		this.isTile = value;
		return this;
	}

	public MarkerType setAlwaysShow(boolean value) {
		this.alwaysShow = value;
		return this;
	}

	public MarkerType setClip(int min, int max) {
		this.clipMin = Math.min(min, max);
		this.clipMax = Math.max(min, max);
		return this;
	}

	public MarkerType setCenter(double x, double y) {
		this.centerX = x;
		this.centerY = y;
		return this;
	}

	public MarkerType setIsTechnical(boolean value) {
		this.isTechnical = value;
		return this;
	}

	public MarkerType setIsFromJson(boolean value) {
		this.isFromJson = value;
		return this;
	}

	public JSONData getJSONData() {
		return data;
	}

	public static class JSONData {
		static final String
			ICONS = "textures",
			SIZE  = "size",
			CLIP_MIN = "clipMin",
			CLIP_MAX = "clipMax",
			ALWAYS_SHOW = "alwaysShow",
			IS_TILE = "isTile",
			IS_TECH = "isTechnical",
			CENTER_X = "centerX",
			CENTER_Y = "centerY",

			NONE = "NONE";


		private final MarkerType type;

		Identifier[] icons;
		Integer viewSize = null, clipMin = null, clipMax = null;
		Boolean alwaysShow = null, isTile = null, isTechnical = null;
		Double centerX = null, centerY = null;

		JSONData(MarkerType type) {
			this.type = type;
		}

		public void saveTo(JsonObject object) {
			if(icons != null) {

				JsonArray arr = new JsonArray();

				for (Identifier loc : icons) {
					arr.add(new JsonPrimitive(loc.toString()));
				}

				object.add(ICONS, arr);
			}

			if(viewSize != null) {
				object.addProperty(SIZE, viewSize);
			}

			if(clipMin != null) {
				object.addProperty(CLIP_MIN, clipMin);
			}

			if(clipMax != null) {
				object.addProperty(CLIP_MAX, clipMax);
			}

			if(alwaysShow != null) {
				object.addProperty(ALWAYS_SHOW, alwaysShow);
			}

			if(isTile != null) {
				object.addProperty(IS_TILE, isTile);
			}

			if(isTechnical != null) {
				object.addProperty(IS_TECH, isTechnical);
			}

			if(centerX != null) {
				object.addProperty(CENTER_X, centerX);
			}

			if(centerY != null) {
				object.addProperty(CENTER_Y, centerY);
			}
		}

		public void readFrom(JsonObject object) {
			if(object.entrySet().size() == 0)
				return;

			Identifier typeName = MarkerType.REGISTRY.getId(type);
			String workingOn = NONE;
			try {
				if(object.has(ICONS) && object.get(ICONS).isJsonArray()) {
					workingOn = ICONS;
					List<Identifier> list = new ArrayList<>();
					int i = 0;
					for (JsonElement elem : object.get(ICONS).getAsJsonArray()) {
						if (elem.isJsonPrimitive()) {
							list.add(AntiqueAtlasMod.id(elem.getAsString()));
						} else {
							Log.warn("Loading marker %s from JSON: Texture item %d isn't a primitive", typeName, i);
						}
						i++;
					}
					icons = list.toArray(new Identifier[0]);
					workingOn = NONE;
				}

				if(object.has(SIZE) && object.get(SIZE).isJsonPrimitive()) {
					workingOn = SIZE;
					viewSize = object.get(SIZE).getAsInt();
					workingOn = NONE;
				}

				if(object.has(CLIP_MIN) && object.get(CLIP_MIN).isJsonPrimitive()) {
					workingOn = CLIP_MIN;
					clipMin = object.get(CLIP_MIN).getAsInt();
					workingOn = NONE;
				}

				if(object.has(CLIP_MAX) && object.get(CLIP_MAX).isJsonPrimitive()) {
					workingOn = CLIP_MAX;
					clipMax = object.get(CLIP_MAX).getAsInt();
					workingOn = NONE;
				}

				if(object.has(ALWAYS_SHOW) && object.get(ALWAYS_SHOW).isJsonPrimitive()) {
					workingOn = ALWAYS_SHOW;
					alwaysShow = object.get(ALWAYS_SHOW).getAsBoolean();
					workingOn = NONE;
				}

				if(object.has(IS_TILE) && object.get(IS_TILE).isJsonPrimitive()) {
					workingOn = IS_TILE;
					isTile = object.get(IS_TILE).getAsBoolean();
					workingOn = NONE;
				}

				if(object.has(IS_TECH) && object.get(IS_TECH).isJsonPrimitive()) {
					workingOn = IS_TECH;
					isTechnical = object.get(IS_TECH).getAsBoolean();
					workingOn = NONE;
				}

				if(object.has(CENTER_X) && object.get(CENTER_X).isJsonPrimitive()) {
					workingOn = CENTER_X;
					centerX = object.get(CENTER_X).getAsDouble();
					workingOn = NONE;
				}

				if(object.has(CENTER_Y) && object.get(CENTER_Y).isJsonPrimitive()) {
					workingOn = CENTER_Y;
					centerY = object.get(CENTER_Y).getAsDouble();
					workingOn = NONE;
				}
			} catch (ClassCastException e) {
				Log.warn(e, "Loading marker $s from JSON: Parsing element %s: element was wrong type!", typeName, workingOn);
			} catch (NumberFormatException e) {
				Log.warn(e, "Loading marker $s from JSON: Parsing element %s: element was an invalid number!", typeName, workingOn);
			}

			if(icons != null)
				type.icons = icons;

			if(viewSize != null)
				type.viewSize = viewSize;
			if(clipMin != null)
				type.clipMin = clipMin;
			if(clipMax != null)
				type.clipMax = clipMax;

			if(alwaysShow != null)
				type.alwaysShow = alwaysShow;
			if(isTile != null)
				type.isTile = isTile;
			if(isTechnical != null)
				type.isTechnical = isTechnical;

			if(centerX != null)
				type.centerX = centerX;
			if(centerY != null)
				type.centerY = centerY;
		}

	}
}
