package hunternif.mc.impl.atlas.structure;

import net.minecraft.util.Identifier;

public class StructurePieceTileXZ extends StructurePieceTile {
    private final Identifier tileZ;

    public StructurePieceTileXZ(Identifier tileX, Identifier tileZ, int priority) {
        super(tileX, priority);
        this.tileZ = tileZ;
    }

    @Override
    public Identifier getTileZ() {
        return tileZ;
    }
}
