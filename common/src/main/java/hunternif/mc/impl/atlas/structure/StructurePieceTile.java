package hunternif.mc.impl.atlas.structure;

import net.minecraft.util.Identifier;

public class StructurePieceTile {

    private final Identifier tile;
    private final int priority;

    public StructurePieceTile(Identifier tile, int priority) {
        this.tile = tile;
        this.priority = priority;
    }

    public Identifier getTile() {
        return tile;
    }

    public Identifier getTileX() {
        return tile;
    }

    public Identifier getTileZ() {
        return tile;
    }


    public int getPriority() {
        return priority;
    }
}

