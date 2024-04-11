package moonfather.workshop_for_handsome_adventurer.integration;

import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PolymorphAccessorServer
{
    public static Optional<CraftingRecipe> getRecipe(AbstractContainerMenu menu, CraftingContainer container, Level level, Player player)
    {
        return RecipeSelection.getPlayerRecipe(menu, RecipeType.CRAFTING, container, level, player);
    }
}