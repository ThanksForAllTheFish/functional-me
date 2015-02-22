package org.t4atf.functionalme;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NaturalNumbersClassifier {
	
	public static final Function<Integer, NNC> NICHOMANUS_CLASSIFICATION = number -> {
		Integer sum = getAliquotSumOf(number).get();
		if(number.equals(sum)) return NNC.PERFECT;
		if(number.compareTo(sum) < 0) return NNC.ABUNDANT;
		return NNC.DEFICIENT;
	};
	
	public static final Function<Integer, Integer> SUM_OF_PRIME_FACTORS = new Function<Integer, Integer>() {
		@Override
		public Integer apply(Integer number) {
			List<Integer> orderedDivisors = getDivisorOf(number).sorted(Comparator.naturalOrder()).filter(n -> n > 1).collect(Collectors.toList());
			return sum(orderedDivisors, number, 0);
		}

		private Integer sum(List<Integer> orderedDivisors, Integer number, int partialSum) {
			if (1 >= number) return partialSum;
			int divisor = orderedDivisors.get(0);
			int next = number / divisor;
			if(next % divisor == 0) return sum(orderedDivisors, next, partialSum + divisor);
			return sum(orderedDivisors.subList(1, orderedDivisors.size()), next, partialSum + divisor);
		}
		
	};

	public enum NNC {
		PERFECT,
		ABUNDANT,
		DEFICIENT;
	}

	public static Stream<Integer> getDivisorOf(int number) {
		return getBy(number, canDivide(number));
	}
	
	public static Stream<Integer> getProperDivisorOf(int number) {
		return getBy(number, canDivide(number).and(isLessThan(number)));
	}

	private static IntPredicate isLessThan(int number) {
		return n -> n < number;
	}

	private static IntPredicate canDivide(int number) {
		return n -> number % n == 0;
	}
	
	private static Stream<Integer> getBy(int number, IntPredicate predicate) {
		return IntStream.rangeClosed(1, number).filter(predicate).boxed();
	}

	public static Optional<Integer> getAliquotSumOf(int number) {
		return getProperDivisorOf(number).reduce(Integer::sum);
	}

	public static <T> T classifyWith(int number, Function<Integer, T> classificationFunction) {
		return classificationFunction.apply(number);
	}

}
