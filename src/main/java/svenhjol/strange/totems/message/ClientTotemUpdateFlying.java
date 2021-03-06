package svenhjol.strange.totems.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import svenhjol.meson.iface.IMesonMessage;
import svenhjol.strange.totems.module.TotemOfFlying;

import java.util.function.Supplier;

public class ClientTotemUpdateFlying implements IMesonMessage {
    public static final int DISABLE = 0;
    public static final int ENABLE = 1;

    private final int status;

    public ClientTotemUpdateFlying(int status) {
        this.status = status;
    }

    public static void encode(ClientTotemUpdateFlying msg, PacketBuffer buf) {
        buf.writeInt(msg.status);
    }

    public static ClientTotemUpdateFlying decode(PacketBuffer buf) {
        return new ClientTotemUpdateFlying(buf.readInt());
    }

    public static class Handler {
        public static void handle(final ClientTotemUpdateFlying msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (TotemOfFlying.client == null)
                    return;

                if (msg.status == ENABLE) {
                    TotemOfFlying.client.enableFlight();
                } else if (msg.status == DISABLE) {
                    TotemOfFlying.client.disableFlight();
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
