#include <libgui.h>
#include <math.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>
screeninfo screen;
#include "Common/precision.h"
FPN RR(FPN min, FPN max){
	return R(max-min)+min;
}
#include "Common/list.h"
#include "Common/timing.h"
#include "Common/vector.h"
#include "Physics/physics.h"
#include "Graphics/graphics.h"
#include "Input/keyboard.h"

int main(int argc, char **argv){
	int objlen;
	if(argc>1) objlen = atoi(argv[1]);
	if(objlen < 1) objlen = 20;
	getinfo(&screen);
	Object *objs = (Object *)calloc(objlen, sizeof(Object));
	int i;
	for(i=0; i<objlen; i++){
		Object obj = {0};
	remakeobj:
		obj.p.x = RR(-1,1);
		obj.p.y = RR(-0.75,1);
		obj.p.z = RR(-1,1);
		obj.r = RR(1.0/100.0,1.0/20.0);
		obj.m = obj.r;
		obj.c = (int)RR(0x010101,0xFFFFFF);
		int j;
		for(j=0; j<i; j++){
			if(VectorMag(VectorSub(objs[j].p, obj.p)) <= (objs[j].r + obj.r))
				goto remakeobj;
		}
		objs[i] = obj;
	}
	list objects = {objs, objlen};
	pthread_t threads[2];
	//pthread_create(&threads[0], NULL, drawthread, (void *)&objects);
	pthread_create(&threads[1], NULL, physthread, (void *)&objects);
	keyboardthread((void *)&objects);
	//keyboardthread(); //this thread returns when the game quits as is
	return 0;
}