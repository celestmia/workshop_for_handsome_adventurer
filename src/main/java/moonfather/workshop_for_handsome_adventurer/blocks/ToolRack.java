package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.BaseContainerBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.PackingTape;
import moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.*;

@ParametersAreNonnullByDefault
public class ToolRack extends Block implements EntityBlock
{
	public ToolRack(int itemCount, String type)
	{
		this(itemCount, "tool_rack", type);
	}
	public ToolRack(int itemCount, String mainType, String subType)
	{
		this(itemCount, mainType, subType, Properties.of().strength(2f, 3f).sound(SoundType.WOOD).ignitedByLava().mapColor(MapColor.COLOR_BROWN).pushReaction(PushReaction.DESTROY));
	}
	public ToolRack(int itemCount, String mainType, String subType, Properties properties)
	{
		super(properties);
		this.itemCount = itemCount;

		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		this.PrepareListOfShapes();

		String translationKeyStructure = "block.{0}.{1}_{2}.tooltip{3}";
		if (subType == null) { translationKeyStructure = "block.{0}.{1}.tooltip{3}"; }
		String translationKey = MessageFormat.format(translationKeyStructure, Constants.MODID, mainType, subType, 1);
		this.Tooltip1 = Component.translatable(translationKey).withStyle(Style.EMPTY.withItalic(true).withColor(0xaa77dd));
		translationKey = MessageFormat.format(translationKeyStructure, Constants.MODID, mainType, subType, 2);
		this.Tooltip2 = Component.translatable(translationKey).withStyle(Style.EMPTY.withItalic(true).withColor(0xaa77dd));
	}

	public static ToolRack create(int itemCount, String type)
	{
		if (ModList.get().isLoaded("tetra"))
		{
			return TetraCompatibleToolRackHelper.create(false, itemCount, type);
		}
		else
		{
			return new ToolRack(itemCount, type);
		}
	}

	////////////////////////////////////////////

	protected final int itemCount;
	protected MutableComponent Tooltip1;
	protected MutableComponent Tooltip2;

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	private static final VoxelShape SHAPE_PLANK1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 1.0D);
	private static final VoxelShape SHAPE_PLANK1E = Block.box(15.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
	private static final VoxelShape SHAPE_PLANK1S = Block.box(1.0D, 1.0D, 15.0D, 15.0D, 15.0D, 16.0D);
	private static final VoxelShape SHAPE_PLANK1W = Block.box(0.0D, 1.0D, 1.0D, 1.0D, 15.0D, 15.0D);



	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter p_60579_, BlockPos p_60580_)
	{
		return this.shapes.get(state.getValue(FACING));
	}


	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter p_60582_, BlockPos p_60583_)
	{
		return this.shapes.get(state.getValue(FACING));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return this.shapes.get(state.getValue(FACING));
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter p_60548_, BlockPos p_60549_)
	{
		return this.shapes.get(state.getValue(FACING));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter worldReader, List<Component> list, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(itemStack, worldReader, list, tooltipFlag);
		list.add(this.Tooltip1);
		list.add(this.Tooltip2);
	}



	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}



	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		// CM MOD START - Allow placement when clicking down or up based on player's facing.
//		if (context.getClickedFace() == Direction.DOWN || context.getClickedFace() == Direction.UP)
//		{
//			return null;
//		}
//		BlockState result = this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
		BlockState result;
		if (context.getClickedFace() == Direction.DOWN || context.getClickedFace() == Direction.UP)
		{
			result = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
		}
		else
		{
			result = this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
		}
		// CM MOD END
		if (this.canSurvive(result, context.getLevel(), context.getClickedPos()))
		{
			return result;
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		// CM MOD START - Make Tool Rack (and subclasses) require no support.
		if (true) {
			return true;
		}
		// CM MOD END

		Direction d = state.getValue(FACING).getOpposite();
		BlockPos target = pos.relative(d.getOpposite());
		return world.getBlockState(target).isFaceSturdy(world, target, d);
	}



	protected final Map<Direction, VoxelShape> shapes = new HashMap<Direction, VoxelShape>(4);
	protected void PrepareListOfShapes()
	{
		this.shapes.put(Direction.NORTH, SHAPE_PLANK1N);
		this.shapes.put(Direction.EAST, SHAPE_PLANK1E);
		this.shapes.put(Direction.SOUTH, SHAPE_PLANK1S);
		this.shapes.put(Direction.WEST, SHAPE_PLANK1W);
	}



	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
	{
		if (! blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) || blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
		{
			return Registration.TOOL_RACK_BE.get().create(pos, blockState);
		}
		else
		{
			return null;
		}
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return null;
	}


	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos rackPos, Block block, BlockPos wallPos, boolean something)
	{
		super.neighborChanged(state, level, rackPos, block, wallPos, something);
		if (!this.canSurvive(state, level, rackPos))
		{
			level.destroyBlock(rackPos, true);
		}
	}


	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() != newState.getBlock())
		{
			BlockEntity be = worldIn.getBlockEntity(pos);
			if (be instanceof BaseContainerBlockEntity rack)
			{
				rack.DropAll();
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}



	private final MutableComponent RackMessage = Component.translatable("message.workshop_for_handsome_adventurer.invalid_item_for_rack");


	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
	{
		if (blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
		{
			BlockPos above = pos.above();
			return this.use(level.getBlockState(above), level, above, player, hand, blockHitResult.withPosition(above));
		}
		boolean doOffhand = OptionsHolder.COMMON.OffhandInteractsWithToolRack.get();
		if (hand == InteractionHand.OFF_HAND)
		{
			return InteractionResult.PASS;
		}
		if (level.isClientSide)
		{
			return InteractionResult.SUCCESS;
			// we were doing just fine without this statement, updating both sides in parallel, but then CarryOn caused desyncs.
			// implementing getUpdatePacket() to return ClientboundBlockEntityDataPacket.create instead of nothing fixed "empty block entity" issue but desyncs remained when clicking quickly. so we're trying server-only plus forced update.
		}

		int slot = this.getTargetedSlot(blockHitResult);
		if (slot >= this.itemCount)
		{
			slot -= this.itemCount;
		}
		ToolRackBlockEntity BE = ((ToolRackBlockEntity)level.getBlockEntity(pos));
		ItemStack existing = BE.GetItem(slot);
		ItemStack itemInMainHand = player.getMainHandItem();
		ItemStack itemInOffHand = player.getOffhandItem();
		if (existing.isEmpty() && ! itemInMainHand.isEmpty())
		{
			if (! this.canDepositItem(itemInMainHand))
			{
				player.displayClientMessage(RackMessage, true);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			//System.out.println("~~~~~ADDED FROM MAIN");
			ItemStack toStore = itemInMainHand.copy();
			toStore.setCount(1);
			BE.DepositItem(slot, toStore);
			itemInMainHand.shrink(1);
			player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
		}
		else if (existing.isEmpty() && itemInMainHand.isEmpty() && (! doOffhand || itemInOffHand.isEmpty()))
		{
			//System.out.println("~~~~~EMPTY TO EMPTY");
		}
		else if (existing.isEmpty() && itemInMainHand.isEmpty() && doOffhand && ! itemInOffHand.isEmpty())
		{
			if (! this.canDepositItem(itemInOffHand))
			{
				player.displayClientMessage(RackMessage, true);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			//System.out.println("~~~~~ADDED FROM OFFHAND");
			ItemStack toStore = itemInOffHand.copy();
			toStore.setCount(1);
			BE.DepositItem(slot, toStore);
			itemInOffHand.shrink(1);
			player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
		}
		else if (! existing.isEmpty() && itemInMainHand.isEmpty() && itemInOffHand.isEmpty() && existing.canPerformAction(ToolActions.SHIELD_BLOCK))
		{
			//System.out.println("~~~~~TAKEN SHIELD");
			//player.addItem(existing);
			player.setItemInHand(InteractionHand.OFF_HAND, existing);
			BE.ClearItem(slot);
			player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
		}
		else if (! existing.isEmpty() && itemInMainHand.isEmpty())
		{
			//System.out.println("~~~~~TAKEN WITH MAIN");
			player.setItemInHand(InteractionHand.MAIN_HAND, existing);
			BE.ClearItem(slot);
			player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
		}
		else if (! existing.isEmpty() && ! itemInMainHand.isEmpty() && doOffhand && itemInOffHand.isEmpty())
		{
			//System.out.println("~~~~~TAKEN WITH OFFHAND");
			if (! itemInMainHand.canPerformAction(ToolActions.SHIELD_BLOCK))
			{
				player.setItemInHand(InteractionHand.OFF_HAND, existing); // normal
			}
			else
			{
				player.setItemInHand(InteractionHand.OFF_HAND, itemInMainHand); // exception: move shield to offhand
				player.setItemInHand(InteractionHand.MAIN_HAND, existing);
			}
			BE.ClearItem(slot);
			player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
		}
		else
		{
			//System.out.println("~~~~~BOTH FULL");
		}
		level.sendBlockUpdated(pos, blockState, blockState, 2);
		return InteractionResult.sidedSuccess(level.isClientSide);
		//return super.use(blockState, level, pos, player, hand, blockHitResult);
	}



	public static int getToolRackSlot(ToolRack block, BlockHitResult blockHitResult)
	{
		return block.getTargetedSlot(blockHitResult);
	}
	protected int getTargetedSlot(BlockHitResult blockHitResult)
	{
		int aboveThisRow = 0;
		double frac = blockHitResult.getLocation().y - blockHitResult.getBlockPos().getY();
		if (frac >= 5/16d) { aboveThisRow = 0; /* row1*/ }
		if (frac < 5/16d && frac >= -5/16d) { aboveThisRow = 2; /* row2*/ }
		if (frac < -5/16d && frac >= -15/16d) { aboveThisRow = 4; /* row3*/ }

		int integral;
		integral = (int) blockHitResult.getLocation().z;
		frac = (blockHitResult.getLocation().z - integral) * blockHitResult.getDirection().getStepX();
		integral = (int) blockHitResult.getLocation().x;
		frac -= (blockHitResult.getLocation().x - integral) * blockHitResult.getDirection().getStepZ();
		boolean left = (frac >= -0.5 && frac < 0) || frac >= 0.5;

		return aboveThisRow + (left ? 0 : 1);
	}

	protected boolean canDepositItem(ItemStack mainHandItem) {
		if (mainHandItem == null || mainHandItem.isEmpty()) {
			return true;
		}
		if (mainHandItem.getMaxStackSize() > 1 && ! (mainHandItem.getItem().equals(Items.LEAD) || PackingTape.isTape(mainHandItem))) {
			return false;
		}
		if (mainHandItem.getItem() instanceof BlockItem || mainHandItem.getItem() instanceof ArmorItem || mainHandItem.getItem() instanceof HorseArmorItem) {
			return false;
		}
		if (mainHandItem.getItem().getFoodProperties() != null || mainHandItem.getItem() instanceof BucketItem || mainHandItem.getItem() instanceof MinecartItem || mainHandItem.getItem() instanceof BoatItem) {
			return false;
		}
		if (mainHandItem.getTag() != null && mainHandItem.getTag().contains("Potion")) {
			return false;
		}
		if (mainHandItem.is(Constants.Tags.NOT_ALLOWED_ON_TOOLRACK) || mainHandItem.is(ItemTags.BOOKSHELF_BOOKS)) {
			return false;
		}
		return true;
	}
}
