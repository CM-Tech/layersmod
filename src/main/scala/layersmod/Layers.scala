package layersmod

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}

@Mod(modid = "layersmod", name = "Test-mod", version = "1.0", modLanguage = "scala")
object Layers {

  @SidedProxy(clientSide = "layersmod.ClientProxy", serverSide = "layersmod.CommonProxy")
  var proxy: CommonProxy = _

  @Mod.EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = proxy.preInit(e)

  @Mod.EventHandler
  def init(e: FMLInitializationEvent): Unit = proxy.init(e)

  @Mod.EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = proxy.postInit(e)
}
