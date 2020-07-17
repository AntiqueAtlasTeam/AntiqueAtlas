package hunternif.mc.impl.atlas.util;

/**
 * A general class that can be marked dirty so that some sort of persistence
 * mechanism know it should save it.
 * @author Hunternif
 */
public abstract class SaveData {
	private boolean dirty;
	
	protected void markDirty() {
		this.dirty = true;
	}
	
	public void setDirty(boolean value) {
		this.dirty = value;
	}
	
	public boolean isDirty() {
		return this.dirty;
	}
	
}
