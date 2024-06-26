package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;

import java.util.Collection;

public class WoodTypeCommonManager
{
    public static String getLogRecipeSubstitute(String wood) { return DynamicAssetCommonConfig.getLogRecipeSubstitution(CustomTripletSupport.stripPrefix(wood)); }

    ////////////////////////////

    public static Collection<WoodSet> getWoodSetsWithDumbassNames()
    {
        return DynamicAssetCommonConfig.getWoodSetsWithDumbassNames();
    }

    public static WoodSet getWoodSet(String wood)
    {
        for (WoodSet set: DynamicAssetCommonConfig.getWoodSetsWithDumbassNames())
        {
            if (CustomTripletSupport.addPrefixTo(set.planks).equals(wood)) { return  set; }
        }
        return null;
    }

    public record WoodSet(String modId, String planks, String slab, String log) { }
}
