package  layersmod.tileentity

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class NBTLuckTileEntity extends TileEntity {
  private var luck = 0

  override def readFromNBT(compound: NBTTagCompound) {
    super.readFromNBT(compound)
    this.luck = compound.getInteger("luck")
  }

  override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(compound)
    compound.setInteger("luck", this.luck)
    compound
  }

  def getLuck: Int = this.luck

  def setLuck(newLuck: Int) {
    this.luck = newLuck
    this.markDirty()
  }
}