package hunternif.mc.impl.atlas.client.gui;

import hunternif.mc.api.client.AtlasClientAPI;
import hunternif.mc.impl.atlas.client.gui.core.GuiComponent;
import hunternif.mc.impl.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.impl.atlas.client.gui.core.ToggleGroup;
import hunternif.mc.impl.atlas.registry.MarkerType;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * This GUI is used select marker icon and enter a label.
 * When the user clicks on the confirmation button, the call to MarkerAPI is made.
 *
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class GuiMarkerFinalizer extends GuiComponent {
    private World world;
    private int atlasID;
    private int markerX;
    private int markerZ;

    MarkerType selectedType = MarkerType.REGISTRY.get(MarkerType.REGISTRY.getDefaultId());

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_SPACING = 4;

    private static final int TYPE_SPACING = 1;
    private static final int TYPE_BG_FRAME = 4;

    private ButtonWidget btnDone;
    private ButtonWidget btnCancel;
    private TextFieldWidget textField;
    private GuiScrollingContainer scroller;
    private ToggleGroup<GuiMarkerInList> typeRadioGroup;

    private final List<IMarkerTypeSelectListener> markerListeners = new ArrayList<>();

    GuiMarkerFinalizer() {
    }

    void setMarkerData(World world, int atlasID, int markerX, int markerZ) {
        this.world = world;
        this.atlasID = atlasID;
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
    public void init() {
        super.init();

        addDrawableChild(btnDone = ButtonWidget.builder(Text.translatable("gui.done"), (button) -> {
            AtlasClientAPI.getMarkerAPI().putMarker(world, true, atlasID, MarkerType.REGISTRY.getId(selectedType), Text.literal(textField.getText()), markerX, markerZ);
            Log.info("Put marker in Atlas #%d \"%s\" at (%d, %d)", atlasID, textField.getText(), markerX, markerZ);

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            world.playSound(player, player.getBlockPos(),
                    SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, SoundCategory.AMBIENT,
                    1F, 1F);
            closeChild();
        }).dimensions(this.width / 2 - BUTTON_WIDTH - BUTTON_SPACING / 2, this.height / 2 + 40, BUTTON_WIDTH, 20).build());
        addDrawableChild(btnCancel = ButtonWidget.builder(Text.translatable("gui.cancel"), (button) -> {
            closeChild();
        }).dimensions(this.width / 2 + BUTTON_SPACING / 2, this.height / 2 + 40, BUTTON_WIDTH, 20).build());
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, (this.width - 200) / 2, this.height / 2 - 81, 200, 20, Text.translatable("gui.antiqueatlas.marker.label"));
        textField.setEditable(true);
        textField.setFocusUnlocked(true);
        textField.setFocused(true);

        scroller = new GuiScrollingContainer();
        scroller.setWheelScrollsHorizontally();
        this.addChild(scroller);

        int typeCount = 0;
        for (MarkerType type : MarkerType.REGISTRY) {
            if (!type.isTechnical())
                typeCount++;
        }
        int allTypesWidth = typeCount *
                (GuiMarkerInList.FRAME_SIZE + TYPE_SPACING) - TYPE_SPACING;
        int scrollerWidth = Math.min(allTypesWidth, 240);
        scroller.setViewportSize(scrollerWidth, GuiMarkerInList.FRAME_SIZE + TYPE_SPACING);
        scroller.setGuiCoords((this.width - scrollerWidth) / 2, this.height / 2 - 25);

        typeRadioGroup = new ToggleGroup<>();
        typeRadioGroup.addListener(button -> {
            selectedType = button.getMarkerType();
            for (IMarkerTypeSelectListener listener : markerListeners) {
                listener.onSelectMarkerType(button.getMarkerType());
            }
        });
        int contentX = 0;
        for (MarkerType markerType : MarkerType.REGISTRY) {
            if (markerType.isTechnical())
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

    public void setMarkerName(Text name) {
        textField.setText(name.getString());
    }

    @Override
    public void closeChild() {
        super.closeChild();
        if (scroller != null) {
            scroller.closeChild();
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
    public void render(DrawContext context, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(context);
        drawCentered(context, Text.translatable("gui.antiqueatlas.marker.label"), this.height / 2 - 97, 0xffffff, true);
        textField.render(context, mouseX, mouseY, partialTick);
        drawCentered(context, Text.translatable("gui.antiqueatlas.marker.type"), this.height / 2 - 44, 0xffffff, true);

        // Darker background for marker type selector
        context.fillGradient(scroller.getGuiX() - TYPE_BG_FRAME, scroller.getGuiY() - TYPE_BG_FRAME,
                scroller.getGuiX() + scroller.getWidth() + TYPE_BG_FRAME,
                scroller.getGuiY() + scroller.getHeight() + TYPE_BG_FRAME,
                0x88101010, 0x99101010);
        super.render(context, mouseX, mouseY, partialTick);
    }

    interface IMarkerTypeSelectListener {
        void onSelectMarkerType(MarkerType markerType);
    }
}
