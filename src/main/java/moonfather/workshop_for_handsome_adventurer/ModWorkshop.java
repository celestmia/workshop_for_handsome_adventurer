package moonfather.workshop_for_handsome_adventurer;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.MessagingInitialization;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.FinderEvents;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetClientConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import moonfather.workshop_for_handsome_adventurer.initialization.CommonSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.DynamicContentRegistration;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.CarryOnBlacklisting;
import moonfather.workshop_for_handsome_adventurer.integration.TOPRegistration;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;


@Mod(Constants.MODID)
public class ModWorkshop
{
    private static final Logger LOGGER = LogUtils.getLogger();


    // bug: toolrack shows axe that is in my hand until i put it into another slot  CAN'T REPRODUCE
    // todo: test carryon
    //////////////
    // creative tab
    // bamboo simple table model ?
    // try master off
    // instant config, do net sync
    // missing mappings.store all wood on registration, read on startup, loop through read types and see which arent in lister, report aliases on common setup or earlier
    public ModWorkshop(IEventBus modBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.STARTUP, DynamicAssetClientConfig.SPEC, Constants.MODID + "-special-client.toml");
        modContainer.registerConfig(ModConfig.Type.STARTUP, DynamicAssetCommonConfig.SPEC, Constants.MODID + "-special-server.toml");
        Registration.init(modBus);
        modBus.addListener(CommonSetup::init);
        modBus.addListener(CarryOnBlacklisting::enqueueIMC);
        modBus.addListener(CreativeTab::onCreativeTabPopulation);
        modBus.addListener(FinderEvents::addServerPack);
        modBus.addListener(MessagingInitialization::register);
        NeoForge.EVENT_BUS.addListener(PotionShelf::onRightClickBlock);
        modBus.addListener(EventPriority.LOWEST, DynamicContentRegistration::handleRegistryEvent);
    }
}
