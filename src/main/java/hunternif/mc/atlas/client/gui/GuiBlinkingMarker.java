package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.gui.core.GuiBlinkingImage;
import hunternif.mc.atlas.registry.MarkerType;

public class GuiBlinkingMarker extends GuiBlinkingImage implements GuiMarkerFinalizer.IMarkerTypeSelectListener {
	public void onSelectMarkerType(MarkerType markerType) {
		setTexture(markerType.getIcon(), GuiAtlas.MARKER_SIZE, GuiAtlas.MARKER_SIZE);
	}
}
