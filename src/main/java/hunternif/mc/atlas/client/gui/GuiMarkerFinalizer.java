package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.atlas.client.gui.core.ToggleGroup;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

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

	private ButtonWidget btnDone;
	private ButtonWidget btnCancel;
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
	protected void onInitialized() {
		super.onInitialized();

		addButton(btnDone = new ButtonWidget(this.width/2 - BUTTON_WIDTH - BUTTON_SPACING/2, this.height/2 + 40, BUTTON_WIDTH, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double double_1, double double_2) {
				super.onPressed(double_1, double_2);
				AtlasAPI.markers.putMarker(world, true, atlasID, MarkerRegistry.getId(selectedType).toString(), textField.getText(), markerX, markerZ);
				Log.info("Put marker in Atlas #%d \"%s\" at (%d, %d)", atlasID, textField.getText(), markerX, markerZ);
				close();
			}

		});
		addButton(btnCancel = new ButtonWidget(this.width/2 + BUTTON_SPACING/2, this.height/2 + 40, BUTTON_WIDTH, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double double_1, double double_2) {
				super.onPressed(double_1, double_2);
				close();
			}
		});
		textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, (this.width - 200)/2, this.height/2 - 81, 200, 20);
		textField.setIsEditable(true);
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
		return textField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return textField.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int aa, int bb, int cc) {
		return textField.keyPressed(aa, bb, cc) || super.keyPressed(aa, bb, cc);
	}

	@Override
	public boolean charTyped(char aa, int bb) {
		return textField.charTyped(aa, bb) || super.charTyped(aa, bb);
	}

	@Override
	public boolean keyReleased(int aa, int bb, int cc) {
		return textField.keyReleased(aa, bb, cc) || super.keyReleased(aa, bb, cc);
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTick) {
		this.drawBackground();
		drawCenteredString(I18n.translate("gui.antiqueatlas.marker.label"), this.height/2 - 97, 0xffffff, true);
		textField.draw(mouseX, mouseY, partialTick);
		drawCenteredString(I18n.translate("gui.antiqueatlas.marker.type"), this.height/2 - 44, 0xffffff, true);

		// Darker background for marker type selector
		drawGradientRect(scroller.getGuiX() - TYPE_BG_FRAME, scroller.getGuiY() - TYPE_BG_FRAME,
				scroller.getGuiX() + scroller.getWidth() + TYPE_BG_FRAME,
				scroller.getGuiY() + scroller.getHeight() + TYPE_BG_FRAME,
				0x88101010, 0x99101010);
		super.draw(mouseX, mouseY, partialTick);
	}

	interface IMarkerTypeSelectListener {
		void onSelectMarkerType(MarkerType markerType);
	}
}
