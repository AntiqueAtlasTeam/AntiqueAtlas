package hunternif.mc.impl.atlas.mixin;

/**
 * This file was copied from https://github.com/Hephaestus-Dev/TinyTweaks/blob/master/src/main/java/dev/hephaestus/tweaks/mixin/VolatileMixinPlugin.java
 *
 * Copyright: https://github.com/Hephaestus-Dev
 * License: MIT
 */

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class VolatileMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment() {
        throw new AssertionError("Not implemented");
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("mixin.dev") && !isDevelopmentEnvironment())
            return false;
        else return !mixinClassName.contains("mixin.prod") || !isDevelopmentEnvironment();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
