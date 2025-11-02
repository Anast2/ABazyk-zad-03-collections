package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public interface Brainfuck {
    void execute();

    static Brainfuck createInstance(String program) {
        return createInstance(program, System.out, System.in, 1024);
    }

    static Brainfuck createInstance(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || program.isEmpty() || out == null || in == null || stackSize < 1) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        return new BrainfuckInterpreter(program, out, in, stackSize);
    }
}

class BrainfuckInterpreter implements Brainfuck {
    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final int stackSize;

    public BrainfuckInterpreter(String program, PrintStream out, InputStream in, int stackSize) {
        this.program = program;
        this.out = out;
        this.in = in;
        this.stackSize = stackSize;
    }

    @Override
    public void execute() {
        byte[] memory = new byte[stackSize];
        int pointer = 0;
        int pc = 0;
        Map<Integer, Integer> bracketMap = loopsMap(program);
        char[] commands = program.toCharArray();

        while (pc < commands.length) {
            switch (commands[pc]) {
                case '>':
                    pointer++;
                    if (pointer >= memory.length)
                        throw new IllegalStateException("Pointer out of bounds (right)");
                    break;
                case '<':
                    pointer--;
                    if (pointer < 0)
                        throw new IllegalStateException("Pointer out of bounds (left)");
                    break;
                case '+':
                    memory[pointer]++;
                    break;
                case '-':
                    memory[pointer]--;
                    break;
                case '.':
                    out.print((char) (memory[pointer] & 0xFF));
                    break;
                case ',':
                    try {
                        int val = in.read();
                        if (val != -1) {
                            memory[pointer] = (byte) val;
                        }
                    } catch (IOException e) {
                        memory[pointer] = 0;
                    }
                    break;
                case '[':
                    if (memory[pointer] == 0) {
                        pc = bracketMap.get(pc);
                    }
                    break;
                case ']':
                    if (memory[pointer] != 0) {
                        pc = bracketMap.get(pc);
                    }
                    break;
            }
            pc++;
        }
    }

    private Map<Integer, Integer> loopsMap(String program) {
        Map<Integer, Integer> map = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < program.length(); i++) {
            char c = program.charAt(i);
            if (c == '[') {
                stack.push(i);
            } else if (c == ']') {
                if (stack.isEmpty())
                    throw new IllegalStateException("Unmatched ] at " + i);
                int open = stack.pop();
                map.put(open, i);
                map.put(i, open);
            }
        }
        if (!stack.isEmpty())
            throw new IllegalStateException("Unmatched [ at " + stack.peek());
        return map;
    }
}
