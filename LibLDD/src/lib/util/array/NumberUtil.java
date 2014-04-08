package lib.util.array;

public class NumberUtil {
	public static void increment(int[] array, int amount) {
		for(int i = 0; i < array.length; i++) {
			array[i] += amount;
		}
	}
}
