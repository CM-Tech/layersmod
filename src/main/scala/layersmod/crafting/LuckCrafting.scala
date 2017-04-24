package layersmod.crafting

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.world.World

case class ItemLuck(item: ItemStack, luck: Int)

class LuckCrafting(output: ItemStack, goodStuff: Array[ItemLuck], badStuff: Array[ItemLuck]) extends IRecipe {
  def getRecipeOutput: ItemStack = output

  def getRemainingItems(inv: InventoryCrafting): NonNullList[ItemStack] = {
    val nonnulllist = NonNullList.withSize[ItemStack](inv.getSizeInventory, ItemStack.EMPTY)
    for (i <- 0 until nonnulllist.size) {
      val itemstack = inv.getStackInSlot(i)
      nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack))
    }
    nonnulllist
  }

  def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
    val center = inv.getStackInRowAndColumn(1, 1).getItem

    val invItems = for {
      i <- 0 until 3
      j <- 0 until 3
    } yield {
      inv.getStackInRowAndColumn(i, j)
    }

    if (invItems.exists { item =>
          (goodStuff.exists { goodItem =>
            this.isEqual(goodItem.item, item)
          } || badStuff.exists { badItem =>
            this.isEqual(badItem.item, item)
          }) && center == output.getItem
        }) true
    else false
  }

  def getCraftingResult(inv: InventoryCrafting): ItemStack = {
    val itemstack = inv.getStackInRowAndColumn(1, 1).copy

    val luckSeq: Seq[Int] = for {
      i <- 0 until 3
      j <- 0 until 3
      item = inv.getStackInRowAndColumn(i, j)
    } yield {
      val positiveLuck = goodStuff.find { goodItem =>
        this.isEqual(goodItem.item, item)
      }.map(_.luck).getOrElse(0)

      val negativeLuck = badStuff.find { badItem =>
        this.isEqual(badItem.item, item)
      }.map(_.luck).getOrElse(0)

      positiveLuck + negativeLuck
    }
    val luckAdded = luckSeq.sum

    val luck: Int = try {
      itemstack.getTagCompound.getInteger("luck")
    } catch {
      case _: NullPointerException => 0
    }

    val tag = new NBTTagCompound()
    tag.setInteger("luck", Math.max(Math.min(luck + luckAdded, 100), -100))
    itemstack.setTagCompound(tag)

    itemstack
  }

  def isEqual(one: ItemStack, two: ItemStack): Boolean = {
    one.getItem == two.getItem && one.getMetadata == two.getMetadata
  }

  def getRecipeSize: Int = 10
}
