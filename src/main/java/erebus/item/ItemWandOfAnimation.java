package erebus.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erebus.ModBlocks;
import erebus.core.helper.Utils;
import erebus.entity.EntityAnimatedBambooCrate;
import erebus.entity.EntityAnimatedBlock;
import erebus.entity.EntityAnimatedChest;
import erebus.tileentity.TileEntityBambooCrate;

public class ItemWandOfAnimation extends Item {

	public ItemWandOfAnimation() {
		setFull3D();
		setTextureName("paper");
		setMaxDamage(64);
		setNoRepair();
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add("Right click blocks to animate them");
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!player.canPlayerEdit(x, y, z, side, is))
			return false;
		else {
			Block blockID = world.getBlock(x, y, z);
			int blockMeta = world.getBlockMetadata(x, y, z);
			if (!world.isRemote && blockID != null && canAnimate(blockID)) {
				EntityAnimatedBlock entityAnimatedBlock;
				if (blockID == Blocks.chest)
					entityAnimatedBlock = new EntityAnimatedChest(world).setContents(Utils.getTileEntity(world, x, y, z, TileEntityChest.class));
				else if (blockID == ModBlocks.bambooCrate)
					entityAnimatedBlock = new EntityAnimatedBambooCrate(world).setContents(Utils.getTileEntity(world, x, y, z, TileEntityBambooCrate.class));
				else
					entityAnimatedBlock = new EntityAnimatedBlock(world);
				world.setBlockToAir(x, y, z);
				entityAnimatedBlock.setLocationAndAngles((double) x + 0.5F, y, (double) z + 0.5F, 0.0F, 0.0F);
				entityAnimatedBlock.setBlock(blockID, blockMeta);
				world.spawnEntityInWorld(entityAnimatedBlock);
				world.playSoundEffect(x, y, z, "erebus:altaroffering", 0.2F, 1.0F);
				is.damageItem(1, player);
				return true;
			}
		}
		return false;
	}

	private boolean canAnimate(Block block) {
		return block == Blocks.chest || !(block instanceof BlockContainer) && block.blockHardness >= 0 && block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() >= 0.7F && block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() >= 0.7F && block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() >= 0.7F;
		// Bamboo Crate removed for now
		// || block.blockID == ModBlocks.bambooCrate.blockID
	}
}