package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.atlas.client.gui.core.ToggleGroup;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This GUI is used select marker icon and enter a label.
 * When the user clicks on the confirmation button, the call to MarkerAPI is made.
 * @author Hunternif
 */
public class GuiMarkerFinalizer extends GuiComponent {
	private static final MarkerType defaultMarker = MarkerTypes.RED_X_SMALL;
	
	private World world;
	private int atlasID;
	private int dimension;
	private int x;
	private int z;
	
	MarkerType selectedType = defaultMarker;
	
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_SPACING = 4;
	
	private static final int TYPE_SPACING = 1;
	private static final int TYPE_BG_FRAME = 4;
	
	private GuiButton btnDone;
	private GuiButton btnCancel;
	private GuiTextField textField;
	private final GuiScrollingContainer scroller;
	private ToggleGroup<GuiMarkerInList> typeRadioGroup;
	
	private final List<IMarkerTypeSelectListener> listeners = new ArrayList<>();
	
	GuiMarkerFinalizer() {
		scroller = new GuiScrollingContainer();
		scroller.setWheelScrollsHorizontally();
		this.addChild(scroller);
	}
	
	void setMarkerData(World world, int atlasID, int dimension, int markerX, int markerZ) {
		this.world = world;
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.x = markerX;
		this.z = markerZ;
		setBlocksScreen(true);
	}
	
	void addListener(IMarkerTypeSelectListener listener) {
		listeners.add(listener);
	}
	void removeListener(IMarkerTypeSelectListener listener) {
		listeners.remove(listener);
	}
	void removeAllListeners() {
		listeners.clear();
	}
	
	@Override
	public void initGui() {
		buttonList.add(btnDone = new GuiButton(0, this.width/2 - BUTTON_WIDTH - BUTTON_SPACING/2, this.height/2 + 40, BUTTON_WIDTH, 20, I18n.format("gui.done")));
		buttonList.add(btnCancel = new GuiButton(0, this.width/2 + BUTTON_SPACING/2, this.height/2 + 40, BUTTON_WIDTH, 20, I18n.format("gui.cancel")));
		textField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, (this.width - 200)/2, this.height/2 - 81, 200, 20);
		textField.setFocused(true);
		textField.setText("");
		
		scroller.removeAllContent();
		int typeCount = 0;
		for (MarkerType type : MarkerRegistry.getValues()) {
			if(!type.isTechnical())
				typeCount++;
		}
		int allTypesWidth = typeCount *
				(GuiMarkerInList.FRAME_SIZE + TYPE_SPACING) - TYPE_SPACING;
		int scrollerWidth = Math.min(allTypesWidth, 240);
		scroller.setViewportSize(scrollerWidth, GuiMarkerInList.FRAME_SIZE);
		scroller.setGuiCoords((this.width - scrollerWidth)/2, this.height/2 - 25);
		
		typeRadioGroup = new ToggleGroup<>();
		typeRadioGroup.addListener(button -> {
            selectedType = button.getMarkerType();
            for (IMarkerTypeSelectListener listener : listeners) {
                listener.onSelectMarkerType(button.getMarkerType());
            }
        });
		int contentX = 0;
		for (MarkerType markerType : MarkerRegistry.getValues()) {
			if(markerType.isTechnical())
				continue;
			GuiMarkerInList markerGui = new GuiMarkerInList(markerType);
			typeRadioGroup.addButton(markerGui);
			if (selectedType.equals(markerType)) {
				typeRadioGroup.setSelectedButton(markerGui);
			}
			scroller.addContent(markerGui).setRelativeX(contentX);
			contentX += GuiMarkerInList.FRAME_SIZE + TYPE_SPACING;
		}
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException {
		super.mouseClicked(par1, par2, par3);
		textField.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		super.keyTyped(par1, par2);
		textField.textboxKeyTyped(par1, par2);
	}
	
	protected void actionPerformed(GuiButton button) {
		if (button == btnDone) {
			AtlasAPI.markers.putMarker(world, true, atlasID, selectedType, textField.getText(), x, z);
			Log.info("Put marker in Atlas #%d \"%s\" at (%d, %d)", atlasID, textField.getText(), x, z);
			close();
		} else if (button == btnCancel) {
			close();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		drawCenteredString(I18n.format("gui.antiqueatlas.marker.label"), this.height/2 - 97, 0xffffff, true);
		textField.drawTextBox();
		drawCenteredString(I18n.format("gui.antiqueatlas.marker.type"), this.height/2 - 44, 0xffffff, true);
		
		// Darkrer background for marker type selector
		drawGradientRect(scroller.getGuiX() - TYPE_BG_FRAME, scroller.getGuiY() - TYPE_BG_FRAME,
				scroller.getGuiX() + scroller.getWidth() + TYPE_BG_FRAME,
				scroller.getGuiY() + scroller.getHeight() + TYPE_BG_FRAME,
				0x88101010, 0x99101010);
		super.drawScreen(mouseX, mouseY, partialTick);
	}
	
	protected interface IMarkerTypeSelectListener {
		void onSelectMarkerType(MarkerType markerType);
	}
}
