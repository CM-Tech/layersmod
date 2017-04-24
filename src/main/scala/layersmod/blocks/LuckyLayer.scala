package layersmod.blocks

import java.util.Random
import javax.annotation.Nullable

import layersmod.crafting.{ItemLuck, LuckCrafting}
import layersmod.tileentity.NBTLuckTileEntity
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.oredict.RecipeSorter
import net.minecraftforge.oredict.RecipeSorter.Category

object LuckyLayer {
  final val LAYERS: PropertyInteger = PropertyInteger.create("layers", 1, 8)
  final val LAYERS_AABB = Array(
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D),
    new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
  )

  val simpleOre     = new LuckyLayer("lucky_layer", Material.TNT)
  val simpleOreItem = new LuckyLayerItem(simpleOre).setRegistryName("lucky_layer")

  def preInit(): Unit = {
    // Register this block and its item into the game
    GameRegistry.register(simpleOre)
    GameRegistry.register(simpleOreItem)

    GameRegistry.addRecipe(
      new LuckCrafting(
        new ItemStack(simpleOreItem),
        Array(
          ItemLuck(new ItemStack(Items.DIAMOND), 12),
          ItemLuck(new ItemStack(Items.EMERALD), 8),
          ItemLuck(new ItemStack(Items.GOLD_INGOT), 6),
          ItemLuck(new ItemStack(Items.IRON_INGOT), 3),
          ItemLuck(new ItemStack(Items.GOLDEN_CARROT), 30),
          ItemLuck(new ItemStack(Items.GOLDEN_APPLE, 1, 0), 40),
          ItemLuck(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 100),
          ItemLuck(new ItemStack(Items.NETHER_STAR), 100)
        ),
        Array(
          ItemLuck(new ItemStack(Items.ROTTEN_FLESH), -5),
          ItemLuck(new ItemStack(Items.SPIDER_EYE), -10),
          ItemLuck(new ItemStack(Items.FERMENTED_SPIDER_EYE), -20),
          ItemLuck(new ItemStack(Items.POISONOUS_POTATO), -10),
          ItemLuck(new ItemStack(Items.FISH, 1, 3), -20)
        )
      )
    )
    RecipeSorter
      .register("layersmod:lucky_layer", classOf[LuckCrafting], Category.SHAPELESS, "after:minecraft:shapeless")
  }
  def clientPreInit(): Unit = {
    // Add a texture for the block's item, block's texture is automatically set up
    val itemModelResourceLocation = new ModelResourceLocation("layersmod:lucky_layer", "inventory")
    ModelLoader.setCustomModelResourceLocation(simpleOreItem, 0, itemModelResourceLocation)
  }
}

import layersmod.blocks.LuckyLayer.{LAYERS, LAYERS_AABB}

class LuckyLayer(name: String, material: Material) extends Block(material) with ITileEntityProvider {

  this.setUnlocalizedName(name)

  this.setRegistryName(name)

  this.setDefaultState(this.blockState.getBaseState.withProperty(LAYERS, Integer.valueOf(1)))

  this.setLightLevel(0.5f)

  this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS)

  override def canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer) = true

  override def onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
    val tileEntity: NBTLuckTileEntity = worldIn.getTileEntity(pos).asInstanceOf[NBTLuckTileEntity]
    if (!worldIn.isRemote) {
      for (_ <- 1 to 64) {
        val entityitem = new EntityItem(worldIn, pos.getX, pos.getY, pos.getZ, new ItemStack(Items.DIAMOND))
        entityitem.setDefaultPickupDelay()
        entityitem.setVelocity(math.random * 2 - 1, math.random * 2 - 1, math.random * 2 - 1)
        worldIn.spawnEntity(entityitem)
      }
    }
  }

  override def createNewTileEntity(worldIn: World, meta: Int) = new NBTLuckTileEntity()

  override def hasTileEntity: Boolean = true

  override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) =
    LAYERS_AABB(state.getValue(LAYERS).intValue)

  override def isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean =
    worldIn.getBlockState(pos).getValue(LAYERS).intValue < 5

  /**
    * Checks if an IBlockState represents a block that is opaque and a full cube.
    */
  override def isFullyOpaque(state: IBlockState): Boolean = state.getValue(LAYERS).intValue == 8

  @Nullable override def getCollisionBoundingBox(blockState: IBlockState,
                                                 worldIn: IBlockAccess,
                                                 pos: BlockPos): AxisAlignedBB = {
    val i             = blockState.getValue(LAYERS).intValue - 1
    val f             = 0.125F
    val axisalignedbb = blockState.getBoundingBox(worldIn, pos)
    new AxisAlignedBB(
      axisalignedbb.minX,
      axisalignedbb.minY,
      axisalignedbb.minZ,
      axisalignedbb.maxX,
      (i.toFloat * f).toDouble,
      axisalignedbb.maxZ
    )
  }

  /**
    * Used to determine ambient occlusion and culling when rebuilding chunks for render
    */
  override def isOpaqueCube(state: IBlockState) = false

  override def isFullCube(state: IBlockState) = false

  override def neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
    this.checkAndDropBlock(worldIn, pos, state)
  }

  private def checkAndDropBlock(worldIn: World, pos: BlockPos, state: IBlockState) =
    if (!this.canPlaceBlockAt(worldIn, pos)) {
      worldIn.setBlockToAir(pos)
      false
    } else true

  override def canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean = {
    val iblockstate = worldIn.getBlockState(pos.down)
    val block       = iblockstate.getBlock

    if ((block == this) && iblockstate.getValue(LAYERS).intValue == 8) true
    else iblockstate.getMaterial.blocksMovement
  }

  @SideOnly(Side.CLIENT) override def shouldSideBeRendered(blockState: IBlockState,
                                                           blockAccess: IBlockAccess,
                                                           pos: BlockPos,
                                                           side: EnumFacing): Boolean =
    if (side eq EnumFacing.UP) true
    else {
      val iblockstate = blockAccess.getBlockState(pos.offset(side))
      if ((iblockstate.getBlock eq this) && iblockstate
            .getValue(LAYERS)
            .intValue >= blockState.getValue(LAYERS).intValue) true
      else super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }

  /**
    * Convert the given metadata into a BlockState for this Block
    */
  override def getStateFromMeta(meta: Int): IBlockState =
    this.getDefaultState.withProperty(LAYERS, Integer.valueOf((meta & 7) + 1))

  /**
    * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
    */
  override def isReplaceable(worldIn: IBlockAccess, pos: BlockPos): Boolean =
    worldIn.getBlockState(pos).getValue(LAYERS).intValue == 1

  /**
    * Convert the BlockState into the correct metadata value
    */
  override def getMetaFromState(state: IBlockState): Int = state.getValue(LAYERS).intValue - 1

  override def quantityDropped(state: IBlockState, fortune: Int, random: Random): Int = 0 //state.getValue(LAYERS) + 1

  override protected def createBlockState = new BlockStateContainer(this, LAYERS)
}
