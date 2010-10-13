
typedef struct {
	Object **objects; //objects list
	Object center; //center of mass object
	int len;
} Region;


void addObject(Region *reg, Object obj){
	int old = reg->len++;
	reg->objects = (Object **)realloc(reg->objects, reg->len*sizeof(Object*));
	reg->objects[old] = (Object *)malloc(sizeof(Object));
	*(reg->objects[old]) = obj;
}