package hunternif.mc.atlas.client.gui;

import net.minecraft.client.resources.I18n;

public enum ExportUpdateListener {
	INSTANCE;
	
	public float maxProgress; // float so that division isn't rounded
	public int currentProgress;
	
	public String status;
	public String header;
	
	public void setStatusString(String status, Object... data) {
		this.status = I18n.format(status, data);
	}
	
	public void setHeaderString(String header, Object... data) {
		this.header = I18n.format(header, data);
	}

	public void setProgressMax(int max) {
		maxProgress = max;
		currentProgress = 0;
	}

	public void setProgress(int progress) {
		currentProgress = progress;
	}

	public void addProgress(int amount) {
		currentProgress += amount;
	}
}
