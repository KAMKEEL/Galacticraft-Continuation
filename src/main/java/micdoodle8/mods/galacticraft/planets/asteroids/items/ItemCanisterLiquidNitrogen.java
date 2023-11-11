package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.item.*;

public class ItemCanisterLiquidNitrogen extends ItemCanisterGeneric
{
    protected IIcon[] icons;
    
    public ItemCanisterLiquidNitrogen(final String assetName) {
        super(assetName);
        this.icons = new IIcon[7];
        this.setAllowedFluid("liquidnitrogen");
        this.setTextureName("galacticraftasteroids:" + assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() == 0) {
            return "item.emptyGasCanister";
        }
        if (itemStack.getItemDamage() == 1) {
            return "item.canister.liquidNitrogen.full";
        }
        return "item.canister.liquidNitrogen.partial";
    }
    
    public IIcon getIconFromDamage(final int par1) {
        final int damage = 6 * par1 / this.getMaxDamage();
        if (this.icons.length > damage) {
            return this.icons[this.icons.length - damage - 1];
        }
        return super.getIconFromDamage(damage);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() > 0) {
            par3List.add(GCCoreUtil.translate("item.canister.liquidNitrogen.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()));
        }
    }
    
    private Block canFreeze(final Block b, final int meta) {
        if (b == Blocks.water) {
            return Blocks.ice;
        }
        if (b == Blocks.lava) {
            return Blocks.obsidian;
        }
        return null;
    }
    
    public ItemStack onItemRightClick(final ItemStack itemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        final int damage = itemStack.getItemDamage() + 125;
        if (damage > itemStack.getMaxDamage()) {
            return itemStack;
        }
        final MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);
        if (movingobjectposition == null) {
            return itemStack;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final int x = movingobjectposition.blockX;
            final int y = movingobjectposition.blockY;
            final int z = movingobjectposition.blockZ;
            if (!par2World.canMineBlock(par3EntityPlayer, x, y, z)) {
                return itemStack;
            }
            if (!par3EntityPlayer.canPlayerEdit(x, y, z, movingobjectposition.sideHit, itemStack)) {
                return itemStack;
            }
            final Block b = par2World.getBlock(x, y, z);
            final int meta = par2World.getBlockMetadata(x, y, z);
            final Block result = this.canFreeze(b, meta);
            if (result != null) {
                this.setNewDamage(itemStack, damage);
                par2World.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1.0f, Item.itemRand.nextFloat() * 0.4f + 0.8f);
                par2World.setBlock(x, y, z, result, 0, 3);
                return itemStack;
            }
        }
        return itemStack;
    }
}
