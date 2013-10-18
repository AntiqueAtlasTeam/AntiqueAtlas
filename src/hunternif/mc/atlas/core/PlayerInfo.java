package hunternif.mc.atlas.core;

import hunternif.mc.atlas.gui.MapTileStitcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerInfo implements IExtendedEntityProperties {
	private EntityPlayer player;
	
	public Atlas atlas;
	
	public PlayerInfo(EntityPlayer player) {
		this.player = player;
		atlas = new Atlas(this, ChunkBiomeAnalyzer.instance, MapTileStitcher.instance);
	}
	void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		atlas.saveToNBT(compound);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		atlas.loadFromNBT(compound);
	}

	@Override
	public void init(Entity entity, World world) {}
	
	public void copyFrom(PlayerInfo info) {
		this.atlas = info.atlas;
	}
}
