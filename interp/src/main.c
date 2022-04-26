#include <stdio.h>
#include <malloc.h>

#define MEM_SIZE 30000
char memory[MEM_SIZE] = { 0 };
int pointer = 0;

int is_operator(char ch);
char* readfile(const char* filename);
int execute(const char* code);

int main(int argc, char* argv[]) {
    if (argc == 1) {
        fprintf(stderr, "Insufficient arguments: Missing file path\n");
        return -1;
    }

    char* code = readfile(argv[1]);
    if (code == NULL) {
        fprintf(stderr, "Failed while trying to read file %s\n", argv[1]);
        return -1;
    }
    int res = execute(code);
    free(code);

    fflush(stdin);

    printf("\n\nPress enter to exit program...");
    getchar();

    return res;
}

int is_operator(char ch) {
    return ch == '+' || ch == '-' || ch == '[' || ch == ']' || ch == '.' || ch == ',' || ch == '<' || ch == '>';
}

char* readfile(const char* filename) {
    FILE* file;
#ifdef _WIN32
    fopen_s(&file, filename, "r");
#else
    file = fopen(filename, "r");
#endif

    if (!file) {
        fprintf(stderr, "Could not open file %s\n", filename);
        return NULL;
    }

    int buffer = 16, current_buffer = buffer, index = 0;
    char* dest = (char*) malloc(sizeof(char) * current_buffer);

    if (dest == NULL) {
        fprintf(stderr, "Failed to allocate memory\n");
        return NULL;
    }

    int ch;
    while ((ch = fgetc(file)) != EOF) {
        if (!is_operator((char) ch)) {
            continue;
        }

        if (index + 1 >= current_buffer) {
            current_buffer += buffer;
            char* tmp = (char*) realloc(dest, current_buffer);
            if (tmp == NULL) {
                free(dest);
                fprintf(stderr, "Failed to reallocate memory\n");
                return NULL;
            }

            dest = tmp;
        }

        dest[index++] = (char) ch;
    }
    dest[index] = '\0';

    fclose(file);
    return dest;
}

int execute(const char* code) {
    while (*code != '\0') {
        switch (*code) {
            case '+':
                memory[pointer]++;
                break;
            case '-':
                if (memory[pointer] - 1 < 0) {
                    fprintf(stderr, "Value can not be negative\n");
                    return -1;
                }
                memory[pointer]--;
                break;
            case '<':
                if (pointer - 1 < 0) {
                    fprintf(stderr, "Pointer can not be negative\n");
                    return -1;
                }
                pointer--;
                break;
            case '>':
                if (pointer + 1 >= MEM_SIZE) {
                    fprintf(stderr, "Pointer reached maximum\n");
                    return -1;
                }
                pointer++;
                break;
            case ',':
                scanf("%c", &memory[pointer]);
                break;
            case '.':
                printf("%c", memory[pointer]);
                break;
            case '[':
                if (memory[pointer] == 0) {
                    int brackets = 0;
                    while (1) {
                        if (*code == '[') {
                            brackets++;
                        } else if (*code == ']') {
                            if (brackets > 0) {
                                brackets--;
                            } else {
                                break;
                            }
                        }
                        code++;
                    }
                }
                break;
            case ']':
                if (memory[pointer] > 0) {
                    int brackets = 0;
                    while (1) {
                        if (*code == ']') {
                            brackets++;
                        } else if (*code == '[') {
                            if (brackets > 0) {
                                brackets--;
                            } else {
                                break;
                            }
                        }
                        code--;
                    }
                }
                break;
        }

        code++;
    }

    return 0;
}

