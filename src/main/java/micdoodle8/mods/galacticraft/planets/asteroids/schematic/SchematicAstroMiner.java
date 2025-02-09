package micdoodle8.mods.galacticraft.planets.asteroids.schematic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets;
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiSchematicAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerSchematicAstroMiner;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class SchematicAstroMiner implements ISchematicPage {

    @Override
    public int getPageID() {
        return ConfigManagerAsteroids.idSchematicRocketT3 + 1;
    }

    @Override
    public int getGuiID() {
        return GuiIdsPlanets.NASA_WORKBENCH_ASTRO_MINER + Constants.MOD_ID_PLANETS.hashCode();
    }

    @Override
    public ItemStack getRequiredItem() {
        return new ItemStack(MarsItems.schematic, 1, 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getResultScreen(EntityPlayer player, int x, int y, int z) {
        return new GuiSchematicAstroMiner(player.inventory, x, y, z);
    }

    @Override
    public Container getResultContainer(EntityPlayer player, int x, int y, int z) {
        return new ContainerSchematicAstroMiner(player.inventory, x, y, z);
    }

    @Override
    public int compareTo(ISchematicPage o) {
        if (this.getPageID() > o.getPageID()) {
            return 1;
        }
        return -1;
    }
}
