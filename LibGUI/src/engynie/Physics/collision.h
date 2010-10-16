//conservation of momentum always, kinetic energy based on elasticity
//a.mx + a.my + a.mz + b.mx + b.my + b.mz must be constant
//a.e + b.e must be constant

typedef struct{
	Vector p;
	FPN d;
	Vector n;
	FPN jr;
} Collision;

//returns collision data
Collision ProcessCollision(Object *a, Object *b){
	Collision ret;
	ret.p = VectorSub(b->p, a->p);
	ret.d = VectorMag(ret.p);
	ret.n = VectorDiv(ret.p,ret.d);
	if(ret.d <= (a->r + b->r)){
		Vector vr = VectorSub(b->v, a->v);
		ret.jr = -2*VectorDot(vr, ret.n)/(1/a->m+1/b->m);
		a->v = VectorAdd(a->v, VectorMul(ret.n, -ret.jr/a->m));
		b->v = VectorAdd(b->v, VectorMul(ret.n, ret.jr/b->m));
	}else{
		ret.jr = 0;
	}
	return ret;
}
