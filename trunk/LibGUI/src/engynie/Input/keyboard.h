FPN keyts = 1.0/100.0;
FPN movespeed = 10;
FPN rotspeed = M_PI;
char keyboardrunning = 1;

void moveCam(double x, double y, double z, Transform trans){
	cam.z += z*trans.cy + x*trans.sy;
	cam.x -= x*trans.cy - z*trans.sy;
	cam.y += y;
}


void keyboard(){
	FPN rts = -currentfpntime();
	updatekeymap();
	Transform trans = GetTransform(rot);
	FPN movedist = movespeed*keyts;
	if(checkkey(KEYW)) moveCam(0,0,-movedist,trans);
	if(checkkey(KEYA)) moveCam(movedist,0,0,trans);
	if(checkkey(KEYS)) moveCam(0,0,movedist,trans);
	if(checkkey(KEYD)) moveCam(-movedist,0,0,trans);
	if(checkkey(KEYX)) moveCam(0,-movedist,0,trans);
	if(checkkey(KEYZ)) moveCam(0,movedist,0,trans);
	FPN rotdist = rotspeed*keyts;
	if(checkkey(KEYI)) rot.x += rotdist;
	if(checkkey(KEYK)) rot.x -= rotdist;
	if(checkkey(KEYJ)) rot.y += rotdist;
	if(checkkey(KEYL)) rot.y -= rotdist;
	if(checkkey(KEYC)){
		rot.x = 0;
		rot.y = 0;
		rot.z = 0;
		cam.x = 0;
		cam.y = 0;
		cam.z = 4.0;
	}
	if(checkkey(KEYT)==1) physts = -physts;
	if(checkkey(KEYQ)) keyboardrunning = 0;
	rts += currentfpntime();
	if(keyts>rts){
		usleep((int)((keyts-rts)*1000000.0));
	}
}

void * keyboardthread(void *atlist){
	list *l = (list *)atlist;
	Object *objs = (Object *)l->mem;
	int objlen = l->len;
	while(keyboardrunning){
		if(drawrunning) drawer(objs, objlen);
		keyboard();
	}
	return NULL;
}