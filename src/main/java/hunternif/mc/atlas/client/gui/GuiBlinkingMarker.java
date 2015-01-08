package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.marker.MarkerTextureMap;

public class GuiBlinkingMarker extends GuiBlinkingImage implements GuiMarkerFinalizer.IMarkerTypeSelectListener {
	public void onSelectMarkerType(String markerType) {
		setTexture(MarkerTextureMap.INSTANCE.getTexture(markerType), GuiAtlas.MARKER_SIZE, GuiAtlas.MARKER_SIZE);
	}
}
