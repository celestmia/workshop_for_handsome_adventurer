package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetClientConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder.*;
import net.neoforged.fml.ModList;

public class WoodTypeClientManager
{
    public static String getLogTextureSubstitute(String wood) { return DynamicAssetClientConfig.getLogTexSubstitution(CustomTripletSupport.stripPrefix(wood)); }

    public static String getTexture1Template(String wood) { return DynamicAssetClientConfig.getPlankPath(WoodTypeLister.getHostMod(wood)); }
    public static String getTexture2Template(String wood) { return DynamicAssetClientConfig.getLogPath(WoodTypeLister.getHostMod(wood)); }
    public static String getTexture2TemplateForMod(String namespace) { return DynamicAssetClientConfig.getLogPath(namespace); }
    public static boolean isUsingDarkerWorkstation(String wood) { return DynamicAssetClientConfig.isUsingDarkerWorkstation(CustomTripletSupport.stripPrefix(wood)); }

    public static String getPlankTextureAuto(String modId, String wood)
    {
        return getFinder().getTexturePathForPlanks(modId, wood);
    }
    public static String getLogTextureAuto(String modId, String wood)
    {
        return getFinder().getTexturePathForLogs(modId, wood);
    }
    public static String getPlankTextureAuto(String modId, String wood, String namePattern)
    {
        return getFinder().getTexturePathForPlanks(modId, wood, namePattern);
    }
    public static String getLogTextureAuto(String modId, String wood, String namePattern)
    {
        return getFinder().getTexturePathForLogs(modId, wood, namePattern);
    }
    public static ITextureFinder getFinder()
    {
        if (textureFinder == null)
        {
            if (DynamicAssetClientConfig.autoSearchEnabled())
            {
                if (DynamicAssetClientConfig.UseDAG.isTrue() && ModList.get().isLoaded("dynamic_asset_generator"))
                {
                    textureFinder = TextureAutoFinderDAG.create();
                }
                else
                {
                    textureFinder = TextureAutoFinderBackup.create();
                }
            }
            else
            {
                textureFinder = new TextureFinderFallback();
            }
        }
        return textureFinder;
    }
    private static ITextureFinder textureFinder = null;
}
