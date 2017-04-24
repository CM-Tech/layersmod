package layersmod.worldgen

import java.util.Random

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.{IChunkGenerator, IChunkProvider}
import net.minecraftforge.fml.common.IWorldGenerator

class WorldNewBlocks extends IWorldGenerator {

  override def generate(random: Random,
                        chunkX: Int,
                        chunkZ: Int,
                        world: World,
                        chunkGenerator: IChunkGenerator,
                        chunkProvider: IChunkProvider) {
    val blockX = chunkX * 16
    val blockZ = chunkZ * 16
    world.provider.getDimension match {
      case -1 =>
      case 0  => generateOverworld(world, random, blockX, blockZ)
      case 1  =>
    }
  }

  private def generateOverworld(world: World, rand: Random, blockX: Int, blockZ: Int) {
    val genRandomBlocks = new GenBlocks()

    val MIN       = 4
    val MAX       = 12
    val numBushes = MIN + rand.nextInt(MAX - MIN)
    for (i <- 0 until numBushes) {
      val randX = blockX + rand.nextInt(16)
      val randZ = blockZ + rand.nextInt(16)
      genRandomBlocks.generate(world, rand, new BlockPos(randX, 24, randZ))
    }

  }

}
