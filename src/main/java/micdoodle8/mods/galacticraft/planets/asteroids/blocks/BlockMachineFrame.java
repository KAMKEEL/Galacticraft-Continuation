package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;
import java.util.Random;

public class BlockMachineFrame extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon[] blockIcons;

    public BlockMachineFrame(String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0F;
        this.setBlockName(assetName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcons = new IIcon[1];
        this.blockIcons[0] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "machineframe");
        this.blockIcon = this.blockIcons[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.blockIcons.length) {
            return this.blockIcon;
        }

        return this.blockIcons[meta];
    }

    @Override
    public Item getItemDropped(int meta, Random random, int par3) {
        return super.getItemDropped(meta, random, par3);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        int var4;

        for (var4 = 0; var4 < this.blockIcons.length; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }
}
