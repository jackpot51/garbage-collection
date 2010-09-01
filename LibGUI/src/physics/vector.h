typedef struct {
	long double x;
	long double y;
	long double z;
} Vector;

Vector VectorAdd(Vector a, Vector b){
	Vector ret = {a.x+b.x, a.y+b.y, a.z+b.z};
	return ret;
}

Vector VectorSub(Vector a, Vector b){
	Vector ret = {a.x-b.x, a.y-b.y, a.z-b.z};
	return ret;
}

Vector VectorMul(Vector a, long double b){
	Vector ret = {a.x*b, a.y*b, a.z*b};
	return ret;
}

Vector VectorDiv(Vector a, long double b){
	Vector ret = {a.x/b, a.y/b, a.z/b};
	return ret;
}

long double VectorDot(Vector a, Vector b){
	return a.x*b.x+a.y*b.y+a.z*b.z;
}

Vector VectorCross(Vector a, Vector b){
	Vector ret = {a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x};
	return ret;
}

long double VectorMag(Vector a){
	return sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
}

long double VectorMag2(Vector a){
	return a.x*a.x+a.y*a.y+a.z*a.z;
}

Vector VectorNorm(Vector a){
	long double mag = VectorMag(a);
	Vector ret = {a.x/mag, a.y/mag, a.z/mag};
	return ret;
}
