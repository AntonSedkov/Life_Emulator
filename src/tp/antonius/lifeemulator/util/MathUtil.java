package tp.antonius.lifeemulator.util;

public class MathUtil {
	
	public static int getValueInRange(int value, int min, int max) {
		return Math.max(Math.min(value, max), min);
	}

}