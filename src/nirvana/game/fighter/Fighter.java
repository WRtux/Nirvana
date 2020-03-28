package nirvana.game.fighter;

public class Fighter {
	
	public static final int ATTR_NUM = 5;
	
	public static int ATTR_ATN = 0;
	public static int ATTR_INS = 1;
	public static int ATTR_DEF = 2;
	public static int ATTR_RES = 3;
	public static int ATTR_SPD = 4;
	
	protected String name;
	
	protected boolean alive = true;
	
	protected int health;
	protected int healthMax;
	protected int mana;
	protected int manaMax;
	
	protected int attrs[];
	
	public Fighter() {
		this.name = "";
		this.health = this.healthMax = 10;
		this.mana = this.manaMax = 0;
		this.attrs = new int[ATTR_NUM];
		for(int i = 0; i < this.attrs.length; i++) this.attrs[i] = 0;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final int getHealth() {
		return this.health;
	}
	public final int getHealthMax() {
		return this.healthMax;
	}
	public final int getMana() {
		return this.mana;
	}
	public final int getManaMax() {
		return this.manaMax;
	}
	
	public final int getAttr(int index) {
		return this.attrs[index];
	}
	
	public final void addHealth(int value) {
		if(!this.alive) return;
		this.health += value;
		if(this.health > this.healthMax) this.health = this.healthMax;
		else if(this.health <= 0) {
			this.alive = false;
			this.health = 0;
		}
	}
	public final void addMana(int value) {
		if(!this.alive) return;
		this.mana += value;
		if(this.mana > this.manaMax) this.mana = this.manaMax;
		else if(this.mana <= 0) this.mana = 0;
	}
	
	public final void recover() {
		this.alive = true;
		this.health = this.healthMax;
		this.mana = this.manaMax;
	}
	
	public final int causeAtnDamage(int atn, boolean critical) {
		float i = atn;
		if(critical) i *= 1.5;
		int damage = (int)(
			Math.sqrt(i * i / this.attrs[ATTR_DEF] / this.attrs[ATTR_DEF]) - this.attrs[ATTR_DEF]
		);
		this.addHealth(-damage);
		return damage;
	}
	public final int causeInsDamage(int ins, float rate) {
		float i = ins * rate;
		int damage = (int)(
			Math.sqrt(i * i / this.attrs[ATTR_RES] / this.attrs[ATTR_RES]) - this.attrs[ATTR_RES]
		);
		this.addHealth(-damage);
		return damage;
	}
	
}
