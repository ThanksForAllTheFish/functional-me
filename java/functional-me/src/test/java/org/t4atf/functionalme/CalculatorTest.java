package org.t4atf.functionalme;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.t4atf.functionalme.Calculator.REVERSE_POLISH_NOTATION;
import static org.t4atf.functionalme.Calculator.SHUNTING_YARD_TOKENIZER;
import static org.t4atf.functionalme.Calculator.Operator.MUL;
import static org.t4atf.functionalme.Calculator.Operator.from;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CalculatorTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	private Calculator calculator = new Calculator();

	@Test
	public void emptyExpressionEvalutesToZero() {
		assertThat(calculator.compute("").isPresent(), equalTo(false));
	}
	
	@Test
	public void noOperationEvaluateToExpression() {
		assertThat(calculator.compute("5").get(), equalTo(new BigDecimal("5")));
		assertThat(calculator.compute("5+5").get(), equalTo(new BigDecimal("10")));
		assertThat(calculator.compute("5+5*2").get(), equalTo(new BigDecimal("15")));
		assertThat(calculator.compute("3 + 4 * 2 / ( 1 - 5 )").get(), equalTo(new BigDecimal("1")));
		
		assertThat(calculator.compute("5+").isPresent(), equalTo(false));
	}
	
	@Test
	public void createKnownOperatorPrecedence() throws Exception {
		assertThat(from("*"), equalTo(MUL));
	}
	
	@Test
	public void createUnknownOperatorPrecedence() throws Exception {
		exception.expect(IllegalArgumentException.class);
		from("p");
	}
	
	@Test
	public void shuntingYardTokenizer() throws Exception {
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5"), equalTo("5"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5+"), equalTo("5+"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5+5"), equalTo("55+"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5+5*2"), equalTo("552*+"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5*5*2"), equalTo("55*2*"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5*5+2"), equalTo("55*2+"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5*(5+2)"), equalTo("552+*"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5/(5-2)"), equalTo("552-/"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("5/(5*2)"), equalTo("552*/"));
		assertThat(SHUNTING_YARD_TOKENIZER.apply("3 + 4 * 2 / ( 1 - 5 )"), equalTo("342*15-/+"));
	}

	@Test
	public void reversePolishNotationCalculator() throws Exception {
		assertThat(REVERSE_POLISH_NOTATION.apply("5").get(), equalTo(new BigDecimal("5")));
		assertThat(REVERSE_POLISH_NOTATION.apply("55+").get(), equalTo(new BigDecimal("10")));
		assertThat(REVERSE_POLISH_NOTATION.apply("552*+").get(), equalTo(new BigDecimal("15")));
		assertThat(REVERSE_POLISH_NOTATION.apply("55*2*").get(), equalTo(new BigDecimal("50")));
		assertThat(REVERSE_POLISH_NOTATION.apply("55*2+").get(), equalTo(new BigDecimal("27")));
		assertThat(REVERSE_POLISH_NOTATION.apply("552+*").get(), equalTo(new BigDecimal("35")));
		assertThat(REVERSE_POLISH_NOTATION.apply("42-").get(), equalTo(new BigDecimal("2")));
		assertThat(REVERSE_POLISH_NOTATION.apply("542-/").get(), equalTo(new BigDecimal("2.5")));
		assertThat(REVERSE_POLISH_NOTATION.apply("552*/").get(), equalTo(new BigDecimal("0.5")));
		assertThat(REVERSE_POLISH_NOTATION.apply("342*15-/+").get(), equalTo(new BigDecimal("1")));

		assertThat(REVERSE_POLISH_NOTATION.apply("5+"), equalTo(Optional.empty()));
		assertThat(REVERSE_POLISH_NOTATION.apply("55++"), equalTo(Optional.empty()));
	}
}
