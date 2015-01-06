package hunternif.mc.atlas.util;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.SubTile;
import hunternif.mc.atlas.client.SubTileQuartet;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.TileRenderIterator;
import hunternif.mc.atlas.client.gui.ExportUpdateListener;
import hunternif.mc.atlas.core.DimensionData;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExportImageUtil {
	public static final int TILE_SIZE = 16;
	
	/** Beware that the background texture doesn't follow the Autotile format. */
	public static final int BG_TILE_SIZE = 22;
	
	/** Opens a dialog and returns the file that was chosen, null if none or error. */
	public static File selectPngFileToSave(String atlasName, ExportUpdateListener listener) {
		listener.setStatusString(I18n.format("gui.antiqueatlas.export.opening"));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			AntiqueAtlasMod.logger.warn("Setting system Look&Feel for JFileChooser", e);
		}
		// Hack to bring the file chooser to front: 
		Frame frame = new Frame();
		frame.setUndecorated(true);
		//frame.setOpacity(0);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.toFront();
		frame.setVisible(false);
		frame.dispose();
		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(I18n.format("gui.antiqueatlas.exportImage"));
		chooser.setSelectedFile(new File(atlasName + ".png"));
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "PNG Image";
			}
			@Override
			public boolean accept(File file) {
				// Accept all files so they are visible
				return true;
			}
		});
		listener.setStatusString(I18n.format("gui.antiqueatlas.export.selectFile"));
		if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
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
		data = data.clone(); // This seems to fix the random holes in the image.
		float totalTiles = data.getSeenChunks().size();
		int drawnTiles = 0;
		
		// Prepare output image
		// Leave padding of one row of map tiles on each side
		int minX = (data.getScope().minX - 1) * TILE_SIZE;
		int minY = (data.getScope().minY - 1) * TILE_SIZE;
		int outWidth = (data.getScope().maxX + 2) * TILE_SIZE - minX;
		int outHeight = (data.getScope().maxY + 2) * TILE_SIZE - minY;
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
		listener.setStatusString("Loading textures...");
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
		listener.setStatusString(I18n.format("gui.antiqueatlas.export.rendering"));
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
		TileRenderIterator iter = new TileRenderIterator(data);
		while(iter.hasNext()) {
			SubTileQuartet subtiles = iter.next();
			for (SubTile subtile : subtiles) {
				if (subtile == null || subtile.tile == null) continue;
				
				// Load tile texture
				ResourceLocation texture = BiomeTextureMap.instance().getTexture(subtile.tile);
				BufferedImage tileImage = textureImageMap.get(texture);
				if (tileImage == null) continue;
				
				graphics.drawImage(tileImage,
						
						TILE_SIZE + subtile.x * TILE_SIZE / 2,
						TILE_SIZE + subtile.y * TILE_SIZE / 2,
						
						TILE_SIZE + (subtile.x + 1) * TILE_SIZE / 2,
						TILE_SIZE + (subtile.y + 1) * TILE_SIZE / 2,
						
						subtile.getTextureU() * TILE_SIZE / 2,
						subtile.getTextureV() * TILE_SIZE / 2,
						
						(subtile.getTextureU() + 1) * TILE_SIZE / 2,
						(subtile.getTextureV() + 1) * TILE_SIZE / 2,
						
						null);
			}
			drawnTiles++;
			if (drawnTiles % 10 == 0) { // Update every 10 tiles
				listener.update((float)drawnTiles / (float) totalTiles);
			}
		}
		
		//TODO: draw markers on the PNG image
			
		try {
			listener.setStatusString(I18n.format("gui.antiqueatlas.export.writing"));
			ImageIO.write(outImage, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
