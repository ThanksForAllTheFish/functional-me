package org.t4atf.functionalme;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NaturalNumbersClassifier {
	
	public static final Function<Integer, NNC> NICHOMANUS_CLASSIFICATION = number1 -> {
		Integer sum = getAliquotSumOf(number1).get();
		if(number1.equals(sum)) return NNC.PERFECT;
		if(number1.compareTo(sum) < 0) return NNC.ABUNDANT;
		return NNC.DEFICIENT;
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
		return getBy(number, isLessThan(number).and(canDivide(number)));
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
