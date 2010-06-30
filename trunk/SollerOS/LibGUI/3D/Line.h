void drawline3D(double ax, double ay, double az, //first 3d point
		double bx, double by, double bz, //second 3d point
		double cx, double cy, double cz, //camera location
		double ox, double oy, double oz, //camera rotation, radians
		double ex, double ey, double ez, //viewer location
		int color){
	Point p = convertPoint(ax, ay, az, cx, cy, cz, ox, oy, oz, ex, ey, ez);
	
	if(p.x < 0 | p.y < 0 | p.x >= screen.x | p.y >= screen.x) return;

	Point q = convertPoint(bx, by, bz, cx, cy, cz, ox, oy, oz, ex, ey, ez);

	if(q.x < 0 | q.y < 0 | q.x >= screen.x | q.y >= screen.x) return;

	drawline((int)p.x, (int)p.y, (int)q.x, (int)q.y, color);
} 

typedef struct{
    double ax;
    double ay;
    double az;
    
    double bx;
    double by;
    double bz;
    
    int color;
} Line;

void drawLineCam(Line l, Camera c){
    drawline3D(l.ax, l.ay, l.az,
	       l.bx, l.by, l.bz,
	       c.cx, c.cy, c.cz,
	       c.ox, c.oy, c.oz,
	       c.ex, c.ey, c.ez,
	       l.color);
}

Line lineVertex(Vertex a, Vertex b, int color){
    Line l;
    l.ax = a.x;
    l.ay = a.y;
    l.az = a.z;
    
    l.bx = b.x;
    l.by = b.y;
    l.bz = b.z;
    
    l.color = color;
    return l;
}

void drawLines3D(Line lines[], int length, Camera cam){
	int i;
	for(i = 0; i < length; i++){
	    drawLineCam(lines[i], cam);
	}
}
