package martian.minefactorial.content.menu;

import martian.minefactorial.content.block.machinery.BlockPumpBE;
import martian.minefactorial.content.registry.MFBlocks;
import martian.minefactorial.content.registry.MFMenuTypes;
import martian.minefactorial.foundation.menu.AbstractMachineContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;

public class ContainerPump extends AbstractMachineContainer<BlockPumpBE> {
	public final int capacity;
	public int fluidStackId, fluidAmount;

	public ContainerPump(int containerId, Inventory playerInventory, BlockPos pos) {
		super(MFMenuTypes.PUMP.get(), MFBlocks.PUMP.get(), 0, containerId, playerInventory, pos);

		this.capacity = this.blockEntity.getTank().getCapacity();

		addEnergySlot(this.blockEntity);
		addWorkSlot(this.blockEntity);
		addIdleSlot(this.blockEntity);

		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return BuiltInRegistries.FLUID.getId(ContainerPump.this.blockEntity.getTank().getFluid().getFluid());
			}

			@Override
			public void set(int value) {
				fluidStackId = value;
			}
		});

		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return ContainerPump.this.blockEntity.getTank().getFluid().getAmount();
			}

			@Override
			public void set(int value) {
				fluidAmount = value;
			}
		});

		addPlayerInventorySlots(playerInventory, 8, 84);
	}
}
