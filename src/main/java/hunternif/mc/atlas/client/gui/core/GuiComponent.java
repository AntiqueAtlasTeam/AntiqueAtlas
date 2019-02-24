package hunternif.mc.atlas.client.gui.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenComponent;
import net.minecraftforge.fml.client.config.GuiUtils;


import none.XX_1_12_2_none_bip_XX;
import none.XX_1_12_2_none_blk_XX;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core visual component class, which facilitates hierarchy. You can add child
 * GuiComponent's to it, and they will be rendered, notified about mouse and
 * keyboard events, window resize and will be moved around together with the
 * parent component.
 */
@Environment(EnvType.CLIENT)
public class GuiComponent extends Screen {
	private GuiComponent parent = null;
	private final List<GuiComponent> children = new CopyOnWriteArrayList<>();

	/** The component's own size. */
    int properWidth;
    int properHeight;
	/** The component's total calculated size, including itself and its children. */
    int contentWidth;
    int contentHeight;
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
	protected final void setCentered() {
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
    int getRelativeX() {
		return parent == null ? guiX : (guiX - parent.guiX);
	}
	/** Y coordinate relative to the parent's top left corner. */
    int getRelativeY() {
		return parent == null ? guiY : (guiY - parent.guiY);
	}

	/** Set this component's own size. This shouldn't affect the size or position of the children. */
    protected void setSize(int width, int height) {
		this.properWidth = width;
		this.properHeight = height;
		this.contentWidth = width;
		this.contentHeight = height;
		invalidateSize();
	}

	/** Adds the child component to this GUI's content and initializes it.
	 * The child is placed at the top left corner of this component.
	 * @return the child added. */
    protected GuiComponent addChild(GuiComponent child) {
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
    protected GuiComponent addChildBehind(GuiComponent behind, GuiComponent child) {
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
			child.a(mc, width, height);
		}
		invalidateSize();
	}
	/** @return the child removed. */
    protected GuiComponent removeChild(GuiComponent child) {
		if (child != null && children.contains(child)) {
			child.parent = null;
			children.remove(child);
			invalidateSize();
			onChildClosed(child);
		}
		return child;
	}
	void removeAllChildren() {
		children.clear();
		invalidateSize();
	}
	/** Null if this is a top-level GUI. */
	public GuiComponent getParent() {
		return parent;
	}
	List<GuiComponent> getChildren() {
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
    protected void setInterceptKeyboard(boolean value) {
		this.interceptsKeyboard = value;
		this.allowUserInput = !interceptsMouse | !interceptsKeyboard;
	}

	@Override
	public void q() throws IOException {
		// Traverse children backwards, because the topmost child should be the
		// first to process input:
		ListIterator<GuiComponent> iter = children.listIterator(children.size());
		while(iter.hasPrevious()) {
			GuiComponent child = iter.previous();
			if (child.blocksScreen) {
				child.q();
				isMouseOver = false;
				return;
			}
		}
		if (interceptsMouse) {
			while (Mouse.next()) {
				this.k();
			}
		}
		if (interceptsKeyboard) {
			while (Keyboard.next()) {
				this.l();
			}
		}
	}

	/** Call this method from within {@link #handleMouseInput()} (or other
	 * mouse-processing methods) if the input has been handled by this GUI
	 * andshouldn't be handled by its parents.
	 * This won't prevent the sibling children of this GUI from handling input. */
    void mouseHasBeenHandled() {
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
	public void k() throws IOException {
		boolean handled = false;
		isMouseOver = false;
		// Traverse children backwards, because the topmost child should be the
		// first to process input:
		ListIterator<GuiComponent> iter = children.listIterator(children.size());
		while(iter.hasPrevious()) {
			GuiComponent child = iter.previous();
			child.k();
			if (child.hasHandledMouse) {
				child.hasHandledMouse = false;
				handled = true;
			}
		}
		if (!handled) {
			isMouseOver = isMouseInRegion(getGuiX(), getGuiY(), getWidth(), getHeight());
			super.k();
		}
	}

	/** Handle keyboard input for this GUI and its children. */
	@Override
	public void l() throws IOException {
		boolean handled = false;
		// Traverse children backwards, because the topmost child should be the
		// first to process input:
		ListIterator<GuiComponent> iter = children.listIterator(children.size());
		while(iter.hasPrevious()) {
			GuiComponent child = iter.previous();
			child.l();
			if (child.hasHandledKeyboard) {
				child.hasHandledKeyboard = false;
				handled = true;
			}
		}
		if (!handled) {
			if (Keyboard.getEventKeyState()) {
				this.a(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}
	}

	@Override
	protected void a(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 && mc.m != null)
        {
            this.client.openScreen(null);

            if (this.mc.m == null)
            {
                this.mc.o();
            }
        }
	}

	/** Render this GUI and its children. */
	@Override
	public void a(int mouseX, int mouseY, float partialTick) {
		super.a(mouseX, mouseY, partialTick);
		for (GuiComponent child : children) {
			if (!child.isClipped) {
				child.a(mouseX, mouseY, partialTick);
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
	public void m() {
		for (GuiComponent child : children) {
			child.m();
		}
		super.m();
	}

	/** Called each in-game tick for this GUI and its children. If this GUI's
	 * size has been invalidated, it will be validated on the next update. */
	@Override
	public void e() {
		for (GuiComponent child : children) {
			child.e();
		}

		super.e();
		if (sizeIsInvalid) {
			validateSize();
		}
	}

	@Override
	public void a(MinecraftClient mc, int width, int height) {
		super.a(mc, width, height);
		for (GuiComponent child : children) {
			child.a(mc, width, height);
		}
	}

	/** Width of the GUI or its contents. This method may be called often so it
	 * should be fast. */
    protected int getWidth() {
		return contentWidth;
	}
	/** Height of the GUI or its contents. This method may be called often so it
	 * should be fast. */
    protected int getHeight() {
		return contentHeight;
	}

	/** If set to true, the parent of this GUI will not render it. */
    void setClipped(boolean value) {
		this.isClipped = value;
	}

	/** Cause the size of the component to be recalculate on the next update
	 * tick. If this GUI has a parent, the parent's size will be invalidated too. */
    private void invalidateSize() {
		sizeIsInvalid = true;
		if (parent != null) {
			parent.invalidateSize();
		}
	}
	/** Recalculate the dimensions of the contents (children) of this GUI. */
    void validateSize() {
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
    boolean isMouseInRegion(int left, int top, int width, int height) {
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
    private void drawHoveringText2(List<String> lines, int x, int y, XX_1_12_2_none_bip_XX font) {
		boolean stencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
		if (stencilEnabled) GL11.glDisable(GL11.GL_STENCIL_TEST);

		GuiUtils.drawHoveringText(lines, x, y, width, height, -1, font);

		if (stencilEnabled) GL11.glEnable(GL11.GL_STENCIL_TEST);
	}

	/** Returns the top level parent of this component, or itself if it has no
	 * parent. Useful for correctly drawing hovering text. */
    private GuiComponent getTopLevelParent() {
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
	protected void drawTooltip(List<String> lines, TextRenderer font) {
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
		TextRenderer font;
		/** Whether to draw this hovering text during rendering current frame.
		 * This flag is reset to false after rendering finishes. */
		boolean shouldDraw = false;
	}

	/** Remove itself from its parent component (if any), notifying it. */
	public void close() {
		if (parent != null) {
			parent.removeChild(this); // This sets parent to null
		} else {
			MinecraftClient.getInstance().openScreen(null);
		}
	}

	/** Called when a child removes itself from this component. */
	protected void onChildClosed(GuiComponent child) {}

	/** Draw a text string centered horizontally, using this GUI's FontRenderer. */
	protected void drawCenteredString(String text, int y, int color, boolean dropShadow) {
		int length = fontRenderer.a(text);
		fontRenderer.a(text, (this.width - length)/2, y, color, dropShadow);
	}

	protected int getMouseX() {
		return Mouse.getX() * width / mc.d;
	}
	protected int getMouseY() {
		return height - Mouse.getY() * height / mc.e - 1;
	}
}
