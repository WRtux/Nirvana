package nirvana.util;

public interface IResultable {
	
	public static final int RESULT_NULL = -1;
	
	public static final int RESULT_EXIT = -4;
	public static final int RESULT_YES = -8;
	public static final int RESULT_NO = -9;
	
	public abstract boolean hasResult();
	public abstract int getResult() throws IllegalStateException;
	
}
