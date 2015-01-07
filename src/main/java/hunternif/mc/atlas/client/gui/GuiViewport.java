package hunternif.mc.atlas.client.gui;

import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * The children of this component are rendered and process input only inside
 * the viewport frame. Use {@link #setSize(int, int)} to set its bounds.
 * @author Hunternif
 */
public class GuiViewport extends GuiComponent {
	/** The container component for content. */
	protected final GuiComponent content = new GuiComponent();
	
	/** Coordinate scale factor relative to the actual screen size. */
	private int screenScale;
	
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
	public void initGui() {
		super.initGui();
		screenScale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor( getGuiX()*screenScale,
				mc.displayHeight - (getGuiY() + properHeight)*screenScale,
				properWidth*screenScale, properHeight*screenScale);
		
		// Draw the content (child GUIs):
		super.drawScreen(mouseX, mouseY, par3);
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	@Override
	public void handleMouseInput() {
		if (isPointInRegion(0, 0, properWidth, properHeight,
				Mouse.getX() * width / mc.displayWidth,
				height - Mouse.getY() * height / mc.displayHeight - 1)) {
			super.handleMouseInput();
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
			if (child.getGuiY() > getGuiY() + properHeight ||
				child.getGuiY() + child.getHeight() < getGuiY() ||
				child.getGuiX() > getGuiX() + properWidth ||
				child.getGuiX() + child.getWidth() < getGuiX()) {
				child.setClipped(true);
			} else {
				child.setClipped(false);
			}
		}
	}
}
