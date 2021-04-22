package hunternif.mc.impl.atlas.forge;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AntiqueAtlasConfigBuilder {
	public static final ForgeConfigSpec.Builder client_builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec client_config;
	
	public static final ForgeConfigSpec.Builder common_builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec common_config;
	
	static {
		AntiqueAtlasConfig.init(client_builder, common_builder);
		client_config = client_builder.build();
		common_config = common_builder.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path) {
		final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
	}
}
