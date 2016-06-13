package hunternif.mc.atlas.client.gui;

public interface ExportUpdateListener {
	void setStatusString(String status, Object... data);
	void setHeaderString(String header, Object... data);
	void setProgressMax(int max);
	void setProgress(int progress);
	void addProgress(int amount);
}
