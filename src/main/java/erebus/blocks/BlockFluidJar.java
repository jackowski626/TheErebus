package erebus.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import erebus.ModBlocks.IHasCustomItem;
import erebus.ModTabs;
import erebus.tileentity.TileEntityFluidJar;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidJar extends BlockContainer implements IHasCustomItem {
	public BlockFluidJar() {
		super(Material.GLASS);
		setHardness(1.0F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(ModTabs.BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityFluidJar();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntityFluidJar) {
				NBTTagCompound nbt = new NBTTagCompound();
				tileentity.writeToNBT(nbt);
				ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 0);
				if (((TileEntityFluidJar) tileentity).tank.getFluidAmount() > 0)
					stack.setTagCompound(nbt);
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.removeTileEntity(pos);
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if (!world.isRemote && stack.hasTagCompound()) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntityFluidJar) {
				if (!stack.getTagCompound().hasKey("Empty")) {
					FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
					((TileEntityFluidJar) tileentity).tank.fillInternal(fluid, true);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		final IFluidHandler fluidHandler = getFluidHandler(world, pos);
		if (fluidHandler != null) {
			FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);//(heldItem, fluidHandler, player);
			return FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null;
		}
		return false;
	}

	@Nullable
	private IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos) {
		TileEntityFluidJar tileentity = (TileEntityFluidJar) world.getTileEntity(pos);
		return tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	}

	@Override
	public ItemBlock getItemBlock() {
		ItemBlock TANK_ITEM = new ItemBlock(this) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
				if(stack.hasTagCompound() && !stack.getTagCompound().hasKey("Empty")) {
					FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
					if(fluid !=null) {
						list.add(TextFormatting.GREEN + "Contains: "+ fluid.getFluid().getLocalizedName(fluid));
						list.add(TextFormatting.BLUE + ""+ fluid.amount +"Mb/32000Mb");
					}
				}
				else
					list.add(TextFormatting.RED + "It's Empty!");
			}
		};
		return TANK_ITEM;
	}

}
