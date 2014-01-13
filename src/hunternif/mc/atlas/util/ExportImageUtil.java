package hunternif.mc.atlas.util;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.ExportUpdateListener;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.MapTile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ExportImageUtil {
	public static final int TILE_SIZE = 16;
	
	/** Beware that the background texture doesn't follow the Autotile format. */
	public static final int BG_TILE_SIZE = 22;
	
	/** Opens a dialog and returns the file that was chosen, null if none or error. */
	public static File selectPngFileToSave(String atlasName) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			AntiqueAtlasMod.logger.log(Level.WARNING, "Setting system Look&Feel for JFileChooser", e);
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Export image");
		chooser.setSelectedFile(new File(atlasName + ".png"));
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "PNG Image";
			}
			@Override
			public boolean accept(File file) {
				// Accept all non-extisted files. PNG extension can be added later.
				return !file.exists();
			}
		});
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			// Check file extension:
			if (!file.getName().substring(file.getName().length() - 4).equalsIgnoreCase(".png")) {
				file = new File(file.getAbsolutePath() + ".png");
			}
			return file;
		}
		return null;
	}
	
	/** Renders the map into file as PNG image. */
	public static void exportPngImage(DimensionData data, File file, ExportUpdateListener listener) {
		Map<ShortVec2, MapTile> tiles = data.getSeenChunks();
		int totalTiles = tiles.size();
		int drawnTiles = 0;
		
		// Prepare output image
		// Leave padding of one row of map tiles on each side
		int minX = (data.getMinX() - 1) * TILE_SIZE;
		int minY = (data.getMinY() - 1) * TILE_SIZE;
		int outWidth = (data.getMaxX() + 1) * TILE_SIZE - minX;
		int outHeight = (data.getMaxY() + 1) * TILE_SIZE - minY;
		AntiqueAtlasMod.logger.info("Image size: " + outWidth + "*" + outHeight);
		BufferedImage outImage = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = outImage.createGraphics();
		
		// Draw background, double scale:
		int scale = 2;
		int bgTilesX = Math.round((float)outWidth / (float)BG_TILE_SIZE / (float)scale);
		int bgTilesY = Math.round((float)outHeight / (float)BG_TILE_SIZE / (float)scale);
		// Count background tiles too:
		totalTiles += (bgTilesX + 1) * (bgTilesY + 1);
		
		// Preload all textures (they should be small enough)
		BufferedImage bg = null;
		Map<ResourceLocation, BufferedImage> textureImageMap = new HashMap<ResourceLocation, BufferedImage>();
		try {
			InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(Textures.EXPORTED_BG).getInputStream();
			bg = ImageIO.read(is);
			is.close();
			
			// Count loaded textures as drawn tiles too:
			List<ResourceLocation> allTextures = BiomeTextureMap.INSTANCE.getAllTextures();
			totalTiles += allTextures.size();
			for (ResourceLocation texture : allTextures) {
				is = Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream();
				BufferedImage tileImage = ImageIO.read(is);
				is.close();
				textureImageMap.put(texture, tileImage);
				drawnTiles++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		listener.update((float)drawnTiles / (float) totalTiles);
		
		//================ Draw map background ================
		// Top left corner:
		graphics.drawImage(bg, 0, 0, BG_TILE_SIZE * scale, BG_TILE_SIZE * scale,
				0, 0, BG_TILE_SIZE, BG_TILE_SIZE, null);
		drawnTiles++;
		// Topmost row:
		for (int x = 1; x < bgTilesX; x++) {
			graphics.drawImage(bg, x*BG_TILE_SIZE*scale, 0, (x + 1)*BG_TILE_SIZE*scale, BG_TILE_SIZE*scale,
					BG_TILE_SIZE, 0, BG_TILE_SIZE*2, BG_TILE_SIZE, null);
			drawnTiles++;
		}
		// Leftmost column:
		for (int y = 1; y < bgTilesY; y++) {
			graphics.drawImage(bg, 0, y*BG_TILE_SIZE*scale, BG_TILE_SIZE*scale, (y + 1)*BG_TILE_SIZE*scale,
					0, BG_TILE_SIZE, BG_TILE_SIZE, BG_TILE_SIZE*2, null);
			drawnTiles++;
		}
		listener.update((float)drawnTiles / (float) totalTiles);
		// Middle:
		for (int x = 1; x < bgTilesX; x++) {
			for (int y = 1; y < bgTilesY; y++) {
				graphics.drawImage(bg,
						x*BG_TILE_SIZE*scale, y*BG_TILE_SIZE*scale,
						(x + 1)*BG_TILE_SIZE*scale, (y + 1)*BG_TILE_SIZE*scale,
						BG_TILE_SIZE, BG_TILE_SIZE, BG_TILE_SIZE*2, BG_TILE_SIZE*2, null);
				drawnTiles++;
			}
		}
		listener.update((float)drawnTiles / (float) totalTiles);
		// Top right corner:
		graphics.drawImage(bg, outWidth - BG_TILE_SIZE*scale, 0,
				outWidth, BG_TILE_SIZE * scale,
				BG_TILE_SIZE*2, 0, BG_TILE_SIZE*3, BG_TILE_SIZE, null);
		drawnTiles++;
		// Rightmost column:
		for (int y = 1; y < bgTilesY; y++) {
			graphics.drawImage(bg,
					outWidth - BG_TILE_SIZE*scale, y*BG_TILE_SIZE*scale,
					outWidth, (y + 1)*BG_TILE_SIZE*scale,
					BG_TILE_SIZE*2, BG_TILE_SIZE, BG_TILE_SIZE*3, BG_TILE_SIZE*2, null);
			drawnTiles++;
		}
		// Bottom left corner:
		graphics.drawImage(bg, 0, outHeight - BG_TILE_SIZE*scale,
				BG_TILE_SIZE*scale, outHeight,
				0, BG_TILE_SIZE*2, BG_TILE_SIZE, BG_TILE_SIZE*3, null);
		drawnTiles++;
		// Bottommost row:
		for (int x = 1; x < bgTilesX; x++) {
			graphics.drawImage(bg, x*BG_TILE_SIZE*scale, outHeight - BG_TILE_SIZE*scale,
					(x + 1)*BG_TILE_SIZE*scale, outHeight,
					BG_TILE_SIZE, BG_TILE_SIZE*2, BG_TILE_SIZE*2, BG_TILE_SIZE*3, null);
			drawnTiles++;
		}
		// Bottom right corner:
		graphics.drawImage(bg, outWidth - BG_TILE_SIZE*scale, outHeight - BG_TILE_SIZE*scale,
				outWidth, outHeight, BG_TILE_SIZE*2, BG_TILE_SIZE*2, BG_TILE_SIZE*3, BG_TILE_SIZE*3, null);
		drawnTiles++;
		listener.update((float)drawnTiles / (float) totalTiles);
	
		//============= Draw actual map tiles ==============
		ShortVec2 coords = new ShortVec2(0, 0);
		for (coords.x = data.getMinX(); coords.x <= data.getMaxX(); coords.x++) {
			for (coords.y = data.getMinY(); coords.y <= data.getMaxY(); coords.y++) {
				MapTile tile = tiles.get(coords);
				if (tile == null) continue;
				
				// Load tile texture
				ResourceLocation texture = BiomeTextureMap.instance().getTexture(tile);
				BufferedImage tileImage = textureImageMap.get(texture);
				if (tileImage == null) continue;
		        
		        // Draw tile on the output Graphics depending on the tile state:
				if (tile.isSingleObject()) {
					graphics.drawImage(tileImage,
							coords.x*TILE_SIZE - minX,
							coords.y*TILE_SIZE - minY,
							(coords.x + 1)*TILE_SIZE - minX,
							(coords.y + 1)*TILE_SIZE - minY,
							0, 0, TILE_SIZE, TILE_SIZE, null);
					continue;
				}
				
				// Render various corners
				int u = 0, v = 0;
				
				// Top left corner:
				if (tile.topLeft == MapTile.CONCAVE) { u = 2; v = 0; }
				else if (tile.topLeft == MapTile.VERTICAL) { u = 0; v = 4; }
				else if (tile.topLeft == MapTile.HORIZONTAL) { u = 2; v = 2; }
				else if (tile.topLeft == MapTile.FULL) { u = 2; v = 4; } 
				else if (tile.topLeft == MapTile.CONVEX) { u = 0; v = 2; }
				graphics.drawImage(tileImage,
						coords.x*TILE_SIZE - minX,
						coords.y*TILE_SIZE - minY,
						coords.x*TILE_SIZE + TILE_SIZE/2 - minX,
						coords.y*TILE_SIZE + TILE_SIZE/2 - minY,
						u*TILE_SIZE/2, v*TILE_SIZE/2,
						(u + 1)*TILE_SIZE/2, (v + 1)*TILE_SIZE/2, null);
				
				// Top right corner:
				if (tile.topRight == MapTile.CONCAVE) { u = 3; v = 0; }
				else if (tile.topRight == MapTile.VERTICAL) { u = 3; v = 4; }
				else if (tile.topRight == MapTile.HORIZONTAL) { u = 1; v = 2; }
				else if (tile.topRight == MapTile.FULL) { u = 1; v = 4; } 
				else if (tile.topRight == MapTile.CONVEX) { u = 3; v = 2; }
				graphics.drawImage(tileImage,
						coords.x*TILE_SIZE + TILE_SIZE/2 - minX,
						coords.y*TILE_SIZE - minY,
						coords.x*TILE_SIZE + TILE_SIZE - minX,
						coords.y*TILE_SIZE + TILE_SIZE/2 - minY,
						u*TILE_SIZE/2, v*TILE_SIZE/2,
						(u + 1)*TILE_SIZE/2, (v + 1)*TILE_SIZE/2, null);
				
				// Bottom left corner:
				if (tile.bottomLeft == MapTile.CONCAVE) { u = 2; v = 1; }
				else if (tile.bottomLeft == MapTile.VERTICAL) { u = 0; v = 3; }
				else if (tile.bottomLeft == MapTile.HORIZONTAL) { u = 2; v = 5; }
				else if (tile.bottomLeft == MapTile.FULL) { u = 2; v = 3; } 
				else if (tile.bottomLeft == MapTile.CONVEX) { u = 0; v = 5; }
				graphics.drawImage(tileImage,
						coords.x*TILE_SIZE - minX,
						coords.y*TILE_SIZE + TILE_SIZE/2 - minY,
						coords.x*TILE_SIZE + TILE_SIZE/2 - minX,
						coords.y*TILE_SIZE + TILE_SIZE - minY,
						u*TILE_SIZE/2, v*TILE_SIZE/2,
						(u + 1)*TILE_SIZE/2, (v + 1)*TILE_SIZE/2, null);
				
				// Bottom right corner:
				if (tile.bottomRight == MapTile.CONCAVE) { u = 3; v = 1; }
				else if (tile.bottomRight == MapTile.VERTICAL) { u = 3; v = 3; }
				else if (tile.bottomRight == MapTile.HORIZONTAL) { u = 1; v = 5; }
				else if (tile.bottomRight == MapTile.FULL) { u = 1; v = 3; } 
				else if (tile.bottomRight == MapTile.CONVEX) { u = 3; v = 5; }
				graphics.drawImage(tileImage,
						coords.x*TILE_SIZE + TILE_SIZE/2 - minX,
						coords.y*TILE_SIZE + TILE_SIZE/2 - minY,
						coords.x*TILE_SIZE + TILE_SIZE - minX,
						coords.y*TILE_SIZE + TILE_SIZE - minY,
						u*TILE_SIZE/2, v*TILE_SIZE/2,
						(u + 1)*TILE_SIZE/2, (v + 1)*TILE_SIZE/2, null);
				
				drawnTiles++;
				if (drawnTiles % 10 == 0) { // Update every 10 tiles
					listener.update((float)drawnTiles / (float) totalTiles);
				}
			}
		}
			
		try {
			ImageIO.write(outImage, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
