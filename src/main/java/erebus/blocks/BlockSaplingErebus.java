package erebus.blocks;

import java.util.Random;

import erebus.ModTabs;
import erebus.world.feature.plant.WorldGenBamboo;
import erebus.world.feature.tree.WorldGenAsperTree;
import erebus.world.feature.tree.WorldGenBalsamTree;
import erebus.world.feature.tree.WorldGenBaobabTree;
import erebus.world.feature.tree.WorldGenCypressTree;
import erebus.world.feature.tree.WorldGenErebusHugeTree;
import erebus.world.feature.tree.WorldGenEucalyptusTree;
import erebus.world.feature.tree.WorldGenMarshwoodTree;
import erebus.world.feature.tree.WorldGenMossbarkTree;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSaplingErebus extends BlockSapling {

	private final EnumWood wood;

	public BlockSaplingErebus(EnumWood wood) {
		this.wood = wood;
		setSoundType(SoundType.PLANT);
		setCreativeTab(ModTabs.PLANTS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab == ModTabs.PLANTS)
			list.add(new ItemStack(this));
	}

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            super.updateTick(world, pos, state, rand);

            if (world.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            	if(this != EnumWood.BAMBOO.getSapling())
            		grow(world, pos, state, rand);
            	else
            		world.setBlockState(pos, EnumWood.BAMBOO.getLog().getDefaultState(), 3);
            }
        }
    }

	@Override
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!TerrainGen.saplingGrowTree(world, rand, pos))
			return;

		WorldGenerator worldGen = null;

		switch (wood) {
			case EUCALYPTUS:
				worldGen = new WorldGenEucalyptusTree();
				break;
			case BAOBAB:
				worldGen = new WorldGenBaobabTree();
				break;
			case ASPER:
				worldGen = new WorldGenAsperTree();
				break;
			case CYPRESS:
				worldGen = new WorldGenCypressTree();
				break;
			case MAHOGANY:
				worldGen = new WorldGenErebusHugeTree(true, true, wood);
				((WorldGenErebusHugeTree) worldGen).prepare(20 + rand.nextInt(5));
				break;
			case BALSAM:
				worldGen = new WorldGenBalsamTree();
				break;
			case MOSSBARK:
				worldGen = new WorldGenMossbarkTree();
				break;
			case MARSHWOOD:
				worldGen = new WorldGenMarshwoodTree();
				break;
			case BAMBOO:
				worldGen = new WorldGenBamboo(true, false);
				break;
			default:
				break;
		}

		if (worldGen == null)
			return;

		world.setBlockToAir(pos);
		if (!worldGen.generate(world, rand, pos))
			world.setBlockState(pos, this.getDefaultState());
	}
}