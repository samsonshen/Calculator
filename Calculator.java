import java.math.BigDecimal;
import java.util.Stack;
public class Calculator {
    // 操作数栈，用于存储输入的数字
    private Stack<BigDecimal> operands = new Stack<>();
    // 操作符栈，用于存储输入的运算符
    private Stack<String> operators = new Stack<>();
    // undo 操作的操作数栈，用于存储需要恢复的操作数
    private Stack<BigDecimal> undoOperands = new Stack<>();
    // undo 操作的操作符栈，用于存储需要恢复的操作符
    private Stack<String> undoOperators = new Stack<>();

    // 获取计算结果，如果操作数栈为空，则返回 0
    public BigDecimal getResult() {
        if (operands.empty()) {
            return BigDecimal.ZERO;
        }
        return operands.peek();
    }

    // 对两个操作数进行指定的运算，并将结果压入操作数栈
    // 如果指定的运算符不支持，抛出 IllegalArgumentException 异常
    // 如果除法运算的除数为 0，抛出 ArithmeticException 异常
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
            // 除法运算保留两位小数，使用 ROUND_HALF_UP 模式进行四舍五入
            operands.push(operand1.divide(operand2, 2, BigDecimal.ROUND_HALF_UP));
        } else {
            throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
        // 将运算符压入操作符栈
        operators.push(operator);
        // 每次进行新的计算操作时，清空 undo 操作栈
        undoOperands.clear();
        undoOperators.clear();
    }

    // 恢复上一次进行的操作
    public void undo() {
        if (!operators.empty()) {
            // 将最后一次操作的操作符和操作数分别压入 undo 操作栈
            undoOperators.push(operators.pop());
            undoOperands.push(operands.pop());
        }
        if (!operators.empty()) {
            // 如果 undo 操作栈中有操作，则判断最后一次进行的操作是否和当前操作一致
            String operator = operators.peek();
            if (!undoOperators.empty() && operator.equals(undoOperators.peek())) {
                operands.push(undoOperands.pop());
                operators.push(undoOperators.pop());
            }
        }
    }

    // 恢复上一次撤销的操作
    public void redo() {
        if (!undoOperators.empty()) {
            // 从 undo 操作栈中取出最后一次撤销的操作符和操作数，并将它们压入操作符栈和操作数栈
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
