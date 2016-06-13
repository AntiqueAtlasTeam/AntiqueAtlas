package hunternif.mc.atlas.client.gui;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.resources.I18n;

public enum ExportProgressOverlay implements ExportUpdateListener {
	INSTANCE;
	
	private float maxProgress; // float so that division isn't rounded;
	private int currentProgress;
	
	private String status;
	private String header;

	@Override
	public void setStatusString(String status, Object... data) {
		this.status = I18n.format(status, data);
	}
	
	@Override
	public void setHeaderString(String header, Object... data) {
		this.header = I18n.format(header, data);
	}

	@Override
	public void setProgressMax(int max) {
		maxProgress = max;
		currentProgress = 0;
	}

	@Override
	public void setProgress(int progress) {
		currentProgress = progress;
	}

	@Override
	public void addProgress(int amount) {
		currentProgress += amount;
	}

	@SubscribeEvent
	public void draw(RenderGameOverlayEvent.Post event) {
		
		if(event.getType() != ElementType.ALL)
			return;
		
	}
	
}
