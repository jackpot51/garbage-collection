//conservation of momentum always, kinetic energy based on elasticity
//a.mx + a.my + a.mz + b.mx + b.my + b.mz must be constant
//a.e + b.e must be constant

typedef struct{
	Vector p;
	FPN d;
	Vector n;
	FPN jr;
} Collision;

//returns distance
Collision ProcessCollision(object *a, object *b){
	Vector p = VectorSub(b->p, a->p);
	FPN d = VectorMag(p);
	Vector n = VectorNorm(p);
	Collision ret;
	ret.p = p;
	ret.d = d;
	ret.n = n;
	if(d <= (a->r + b->r)){
		Vector vr = VectorSub(b->v, a->v);
		FPN cor = 1; //elastic
		FPN jr = -(1+cor)*VectorDot(vr, n)/(1/a->m+1/b->m);
		ret.jr = jr;
		a->v = VectorSub(a->v, VectorMul(VectorDiv(n, a->m), jr));
		b->v = VectorAdd(b->v, VectorMul(VectorDiv(n, b->m), jr));
	}else{
		ret.jr = 0;
	}
	return ret;
}
