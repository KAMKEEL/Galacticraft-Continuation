package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack extends EntityAIBase {

    private final EntityLiving entityHost;

    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;

    private int rangedAttackTime;
    private final double entityMoveSpeed;
    private final int field_96561_g;

    private final int maxRangedAttackTime;
    private final float field_96562_i;
    private final float field_82642_h;

    public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, double par2, int par4, float par5) {
        this(par1IRangedAttackMob, par2, par4, par4, par5);
    }

    public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, double par2, int par4, int par5, float par6) {
        this.rangedAttackTime = -1;

        if (!(par1IRangedAttackMob instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackEntityHost = par1IRangedAttackMob;
        this.entityHost = (EntityLiving) par1IRangedAttackMob;
        this.entityMoveSpeed = par2;
        this.field_96561_g = par4;
        this.maxRangedAttackTime = par5;
        this.field_96562_i = par6;
        this.field_82642_h = par6 * par6;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        final EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        }
        this.attackTarget = entitylivingbase;
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return this.shouldExecute() || !this.entityHost.getNavigator()
            .noPath();
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.attackTarget = null;
        this.rangedAttackTime = -1;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        final double d0 = this.entityHost
            .getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        final boolean flag = this.entityHost.getEntitySenses()
            .canSee(this.attackTarget);

        this.entityHost.getNavigator()
            .tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);

        this.entityHost.getLookHelper()
            .setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        float f;

        if (--this.rangedAttackTime == 0) {
            if (d0 > this.field_82642_h || !flag) {
                return;
            }

            f = MathHelper.sqrt_double(d0) / this.field_96562_i;
            float f1 = f;

            if (f < 0.1F) {
                f1 = 0.1F;
            }

            if (f1 > 1.0F) {
                f1 = 1.0F;
            }

            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f1);
            this.rangedAttackTime = MathHelper
                .floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
        } else if (this.rangedAttackTime < 0) {
            f = MathHelper.sqrt_double(d0) / this.field_96562_i;
            this.rangedAttackTime = MathHelper
                .floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
        }
    }
}
