package svenhjol.strange.traveljournal.message;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import svenhjol.meson.Meson;
import svenhjol.meson.iface.IMesonMessage;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

public class ClientTravelJournalEntries implements IMesonMessage
{
    private String serialized = "";
    private CompoundNBT entries;

    public ClientTravelJournalEntries(CompoundNBT entries)
    {
        this.entries = entries;

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(entries, out);
            serialized = DatatypeConverter.printBase64Binary(out.toByteArray());
        } catch (Exception e) {
            Meson.warn("Failed to compress entries");
        }
    }

    public static void encode(ClientTravelJournalEntries msg, PacketBuffer buf)
    {
        buf.writeString(msg.serialized);
    }

    public static ClientTravelJournalEntries decode(PacketBuffer buf)
    {
        CompoundNBT entries = new CompoundNBT();

        try {
            final byte[] byteData = DatatypeConverter.parseBase64Binary(buf.readString());
            entries = CompressedStreamTools.readCompressed(new ByteArrayInputStream(byteData));
        } catch (Exception e) {
            Meson.warn("Failed to uncompress entries");
        }

        return new ClientTravelJournalEntries(entries);
    }

    public static class Handler
    {
        public static CompoundNBT entries = new CompoundNBT();
        public static boolean updated = false;

        public static void handle(final ClientTravelJournalEntries msg, Supplier <NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                entries = msg.entries;
                updated = true;
            });
            ctx.get().setPacketHandled(true);
        }

        public static void clearUpdates()
        {
            entries = new CompoundNBT();
            updated = false;
        }
    }
}