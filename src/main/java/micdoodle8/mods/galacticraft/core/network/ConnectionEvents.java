package micdoodle8.mods.galacticraft.core.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager;
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.MapUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;

@SuppressWarnings("unchecked")
public class ConnectionEvents {

    private static boolean clientConnected = false;

    static {
        EnumConnectionState.field_150761_f.put(PacketSimple.class, EnumConnectionState.PLAY);
        EnumConnectionState.PLAY.field_150770_i.put(2515, PacketSimple.class);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        ChunkLoadingCallback.onPlayerLogout(event.player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        ChunkLoadingCallback.onPlayerLogin(event.player);

        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP thePlayer = (EntityPlayerMP) event.player;
            final GCPlayerStats stats = GCPlayerStats.get(thePlayer);
            SpaceStationWorldData.checkAllStations(thePlayer, stats);
            GalacticraftCore.packetPipeline.sendTo(
                new PacketSimple(
                    EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID,
                    new Object[] { WorldUtil.spaceStationDataToString(stats.spaceStationDimensionData) }),
                thePlayer);
            final SpaceRace raceForPlayer = SpaceRaceManager.getSpaceRaceFromPlayer(
                thePlayer.getGameProfile()
                    .getName());
            if (raceForPlayer != null) {
                SpaceRaceManager.sendSpaceRaceData(thePlayer, raceForPlayer);
            }
        }

        if (event.player.worldObj.provider instanceof WorldProviderSpaceStation
            && event.player instanceof EntityPlayerMP) {
            ((WorldProviderSpaceStation) event.player.worldObj.provider).getSpinManager()
                .sendPacketsToClient((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onConnectionReceived(ServerConnectionFromClientEvent event) {
        if (ConfigManagerCore.enableDebug) {
            final Integer[] idList = (Integer[]) WorldUtil.getPlanetList()
                .get(0);
            StringBuilder ids = new StringBuilder();
            for (final Integer element : idList) {
                ids.append(element.toString())
                    .append(" ");
            }
            GCLog.info("Galacticraft server sending dimension IDs to connecting client: " + ids.toString());
        }
        event.manager.scheduleOutboundPacket(ConnectionPacket.createDimPacket(WorldUtil.getPlanetListInts()));
        event.manager.scheduleOutboundPacket(ConnectionPacket.createSSPacket(WorldUtil.getSpaceStationListInts()));
        event.manager
            .scheduleOutboundPacket(ConnectionPacket.createConfigPacket(ConfigManagerCore.getServerConfigOverride()));
    }

    @SubscribeEvent
    public void onConnectionOpened(ClientConnectedToServerEvent event) {
        if (!event.isLocal) {
            ConnectionEvents.clientConnected = true;
        }
        MapUtil.resetClient();
    }

    @SubscribeEvent
    public void onConnectionClosed(ClientDisconnectionFromServerEvent event) {
        if (ConnectionEvents.clientConnected) {
            ConnectionEvents.clientConnected = false;
            WorldUtil.unregisterPlanets();
            WorldUtil.unregisterSpaceStations();
            ConfigManagerCore.restoreClientConfigOverrideable();
        }
    }
}
