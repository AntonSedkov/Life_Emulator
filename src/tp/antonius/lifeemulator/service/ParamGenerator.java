package tp.antonius.lifeemulator.service;

import java.util.Random;

import tp.antonius.lifeemulator.util.MathUtil;

public class ParamGenerator {
	private static Random random = new Random();

	private ParamGenerator() {
		throw new AssertionError("Wrong initialization");
	}

	public static boolean generateBoolean() {
		return random.nextBoolean();
	}

	public static boolean generateBoolean(int probability) {
		probability = MathUtil.getValueInRange(probability, 0, 100);
		return (random.nextInt(100) < probability);
	}

	public static int generateInteger(int min, int max) {
		if (min > max) {
			throw new RuntimeException(String.format(
					"[RG] Wrong parameters [min] = [%s] and [max] = [%s]. Parameter [max] must be >= [min].", min,
					max));
		}
		return min + random.nextInt(max - min + 1);
	}

}