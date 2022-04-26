import java.io.*;


public class Interpreter {
    private final int memSize = 30000;
    private final char[] memory;
    private int pointer;

    public Interpreter() {
        this.memory = new char[this.memSize];
        this.pointer = 0;
    }

    private void resetMemory() {
        for (int i = 0; i < this.memSize; i++) {
            memory[i] = 0;
        }
    }

    public void readAndExecute(String fileName) throws IOException {
        File file = new File(fileName);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        this.run(builder.toString());
    }

    public void run(String code) {
        this.resetMemory();
        InputStreamReader reader = new InputStreamReader(System.in);

        for (int i = 0; i < code.length(); i++) {
            switch (code.charAt(i)) {
                case '+':
                    memory[pointer]++;
                    break;
                case '-':
                    if (memory[pointer] - 1 < 0) {
                        System.err.println("Value can not be negative");
                        return;
                    }
                    memory[pointer]--;
                    break;
                case '<':
                    if (pointer - 1 < 0) {
                        System.err.println("Pointer can not be negative\n");
                        return;
                    }
                    pointer--;
                    break;
                case '>':
                    if (pointer + 1 >= this.memSize) {
                        System.err.println("Pointer reached maximum\n");
                        return;
                    }
                    pointer++;
                    break;
                case '.':
                    System.out.print(memory[pointer]);
                    break;
                case ',':
                    try {
                        memory[pointer] = (char) reader.read();
                    } catch (IOException e) {
                        System.err.println("Error reading char");
                        e.printStackTrace();
                    }
                    break;
                case '[':
                    if (memory[pointer] == 0) {
                        int brackets = 0;
                        while (true) {
                            char ch = code.charAt(i);
                            if (ch == '[') {
                                brackets++;
                            } else if (ch == ']') {
                                if (brackets > 0) {
                                    brackets--;
                                } else {
                                    break;
                                }
                            }
                            i++;
                        }
                    }
                    break;
                case ']':
                    if (memory[pointer] > 0) {
                        int brackets = 0;
                        while (true) {
                            char ch = code.charAt(i);
                            if (ch == ']') {
                                brackets++;
                            } else if (ch == '[') {
                                if (brackets > 0) {
                                    brackets--;
                                } else {
                                    break;
                                }
                            }
                            i--;
                        }
                    }
                    break;
            }
        }
        System.out.println();
    }
}
