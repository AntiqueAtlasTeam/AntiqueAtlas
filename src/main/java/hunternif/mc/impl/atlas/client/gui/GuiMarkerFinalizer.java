package hunternif.mc.impl.atlas.client.gui;

import hunternif.mc.api.client.AtlasClientAPI;
import hunternif.mc.impl.atlas.client.gui.core.GuiComponent;
import hunternif.mc.impl.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.impl.atlas.client.gui.core.ToggleGroup;
 import hunternif.mc.impl.atlas.registry.MarkerType;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * This GUI is used select marker icon and enter a label.
 * When the user clicks on the confirmation button, the call to MarkerAPI is made.
 *
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class GuiMarkerFinalizer extends GuiComponent {
    private Level world;
    private int atlasID;
    private int markerX;
    private int markerZ;

    MarkerType selectedType = MarkerType.REGISTRY.get(MarkerType.REGISTRY.getDefaultKey());

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_SPACING = 4;

    private static final int TYPE_SPACING = 1;
    private static final int TYPE_BG_FRAME = 4;

    private Button btnDone;
    private Button btnCancel;
    private EditBox textField;
    private GuiScrollingContainer scroller;
    private ToggleGroup<GuiMarkerInList> typeRadioGroup;

    private final List<IMarkerTypeSelectListener> markerListeners = new ArrayList<>();

    GuiMarkerFinalizer() {
    }

    void setMarkerData(Level world, int atlasID, int markerX, int markerZ) {
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

        addRenderableWidget(btnDone = new Button(this.width / 2 - BUTTON_WIDTH - BUTTON_SPACING / 2, this.height / 2 + 40, BUTTON_WIDTH, 20, new TranslatableComponent("gui.done"), (button) -> {
            AtlasClientAPI.getMarkerAPI().putMarker(world, true, atlasID, MarkerType.REGISTRY.getKey(selectedType), new TextComponent(textField.getValue()), markerX, markerZ);
            Log.info("Put marker in Atlas #%d \"%s\" at (%d, %d)", atlasID, textField.getValue(), markerX, markerZ);

            LocalPlayer player = Minecraft.getInstance().player;
            world.playSound(player, player.blockPosition(),
                    SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.AMBIENT,
                    1F, 1F);
            close();
        }));
        addRenderableWidget(btnCancel = new Button(this.width / 2 + BUTTON_SPACING / 2, this.height / 2 + 40, BUTTON_WIDTH, 20, new TranslatableComponent("gui.cancel"), (button) -> {
            close();
        }));
        textField = new EditBox(Minecraft.getInstance().font, (this.width - 200) / 2, this.height / 2 - 81, 200, 20, new TranslatableComponent("gui.antiqueatlas.marker.label"));
        textField.setEditable(true);
        textField.setValue("");

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

    public void setMarkerName(Component name) {
        textField.setValue(name.getContents());
    }

    @Override
    public void close() {
        super.close();
        if (scroller != null) {
            scroller.close();
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
    public void render(PoseStack matrices, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(matrices);
        drawCentered(matrices, new TranslatableComponent("gui.antiqueatlas.marker.label"), this.height / 2 - 97, 0xffffff, true);
        textField.render(matrices, mouseX, mouseY, partialTick);
        drawCentered(matrices, new TranslatableComponent("gui.antiqueatlas.marker.type"), this.height / 2 - 44, 0xffffff, true);

        // Darker background for marker type selector
        fillGradient(matrices, scroller.getGuiX() - TYPE_BG_FRAME, scroller.getGuiY() - TYPE_BG_FRAME,
                scroller.getGuiX() + scroller.getWidth() + TYPE_BG_FRAME,
                scroller.getGuiY() + scroller.getHeight() + TYPE_BG_FRAME,
                0x88101010, 0x99101010);
        super.render(matrices, mouseX, mouseY, partialTick);
    }

    interface IMarkerTypeSelectListener {
        void onSelectMarkerType(MarkerType markerType);
    }
}
