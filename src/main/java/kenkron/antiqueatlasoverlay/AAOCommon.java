package kenkron.antiqueatlasoverlay;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class AAOCommon {

    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("AAO:Server Side preInit, I do nothing");
    }

    public void init(FMLInitializationEvent event) {
        System.out.println("AAO:Server Side init, I do nothing");
    }
}
