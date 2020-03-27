package nirvana.game.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nirvana.control.ErrorHandler;

public final class ItemStack {
	
	protected final Item item;
	protected int num;
	protected int extData = 0;
	
	public ItemStack(short id, int num, int extData) {
		if(num < 0) ErrorHandler.riseError(ItemStack.class, new IllegalArgumentException("Negtive item num."));
		this.item = Item.getItem(id);
		this.num = num;
		this.extData = extData;
	}
	public ItemStack(short id, int extData) {
		this(id, 1, extData);
	}
	public ItemStack(short id) {
		this(id, 0);
	}
	
	public static ItemStack constructStack(DataInputStream stream) throws IOException {
		return new ItemStack(stream.readShort(), stream.readShort(), stream.readInt());
	}
	
	public static void exportStack(DataOutputStream stream, ItemStack stack) throws IOException {
		stream.writeShort(stack.item.id);
		stream.writeShort(stack.num);
		stream.writeInt(stack.extData);
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public int getNum() {
		return this.num;
	}
	public int getExtData() {
		return this.extData;
	}
	
	public ItemStack split(int num, boolean sim) {
		if(num < 0)
			ErrorHandler.riseError(ItemStack.class, new IllegalArgumentException("Negtive item num."));
		num = Math.min(num, this.num);
		if(!sim) this.num -= num;
		return new ItemStack(this.item.id, num, this.extData);
	}
	
	public void merge(ItemStack stack) {
		if(stack == null) return;
		if(stack.num < 0)
			ErrorHandler.riseError(ItemStack.class, new IllegalArgumentException("Negtive item num."));
		if(stack.item.id == this.item.id && stack.extData == this.extData) {
			this.num += stack.num;
			stack.num = 0;
		}
	}
	
	public int[] getStateEffect() {
		if(this.item.isUsable()) {
			int[] tmp = this.item.effectData;
			return new int[] {tmp[0], tmp[1]};
		} else return null;
	}
	
	public int[] getEquipEffect() {
		if(this.item.isEquip()) {
			int[] tmp = this.item.effectData;
			return new int[] {
				tmp[0] * (7 + this.extData) / 7, //14.3%
				tmp[1] * (9 + this.extData) / 9, //11.1%
				tmp[2] * (8 + this.extData) / 8, //12.5%
				tmp[3] * (10 + this.extData) / 10, //10.0%
				tmp[4]
			};
		} else return null;
	}
	
}
