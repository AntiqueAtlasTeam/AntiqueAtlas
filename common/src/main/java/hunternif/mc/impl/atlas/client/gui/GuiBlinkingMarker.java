package hunternif.mc.impl.atlas.client.gui;

import hunternif.mc.impl.atlas.client.gui.core.GuiBlinkingImage;
import hunternif.mc.impl.atlas.registry.MarkerType;

public class GuiBlinkingMarker extends GuiBlinkingImage implements GuiMarkerFinalizer.IMarkerTypeSelectListener {
    public void onSelectMarkerType(MarkerType markerType) {
        setTexture(markerType.getTexture(), GuiAtlas.MARKER_SIZE, GuiAtlas.MARKER_SIZE);
    }
}
