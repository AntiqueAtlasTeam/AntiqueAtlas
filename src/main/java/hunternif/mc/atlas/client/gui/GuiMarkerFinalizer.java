package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.atlas.client.gui.core.ToggleGroup;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

/**
 * This GUI is used select marker icon and enter a label.
 * When the user clicks on the confirmation button, the call to MarkerAPI is made.
 * @author Hunternif
 */
public class GuiMarkerFinalizer extends GuiComponent {
	private World world;
	private int atlasID;
	private DimensionType dimension;
	private int markerX;
	private int markerZ;

	MarkerType selectedType = MarkerRegistry.findDefault();

	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_SPACING = 4;

	private static final int TYPE_SPACING = 1;
	private static final int TYPE_BG_FRAME = 4;

	private Button btnDone;
	private Button btnCancel;
	private TextFieldWidget textField;
	private final GuiScrollingContainer scroller;
	private ToggleGroup<GuiMarkerInList> typeRadioGroup;

	private final List<IMarkerTypeSelectListener> markerListeners = new ArrayList<>();

	GuiMarkerFinalizer() {
		scroller = new GuiScrollingContainer();
		scroller.setWheelScrollsHorizontally();
		this.addChild(scroller);
	}

	void setMarkerData(World world, int atlasID, DimensionType dimension, int markerX, int markerZ) {
		this.world = world;
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.markerX = markerX;
		this.markerZ = markerZ;
		setBlocksScreen(true);
	}

	void addMarkerListener(IMarkerTypeSelectListener listener) {
		markerListeners.add(listener);
	}

	void removeMarkerListener(IMarkerTypeSelectListener listener) {
		markerListeners.remove(listener);
	}

	void removeAllMarkerListeners() {
		markerListeners.clear();
	}

	@Override
	protected void init() {
		super.init();

		addButton(btnDone = new Button(this.width/2 - BUTTON_WIDTH - BUTTON_SPACING/2, this.height/2 + 40, BUTTON_WIDTH, 20, I18n.format("gui.done"), (button) -> {
			AtlasAPI.markers.putMarker(world, true, atlasID, MarkerRegistry.getId(selectedType).toString(), textField.getText(), markerX, markerZ);
			Log.info("Put marker in Atlas #%d \"%s\" at (%d, %d)", atlasID, textField.getText(), markerX, markerZ);
			close();
		}));
		addButton(btnCancel = new Button(this.width/2 + BUTTON_SPACING/2, this.height/2 + 40, BUTTON_WIDTH, 20, I18n.format("gui.cancel"), (button) -> {
			close();
		}));
		textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, (this.width - 200)/2, this.height/2 - 81, 200, 20, I18n.format("gui.antiqueatlas.marker.label"));
		textField.setEnabled(true);
		textField.setText("");

		scroller.removeAllContent();
		int typeCount = 0;
		for (MarkerType type : MarkerRegistry.iterable()) {
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
            for (IMarkerTypeSelectListener listener : markerListeners) {
                listener.onSelectMarkerType(button.getMarkerType());
            }
        });
		int contentX = 0;
		for (MarkerType markerType : MarkerRegistry.iterable()) {
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button) || textField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int aa, int bb, int cc) {
		return super.keyPressed(aa, bb, cc) || textField.keyPressed(aa, bb, cc);
	}

	@Override
	public boolean charTyped(char aa, int bb) {
		return super.charTyped(aa, bb) || textField.charTyped(aa, bb);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.renderBackground();
		drawCenteredString(I18n.format("gui.antiqueatlas.marker.label"), this.height/2 - 97, 0xffffff, true);
		textField.render(mouseX, mouseY, partialTick);
		drawCenteredString(I18n.format("gui.antiqueatlas.marker.type"), this.height/2 - 44, 0xffffff, true);

		// Darker background for marker type selector
		fillGradient(scroller.getGuiX() - TYPE_BG_FRAME, scroller.getGuiY() - TYPE_BG_FRAME,
				scroller.getGuiX() + scroller.getWidth() + TYPE_BG_FRAME,
				scroller.getGuiY() + scroller.getHeight() + TYPE_BG_FRAME,
				0x88101010, 0x99101010);
		super.render(mouseX, mouseY, partialTick);
	}

	interface IMarkerTypeSelectListener {
		void onSelectMarkerType(MarkerType markerType);
	}
}
