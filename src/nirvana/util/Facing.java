package nirvana.util;

public final class Facing {
	
	public static final byte NULL = -1;
	
	public static final byte ANY = 0;
	public static final byte UP = 1;
	public static final byte DOWN = 4;
	public static final byte LEFT = 2;
	public static final byte RIGHT = 3;
	
	public static byte getOpposite(byte facing) {
		switch(facing) {
		case ANY:
			return ANY;
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		default:
			return NULL;
		}
	}
	
}
