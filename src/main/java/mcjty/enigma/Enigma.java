package mcjty.enigma;


import mcjty.enigma.code.Scope;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.proxy.CommonProxy;
import mcjty.enigma.varia.StringRegister;
import mcjty.lib.compat.CompatCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = Enigma.MODID, name = Enigma.MODNAME,
        dependencies =
                        "required-after:compatlayer@[" + Enigma.COMPATLAYER_VER + ",);" +
                        "after:Forge@[" + Enigma.MIN_FORGE10_VER + ",);" +
                        "after:forge@[" + Enigma.MIN_FORGE11_VER + ",)",
        version = Enigma.MODVERSION,
        acceptedMinecraftVersions = "[1.10,1.12)")
public class Enigma {

    public static final String MODID = "enigma";
    public static final String MODNAME = "Enigma";
    public static final String MODVERSION = "0.0.1";

    public static final String MIN_FORGE10_VER = "12.18.2.2116";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String COMPATLAYER_VER = "0.2.5";

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    @SidedProxy(clientSide = "mcjty.enigma.proxy.ClientProxy", serverSide = "mcjty.enigma.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static Enigma instance;
    public static Logger logger;

    public static CreativeTabs tabEnigma = new CompatCreativeTabs("Enigma") {
        @Override
        protected Item getItem() {
            return Items.ENCHANTED_BOOK;
        }
    };

    public static Scope root;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        ProgressHolder.clearInstance();
    }


    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandDump());
//        event.registerServerCommand(new CommandGen());
//        event.registerServerCommand(new CommandRebuild());
//        event.registerServerCommand(new CommandCheck());
    }

    public String getModId() {
        return MODID;
    }
}
