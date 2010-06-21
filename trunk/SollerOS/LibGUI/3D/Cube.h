
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
    drawLineCam(lineVertex(c.a,c.b), cam, c.color);
    drawLineCam(lineVertex(c.b,c.c), cam, c.color);
    drawLineCam(lineVertex(c.c,c.d), cam, c.color);
    drawLineCam(lineVertex(c.d,c.a), cam, c.color);
    drawLineCam(lineVertex(c.e,c.f), cam, c.color);
    drawLineCam(lineVertex(c.f,c.g), cam, c.color);
    drawLineCam(lineVertex(c.g,c.h), cam, c.color);
    drawLineCam(lineVertex(c.h,c.e), cam, c.color);
    drawLineCam(lineVertex(c.a,c.e), cam, c.color);
    drawLineCam(lineVertex(c.b,c.f), cam, c.color);
    drawLineCam(lineVertex(c.c,c.g), cam, c.color);
    drawLineCam(lineVertex(c.d,c.h), cam, c.color);
}

void clearCube(Cube c, Camera cam){
    int oldc = c.color;
    c.color = 0;
    drawCube(c, cam);
    c.color = oldc;
} 
