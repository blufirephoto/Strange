package svenhjol.strange.base;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import svenhjol.meson.MesonInstance;
import svenhjol.meson.handler.RegistryHandler;
import svenhjol.strange.Strange;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StrangeSounds {
    public static List<SoundEvent> soundsToRegister = new ArrayList<>();

    // action/event sounds
    public static final SoundEvent QUEST_ACTION_COMPLETE = createSound("quest_action_complete");
    public static final SoundEvent QUEST_ACTION_COUNT = createSound("quest_action_count");
    public static final SoundEvent RUNESTONE_TRAVEL = createSound("runestone_travel");
    public static final SoundEvent SCREENSHOT = createSound("screenshot");

    public static SoundEvent createSound(String name) {
        ResourceLocation res = new ResourceLocation(Strange.MOD_ID, name);
        SoundEvent sound = new SoundEvent(res).setRegistryName(res);
        soundsToRegister.add(sound);
        return sound;
    }

    public static void init(MesonInstance instance) {
        soundsToRegister.forEach(RegistryHandler::registerSound);
        instance.log.debug("Registered sounds");
    }
}
