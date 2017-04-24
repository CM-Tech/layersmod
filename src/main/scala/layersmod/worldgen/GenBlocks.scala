package layersmod.worldgen

import java.util.Random

import layersmod.blocks.LuckyLayer
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator

class GenBlocks extends WorldGenerator {
  def getGroundFromAbove(world: World, x: Int, z: Int): Int = {
    var y           = 255
    var foundGround = false
    while (!foundGround && (y - 1) >= 0) {
      y -= 1
      val blockAt = world.getBlockState(new BlockPos(x, y, z))
      foundGround = blockAt.getMaterial.blocksMovement()
    }
    y
  }

  override def generate(worldIn: World, rand: Random, pos: BlockPos): Boolean = {
    val surfaceLayer = LuckyLayer.simpleOre
    val y            = 1 + getGroundFromAbove(worldIn, pos.getX, pos.getZ)
    if (y >= pos.getY) {
      val orePos    = new BlockPos(pos.getX, y, pos.getZ)
      val toReplace = worldIn.getBlockState(orePos)
      if (toReplace.getBlock == Blocks.AIR || toReplace.getMaterial == Material.PLANTS) {
        worldIn.setBlockState(
          orePos,
          surfaceLayer.getDefaultState.withProperty(LuckyLayer.LAYERS, Integer.valueOf((math.random * 7 + 1).toInt)),
          10
        )
      }
    }

    false
  }
}
