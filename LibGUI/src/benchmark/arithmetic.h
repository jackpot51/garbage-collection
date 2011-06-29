//This should include functions that benchmark the performance of
//floats, doubles, long doubles, ints, and long ints when used
//with standard operators.

#define MAX_I 1000000

#define MATHBENCH(NAME,OPERATOR,TYPE)\
	void  NAME ## _ ## TYPE(){\
		TYPE a = 1;\
		TYPE b = 1;\
		TYPE c;\
		int i;\
		for(i=0; i<MAX_I; i++)\
			c = a OPERATOR b;\
	}


#define MATHOP(NAME,OPERATOR)\
	MATHBENCH(NAME,OPERATOR,int)\
	MATHBENCH(NAME,OPERATOR,long)\
	MATHBENCH(NAME,OPERATOR,float)\
	MATHBENCH(NAME,OPERATOR,double)\
	MATHBENCH(NAME,OPERATOR,quadruple)

MATHOP(add, +)
MATHOP(sub, -)
MATHOP(mul, *)
MATHOP(div, /)

MATHBENCH(mod, %, int)
MATHBENCH(mod, %, long)