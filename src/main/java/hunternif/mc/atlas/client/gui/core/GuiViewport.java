package hunternif.mc.atlas.client.gui.core;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

/**
 * The children of this component are rendered and process input only inside
 * the viewport frame. Use {@link #setSize(int, int)} to set its bounds.
 * @author Hunternif
 */
public class GuiViewport extends GuiComponent {
	/** The container component for content. */
	final GuiComponent content = new GuiComponent();
	
	/** Coordinate scale factor relative to the actual screen size. */
	private double screenScale;
	
	public GuiViewport() {
		this.addChild(content);
	}
	
	/** Add scrolling content. Use removeContent to remove it.
	 * @return the child added */
	public GuiComponent addContent(GuiComponent child) {
		return content.addChild(child);
	}
	/** @return the child removed */
	public GuiComponent removeContent(GuiComponent child) {
		return content.removeChild(child);
	}
	public void removeAllContent() {
		content.removeAllChildren();
	}
	
	@Override
	public void init(Minecraft client, int w, int h) {
		super.init(client, w, h);
		screenScale = client.getMainWindow().getGuiScaleFactor();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float par3) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) (getGuiX()*screenScale),
				(int) (minecraft.getMainWindow().getFramebufferHeight() - (getGuiY() + properHeight)*screenScale),
				(int) (properWidth*screenScale), (int) (properHeight*screenScale));
		
		// Draw the content (child GUIs):
		super.render(mouseX, mouseY, par3);
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	@Override
	boolean iterateMouseInput(UiCall callMethod) {
		if (isMouseInRegion(getGuiX(), getGuiY(), properWidth, properHeight)) {
			return iterateInput(callMethod);
		} else {
			return false;
		}
	}
	
	@Override
	public int getWidth() {
		return properWidth;
	}
	@Override
	public int getHeight() {
		return properHeight;
	}
	
	@Override
	protected void validateSize() {
		super.validateSize();
		// Update the clipping flag on content's child components:
		for (GuiComponent child : this.getChildren()) {
			child.setClipped(child.getGuiY() > getGuiY() + properHeight ||
					child.getGuiY() + child.getHeight() < getGuiY() ||
					child.getGuiX() > getGuiX() + properWidth ||
					child.getGuiX() + child.getWidth() < getGuiX());
		}
	}
}
