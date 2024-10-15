package martian.minefactorial.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMachineBE
		extends AbstractEnergyBE
		implements IMachineBE, ITickableBE
{
	private int currentWork = 0;
	private int currentIdleTime = 0;
	private boolean isIdle = true;

	public AbstractMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public int getCurrentWork() {
		return currentWork;
	}

	public int getCurrentIdleTime() {
		return currentIdleTime;
	}

	public abstract boolean checkForWork();

	public abstract void doWork();

	@Override
	public void serverTick() {
		// Check if idle timer has expired
		if (isIdle) {
			if (++currentIdleTime >= getIdleTime()) {
				currentIdleTime = 0;
				if (this.getEnergyStored() >= this.getEnergyPerWork() && checkForWork()) {
					isIdle = false;
				}
			}
			return;
		}

		// Do work
		if (++currentWork >= getMaxWork()) {
			// If we do not have enough energy, immediately go idle
			if (this.getEnergyStored() < this.getEnergyPerWork()) {
				isIdle = true;
				currentIdleTime = 0;
				currentWork = 0;
				return;
			}

			this.getEnergyStorage().forceExtractEnergy(getEnergyPerWork(), false);
			currentWork = 0;
			doWork();

			if (!checkForWork()) {
				isIdle = true;
				currentIdleTime = 0;
			}
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("Work", currentWork);
		tag.putInt("Idle", currentIdleTime);
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		if (tag.contains("Work"))
			this.currentWork = tag.getInt("Work");

		if (tag.contains("Idle"))
			this.currentIdleTime = tag.getInt("Idle");
	}
}
