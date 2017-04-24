package layersmod.blocks
import layersmod.tileentity.NBTLuckTileEntity
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand, SoundCategory}
import net.minecraft.util.math.{BlockPos, MathHelper}
import net.minecraft.world.World

class LuckyLayerItem(block: Block) extends ItemBlock(block) {
  this.setHasSubtypes(false)

  override def getItemStackLimit(stack: ItemStack): Int =
    if (this.getLuck(stack) == 0) 64 else 1

  def getLuck(stack: ItemStack): Int =
    try {
      stack.getTagCompound.getInteger("luck")
    } catch {
      case _: NullPointerException => 0
    }

  override def showDurabilityBar(stack: ItemStack): Boolean = this.getLuck(stack) != 0

  override def getDurabilityForDisplay(stack: ItemStack): Double = 1 - Math.abs(Math.round(this.getLuck(stack)*16/100)) / 16.0

  override def getRGBDurabilityForDisplay(stack: ItemStack): Int = {
    MathHelper.hsvToRGB(
      if (this.getLuck(stack) < 0) 0 else 1 / 3.0F,
      1.0F,
      1.0F
    )
  }

  override def onItemUse(player: EntityPlayer,
                         worldIn: World,
                         pos: BlockPos,
                         hand: EnumHand,
                         facing: EnumFacing,
                         hitX: Float,
                         hitY: Float,
                         hitZ: Float): EnumActionResult = {
    val itemstack = player.getHeldItem(hand)
    if (!itemstack.isEmpty && player.canPlayerEdit(pos, facing, itemstack)) {
      var iblockstate = worldIn.getBlockState(pos)
      var block       = iblockstate.getBlock
      var blockpos    = pos
      if (((facing != EnumFacing.UP) || (block != this.block)) && !block.isReplaceable(worldIn, pos)) {
        blockpos = pos.offset(facing)
        iblockstate = worldIn.getBlockState(blockpos)
        block = iblockstate.getBlock
      }
      if (block == this.block) {
        val i = iblockstate.getValue(LuckyLayer.LAYERS).intValue
        if (i < 8) {
          val iblockstate1  = iblockstate.withProperty(LuckyLayer.LAYERS, Integer.valueOf(i + 1))
          val axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, blockpos)
          if ((axisalignedbb != Block.NULL_AABB) && worldIn.checkNoEntityCollision(axisalignedbb.offset(blockpos)) && worldIn
                .setBlockState(blockpos, iblockstate1, 10)) {
            val soundtype = this.block.getSoundType(iblockstate1, worldIn, pos, player)
            worldIn.playSound(
              player,
              blockpos,
              soundtype.getPlaceSound,
              SoundCategory.BLOCKS,
              (soundtype.getVolume + 1.0F) / 2.0F,
              soundtype.getPitch * 0.8F
            )
            itemstack.shrink(1)
            return EnumActionResult.SUCCESS
          }
        }
      }
      super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    } else EnumActionResult.FAIL
  }

  /**
    * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
    * placed as a Block (mostly used with ItemBlocks).
    */
  override def getMetadata(damage: Int): Int = damage

  override def placeBlockAt(stack: ItemStack,
                            player: EntityPlayer,
                            world: World,
                            pos: BlockPos,
                            side: EnumFacing,
                            hitX: Float,
                            hitY: Float,
                            hitZ: Float,
                            newState: IBlockState): Boolean = {
    if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
      val tile = world.getTileEntity(pos).asInstanceOf[NBTLuckTileEntity]
      if (tile != null && stack.getTagCompound != null) {
        val newLuck = stack.getTagCompound.getInteger("luck")
        val height  = Math.min(4 + (newLuck * 4 / 100), 8)
        val iBlockState = world
          .getBlockState(pos)
          .withProperty(LuckyLayer.LAYERS, Integer.valueOf(height))
        world.setBlockState(pos, iBlockState, 10)
        tile.setLuck(newLuck)
        tile.markDirty()
      }
      return true
    }
    false
  }
}
