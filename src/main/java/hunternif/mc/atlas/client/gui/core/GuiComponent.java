package hunternif.mc.atlas.client.gui.core;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * Core visual component class, which facilitates hierarchy. You can add child
 * GuiComponent's to it, and they will be rendered, notified about mouse and
 * keyboard events, window resize and will be moved around together with the
 * parent component.
 */
@SideOnly(Side.CLIENT)
public class GuiComponent extends GuiScreen {
	private GuiComponent parent = null;
	private final List<GuiComponent> children = new CopyOnWriteArrayList<>();
	
	/** The component's own size. */
	protected int properWidth, properHeight;
	/** The component's total calculated size, including itself and its children. */
	protected int contentWidth, contentHeight;
	/** If true, content size will be validated on the next update. */
	private boolean sizeIsInvalid = false;
	/** If true, this GUI will not be rendered. */
	private boolean isClipped = false;
	/** This flag is updated on every mouse event. */
	protected boolean isMouseOver = false;
	
	/** If true, mouse actions will only affect this GUI and its children,
	 * else they will only affect the in-game controller. */
	private boolean interceptsMouse = true;
	/** If true, pressing keyboard keys will affect this GUI, it's children,
	 * and the in-game controller. */
	private boolean interceptsKeyboard = true;
	/** These flags are set temporarily when this GUI has finished handling
	 * input and won't let the parent handle it, after which they are reset.
	 * This won't prevent the sibling children of this GUI from handling input.*/
	private boolean hasHandledKeyboard = false, hasHandledMouse = false;
	/** If true, no input is handled by the parent or any sibling GUIs. */
	private boolean blocksScreen = false;
	
	/** guiX and guiY are absolute coordinates on the screen. */
	private int guiX = 0, guiY = 0;
	
	/** Set absolute coordinates of the top left corner of this component on
	 * the screen. If this GUI has a parent, its size will be invalidated. */
	public void setGuiCoords(int x, int y) {
		int dx = x - guiX;
		int dy = y - guiY;
		this.guiX = x;
		this.guiY = y;
		for (GuiComponent child : children) {
			child.offsetGuiCoords(dx, dy);
		}
		if (parent != null && (dx != 0 || dy != 0)) {
			parent.invalidateSize();
		}
	}
	/** Set coordinates relative to the parent's (or to the screen, if none)
	 * top left corner. */
	public final void setRelativeCoords(int x, int y) {
		if (parent != null) {
			setGuiCoords(parent.getGuiX() + x, parent.getGuiY() + y);
		} else {
			setGuiCoords(x, y);
		}
	}
	/** Set x coordinate relative to the parent's (or the screen, if none) left. */
	public final void setRelativeX(int x) {
		if (parent != null) {
			setGuiCoords(parent.getGuiX() + x, guiY);
		} else {
			setGuiCoords(x, guiY);
		}
	}
	/** Set y coordinate relative to the parent's (or the screen, if none) top. */
	public final void setRelativeY(int y) {
		if (parent != null) {
			setGuiCoords(guiX, parent.getGuiY() + y);
		} else {
			setGuiCoords(guiX, y);
		}
	}
	/** Offset the component's coordinates by the given values. If the component
	 * has only just been added to a parent component, the result will be the
	 * same as setRelativeGuiCoords(). */
	public final void offsetGuiCoords(int dx, int dy) {
		setGuiCoords(guiX + dx, guiY + dy);
	}
	/** Position this component in the center of its parent. */
	public final void setCentered() {
		validateSize();
		if (parent == null) {
			setGuiCoords((this.width - getWidth()) / 2, (this.height - getHeight()) / 2);
		} else {
			setRelativeCoords((parent.getWidth() - getWidth())/2, (parent.getHeight() - getHeight())/2);
		}
	}
	/** Absolute X coordinate on the screen. */
	public int getGuiX() {
		return guiX;
	}
	/** Absolute Y coordinate on the screen. */
	public int getGuiY() {
		return guiY;
	}
	/** X coordinate relative to the parent's top left corner. */
	public int getRelativeX() {
		return parent == null ? guiX : (guiX - parent.guiX);
	}
	/** Y coordinate relative to the parent's top left corner. */
	public int getRelativeY() {
		return parent == null ? guiY : (guiY - parent.guiY);
	}
	
	/** Set this component's own size. This shouldn't affect the size or position of the children. */
	public void setSize(int width, int height) {
		this.properWidth = width;
		this.properHeight = height;
		this.contentWidth = width;
		this.contentHeight = height;
		invalidateSize();
	}
	
	/** Adds the child component to this GUI's content and initializes it.
	 * The child is placed at the top left corner of this component.
	 * @return the child added. */
	public GuiComponent addChild(GuiComponent child) {
		doAddChild(null, child, null);
		return child;
	}
	/** Adds the child component to this GUI's content and initializes it.
	 * The child is placed in the list immediately after the specified child,
	 * which is equivalent to putting it in front of that child in Z-order.
	 * The child is placed at the top left corner of this component.
	 * @return the child added. */
	public GuiComponent addChildInfrontOf(GuiComponent inFrontOf, GuiComponent child) {
		doAddChild(inFrontOf, child, null);
		return child;
	}
	/** Adds the child component to this GUI's content and initializes it.
	 * The child is placed in the list immediately before the specified child,
	 * which is equivalent to putting it behind that child in Z-order.
	 * The child is placed at the top left corner of this component.
	 * @return the child added. */
	public GuiComponent addChildBehind(GuiComponent behind, GuiComponent child) {
		doAddChild(null, child, behind);
		return child;
	}
	private void doAddChild(GuiComponent inFrontOf, GuiComponent child, GuiComponent behind) {
		if (child == null || children.contains(child) || parent == child) {
			return;
		}
		int i = children.indexOf(inFrontOf);
		if (i == -1) {
			int j = children.indexOf(behind);
			if (j == -1) {
				children.add(child);
			} else {
				children.add(j, child);
			}
		} else {
			children.add(i + 1, child);
		}
		child.parent = this;
		child.setGuiCoords(guiX, guiY);
		if (mc != null) {
			child.setWorldAndResolution(mc, width, height);
		}
		invalidateSize();
	}
	/** @return the child removed. */
	public GuiComponent removeChild(GuiComponent child) {
		if (child != null && children.contains(child)) {
			child.parent = null;
			children.remove(child);
			invalidateSize();
			onChildClosed(child);
		}
		return child;
	}
	public void removeAllChildren() {
		children.clear();
		invalidateSize();
	}
	/** Null if this is a top-level GUI. */
	public GuiComponent getParent() {
		return parent;
	}
	public List<GuiComponent> getChildren() {
		return children;
	}
	
	/** If true, mouse actions will only affect this GUI and its children,
	 * else they will only affect the in-game controller. */
	public void setInterceptMouse(boolean value) {
		this.interceptsMouse = value;
		this.allowUserInput = !interceptsMouse | !interceptsKeyboard;
	}
	/** If true, pressing keyboard keys will affect this GUI, it's children,
	 * and the in-game controller. */
	public void setInterceptKeyboard(boolean value) {
		this.interceptsKeyboard = value;
		this.allowUserInput = !interceptsMouse | !interceptsKeyboard;
	}

	@Override
	public void handleInput() throws IOException {
		// Traverse children backwards, because the topmost child should be the
		// first to process input:
		ListIterator<GuiComponent> iter = children.listIterator(children.size());
		while(iter.hasPrevious()) {
			GuiComponent child = iter.previous();
			if (child.blocksScreen) {
				child.handleInput();
				isMouseOver = false;
				return;
			}
		}
		if (interceptsMouse) {
			while (Mouse.next()) {
				this.handleMouseInput();
			}
		}
		if (interceptsKeyboard) {
			while (Keyboard.next()) {
				this.handleKeyboardInput();
			}
		}
	}
	
	/** Call this method from within {@link #handleMouseInput()} (or other
	 * mouse-processing methods) if the input has been handled by this GUI
	 * andshouldn't be handled by its parents.
	 * This won't prevent the sibling children of this GUI from handling input. */
	protected void mouseHasBeenHandled() {
		this.hasHandledMouse = true;
	}
	/** Call this method from within {@link #handleKeyboardInput()} if the input
	 * has been handled by this GUI and shouldn't be handled by its parents.
	 * This won't prevent the sibling children of this GUI from handling input. */
	protected void keyboardHasBeenHandled() {
		this.hasHandledKeyboard = true;
	}
	/** If true, no input is handled by the parent or any sibling GUIs. */
	protected void setBlocksScreen(boolean value) {
		this.blocksScreen = value;
	}
	
	/** Handle mouse input for this GUI and its children. */
	@Override
	public void handleMouseInput() throws IOException {
		boolean handled = false;
		isMouseOver = false;
		// Traverse children backwards, because the topmost child should be the
		// first to process input:
		ListIterator<GuiComponent> iter = children.listIterator(children.size());
		while(iter.hasPrevious()) {
			GuiComponent child = iter.previous();
			child.handleMouseInput();
			if (child.hasHandledMouse) {
				child.hasHandledMouse = false;
				handled = true;
			}
		}
		if (!handled) {
			isMouseOver = isMouseInRegion(getGuiX(), getGuiY(), getWidth(), getHeight());
			super.handleMouseInput();
		}
	}
	
	/** Handle keyboard input for this GUI and its children. */
	@Override
	public void handleKeyboardInput() throws IOException {
		boolean handled = false;
		// Traverse children backwards, because the topmost child should be the
		// first to process input:
		ListIterator<GuiComponent> iter = children.listIterator(children.size());
		while(iter.hasPrevious()) {
			GuiComponent child = iter.previous();
			child.handleKeyboardInput();
			if (child.hasHandledKeyboard) {
				child.hasHandledKeyboard = false;
				handled = true;
			}
		}
		if (!handled) {
			if (Keyboard.getEventKeyState()) {
				this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}
	}
	
	/** Render this GUI and its children. */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		for (GuiComponent child : children) {
			if (!child.isClipped) {
				child.drawScreen(mouseX, mouseY, partialTick);
			}
		}
		// Draw any hovering text requested by child components:
		if (hoveringTextInfo.shouldDraw) {
			drawHoveringText2(hoveringTextInfo.lines, hoveringTextInfo.x, hoveringTextInfo.y, hoveringTextInfo.font);
			hoveringTextInfo.shouldDraw = false;
		}
	}
	
	/** Called when the GUI is unloaded, called for each child as well. */
	@Override
	public void onGuiClosed() {
		for (GuiComponent child : children) {
			child.onGuiClosed();
		}
		super.onGuiClosed();
	}
	
	/** Called each in-game tick for this GUI and its children. If this GUI's
	 * size has been invalidated, it will be validated on the next update. */
	@Override
	public void updateScreen() {
		for (GuiComponent child : children) {
			child.updateScreen();
		}
		super.updateScreen();
		if (sizeIsInvalid) {
			validateSize();
		}
	}
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		for (GuiComponent child : children) {
			child.setWorldAndResolution(mc, width, height);
		}
	}
	
	/** Width of the GUI or its contents. This method may be called often so it
	 * should be fast. */
	public int getWidth() {
		return contentWidth;
	}
	/** Height of the GUI or its contents. This method may be called often so it
	 * should be fast. */
	public int getHeight() {
		return contentHeight;
	}
	
	/** If set to true, the parent of this GUI will not render it. */
	protected void setClipped(boolean value) {
		this.isClipped = value;
	}
	
	/** Cause the size of the component to be recalculate on the next update
	 * tick. If this GUI has a parent, the parent's size will be invalidated too. */
	protected void invalidateSize() {
		sizeIsInvalid = true;
		if (parent != null) {
			parent.invalidateSize();
		}
	}
	/** Recalculate the dimensions of the contents (children) of this GUI. */
	protected void validateSize() {
		int leftmost = Integer.MAX_VALUE;
		int rightmost = Integer.MIN_VALUE;
		int topmost = Integer.MAX_VALUE;
		int bottommost = Integer.MIN_VALUE;
		for (GuiComponent child : children) {
			int x = child.getGuiX();
			if (x < leftmost) {
				leftmost = x;
			}
			int childWidth = child.getWidth();
			if (x + childWidth > rightmost) {
				rightmost = x + childWidth;
			}
			int y = child.getGuiY();
			if (y < topmost) {
				topmost = y;
			}
			int childHeight = child.getHeight();
			if (y + childHeight > bottommost) {
				bottommost = y + childHeight;
			}
		}
		contentWidth = Math.max(properWidth, rightmost - leftmost);
		contentHeight = Math.max(properHeight, bottommost - topmost);
		sizeIsInvalid = false;
	}
	
	/** Returns true, if the mouse cursor is within the specified bounds.
	 * Note: left and top are absolute. */
	protected boolean isMouseInRegion(int left, int top, int width, int height) {
		int mouseX = getMouseX();
		int mouseY = getMouseY();
		return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY < top + height;
	}
	/**
	 * Returns true if the mouse cursor is within a rectangular box of the specified
	 * size with its center at the specified point.
	 * @param x center of the box, absolute
	 * @param y center of the box, absolute
	 * @param radius half the side of the box
	 */
	protected boolean isMouseInRadius(int x, int y, int radius) {
		int mouseX = getMouseX();
		int mouseY = getMouseY();
		return mouseX >= x - radius && mouseX < x + radius && mouseY >= y - radius && mouseY < y + radius;
	}
	
	/** Draws a standard Minecraft hovering text window, constrained by this
	 * component's dimensions (i.e. if it won't fit in when drawn to the left
	 * of the cursor, it will be drawn to the right instead). */
	protected void drawHoveringText2(List<String> lines, int x, int y, FontRenderer font) {
		if (!lines.isEmpty()) {
			// Stencil test is used by VScrollingComponent to hide the content
			// that is currently outside the viewport; that shouldn't affect
			// hovering text though. 
			boolean stencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
			if (stencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);
			RenderHelper.disableStandardItemLighting();
			
			int k = 0;
			for (String s : lines) {
				int l = font.getStringWidth(s);

				if (l > k) {
					k = l;
				}
			}

			int i1 = x + 12;
			int j1 = y - 12;
			int k1 = 8;

			if (lines.size() > 1) {
				k1 += 2 + (lines.size() - 1) * 10;
			}

			if (i1 + k > width) {
				i1 -= 28 + k;
			}

			if (j1 + k1 + 6 > height) {
				j1 = height - k1 - 6;
			}

			int l1 = -267386864;
			this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
			this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
			this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
			this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
			this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
			int i2 = 1347420415;
			int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
			this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
			this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
			this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
			this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

			for (int k2 = 0; k2 < lines.size(); ++k2) {
				String s1 = lines.get(k2);
				font.drawStringWithShadow(s1, i1, j1, -1);

				if (k2 == 0) {
					j1 += 2;
				}

				j1 += 10;
			}
			if (stencilEnabled) GL11.glEnable(GL11.GL_STENCIL_TEST);
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableBlend();
		}
	}
	
	/** Returns the top level parent of this component, or itself if it has no
	 * parent. Useful for correctly drawing hovering text. */
	public GuiComponent getTopLevelParent() {
		GuiComponent component = this;
		while (component.parent != null) {
			component = component.parent;
		}
		return component;
	}
	
	/**
	 * Draws a text tooltip at mouse coordinates.
	 * <p>
	 * Same as {@link #drawHoveringText2(List, int, int, FontRenderer)}, but
	 * the text is drawn on the top level parent component, after all its child
	 * components have finished drawing. This allows the hovering text to be
	 * unobscured by other components.
	 * </p>
	 * <p>
	 * Only one instance of hovering text can be drawn via this method, i.e.
	 * from several components which occupy the same position on the screen.
	 * </p>
	 * */
	protected void drawTooltip(List<String> lines, FontRenderer font) {
		GuiComponent topLevel = getTopLevelParent();
		topLevel.hoveringTextInfo.lines = lines;
		topLevel.hoveringTextInfo.x = getMouseX();
		topLevel.hoveringTextInfo.y = getMouseY();
		topLevel.hoveringTextInfo.font = font;
		topLevel.hoveringTextInfo.shouldDraw = true;
	}
	
	/** Wrapper for data used to draw hovering text at the end of rendering
	 * current frame. It is used by child components that wish to draw hovering
	 * text unobscured by their neighboring components. */
	private final HoveringTextInfo hoveringTextInfo = new HoveringTextInfo();
	private static class HoveringTextInfo {
		List<String> lines;
		int x, y;
		FontRenderer font;
		/** Whether to draw this hovering text during rendering current frame.
		 * This flag is reset to false after rendering finishes. */
		boolean shouldDraw = false;
	}
	
	/** Remove itself from its parent component (if any), notifying it. */
	public void close() {
		if (parent != null) {
			parent.removeChild(this); // This sets parent to null
		} else {
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}
	
	/** Called when a child removes itself from this component. */
	protected void onChildClosed(GuiComponent child) {}
	
	/** Draw a text string centered horizontally, using this GUI's FontRenderer. */
	protected void drawCenteredString(String text, int y, int color, boolean dropShadow) {
		int length = fontRenderer.getStringWidth(text);
		fontRenderer.drawString(text, (this.width - length)/2, y, color, dropShadow);
	}
	
	protected int getMouseX() {
		return Mouse.getX() * width / mc.displayWidth;
	}
	protected int getMouseY() {
		return height - Mouse.getY() * height / mc.displayHeight - 1;
	}
}
