typedef struct{
	double x;
	double y;
	double zs; //z multiplier, can be used for scaling
} Point;

typedef struct{
    double x;
    double y;
    double z;
} Vertex;

Point invalidpoint = {-1, -1};

Point convertPoint(double ax, double ay, double az, //point
							double cx, double cy, double cz, //camera location
							double ox, double oy, double oz, //camera rotation, radians
							double ex, double ey, double ez){ //viewer location
		double dz = cos(ox)*(cos(oy)*(az-cz)+sin(oy)*(sin(oz)*(ay-cy)+cos(oz)*(ax-cx)))-sin(ox)*(cos(oz)*(ay-cy)-sin(oz)*(ax-cx));
		if(ez/dz < 0) return invalidpoint;
		double dx = cos(oy)*(sin(oz)*(ay-cy)+cos(oz)*(ax-cx))-sin(oy)*(az-cz);
		double dy = -(sin(ox)*(cos(oy)*(az-cz)+sin(oy)*(sin(oz)*(ay-cy)+cos(oz)*(ax-cx)))+cos(ox)*(cos(oz)*(ay-cy)-sin(oz)*(ax-cx)));
		Point p;
		p.zs = ez/dz*screen.y/2;
		p.x = (dx-ex)*p.zs + screen.y/2;
		p.y = (dy-ey)*p.zs + screen.y/2;
		return p;
}

Point convertPointCamera(double x, double y, double z, Camera c){
	return convertPoint(x, y, z, c.cx, c.cy, c.cz, c.ox, c.oy, c.oz, c.ex, c.ey, c.ez);
}

Point convertVertexCamera(Vertex v, Camera c){
	return convertPointCamera(v.x, v.y, v.z, c);
}