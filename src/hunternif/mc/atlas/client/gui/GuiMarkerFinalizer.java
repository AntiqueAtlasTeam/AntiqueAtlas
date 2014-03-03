package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.world.World;

/**
 * This GUI is used to enter marker label and, in future, select its icon and
 * color. When the user clicks on the confirmation button, the call to MarkerAPI
 * is made.
 * @author Hunternif
 */
public class GuiMarkerFinalizer extends GuiComponent {
	public static final String defaultMarker = "red_x_large";
	
	private World world;
	private int atlasID, dimension, x, z;
	
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_SPACING = 4;
	
	private GuiButton btnDone;
	private GuiButton btnCancel;
	private GuiTextField textField;
	
	private FontRenderer font;
	
	public void setMarkerData(World world, int atlasID, int dimension, int markerX, int markerZ) {
		this.world = world;
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.x = markerX;
		this.z = markerZ;
		font = Minecraft.getMinecraft().fontRenderer;
	}
	
	@Override
	public void initGui() {
		buttonList.add(btnDone = new GuiButton(0, this.width/2 - BUTTON_WIDTH - BUTTON_SPACING/2, this.height / 4 + 120, 80, 20, "Done"));
		buttonList.add(btnCancel = new GuiButton(0, this.width/2 + BUTTON_SPACING/2, this.height / 4 + 120, 80, 20, "Cancel"));
		textField = new GuiTextField(font, (this.width - 200)/2, this.height/2 - 40, 200, 20);
		textField.setFocused(true);
		textField.setText("");
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		textField.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		textField.textboxKeyTyped(par1, par2);
	}
	
	protected void actionPerformed(GuiButton button) {
		if (button == btnDone) {
			AtlasAPI.getMarkerAPI().putMarker(world, dimension, atlasID, defaultMarker, textField.getText(), x, z);
			AntiqueAtlasMod.logger.info("Put marker in Atlas #" + atlasID + " \"" + textField.getText() + "\" at (" + x + ", " + z + ")");
			close();
		} else if (button == btnCancel) {
			close();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		int stringWidth = font.getStringWidth("Enter label:");
		font.drawStringWithShadow("Enter label:", (this.width - stringWidth)/2, this.height/2 - 57, 0xffffff);
		textField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTick);
	}
}
