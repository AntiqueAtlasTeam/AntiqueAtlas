package hunternif.mc.impl.atlas.util;

import java.awt.*;
import java.awt.image.*;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

class RenderedImageScanned implements RenderedImage {

    private final int width;
    private final int height;
    private int bufY;
    private final BufferedImage scanBuffer;
    private final Consumer<Graphics2D> generator;
    private final IntConsumer rowListener;

    public RenderedImageScanned(int width, int height, BufferedImage scanBuffer, Consumer<Graphics2D> generator, IntConsumer rowListener) {
        this.width = width;
        this.height = height;
        this.scanBuffer = scanBuffer;
        this.generator = generator;
        this.rowListener = rowListener;

        bufY = 0;
        redrawToBuffer();
    }

    private void redrawToBuffer() {
        Log.info("Redrawing at %d", bufY);

        Graphics2D graphics = scanBuffer.createGraphics();
        graphics.setBackground(new Color(0, 0, 0, 0));
        graphics.clearRect(0, 0, scanBuffer.getWidth(), scanBuffer.getHeight());

        graphics.translate(0, -bufY);
        generator.accept(graphics);
    }

    @Override
    public Raster getData(Rectangle rect) {
        if (rect.height > scanBuffer.getHeight())
            return null;

        if (rect.y >= bufY + scanBuffer.getHeight() || rect.y + rect.height <= bufY) {
            bufY = rect.y;
            redrawToBuffer();
        }
        rowListener.accept(rect.y - bufY);
        Raster r = scanBuffer.getData(new Rectangle(rect.x, rect.y - bufY, rect.width, rect.height));

        r = r.createTranslatedChild(r.getMinX(), r.getMinY() + bufY);
        return r;
    }

    @Override
    public Vector<RenderedImage> getSources() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return scanBuffer.getProperty(name);
    }

    @Override
    public String[] getPropertyNames() {
        return scanBuffer.getPropertyNames();
    }

    @Override
    public ColorModel getColorModel() {
        return scanBuffer.getColorModel();
    }

    @Override
    public SampleModel getSampleModel() {
        return scanBuffer.getSampleModel();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getMinX() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getNumXTiles() {
        return 1;
    }

    @Override
    public int getNumYTiles() {
        return (int) Math.ceil((float) this.height / (float) scanBuffer.getHeight());
    }

    @Override
    public int getMinTileX() {
        return 0;
    }

    @Override
    public int getMinTileY() {
        return 0;
    }

    @Override
    public int getTileWidth() {
        return width;
    }

    @Override
    public int getTileHeight() {
        return height;
    }

    @Override
    public int getTileGridXOffset() {
        return 0;
    }

    @Override
    public int getTileGridYOffset() {
        return 0;
    }

    @Override
    public Raster getTile(int tileX, int tileY) {
        return null;
    }

    @Override
    public Raster getData() {
        return null;
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        return null;
    }
}
