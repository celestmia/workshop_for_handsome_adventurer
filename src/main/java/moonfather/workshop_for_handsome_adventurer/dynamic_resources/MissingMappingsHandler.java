package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.BlockTagWriter2;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class MissingMappingsHandler
{
    public static void prepareMappings()
    {
        for (String prefix : BlockTagWriter2.files)
        {
            String destination = ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix + "oak").toString();
            //BuiltInRegistries.BLOCK.addAlias(null, null);
        }
        //"every_compat" "wfha"
    }
}
