package lab.networks;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Main {
    final private static int memorySize = 30000;
    private static int executionPointer = 0;
    private static byte[] memoryArray = new byte[memorySize];
    private static int counter = 0;

    public static void main(String[] args) {
        if (args.length > 0)
            try (FileInputStream fileInputStream = new FileInputStream(new File(args[0]))) {
                execute(fileInputStream);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        else
            execute(System.in);
    }

    private static void execute(@NotNull InputStream input) {
        try {
            do {
                counter++;
                char comm = (char) input.read();
                switch (comm) {
                    case '+':
                        memoryArray[executionPointer]++;
                        break;
                    case '-':
                        memoryArray[executionPointer]--;
                        break;
                    case '>':
                        executionPointer++;
                        checkPointer();
                        break;
                    case '<':
                        executionPointer--;
                        checkPointer();
                        break;
                    case '.':
                        System.out.print((char) memoryArray[executionPointer]);
                        break;
                    case ',':
                        memoryArray[executionPointer] = (byte) System.in.read();
                        counter++;
                        if (memoryArray[executionPointer] == -1)
                            throw new IOException("Syntax error. Execution aborted!");
                        break;
                    case '[':
                        byte[] loopArray = parseLoop(input);
                        while (memoryArray[executionPointer] != 0)
                            execute(new ByteArrayInputStream(loopArray));
                        break;
                    case ' ':
                    case '\r':
                    case '\n':
                        break;
                    case ']':
                    default:
                        throw new IOException("Syntax error. Execution aborted!");
                }
            }
            while (input.available() != 0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(counter);
            System.exit(1);
        }
    }

    private static byte[] parseLoop(@NotNull InputStream input) throws IOException {
        int levelCounter = 1;
        ArrayList<Byte> commands = new ArrayList<>();
        while (levelCounter > 0) {
            int command = input.read();
            if (command == -1) throw new IOException();
            else if ((char) command == '[') levelCounter++;
            else if ((char) command == ']') levelCounter--;

            if (levelCounter > 0)
                commands.add((byte) command);
        }

        byte[] array = new byte[commands.size()];
        IntStream.range(0, array.length).forEach(i -> array[i] = commands.get(i));
        return array;
    }

    private static void checkPointer() {
        if (executionPointer >= memorySize)
            executionPointer = 0;
        else if (executionPointer < 0)
            executionPointer = memorySize - 1;
    }
}
