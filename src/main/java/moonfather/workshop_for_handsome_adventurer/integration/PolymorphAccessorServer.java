package moonfather.workshop_for_handsome_adventurer.integration;

import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PolymorphAccessorServer
{
    public static Optional<CraftingRecipe> getRecipe(AbstractContainerMenu menu, CraftingContainer container, Level level, Player player)
    {
        Optional<RecipeHolder<CraftingRecipe>> holder = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container.asCraftInput(), level);
        if (holder.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(holder.get().value());
        // when polymorph is out, delete 5 rows above this
        //return RecipeSelection.getPlayerRecipe(menu, RecipeType.CRAFTING, container, level, player);
    }
}