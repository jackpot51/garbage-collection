#include "LibGUI/view.h"

Vector cam = {0,0,4};
Vector rot = {0,0,0};
FPN drawts = 1.0/60.0;
char drawrunning = 1;

void drawer(Object *objs, int objlen){
	FPN rts = -currentfpntime();
	View v = makeView(objs, objlen, cam, GetTransform(rot), screen);
	clear(0);
	drawView(&v);
	update();
	freeView(&v);
	rts += currentfpntime();
	if(drawts>rts){
		usleep((int)((drawts-rts)*1000000.0));
	}
}

void * drawthread(void *atlist){
	list *l = (list *)atlist;
	Object *objs = (Object *)l->mem;
	int objlen = l->len;
	while(drawrunning){
		drawer(objs, objlen);
	}
	return NULL;
}
