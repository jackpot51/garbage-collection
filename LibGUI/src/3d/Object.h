typedef struct{
	double m; //mass
	double h; //height
	double w; //width
	double l; //length

	double x; //location x
	double y; //location y
	double z; //location z
	double vx; //velocity x
	double vy; //velocity y
	double vz; //velocity z
	double fx; //force x
	double fy; //force y
	double fz; //force z

	double ox; //orientation x
	double oy; //orientation y
	double oz; //orientation z
	double wx; //angular speed x
	double wy; //angular speed y
	double wz; //angular speed z
	double tx; //torque x
	double ty; //torque y
	double tz; //torque z
} Object;

void processCollision(Object *a, Object *b);

void updateObject(Object *o){
	o->vx += o->fx/o->m;
	o->vy += o->fy/o->m;
	o->vz += o->fz/o->m;

	o->x += o->vx;
	o->y += o->vy;
	o->z += o->vz;

	o->wx += o->tx/o->m;
	o->wy += o->ty/o->m;
	o->wz += o->tz/o->m;

	o->ox += o->wx;
	o->oy += o->wy;
	o->oz += o->wz;
}
