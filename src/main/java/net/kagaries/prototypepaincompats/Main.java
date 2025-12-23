package net.kagaries.prototypepaincompats;

import com.mojang.logging.LogUtils;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.kagaries.prototypepaincompats.custom.moodles.CustomMoodles;
import net.kagaries.prototypepaincompats.events.CreateEvents;
import net.kagaries.prototypepaincompats.events.CuffedEvents;
import net.kagaries.prototypepaincompats.events.SirinHeadEvents;
import net.kagaries.prototypepaincompats.network.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {
    public static boolean SirinHeadLoaded; //sirin
    public static boolean CreateLoaded;
    public static boolean CuffedLoaded;

    // Define mod id in a common place for everything to reference
    public static final String MODID = "prototypepaincompats";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;

        ModList modList = ModList.get();

        CuffedLoaded = modList.isLoaded("cuffed");
        SirinHeadLoaded = modList.isLoaded("sirenhead");
        CreateLoaded = modList.isLoaded("create");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        if (SirinHeadLoaded) {
            LOGGER.info("Enabling compatibility for Siren Head: The Arrival");
            eventBus.register(SirinHeadEvents.class);
        }

        if (CreateLoaded) {
            LOGGER.info("Enabling compatibility for Create");
            eventBus.register(CreateEvents.class);
        }

        if (CuffedLoaded) {
            LOGGER.info("Enabling compatibility for Cuffed");
            eventBus.register(CuffedEvents.class);
        }

        CustomMoodles.init();

        //TODO: Add configs
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
