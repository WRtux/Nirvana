package nirvana.game.item;

import java.util.Vector;

public final class Inventory {
	
	protected final Vector inventory;
	
	public Inventory() {
		this.inventory = new Vector();
	}
	
	public Inventory(ItemStack[] stacks) {
		this.inventory = new Vector(stacks.length);
		for(int i = 0; i < stacks.length; i++) this.inventory.addElement(stacks[i]);
	}
	
	public ItemStack stackAt(int index) {
		return (ItemStack)this.inventory.elementAt(index);
	}
	
	public int[] getCategorizedIndicies(byte category) {
		int n = 0;
		for(int i = 0, size = this.inventory.size(); i < size; i++) {
			ItemStack stack = (ItemStack)this.inventory.elementAt(i);
			if(stack.item.category == category) n++;
		}
		int[] indices = new int[n];
		n = 0;
		for(int i = 0, size = this.inventory.size(); i < size; i++) {
			ItemStack stack = (ItemStack)this.inventory.elementAt(i);
			if(stack.item.category == category) indices[n++] = i;
		}
		return indices;
	}
	
	public ItemStack split(short id, int num, int extData, boolean sim) {
		int n = 0;
		for(int i = 0, size = this.inventory.size(); i < size; i++) {
			ItemStack stack = (ItemStack)this.inventory.elementAt(i);
			if(stack.item.id == id && stack.extData == extData) {
				n += stack.split(num - n, sim).num;
				if(!sim && stack.num == 0) this.inventory.removeElementAt(i);
				if(n == num) break;
			}
		}
		if(n != 0) return new ItemStack(id, n, extData);
		else return null;
	}
	
	public ItemStack extract(int index, int num, boolean sim) {
		ItemStack stack = (ItemStack)this.inventory.elementAt(index);
		ItemStack out = stack.split(num, sim);
		if(!sim && stack.num == 0) this.inventory.removeElementAt(index);
		return out;
	}
	public ItemStack extract(int index, boolean sim) {
		ItemStack stack = (ItemStack)this.inventory.elementAt(index);
		if(!sim) this.inventory.removeElementAt(index);
		return stack;
	}
	
	public void merge(ItemStack stack) {
		if(stack == null) return;
		for(int i = 0, size = this.inventory.size(); i < size; i++) {
			ItemStack in = (ItemStack)this.inventory.elementAt(i);
			in.merge(stack);
			if(stack.num == 0) return;
		}
		inventory.addElement(stack);
		stack.num = 0;
	}
	
}
