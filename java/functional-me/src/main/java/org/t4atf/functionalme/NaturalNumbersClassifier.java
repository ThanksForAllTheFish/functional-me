package org.t4atf.functionalme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NaturalNumbersClassifier {
	
	public static final Function<Integer, Boolean> PRIME_CLASSIFICATION = number -> {
		return isPrime(number);
	};

	public static final Function<Integer, NNC> NICHOMANUS_CLASSIFICATION = number -> {
		final Integer sum = getAliquotSumOf(number).get();
		if (number.equals(sum))
			return NNC.PERFECT;
		if (number.compareTo(sum) < 0)
			return NNC.ABUNDANT;
		return NNC.DEFICIENT;
	};

	public static final Function<Integer, Integer> SUM_OF_PRIME_FACTORS = new Function<Integer, Integer>() {
		@Override
		public Integer apply(final Integer number) {
			final List<Integer> orderedDivisors = getDivisorOf(number)
					.sorted(Comparator.naturalOrder())
					.filter(n -> n > 1)
					.collect(toImmutableList());
			return sum(orderedDivisors, number, 0);
		}

		private <T> Collector<T, List<T>, List<T>> toImmutableList() {
			final Function<List<T>, List<T>> finisher = input -> Collections.unmodifiableList(input);
			return 
					Collector.of(ArrayList::new, List::add, 
							(left, right) -> {
								left.addAll(right);
								return left;
							}, 
							finisher);
		}

		private Integer sum(final List<Integer> orderedDivisors, final Integer number, final int partialSum) {
			if (1 >= number) return partialSum;
			final int divisor = orderedDivisors.get(0);
			final int next = number / divisor;
			if (next % divisor == 0) return sum(orderedDivisors, next, partialSum + divisor);
			return 
					sum(orderedDivisors.subList(1, orderedDivisors.size()), next,	partialSum + divisor);
		}

	};

	public enum NNC {
		PERFECT, ABUNDANT, DEFICIENT;
	}

	public static Stream<Integer> getDivisorOf(final int number) {
		return getBy(number, canDivide(number));
	}
	
	public static Stream<Integer> getPrimeDivisorOf(final int number) {
		return getBy(number, canDivide(number).and(n -> isPrime(n)));
	}

	public static Stream<Integer> getProperDivisorOf(final int number) {
		return getBy(number, canDivide(number).and(isLessThan(number)));
	}

	public static Optional<Integer> getAliquotSumOf(final int number) {
		return getProperDivisorOf(number).reduce(Integer::sum);
	}

	public static <T> T classifyWith(final int number,
			final Function<Integer, T> classificationFunction) {
		return classificationFunction.apply(number);
	}

	private static IntPredicate isLessThan(final int number) {
		return n -> n < number;
	}

	private static IntPredicate canDivide(final int number) {
		return n -> number % n == 0;
	}

	private static boolean isPrime(int number) {
		return number > 1 && getDivisorOf(number).count() == 2;
	}

	private static Stream<Integer> getBy(final int number, final IntPredicate predicate) {
		return IntStream.rangeClosed(1, number).filter(predicate).boxed();
	}

}
