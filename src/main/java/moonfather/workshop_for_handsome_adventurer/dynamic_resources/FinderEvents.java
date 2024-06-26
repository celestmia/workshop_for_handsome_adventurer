package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.List;
import java.util.Optional;

public class FinderEvents
{
    public static void addClientPack(AddPackFindersEvent event)
    {
        if (DynamicAssetCommonConfig.masterLeverOn() && event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            event.addRepositorySource((packConsumer) ->
            {
                Component title = Component.literal("Workshop - client assets");
                PackLocationInfo locationInfo = new PackLocationInfo(Constants.MODID + "_client", title, PackSource.DEFAULT, Optional.empty());
                Pack.ResourcesSupplier resourceSupplier = new SimpleResourceSupplier(new OurClientPack(locationInfo));
                PackSelectionConfig packSelectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, true);
                Pack.Metadata metadata = new Pack.Metadata(title, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
                Pack pack = new Pack(locationInfo, resourceSupplier, metadata, packSelectionConfig);
                packConsumer.accept(pack);
            });
        }
    }

    public static void addServerPack(AddPackFindersEvent event)
    {
        if (DynamicAssetCommonConfig.masterLeverOn() && event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource((packConsumer) ->
                    {
                        Component title = Component.literal("Workshop - server-side generated assets");
                        PackLocationInfo locationInfo = new PackLocationInfo(Constants.MODID + "_server", title, PackSource.DEFAULT, Optional.empty());
                        Pack.ResourcesSupplier resourceSupplier = new SimpleResourceSupplier(new OurServerPack(locationInfo));
                        PackSelectionConfig packSelectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, true);
                        Pack.Metadata metadata = new Pack.Metadata(title, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
                        Pack pack = new Pack(locationInfo, resourceSupplier, metadata, packSelectionConfig);
                        packConsumer.accept(pack);

                        Component title2 = Component.literal("Workshop - server-side generated tags");
                        PackLocationInfo locationInfo2 = new PackLocationInfo(Constants.MODID + "_server2", title2, PackSource.DEFAULT, Optional.empty());
                        Pack.ResourcesSupplier resourceSupplier2 = new SimpleResourceSupplier(new OurServerPack2(locationInfo2));
                        PackSelectionConfig packSelectionConfig2 = new PackSelectionConfig(true, Pack.Position.BOTTOM, true);
                        Pack.Metadata metadata2 = new Pack.Metadata(title2, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
                        Pack pack2 = new Pack(locationInfo2, resourceSupplier2, metadata2, packSelectionConfig2);
                        packConsumer.accept(pack2);
                    }
            );
        }
    }
}
