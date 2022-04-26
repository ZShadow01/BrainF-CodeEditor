import sys


class Interpreter:
    MEM_SIZE = 30000
    
    def __init__(self):
        self.memory = [0] * self.MEM_SIZE
        self.pointer = 0

    def reset_memory(self):
        for i in range(self.MEM_SIZE):
            self.memory[i] = 0
        self.pointer = 0

    def read_and_execute(self, filename: str):
        with open(filename, 'r') as f:
            code = f.read().replace('\n', '')
            self.execute(code)

    @staticmethod
    def __write_error(self, err: str):
        sys.stderr.write(err + "\n")
        sys.stderr.flush()

    def execute(self, code: str):
        self.reset_memory()
        i = 0
        while i < len(code):
            if code[i] == '+':
                self.memory[self.pointer] += 1
            elif code[i] == '-':
                if self.memory[self.pointer] - 1 < 0:
                    self.__write_error("Value can not be negative")
                    return
                self.memory[self.pointer] -= 1
            elif code[i] == '>':
                if self.pointer + 1 >= self.MEM_SIZE:
                    self.__write_error("Pointer reached maximum")
                    return
                self.pointer += 1
            elif code[i] == '<':
                if self.pointer - 1 < 0:
                    self.__write_error("Pointer can not be negative")
                    return
                self.pointer -= 1
            elif code[i] == '.':
                print(chr(self.memory[self.pointer]), end='')
            elif code[i] == ',':
                self.memory[self.pointer] = ord(input()[0])
            elif code[i] == '[':
                if self.memory[self.pointer] == 0:
                    brackets = 0
                    while True:
                        if code[i] == '[':
                            brackets += 1
                        elif code[i] == ']':
                            if brackets > 0:
                                brackets -= 1
                            else:
                                break
                        i += 1
            elif code[i] == ']':
                if self.memory[self.pointer] > 0:
                    brackets = 0
                    while True:
                        if code[i] == ']':
                            brackets += 1
                        elif code[i] == '[':
                            if brackets > 0:
                                brackets -= 1
                            else:
                                break

                        i -= 1

            i += 1


def main():
    interpreter = Interpreter()
    interpreter.read_and_execute('main.bf')


if __name__ == '__main__':
    main()
