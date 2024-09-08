package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;


public class DualToolRack extends ToolRack
{
    public DualToolRack(int itemCount, String type)
    {
        super(itemCount, type);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
    }

    public static ToolRack create(int itemCount, String type)
    {
        if (ModList.get().isLoaded("tetra"))
        {
            return TetraCompatibleToolRackHelper.create(true, itemCount, type);
        }
        else
        {
            return new DualToolRack(itemCount, type);
        }
    }

    ////////////////////////////////////

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF);
        builder.add(DualTableBaseBlock.BEING_PLACED);
        super.createBlockStateDefinition(builder);
    }



    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        // CM MOD START - Allow placement when clicking down or up based on player's facing.
//        BlockPos target = context.getClickedPos();
//        Level level = context.getLevel();
//        BlockPos below = target.below();
//        if (target.getY() <= level.getMinBuildHeight() || ! level.getBlockState(below).canBeReplaced(context))
//        {
//            return null;
//        }
//        BlockPos back = target.relative(context.getClickedFace().getOpposite());
//        if (! level.getBlockState(back).isFaceSturdy(level, back, context.getClickedFace()))
//        {
//            return null;
//        }
//        back = back.below();
//        if (! level.getBlockState(back).isFaceSturdy(level, back, context.getClickedFace()))
//        {
//            return null;
//        }
//        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite()).setValue(DualTableBaseBlock.BEING_PLACED, true).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();

        if (!level.getBlockState(clickedPos).canBeReplaced(context))
        {
            return null;
        }

        Direction clickedFace = context.getClickedFace();
        Direction facing;

        if (clickedFace == Direction.DOWN || clickedFace == Direction.UP)
        {
            facing = context.getHorizontalDirection();
        }
        else
        {
            facing = context.getClickedFace().getOpposite();
        }

        BlockState lowerState = this.defaultBlockState().setValue(FACING, facing).setValue(DualTableBaseBlock.BEING_PLACED, true).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        BlockState upperState = this.defaultBlockState().setValue(FACING, facing).setValue(DualTableBaseBlock.BEING_PLACED, true).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
        BlockPos lowerPos;
        BlockPos upperPos;
        BlockState placingState;
        BlockPos otherPos;

        if (clickedPos.getY() > level.getMinBuildHeight() && level.getBlockState(clickedPos.below()).canBeReplaced(context))
        {
            lowerPos = clickedPos.below();
            upperPos = clickedPos;
            placingState = upperState;
            otherPos = lowerPos;
        }
        else if (clickedPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(clickedPos.above()).canBeReplaced(context))
        {
            lowerPos = clickedPos;
            upperPos = clickedPos.above();
            placingState = lowerState;
            otherPos = upperPos;
        }
        else
        {
            return null;
        }

        if (!level.getBlockState(otherPos).canBeReplaced(context)) {
            return null;
        }

        if (!this.canSurvive(lowerState, level, lowerPos) || !this.canSurvive(upperState, level, upperPos))
        {
            return null;
        }

        return placingState;
        // CM MOD END
    }



    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack)
    {
        // CM MOD START - Allow placement when clicking down or up based on player's facing.
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
        {
            level.setBlock(pos.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
            level.setBlock(pos, state.setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
            return;
        }
        // CM MOD END

        level.setBlock(pos.below(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
        level.setBlock(pos, state.setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
    }

    // CM MOD START - Stop the upper part of the block from being dropped in creative mode
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            if (half == DoubleBlockHalf.LOWER) {
                BlockPos upperPos = pos.above();
                BlockState upperState = level.getBlockState(upperPos);
                if (upperState.is(state.getBlock()) && upperState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                    BlockState newUpperState = upperState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(upperPos, newUpperState, 35);
                    level.levelEvent(player, 2001, upperPos, Block.getId(upperState));
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }
    // CM MOD END


    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean something)
    {
        super.neighborChanged(state, level, pos, block, pos2, something);
        // CM MOD START - Not needed
//        if (!this.canSurvive(state, level, pos))
//        {
//            level.destroyBlock(pos, true);
//        }
        // CM MOD END
    }



    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos)
    {
        // CM MOD START - Make Dual Tool Rack (and subclasses) require no support.
//        BlockPos back = pos.relative(state.getValue(FACING));
//        BlockState blockstate = levelReader.getBlockState(back);
//        if (! blockstate.isFaceSturdy(levelReader, back, state.getValue(FACING).getOpposite()))
//        {
//            return false;
//        }
        // CM MOD END
        if (state.getValue(DualTableBaseBlock.BEING_PLACED) == false)
        {
            if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
            {
                return levelReader.getBlockState(pos.above()).is(this);
            }
            else
            {
                return levelReader.getBlockState(pos.below()).is(this);
            }
        }
        return true;
    }


    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    ///////////////////////////////////////////////////////

    private static final VoxelShape SHAPE_PLANK1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 16.0D, 1.0D);
    private static final VoxelShape SHAPE_PLANK1E = Block.box(15.0D, 1.0D, 1.0D, 16.0D, 16.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK1S = Block.box(1.0D, 1.0D, 15.0D, 15.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_PLANK1W = Block.box(0.0D, 1.0D, 1.0D, 1.0D, 16.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK2N = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 15.0D, 1.0D);
    private static final VoxelShape SHAPE_PLANK2E = Block.box(15.0D, 0.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK2S = Block.box(1.0D, 0.0D, 15.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_PLANK2W = Block.box(0.0D, 0.0D, 1.0D, 1.0D, 15.0D, 15.0D);



    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter p_60579_, BlockPos p_60580_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter p_60582_, BlockPos p_60583_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter p_60548_, BlockPos p_60549_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    private static VoxelShape resolveShape(Direction direction, DoubleBlockHalf half)
    {
        if (direction.equals(Direction.NORTH) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1N;
        if (direction.equals(Direction.WEST) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1W;
        if (direction.equals(Direction.EAST) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1E;
        if (direction.equals(Direction.SOUTH) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1S;
        if (direction.equals(Direction.NORTH) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2N;
        if (direction.equals(Direction.WEST) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2W;
        if (direction.equals(Direction.EAST) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2E;
        if (direction.equals(Direction.SOUTH) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2S;
        return null;
    }
}
