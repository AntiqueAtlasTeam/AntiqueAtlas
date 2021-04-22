package hunternif.mc.impl.atlas.client;

import static hunternif.mc.impl.atlas.client.Textures.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import hunternif.mc.impl.atlas.ClientProxy;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import hunternif.mc.impl.atlas.util.Log;

@OnlyIn(Dist.CLIENT)
public class TextureSet implements Comparable<TextureSet> {
	
	/** Name of the texture pack to write in the config file. */
	public final ResourceLocation name;
	
	/** The actual textures in this set. */
	public final ITexture[] textures;
	
	/** Texture sets that a tile rendered with this set can be stitched to,
	 * excluding itself. */
	private final Set<ResourceLocation> stitchTo = new HashSet<>();
	private final Set<ResourceLocation> stitchToHorizontal = new HashSet<>();
	private final Set<ResourceLocation> stitchToVertical = new HashSet<>();
	private final ResourceLocation[] texturePaths;
	
	private boolean stitchesToNull = false;
	private boolean anisotropicStitching = false;
	
	/** Name has to be unique, it is used for equals() tests. */
	public TextureSet(ResourceLocation name, ResourceLocation... textures) {
        this.name = name;
        this.texturePaths = textures;
        this.textures = new ITexture[textures.length];
	}
	
	/** Allow this texture set to be stitched to empty space, i.e. edge of the map. */
	public TextureSet stitchesToNull() {
		this.stitchesToNull = true;
		return this;
	}
	
	/** Add other texture sets that this texture set will be stitched to
	 * (but the opposite may be false, in case of asymmetric stitching.) */
	@SuppressWarnings("UnusedReturnValue")
    public TextureSet stitchTo(ResourceLocation... textureSets) {
		Collections.addAll(stitchTo, textureSets);
		Collections.addAll(stitchToHorizontal, textureSets);
        Collections.addAll(stitchToVertical, textureSets);
		return this;
	}
	
	public TextureSet stitchToHorizontal(ResourceLocation... textureSets) {
		this.anisotropicStitching = true;
		Collections.addAll(stitchToHorizontal, textureSets);
		return this;
	}
	public TextureSet stitchToVertical(ResourceLocation... textureSets) {
		this.anisotropicStitching = true;
		Collections.addAll(stitchToVertical, textureSets);
		return this;
	}
	
	/** Actually used when stitching along the diagonal. */
	public boolean shouldStitchTo(TextureSet toSet) {
		return toSet == this || stitchesToNull && toSet == null || stitchTo.contains(toSet.name);
	}
	public boolean shouldStitchToHorizontally(TextureSet toSet) {
		if (toSet == this || stitchesToNull && toSet == null) return true;
		if (anisotropicStitching) return stitchToHorizontal.contains(toSet.name);
		else return stitchTo.contains(toSet.name);
	}
	public boolean shouldStitchToVertically(TextureSet toSet) {
		if (toSet == this || stitchesToNull && toSet == null) return true;
		if (anisotropicStitching) return stitchToVertical.contains(toSet.name);
		else return stitchTo.contains(toSet.name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TextureSet)) {
			return false;
		}
		TextureSet set = (TextureSet) obj;
		return this.name.equals(set.name);
	}
	
	public ITexture getTexture(int variationNumber) {
        return textures[variationNumber % textures.length];
    }
	
	public ResourceLocation[] getTexturePaths() {
        return texturePaths;
    }

    public void loadTextures() {
        for (int i = 0; i < texturePaths.length; i++) {
        	if (!Textures.TILE_TEXTURES_MAP.containsKey(texturePaths[i])) {
                throw new RuntimeException("Couldn't find the specified texture: " + texturePaths[i].toString());
            }
            textures[i] = Textures.TILE_TEXTURES_MAP.get(texturePaths[i]);
        }
    }
    
    /**
     * This method goes through the list of all TextureSets this should stitch to and assert that these TextureSet exist
     */
    public void checkStitching() {
        stitchTo.stream().filter(identifier -> !TextureSetMap.isRegistered(identifier)).forEach(identifier -> {
            Log.error("The texture set %s tries to stitch to %s, which does not exists.", name, identifier);
        });
        stitchToVertical.stream().filter(identifier -> !TextureSetMap.isRegistered(identifier)).forEach(identifier -> {
            Log.error("The texture set %s tries to stitch vertically to %s, which does not exists.", name, identifier);
        });
        stitchToHorizontal.stream().filter(identifier -> !TextureSetMap.isRegistered(identifier)).forEach(identifier -> {
            Log.error("The texture set %s tries to stitch horizontally to %s, which does not exists.", name, identifier);
        });
    }
	
	/** A special texture set that is stitched to everything except water. */
	static class TextureSetShore extends TextureSet {
		public final ResourceLocation waterName;
        private TextureSet water;
		TextureSetShore(ResourceLocation name, ResourceLocation water, ResourceLocation... textures) {
			super(name, textures);
            this.waterName = water;
		}
		public void loadWater() {
            water = TextureSetMap.instance().getByName(waterName);
        }
		@Override
		public boolean shouldStitchToHorizontally(TextureSet otherSet) {
			return otherSet == this || !water.shouldStitchToHorizontally(otherSet);
		}
		@Override
		public boolean shouldStitchToVertically(TextureSet otherSet) {
			return otherSet == this || !water.shouldStitchToVertically(otherSet);
		}
	}

	@Override
	public int compareTo(TextureSet textureSet) {
		return name.toString().compareTo(textureSet.name.toString());
	}
}
