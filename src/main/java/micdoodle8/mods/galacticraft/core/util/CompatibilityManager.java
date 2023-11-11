package micdoodle8.mods.galacticraft.core.util;

import java.lang.reflect.*;
import net.minecraft.block.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import cpw.mods.fml.common.registry.*;
import net.minecraft.entity.player.*;

public class CompatibilityManager
{
    private static boolean modIc2Loaded;
    private static boolean modBCraftEnergyLoaded;
    private static boolean modBCraftTransportLoaded;
    private static boolean modGTLoaded;
    private static boolean modTELoaded;
    private static boolean modAetherIILoaded;
    private static boolean modBasicComponentsLoaded;
    private static boolean modAppEngLoaded;
    private static boolean modPneumaticCraftLoaded;
    private static boolean modMatterOverdriveLoaded;
    private static Method androidPlayerGet;
    private static Method androidPlayerIsAndroid;
    public static Class<? extends BlockContainer> classBCBlockGenericPipe;
    public static Class<?> classGTOre;
    public static Method methodBCBlockPipe_createPipe;
    
    public static void checkForCompatibleMods() {
        if (Loader.isModLoaded("gregtech") || Loader.isModLoaded("GregTech_Addon") || Loader.isModLoaded("GregTech")) {
            CompatibilityManager.modGTLoaded = true;
            try {
                final Class<?> clazz = Class.forName("gregtech.common.blocks.GT_Block_Ores");
                if (clazz != null) {
                    CompatibilityManager.classGTOre = clazz;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Loader.isModLoaded("ThermalExpansion")) {
            CompatibilityManager.modTELoaded = true;
        }
        if (Loader.isModLoaded("IC2")) {
            CompatibilityManager.modIc2Loaded = true;
            try {
                final Class<?> clazz = Class.forName("ic2.core.block.wiring.TileEntityCable");
                if (clazz != null) {
                    BlockEnclosed.onBlockNeighbourChangeIC2 = clazz.getMethod("onNeighborBlockChange", (Class<?>[])new Class[0]);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Loader.isModLoaded("BuildCraft|Energy")) {
            CompatibilityManager.modBCraftEnergyLoaded = true;
        }
        if (Loader.isModLoaded("BuildCraft|Transport")) {
            CompatibilityManager.modBCraftTransportLoaded = true;
            try {
                BlockEnclosed.blockPipeBC = (BlockContainer)GameRegistry.findBlock("BuildCraft|Transport", "pipeBlock");
                CompatibilityManager.classBCBlockGenericPipe = BlockEnclosed.blockPipeBC.getClass();
                for (final Method m : CompatibilityManager.classBCBlockGenericPipe.getMethods()) {
                    if (m.getName().equals("createPipe") && m.getParameterTypes().length == 1) {
                        CompatibilityManager.methodBCBlockPipe_createPipe = m;
                        break;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            BlockEnclosed.initialiseBC();
            if (CompatibilityManager.methodBCBlockPipe_createPipe == null) {
                CompatibilityManager.modBCraftTransportLoaded = false;
            }
        }
        if (Loader.isModLoaded("AetherII")) {
            CompatibilityManager.modAetherIILoaded = true;
        }
        if (Loader.isModLoaded("BasicComponents")) {
            CompatibilityManager.modBasicComponentsLoaded = true;
        }
        if (Loader.isModLoaded("appliedenergistics2")) {
            CompatibilityManager.modAppEngLoaded = true;
        }
        if (Loader.isModLoaded("PneumaticCraft")) {
            CompatibilityManager.modPneumaticCraftLoaded = true;
        }
        if (Loader.isModLoaded("mo")) {
            try {
                final Class<?> androidPlayer = Class.forName("matteroverdrive.entity.player.AndroidPlayer");
                CompatibilityManager.androidPlayerGet = androidPlayer.getMethod("get", EntityPlayer.class);
                CompatibilityManager.androidPlayerIsAndroid = androidPlayer.getMethod("isAndroid", (Class<?>[])new Class[0]);
                CompatibilityManager.modMatterOverdriveLoaded = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean isIc2Loaded() {
        return CompatibilityManager.modIc2Loaded;
    }
    
    public static boolean isBCraftTransportLoaded() {
        return CompatibilityManager.modBCraftTransportLoaded;
    }
    
    public static boolean isBCraftEnergyLoaded() {
        return CompatibilityManager.modBCraftEnergyLoaded;
    }
    
    public static boolean isTELoaded() {
        return CompatibilityManager.modTELoaded;
    }
    
    public static boolean isGTLoaded() {
        return CompatibilityManager.modGTLoaded;
    }
    
    public static boolean isAIILoaded() {
        return CompatibilityManager.modAetherIILoaded;
    }
    
    public static boolean isBCLoaded() {
        return CompatibilityManager.modBasicComponentsLoaded;
    }
    
    public static boolean isAppEngLoaded() {
        return CompatibilityManager.modAppEngLoaded;
    }
    
    public static boolean isPneumaticCraftLoaded() {
        return CompatibilityManager.modPneumaticCraftLoaded;
    }
    
    public static boolean isAndroid(final EntityPlayer player) {
        if (CompatibilityManager.modMatterOverdriveLoaded) {
            try {
                final Object androidPlayer = CompatibilityManager.androidPlayerGet.invoke(null, player);
                if (androidPlayer != null) {
                    return (boolean)CompatibilityManager.androidPlayerIsAndroid.invoke(androidPlayer, new Object[0]);
                }
            }
            catch (Exception ex) {}
        }
        return false;
    }
    
    static {
        CompatibilityManager.classBCBlockGenericPipe = null;
        CompatibilityManager.classGTOre = null;
        CompatibilityManager.methodBCBlockPipe_createPipe = null;
    }
}
