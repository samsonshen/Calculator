import java.math.BigDecimal;
import java.util.Stack;
public class Calculator {
    private Stack<BigDecimal> operands = new Stack<>();
    private Stack<String> operators = new Stack<>();
    private Stack<BigDecimal> undoOperands = new Stack<>();
    private Stack<String> undoOperators = new Stack<>();

    public BigDecimal getResult() {
        if (operands.empty()) {
            return BigDecimal.ZERO;
        }
        return operands.peek();
    }

    public void calculate(String operator, BigDecimal operand1, BigDecimal operand2) {
        if (operator.equals("+")) {
            operands.push(operand1.add(operand2));
        } else if (operator.equals("-")) {
            operands.push(operand1.subtract(operand2));
        } else if (operator.equals("*")) {
            operands.push(operand1.multiply(operand2));
        } else if (operator.equals("/")) {
            if (operand2.equals(BigDecimal.ZERO)) {
                throw new ArithmeticException("Division by zero!");
            }
            operands.push(operand1.divide(operand2, 2, BigDecimal.ROUND_HALF_UP));
        } else {
            throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
        operators.push(operator);
        undoOperands.clear();
        undoOperators.clear();
    }

    public void undo() {
        if (!operators.empty()) {
            undoOperators.push(operators.pop());
            undoOperands.push(operands.pop());
        }
        if (!operators.empty()) {
            String operator = operators.peek();
            if (!undoOperators.empty() && operator.equals(undoOperators.peek())) {
                operands.push(undoOperands.pop());
                operators.push(undoOperators.pop());
            }
        }
    }

    public void redo() {
        if (!undoOperators.empty()) {
            String operator = undoOperators.pop();
            BigDecimal operand = undoOperands.pop();
            operands.push(operand);
            operators.push(operator);
        }
    }

    public static void main(String[] args) {
        // 测试一次性输入两个计算参数
        Calculator calculator = new Calculator();
        calculator.calculate("+", new BigDecimal("1"), new BigDecimal("2"));
        System.out.println(calculator.getResult()); // 应该输出 3
        calculator.calculate("-", new BigDecimal("5"), new BigDecimal("3"));
        System.out.println(calculator.getResult()); // 应该输出 2.00
        calculator.calculate("*", new BigDecimal("6"), new BigDecimal("4"));
        System.out.println(calculator.getResult()); // 应该输出 24
        calculator.calculate("/", new BigDecimal("7"), new BigDecimal("2"));
        System.out.println(calculator.getResult()); // 应该输出 3.50

        // 测试 undo 和 redo
        calculator.undo();
        System.out.println(calculator.getResult()); // 应该输出 24
        calculator.redo();
        System.out.println(calculator.getResult()); // 应该输出 3.50

        // 测试除零的情况
        try {
            calculator.calculate("/", new BigDecimal("1"), new BigDecimal("0"));
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage()); // 应该输出 Division by zero!
        }

        // 测试不支持的运算符的情况
        try {
            calculator.calculate("%", new BigDecimal("1"), new BigDecimal("2"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // 应该输出 Unsupported operator: %
        }
    }
}
