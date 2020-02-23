package svenhjol.strange.ambience.client;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import svenhjol.strange.ambience.client.ambience.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AmbienceClient
{
    public Handler handler;

    public AmbienceClient() { }

    public static class Handler
    {
        private PlayerEntity player;
        private List<BaseAmbientSounds> ambientSounds = new ArrayList<>();

        public Handler(PlayerEntity player, SoundHandler soundHandler)
        {
            this.player = player;

            ambientSounds.addAll(Arrays.asList(
                new BeachAmbientSounds(player, soundHandler),
                new CaveAmbientSounds(player, soundHandler),
                new DeepAmbientSounds(player, soundHandler),
                new HighAmbientSounds(player, soundHandler),
                new MineshaftAmbientSounds(player, soundHandler),
                new OceanAmbientSounds(player, soundHandler),
                new NetherAmbientSounds(player, soundHandler),
                new VillageAmbientSounds(player, soundHandler),

                new CaveAmbientSounds.CrystalCaves(player, soundHandler),
                new DesertAmbientSounds.Day(player, soundHandler),
                new DesertAmbientSounds.Night(player, soundHandler),
                new ExtremeHillsAmbientSounds.Day(player, soundHandler),
                new ExtremeHillsAmbientSounds.Night(player, soundHandler),
                new ForestAmbientSounds.Day(player, soundHandler),
                new ForestAmbientSounds.Night(player, soundHandler),
                new IcyAmbientSounds.Day(player, soundHandler),
                new IcyAmbientSounds.Night(player, soundHandler),
                new JungleAmbientSounds.Day(player, soundHandler),
                new JungleAmbientSounds.Night(player, soundHandler),
                new PlainsAmbientSounds.Day(player, soundHandler),
                new PlainsAmbientSounds.Night(player, soundHandler),
                new SavannaAmbientSounds.Day(player, soundHandler),
                new SavannaAmbientSounds.Night(player, soundHandler),
                new SwampAmbientSounds.Day(player, soundHandler),
                new SwampAmbientSounds.Night(player, soundHandler),
                new TaigaAmbientSounds.Day(player, soundHandler),
                new TaigaAmbientSounds.Night(player, soundHandler)
            ));
        }

        public void tick()
        {
            if (!player.isAlive() || player.world == null) return;
            ambientSounds.forEach(BaseAmbientSounds::tick);
        }
    }
}
