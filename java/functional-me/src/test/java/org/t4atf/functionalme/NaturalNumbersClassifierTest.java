package org.t4atf.functionalme;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.t4atf.functionalme.NaturalNumbersClassifier.*;

import java.util.stream.Collectors;

import org.junit.Test;

public class NaturalNumbersClassifierTest {

	@Test
	public void divisor() {
		assertThat(getDivisorOf(6).collect(Collectors.toSet()), containsInAnyOrder(1, 2, 3, 6));
		assertThat(getDivisorOf(3).collect(Collectors.toSet()), containsInAnyOrder(1, 3));
		assertThat(getDivisorOf(12).collect(Collectors.toSet()), containsInAnyOrder(1, 2, 3, 4, 6, 12));
		assertThat(getDivisorOf(18).collect(Collectors.toSet()), containsInAnyOrder(1, 2, 3, 6, 9, 18));
	}
	
	@Test
	public void properDivisor() {
		assertThat(getProperDivisorOf(6).collect(Collectors.toSet()), containsInAnyOrder(1, 2, 3));
		assertThat(getProperDivisorOf(3).collect(Collectors.toSet()), containsInAnyOrder(1));
		assertThat(getProperDivisorOf(12).collect(Collectors.toSet()), containsInAnyOrder(1, 2, 3, 4, 6));
		assertThat(getProperDivisorOf(18).collect(Collectors.toSet()), containsInAnyOrder(1, 2, 3, 6, 9));
	}
	
	@Test
	public void aliquotSum() {
		assertThat(getAliquotSumOf(6).get(), equalTo(6));
		assertThat(getAliquotSumOf(3).get(), equalTo(1));
		assertThat(getAliquotSumOf(12).get(), equalTo(16));
		assertThat(getAliquotSumOf(18).get(), equalTo(21));
	}

	@Test
	public void nichomanus() {
		assertThat(classifyWith(6, NICHOMANUS_CLASSIFICATION), equalTo(NaturalNumbersClassifier.NNC.PERFECT));
		assertThat(classifyWith(3, NICHOMANUS_CLASSIFICATION), equalTo(NaturalNumbersClassifier.NNC.DEFICIENT));
		assertThat(classifyWith(12, NICHOMANUS_CLASSIFICATION), equalTo(NaturalNumbersClassifier.NNC.ABUNDANT));
		assertThat(classifyWith(18, NICHOMANUS_CLASSIFICATION), equalTo(NaturalNumbersClassifier.NNC.ABUNDANT));
	}
	
	@Test
	public void sumOfPrimeFactor() {
		assertThat(classifyWith(1, SUM_OF_PRIME_FACTORS), equalTo(0));
		assertThat(classifyWith(2, SUM_OF_PRIME_FACTORS), equalTo(2));
		assertThat(classifyWith(6, SUM_OF_PRIME_FACTORS), equalTo(5));
		assertThat(classifyWith(3, SUM_OF_PRIME_FACTORS), equalTo(3));
		assertThat(classifyWith(12, SUM_OF_PRIME_FACTORS), equalTo(7));
		assertThat(classifyWith(18, SUM_OF_PRIME_FACTORS), equalTo(8));
	}
}
