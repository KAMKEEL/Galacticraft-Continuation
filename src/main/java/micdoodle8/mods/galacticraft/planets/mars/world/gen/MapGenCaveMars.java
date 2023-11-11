package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.init.*;
import net.minecraft.world.*;

public class MapGenCaveMars extends MapGenBaseMeta
{
    public static final int BREAK_THROUGH_CHANCE = 25;
    
    protected void generateLargeCaveNode(final long par1, final int par3, final int par4, final Block[] blockIdArray, final byte[] metaArray, final double par6, final double par8, final double par10) {
        this.generateCaveNode(par1, par3, par4, blockIdArray, metaArray, par6, par8, par10, 1.0f + this.rand.nextFloat() * 6.0f, 0.0f, 0.0f, -1, -1, 0.5);
    }
    
    protected void generateCaveNode(final long par1, final int par3, final int par4, final Block[] blockIdArray, final byte[] metaArray, double par6, double par8, double par10, final float par12, float par13, float par14, int par15, int par16, final double par17) {
        final double d4 = par3 * 16 + 8;
        final double d5 = par4 * 16 + 8;
        float f3 = 0.0f;
        float f4 = 0.0f;
        final Random random = new Random(par1);
        if (par16 <= 0) {
            final int j1 = this.range * 16 - 16;
            par16 = j1 - random.nextInt(j1 / 4);
        }
        boolean flag = false;
        if (par15 == -1) {
            par15 = par16 / 2;
            flag = true;
        }
        final int k1 = random.nextInt(par16 / 2) + par16 / 4;
        final boolean flag2 = random.nextInt(6) == 0;
        while (par15 < par16) {
            final double d6 = 1.5 + MathHelper.sin(par15 * 3.1415927f / par16) * par12 * 1.0f;
            final double d7 = d6 * par17;
            final float f5 = MathHelper.cos(par14);
            final float f6 = MathHelper.sin(par14);
            par6 += MathHelper.cos(par13) * f5;
            par8 += f6;
            par10 += MathHelper.sin(par13) * f5;
            if (flag2) {
                par14 *= 0.92f;
            }
            else {
                par14 *= 0.7f;
            }
            par14 += f4 * 0.1f;
            par13 += f3 * 0.1f;
            f4 *= 0.9f;
            f3 *= 0.75f;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (!flag && par15 == k1 && par12 > 1.0f && par16 > 0) {
                this.generateCaveNode(random.nextLong(), par3, par4, blockIdArray, metaArray, par6, par8, par10, random.nextFloat() * 0.5f + 0.5f, par13 - 1.5707964f, par14 / 3.0f, par15, par16, 1.0);
                this.generateCaveNode(random.nextLong(), par3, par4, blockIdArray, metaArray, par6, par8, par10, random.nextFloat() * 0.5f + 0.5f, par13 + 1.5707964f, par14 / 3.0f, par15, par16, 1.0);
                return;
            }
            if (flag || random.nextInt(4) != 0) {
                final double d8 = par6 - d4;
                final double d9 = par10 - d5;
                final double d10 = par16 - par15;
                final double d11 = par12 + 2.0f + 16.0f;
                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return;
                }
                if (par6 >= d4 - 16.0 - d6 * 2.0 && par10 >= d5 - 16.0 - d6 * 2.0 && par6 <= d4 + 16.0 + d6 * 2.0 && par10 <= d5 + 16.0 + d6 * 2.0) {
                    int l1 = MathHelper.floor_double(par6 - d6) - par3 * 16 - 1;
                    int i2 = MathHelper.floor_double(par6 + d6) - par3 * 16 + 1;
                    int j2 = MathHelper.floor_double(par8 - d7) - 1;
                    int k2 = MathHelper.floor_double(par8 + d7) + 1;
                    int l2 = MathHelper.floor_double(par10 - d6) - par4 * 16 - 1;
                    int i3 = MathHelper.floor_double(par10 + d6) - par4 * 16 + 1;
                    if (l1 < 0) {
                        l1 = 0;
                    }
                    if (i2 > 16) {
                        i2 = 16;
                    }
                    if (j2 < 1) {
                        j2 = 1;
                    }
                    if (k2 > 120) {
                        k2 = 120;
                    }
                    if (l2 < 0) {
                        l2 = 0;
                    }
                    if (i3 > 16) {
                        i3 = 16;
                    }
                    final boolean flag3 = false;
                    for (int j3 = l1; j3 < i2; ++j3) {
                        for (int l3 = l2; l3 < i3; ++l3) {
                            for (int i4 = k2 + 1; i4 >= j2 - 1; --i4) {
                                if (i4 >= 0 && i4 < 128 && i4 != j2 - 1 && j3 != l1 && j3 != i2 - 1 && l3 != l2 && l3 != i3 - 1) {
                                    i4 = j2;
                                }
                            }
                        }
                    }
                    for (int localY = j2; localY < k2; ++localY) {
                        final double yfactor = (localY + 0.5 - par8) / d7;
                        final double yfactorSq = yfactor * yfactor;
                        for (int localX = l1; localX < i2; ++localX) {
                            final double zfactor = (localX + par3 * 16 + 0.5 - par6) / d6;
                            final double zfactorSq = zfactor * zfactor;
                            for (int localZ = l2; localZ < i3; ++localZ) {
                                final double xfactor = (localZ + par4 * 16 + 0.5 - par10) / d6;
                                final double xfactorSq = xfactor * xfactor;
                                if (xfactorSq + zfactorSq < 1.0) {
                                    final int coords = (localX * 16 + localZ) * 256 + localY;
                                    if (yfactor > -0.7 && xfactorSq + yfactorSq + zfactorSq < 1.0 && blockIdArray[coords] == MarsBlocks.marsBlock) {
                                        if (metaArray[coords] == 6 || metaArray[coords] == 9) {
                                            blockIdArray[coords] = Blocks.air;
                                        }
                                        else if (metaArray[coords] == 5 && random.nextInt(25) == 0) {
                                            blockIdArray[coords] = Blocks.air;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            }
            ++par15;
        }
    }
    
    protected void recursiveGenerate(final World par1World, final int par2, final int par3, final int par4, final int par5, final Block[] blockIdArray, final byte[] metaArray) {
        int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
        if (this.rand.nextInt(15) != 0) {
            var7 = 0;
        }
        for (int var8 = 0; var8 < var7; ++var8) {
            final double var9 = par2 * 16 + this.rand.nextInt(16);
            final double var10 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            final double var11 = par3 * 16 + this.rand.nextInt(16);
            int var12 = 1;
            if (this.rand.nextInt(4) == 0) {
                this.generateLargeCaveNode(this.rand.nextLong(), par4, par5, blockIdArray, metaArray, var9, var10, var11);
                var12 += this.rand.nextInt(4);
            }
            for (int var13 = 0; var13 < var12; ++var13) {
                final float var14 = this.rand.nextFloat() * 3.1415927f * 2.0f;
                final float var15 = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f;
                float var16 = this.rand.nextFloat() * 2.0f + this.rand.nextFloat();
                if (this.rand.nextInt(10) == 0) {
                    var16 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0f + 1.0f;
                }
                this.generateCaveNode(this.rand.nextLong(), par4, par5, blockIdArray, metaArray, var9, var10, var11, var16, var14, var15, 0, 0, 1.0);
            }
        }
    }
}
