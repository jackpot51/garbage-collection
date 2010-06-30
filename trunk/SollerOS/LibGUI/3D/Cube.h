
typedef struct{
    Vertex a;
    Vertex b;
    Vertex c;
    Vertex d;
    Vertex e;
    Vertex f;
    Vertex g;
    Vertex h;
    int color;
} Cube;
		    
Cube newCube(double size, double x, double y, double z, int color){
	double s = size/2;
	Cube c = {{x+s,y+s,z+s},
		{x+s,y-s,z+s},
		{x-s,y-s,z+s},
		{x-s,y+s,z+s},
		{x+s,y+s,z-s},
		{x+s,y-s,z-s},
		{x-s,y-s,z-s},
		{x-s,y+s,z-s},
		color};
	return c;
}

void drawCube(Cube c, Camera cam){
    drawLineCam(lineVertex(c.a,c.b,c.color), cam);
    drawLineCam(lineVertex(c.b,c.c,c.color), cam);
    drawLineCam(lineVertex(c.c,c.d,c.color), cam);
    drawLineCam(lineVertex(c.d,c.a,c.color), cam);
    drawLineCam(lineVertex(c.e,c.f,c.color), cam);
    drawLineCam(lineVertex(c.f,c.g,c.color), cam);
    drawLineCam(lineVertex(c.g,c.h,c.color), cam);
    drawLineCam(lineVertex(c.h,c.e,c.color), cam);
    drawLineCam(lineVertex(c.a,c.e,c.color), cam);
    drawLineCam(lineVertex(c.b,c.f,c.color), cam);
    drawLineCam(lineVertex(c.c,c.g,c.color), cam);
    drawLineCam(lineVertex(c.d,c.h,c.color), cam);
}

void clearCube(Cube c, Camera cam){
    int oldc = c.color;
    c.color = 0;
    drawCube(c, cam);
    c.color = oldc;
} 
