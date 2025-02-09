package micdoodle8.mods.galacticraft.planets.asteroids.entities.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;

public class AsteroidsPlayerHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogin();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogout();
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerRespawn();
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayerMP && GCPlayerStats.get((EntityPlayerMP) event.entity) == null) {
            GCPlayerStats.register((EntityPlayerMP) event.entity);
        }
    }

    private void onPlayerLogin() {}

    private void onPlayerLogout() {}

    private void onPlayerRespawn() {}

    public void onPlayerUpdate(EntityPlayerMP player) {
        if (!player.worldObj.isRemote && player.worldObj.provider instanceof WorldProviderAsteroids) {
            final int f = 50;

            if (player.worldObj.rand.nextInt(f) == 0 && player.posY < 260D) {
                final EntityPlayer closestPlayer = player.worldObj.getClosestPlayerToEntity(player, 100);

                if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                    double x, y, z;
                    double motX, motY, motZ;
                    final double r = player.worldObj.rand.nextInt(60) + 30D;
                    final double theta = Math.PI * 2.0 * player.worldObj.rand.nextDouble();
                    x = player.posX + Math.cos(theta) * r;
                    y = player.posY + player.worldObj.rand.nextInt(5);
                    z = player.posZ + Math.sin(theta) * r;
                    motX = (player.posX - x + (player.worldObj.rand.nextDouble() - 0.5) * 40) / 400.0F;
                    motY = (player.worldObj.rand.nextDouble() - 0.5) * 0.4;
                    motZ = (player.posZ - z + (player.worldObj.rand.nextDouble() - 0.5) * 40) / 400.0F;

                    final EntitySmallAsteroid smallAsteroid = new EntitySmallAsteroid(player.worldObj);
                    smallAsteroid.setPosition(x, y, z);
                    smallAsteroid.motionX = motX;
                    smallAsteroid.motionY = motY;
                    smallAsteroid.motionZ = motZ;
                    smallAsteroid.spinYaw = player.worldObj.rand.nextFloat() * 4;
                    smallAsteroid.spinPitch = player.worldObj.rand.nextFloat() * 2;

                    player.worldObj.spawnEntityInWorld(smallAsteroid);
                }
            }
        }
    }
}
