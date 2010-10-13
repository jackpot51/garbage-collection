#include <math.h>
#include <stdarg.h>
#include <stdio.h>
#include <time.h>
typedef long double quadruple;
#include "arithmetic.h"
#include "disk.h"
//#include "graphics.h"
#include "memory.h"
#include "processor.h"
int level = 0;

clock_t benchmark(void (*func)()){
	clock_t st = clock();
	(*func)();
	return clock() - st;
}

void printlevel(int level, char *fmt, ...){
	va_list va;
	va_start(va, fmt);
	int i;
	for(i = 0; i < level; i++) printf("  "); 
	vprintf(fmt, va);
	printf("\n");
	va_end(va);
}

void printbench(char *name, void (*func)()){
	printlevel(level, "%s:\t%ld", name, (long)benchmark(func));
}

#define Level(NAME) int NAME ## _previous = level++;\
					for(printlevel(NAME ## _previous, #NAME);level!=(NAME ## _previous);level--)


#define MATHTEST(NAME)\
	printbench("Integer", &NAME ## _int);\
	printbench("Long", &NAME ## _long);\
	printbench("Float", &NAME ## _float);\
	printbench("Double", &NAME ## _double);\
	printbench("Quadruple", &NAME ## _quadruple);

int main(int argc, char **argv){
	Level(Arithmetic){
		Level(Add){
			MATHTEST(add)
		}
	
		Level(Subtract){
			MATHTEST(sub)
		}
	
		Level(Multiply){
			MATHTEST(mul)
		}
	
		Level(Divide){
			MATHTEST(div)
		}
		Level(Modulus){
			printbench("Integer", &mod_int);
			printbench("Long", &mod_long);
		}
	}
	/*
	Level(Graphics){
		printbench("Clear", &guiClear);
		printbench("Lines", &guiLines);
		printbench("Text", &guiText);
		printbench("Circles", &guiCircles);
		printbench("Rectangles", &guiRectangles);
	}
	*/
	return 0;
}