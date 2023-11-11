package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.tileentity.*;
import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.miccore.*;
import java.lang.annotation.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;
import net.minecraft.entity.player.*;

public abstract class TileEntityAdvanced extends TileEntity implements IPacketReceiver
{
    public int ticks;
    private LinkedHashSet<Field> fieldCacheClient;
    private LinkedHashSet<Field> fieldCacheServer;
    private Map<Field, Object> lastSentData;
    private boolean networkDataChanged;
    
    public TileEntityAdvanced() {
        this.ticks = 0;
        this.lastSentData = new HashMap<Field, Object>();
        this.networkDataChanged = false;
    }
    
    public void updateEntity() {
        if (this.ticks == 0) {
            this.initiate();
            if (this.isNetworkedTile()) {
                if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
                    this.initFieldCache();
                }
                if (this.worldObj != null && this.worldObj.isRemote && this.fieldCacheClient.size() > 0) {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketDynamic((TileEntity)this));
                }
            }
        }
        ++this.ticks;
        if (this.isNetworkedTile() && this.ticks % this.getPacketCooldown() == 0) {
            if (this.worldObj.isRemote && this.fieldCacheServer.size() > 0) {
                final PacketDynamic packet = new PacketDynamic((TileEntity)this);
                if (this.networkDataChanged) {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)packet);
                }
            }
            else if (!this.worldObj.isRemote && this.fieldCacheClient.size() > 0) {
                final PacketDynamic packet = new PacketDynamic((TileEntity)this);
                if (this.networkDataChanged) {
                    GalacticraftCore.packetPipeline.sendToAllAround((IPacket)packet, new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, (double)this.xCoord, (double)this.yCoord, (double)this.zCoord, this.getPacketRange()));
                }
            }
        }
    }
    
    private void initFieldCache() {
        try {
            this.fieldCacheClient = new LinkedHashSet<Field>();
            this.fieldCacheServer = new LinkedHashSet<Field>();
            for (final Field field : this.getClass().getFields()) {
                if (field.isAnnotationPresent(Annotations.NetworkedField.class)) {
                    final Annotations.NetworkedField f = field.getAnnotation(Annotations.NetworkedField.class);
                    if (f.targetSide() == Side.CLIENT) {
                        this.fieldCacheClient.add(field);
                    }
                    else {
                        this.fieldCacheServer.add(field);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public abstract double getPacketRange();
    
    public abstract int getPacketCooldown();
    
    public abstract boolean isNetworkedTile();
    
    public void addExtraNetworkedData(final List<Object> networkedList) {
    }
    
    public void readExtraNetworkedData(final ByteBuf dataStream) {
    }
    
    public void initiate() {
    }
    
    public void getNetworkedData(final ArrayList<Object> sendData) {
        Set<Field> fieldList = null;
        boolean changed = false;
        if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
            this.initFieldCache();
        }
        if (this.worldObj.isRemote) {
            fieldList = this.fieldCacheServer;
        }
        else {
            fieldList = this.fieldCacheClient;
        }
        for (final Field f : fieldList) {
            boolean fieldChanged = false;
            try {
                final Object data = f.get(this);
                final Object lastData = this.lastSentData.get(f);
                if (!NetworkUtil.fuzzyEquals(lastData, data)) {
                    fieldChanged = true;
                }
                sendData.add(data);
                if (fieldChanged) {
                    this.lastSentData.put(f, NetworkUtil.cloneNetworkedObject(data));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            changed |= fieldChanged;
        }
        if (changed) {
            this.addExtraNetworkedData(sendData);
        }
        else {
            final ArrayList<Object> prevSendData = new ArrayList<Object>(sendData);
            this.addExtraNetworkedData(sendData);
            if (!prevSendData.equals(sendData)) {
                changed = true;
            }
        }
        this.networkDataChanged = changed;
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
            this.initFieldCache();
        }
        if (this.worldObj.isRemote && this.fieldCacheClient.size() == 0) {
            return;
        }
        if (!this.worldObj.isRemote && this.fieldCacheServer.size() == 0) {
            return;
        }
        Set<Field> fieldSet = null;
        if (this.worldObj.isRemote) {
            fieldSet = this.fieldCacheClient;
        }
        else {
            fieldSet = this.fieldCacheServer;
        }
        for (final Field field : fieldSet) {
            try {
                final Object obj = NetworkUtil.getFieldValueFromStream(field, buffer, this.worldObj);
                field.set(this, obj);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.readExtraNetworkedData(buffer);
    }
    
    public void handlePacketData(final Side side, final EntityPlayer player) {
    }
}
