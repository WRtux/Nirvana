package nirvana.game.fighter;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.game.item.Item;
import nirvana.game.item.ItemStack;

public final class Character extends Fighter {
	
	protected static final int faceWidth = 60;
	protected static final int faceHeight = 60;
	
	//TODO fit equip ability table
	
	private static String[] charNames;
	
	private static Image imgFaces;
	private static WeakReference[] refFaces;
	
	public static String vocationName;
	private static String[] vocationNames;
	private static String[] charDescrs;
	
	public final byte id;
	
	protected ItemStack[] equips;
	
	protected static Image getFace(int index) {
		Image icon = (Image)refFaces[index].get();
		if(icon == null) {
			icon = Image.createImage(
				imgFaces,
				index * faceWidth, 0, faceWidth, faceHeight, Sprite.TRANS_NONE
			);
			refFaces[index] = new WeakReference(icon);
		}
		return icon;
	}
	
	protected Character(byte id) {
		super();
		this.id = id;
		this.name = charNames[id];
		this.equips = new ItemStack[Item.EQUIPSLOTS];
	}
	
	public static Character constructCharacter(DataInputStream stream) throws IOException {
		Character character = new Character(stream.readByte());
		character.alive = stream.readByte() == 0x10;
		character.health = stream.readShort();
		character.mana = stream.readShort();
		for(int i = 0; i < character.equips.length; i++)
			character.equips[i] = ItemStack.constructStack(stream);
		return character;
	}
	
	public Image getFace() {
		return getFace(this.id);
	}
	
	public String getVocationName() {
		return vocationNames[this.id];
	}
	public String getDescr() {
		return charDescrs[this.id];
	}
	
	public ItemStack getEquip(int slot) {
		return this.equips[slot];
	}
	
	public ItemStack equip(int slot, ItemStack equip) {
		if(equip == null) return null;
		if(equip.getNum() == 0 || (equip.getItem().getEquipSlot() != slot)) return null;
		ItemStack unequip = this.unequip(slot);
		int[] effectData = equip.getEquipEffect();
		this.equips[slot] = equip.split(1, false);
		for(int i = 0; i < this.attrs.length; i++)
			this.attrs[i] += effectData[i];
		return unequip;
	}
	
	public ItemStack unequip(int slot) {
		ItemStack equip = this.equips[slot];
		if(equip == null) return null;
		int[] effectData = equip.getEquipEffect();
		this.equips[slot] = null;
		for(int i = 0; i < this.attrs.length; i++)
			this.attrs[i] -= effectData[i];
		return equip;
	}
	
}
