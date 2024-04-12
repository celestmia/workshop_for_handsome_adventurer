package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class PolymorphAccessorClient
{
    public static void setTargetSlot(Slot slot)
    {
        RecipesWidget.get().ifPresent(
                (widget) -> {
                    if (widget instanceof PolymorphAccessorClient.OurPlayerRecipesWidget ourWidget)
                    {
                        ourWidget.setDestinationSlot(slot);
                    }
                }
        );
    }

    public static void register()
    {
        PolymorphApi.client().registerWidget(
                containerScreen ->
                {
                    if (containerScreen instanceof SimpleTableCraftingScreen)
                    {
                        return new OurPlayerRecipesWidget(containerScreen, containerScreen.getMenu().slots.get(SimpleTableMenu.RESULT_SLOT));
                    }
                    return null;
                }
        );
    }

    //////////////////////////

    private static class OurPlayerRecipesWidget extends PlayerRecipesWidget
    {
        public OurPlayerRecipesWidget(AbstractContainerScreen<?> containerScreen, Slot outputSlot)
        {
            super(containerScreen, outputSlot);
            this.output = outputSlot;
        }

        public void setDestinationSlot(Slot newOutput)
        {
            this.output = newOutput;
            this.resetWidgetOffsets();
        }
        private Slot output;

        @Override
        public Slot getOutputSlot()
        {
            return this.output;
        }

        ////////

        @Override
        public void selectRecipe(ResourceLocation resourceLocation)
        {
            super.selectRecipe(resourceLocation);
            PacketSender.sendCraftingResultUpdateRequestToServer(this.getOutputSlot().index);
        }
    }
}
