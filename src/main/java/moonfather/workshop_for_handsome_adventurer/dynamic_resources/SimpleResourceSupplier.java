package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;

public class SimpleResourceSupplier implements Pack.ResourcesSupplier
{
    public SimpleResourceSupplier(PackResources actualPack)
    {
        this.packContents = actualPack;
    }
    private final PackResources packContents;

    @Override
    public PackResources openPrimary(PackLocationInfo packLocationInfo)
    {
        if (packLocationInfo.equals(this.packContents.location()))
        {
            return this.packContents;
        }
        return null;
    }

    @Override
    public PackResources openFull(PackLocationInfo packLocationInfo, Pack.Metadata metadata)
    {
        if (packLocationInfo.equals(this.packContents.location()))
        {
            return this.packContents;
        }
        return null;
    }
}
