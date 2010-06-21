void drawline3D(double ax, double ay, double az, //first 3d point
		double bx, double by, double bz, //second 3d point
		double cx, double cy, double cz, //camera location
		double tx, double ty, double tz, //camera rotation, radians
		double ex, double ey, double ez, //viewer location
		int color){
	double dx = cos(ty)*(sin(tz)*(ay-cy)+cos(tz)*(ax-cx))-sin(ty)*(az-cz);
	double dy = sin(tx)*(cos(ty)*(az-cz)+sin(ty)*(sin(tz)*(ay-cy)+cos(tz)*(ax-cx)))+cos(tx)*(cos(tz)*(ay-cy)-sin(tz)*(ax-cx));
	double dz = cos(tx)*(cos(ty)*(az-cz)+sin(ty)*(sin(tz)*(ay-cy)+cos(tz)*(ax-cx)))-sin(tx)*(cos(tz)*(ay-cy)-sin(tz)*(ax-cx));
	if(ez/dz < 0) return;
	double px = (dx-ex)*(ez/dz)*screen.x/2 + screen.x/2;
	double py = (dy-ey)*(ez/dz)*screen.y/2 + screen.y/2;

	if(px < 0) return;//px=0;
	if(px > screen.x) return;//px=screen.x-1;
	if(py < 0) return;//py=0;
	if(py > screen.y) return;//py=screen.y-1;

	dx = cos(ty)*(sin(tz)*(by-cy)+cos(tz)*(bx-cx))-sin(ty)*(bz-cz);
	dy = sin(tx)*(cos(ty)*(bz-cz)+sin(ty)*(sin(tz)*(by-cy)+cos(tz)*(bx-cx)))+cos(tx)*(cos(tz)*(by-cy)-sin(tz)*(bx-cx));
	dz = cos(tx)*(cos(ty)*(bz-cz)+sin(ty)*(sin(tz)*(by-cy)+cos(tz)*(bx-cx)))-sin(tx)*(cos(tz)*(by-cy)-sin(tz)*(bx-cx));
	if(ez/dz < 0) return;
	double qx = (dx-ex)*(ez/dz)*screen.x/2 + screen.x/2;
	double qy = (dy-ey)*(ez/dz)*screen.y/2 + screen.y/2;

	if(qx < 0) return;//qx=0;
	if(qx > screen.x) return;//qx=screen.x-1;
	if(qy < 0) return;//qy=0;
	if(qy > screen.y) return;//qy=screen.y-1;

	drawline((int)px, (int)py, (int)qx, (int)qy, color);
} 

typedef struct{
    double ax;
    double ay;
    double az;
    
    double bx;
    double by;
    double bz;
} Line;

typedef struct{
    double x;
    double y;
    double z;
} Vertex;

void drawLineCam(Line l, Camera c, int color){
    drawline3D(l.ax, l.ay, l.az,
	       l.bx, l.by, l.bz,
	       c.cx, c.cy, c.cz,
	       c.ox, c.oy, c.oz,
	       c.ex, c.ey, c.ez,
	       color);
}

Line lineVertex(Vertex a, Vertex b){
    Line l;
    l.ax = a.x;
    l.ay = a.y;
    l.az = a.z;
    
    l.bx = b.x;
    l.by = b.y;
    l.bz = b.z;
    return l;
}

void drawLines(Line lines[], int length, int color, Camera cam){
	int i;
	for(i = 0; i < length; i++){
	    drawLineCam(lines[i], cam, color);
	}
}
