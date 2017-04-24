package layersmod

import layersmod.blocks.LuckyLayer
import layersmod.tileentity.NBTLuckTileEntity
import layersmod.worldgen.WorldNewBlocks
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.registry.GameRegistry

class CommonProxy {
  def preInit(e: FMLPreInitializationEvent): Unit = {

    GameRegistry.registerTileEntity(classOf[NBTLuckTileEntity], "SimpleOreData")
    LuckyLayer.preInit()

  }

  def init(e: FMLInitializationEvent): Unit = {

    println("mmmmmmm                 m                             #")
    println("   #     mmm    mmm   mm#mm         mmmmm   mmm    mmm#")
    println("   #    #\"  #  #   \"    #           # # #  #\" \"#  #\" \"#")
    println("   #    #\"\"\"\"   \"\"\"m    #     \"\"\"   # # #  #   #  #   #")
    println("   #    \"#mm\"  \"mmm\"    \"mm         # # #  \"#m#\"  \"#m##")

    GameRegistry.registerWorldGenerator(new WorldNewBlocks(), 10)

  }

  def postInit(e: FMLPostInitializationEvent): Unit = {}

}

class ClientProxy extends CommonProxy {
  override def preInit(e: FMLPreInitializationEvent): Unit = {
    super.preInit(e)

    LuckyLayer.clientPreInit()
  }
  override def init(e: FMLInitializationEvent): Unit = super.init(e)

  override def postInit(e: FMLPostInitializationEvent): Unit = super.postInit(e)

}
