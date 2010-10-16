#include "object.h"
#include "collision.h"

Vector gravity = {0,-9.8E-1,0};
FPN physts = 1.0/100.0;
char physrunning = 1;

void * physthread(void *atlist){
	list *l = (list *)atlist;
	Object *objs = (Object *)l->mem;
	int objlen = l->len;
	FPN sts = physts;
	while(physrunning){
		FPN rts = -currentfpntime();
		int i;
		for(i=0; i<objlen; i++){
			int j;
			for(j=0; j<i; j++){
				Collision col = ProcessCollision(&objs[i], &objs[j]);
			}
			objs[i].p = VectorAdd(objs[i].p, VectorMul(objs[i].v, physts));
			objs[i].v = VectorAdd(objs[i].v, VectorMul(gravity, physts));
			if((objs[i].p.y - objs[i].r)<-1){
				if(physts > 0) objs[i].v.y = M(fabs, objs[i].v.y);
				else objs[i].v.y = -M(fabs, objs[i].v.y);
			}
		}
		rts += currentfpntime();
		if(sts>rts){
			usleep((int)((sts-rts)*1000000.0));
		}
	}
	return NULL;
}