package org.t4atf.functionalme;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Calculator {
	
	public enum OperatorAssociativity {
		LEFT, RIGHT;
	}
	
	public enum Operator {
		MUL("*", 2, OperatorAssociativity.LEFT),
		DIV("/", 2, OperatorAssociativity.LEFT),
		PLUS("+", 1, OperatorAssociativity.LEFT),
		MINUS("-", 1, OperatorAssociativity.LEFT);
		
		private String operator;
		private int precedence;
		private OperatorAssociativity associativity;

		private Operator(String operator, int precedence, OperatorAssociativity associativity) {
			this.operator = operator;
			this.precedence = precedence;
			this.associativity = associativity;
		}
		
		public String getOperator() {
			return operator;
		}
		
		public int getPrecedence() {
			return precedence;
		}
		
		public static Operator from(String operator) {
			Optional<Operator> eventual = optional(operator);
			if(eventual.isPresent()) return eventual.get();
			throw new IllegalArgumentException("Unknown operator " + operator);
		}
		
		public static boolean isOperator(String operator) {
			return optional(operator).isPresent();
		}

		private static Optional<Operator> optional(String operator) {
			return Arrays.asList(values()).parallelStream().filter(op -> op.operator.equals(operator)).findFirst();
		}

		public boolean isLeftAssociative() {
			return associativity.equals(OperatorAssociativity.LEFT);
		}

		public boolean precedesOrEquals(Operator other) {
			return precedence <= other.precedence;
		}

	}
	
	public static final Function<String, Optional<BigDecimal>> REVERSE_POLISH_NOTATION = new Function<String, Optional<BigDecimal>>() {
		
		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> multiplier = (multiplicand, multiplier) -> multiplicand.multiply(multiplier);
		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> divider = (dividend, divisor) -> dividend.divide(divisor);
		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> adder = (augend, addend) -> augend.add(addend);
		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> substractor = (minued, subtrahend) -> minued.subtract(subtrahend);

		@Override
		public Optional<BigDecimal> apply(String input) {
			try {
				return tryCalculation(input);
			} catch (EmptyStackException ese) {
				return Optional.empty();
			}
		}

		private Optional<BigDecimal> tryCalculation(String input) {
			Stack<BigDecimal> partial = TOKENIZER.apply(input).stream()
					.collect(
							Stack::new, 
							(Stack<BigDecimal> stack, String next) ->	addTo(stack, next), 
							(Stack<BigDecimal> s1, Stack<BigDecimal> s2) ->	s1.addAll(s2)
					);
			return Optional.of(partial.pop());
		}

		private void addTo(Stack<BigDecimal> partial, String token) {
			if(Character.isDigit(token.toCharArray()[0])) {
				partial.add(new BigDecimal(token));
			} else {
				Operator op = Operator.from(token);
				if(Operator.MUL.equals(op)) {
					perform(partial, multiplier);
				} else if(Operator.DIV.equals(op)) {
					reversePerform(partial, divider);
				} else if(Operator.PLUS.equals(op)) {
					perform(partial, adder);
				} else {
					reversePerform(partial, substractor);
				}
			}
		}

		private void reversePerform(Stack<BigDecimal> partial, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
			BigDecimal second = partial.pop();
			BigDecimal first = partial.pop();
			partial.add(operation.apply(first, second));
		}

		private void perform(Stack<BigDecimal> partial, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
			BigDecimal first = partial.pop();
			BigDecimal second = partial.pop();
			partial.add(operation.apply(first, second));
		}
	};
	
	public static final Function<String, String> SHUNTING_YARD_TOKENIZER = new Function<String, String>() {
		@Override
		public String apply(String input) {
			Stack<String> output = new Stack<>();
			LinkedList<String> operators = new LinkedList<>();
			List<String> tokens = TOKENIZER.apply(input);
			for(String c : tokens) {
				if(Character.isDigit(c.toCharArray()[0])) {
					output.add(c);
				} else if(Operator.isOperator(c)){
					Operator operator = Operator.from(c);
					int i = 0;
					while(i < operators.size() && Operator.isOperator(operators.get(i)) && operator.isLeftAssociative() && operator.precedesOrEquals(Operator.from(operators.get(i)))) {
						output.add(operators.remove(i));
					}
					operators.add(i, operator.getOperator());
				} else if(isLeftParenthesis(c)) {
					operators.add(0, c);
				} else if(isRightParenthesis(c)) {
					int i = 0;
					while(i < operators.size() && !isLeftParenthesis(operators.get(i))) {
						output.add(operators.remove(i));
					}
					operators.remove(i);
				}
			}
			output.addAll(operators);
			return output.stream().collect(Collectors.joining(""));
		}

		private boolean isRightParenthesis(String c) {
			return ")".equals(c);
		}

		private boolean isLeftParenthesis(String c) {
			return "(".equals(c);
		}

	};
	
	private static final Function<String, List<String>> TOKENIZER = input -> input.chars()
			.collect(
					ArrayList::new, 
					(list, elem) -> list.add(Character.toString((char) elem)), 
					(a, b) ->	a.addAll(b)
			); 

	public Optional<BigDecimal> compute(String expression) {
		if(null == expression || expression.trim().isEmpty()) return Optional.empty();
		return REVERSE_POLISH_NOTATION.apply( SHUNTING_YARD_TOKENIZER.apply(expression) );
	}
}
