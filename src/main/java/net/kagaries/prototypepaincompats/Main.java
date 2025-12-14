package net.kagaries.prototypepaincompats;

import com.mojang.logging.LogUtils;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.kagaries.prototypepaincompats.events.CreateEvents;
import net.kagaries.prototypepaincompats.events.SirinHeadEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {
    public static boolean SirinHeadLoaded = ModList.get().isLoaded("siren_head"); //sirin
    public static boolean CreateLoaded = ModList.get().isLoaded("create");
    public static CustomPlayerHealthData healthData = new CustomPlayerHealthData();

    // Define mod id in a common place for everything to reference
    public static final String MODID = "prototypepaincompats";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;

        if (SirinHeadLoaded) {
            LOGGER.info("Enabling compatibility for Siren Head: The Arrival");
            eventBus.register(SirinHeadEvents.class);
        }

        if (CreateLoaded) {
            LOGGER.info("Enabling compatibility for Create");
            eventBus.register(CreateEvents.class);
        }

        //TODO: Add configs
    }
}
