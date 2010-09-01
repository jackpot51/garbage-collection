#include <libgui.h>
screeninfo screen;
#include <math.h>
char wireframe = 0;
#include "Camera.h"
#include "Object.h"
#include "Player.h"
#include "Point.h"
#include "Line.h"
#include "Triangle.h"
#include "Sphere.h"
#include "Cube.h"

int main(int argc, char *argv[]){
	getinfo(&screen);
	clear(0);
	
	Triangle tri[] = {
	    {-1,0,1, -1,1,1, -2,0,1, 0xFFFF},{-2,1,1, -1,1,1, -2,0,1, 0xFFFF},
	    {-1,0,0, -1,1,0, -2,0,0, 0xFFFF},{-2,1,0, -1,1,0, -2,0,0, 0xFFFF},
	    
	    {-1,0,0, -1,0,1, -2,0,0, 0xFFFF},{-2,0,0, -1,0,1, -2,0,1, 0xFFFF},
	    {-1,1,0, -1,1,1, -2,1,0, 0xFFFF},{-2,1,0, -1,1,1, -2,1,1, 0xFFFF},
	    
	    {-1,0,0, -1,1,1, -1,0,1, 0xFFFF},{-1,0,0, -1,1,1, -1,1,0, 0xFFFF},
	    {-2,0,0, -2,1,1, -2,0,1, 0xFFFF},{-2,0,0, -2,1,1, -2,1,0, 0xFFFF}
	};
	
	Line complex[] = {
	    {2,2,2, 3,2,2, 0xFFFFFF},
	    {2,2,2, 2,2,3, 0xFFFFFF},
	    {3,2,2, 3,2,3, 0xFFFFFF},
	    {2,2,3, 3,2,3, 0xFFFFFF},

	    {2,2,2, 2.5,1,2.5, 0xFFFFFF},
	    {2,2,3, 2.5,1,2.5, 0xFFFFFF},
	    {3,2,2, 2.5,1,2.5, 0xFFFFFF},
	    {3,2,3, 2.5,1,2.5, 0xFFFFFF}
	};
	
	Camera defaultcam = {0, 0, -2, 0, 0, 0, 0, 0, 1};
	Player p = {defaultcam, {100,2,0.5,0.25, //size
				defaultcam.cx,defaultcam.cy,defaultcam.cz, //position
				0,0,0, //velocity
				0,0,0, //force
				defaultcam.ox,defaultcam.oy,defaultcam.oz, //orientation
				0,0,0, //angular speed
				0,0,0 //torque
				}};
	int i;
	Cube c[] = {newCube(0.25, 0, 0, 0, 0xFF),
		    newCube(0.5, 1, 1, 1, 0xFF00),
		    newCube(1, -2, 2, 2, 0xFF0000)};
	char update = 1;
	while(!checkkey(KEYQ)){
		if(checkkey(KEYW)) movePlayer(&p, 0, 0, 0.2);
		if(checkkey(KEYA)) movePlayer(&p, 0.2, 0, 0);
		if(checkkey(KEYS)) movePlayer(&p, 0, 0, -0.2);
		if(checkkey(KEYD)) movePlayer(&p, -0.2, 0, 0);
		if(checkkey(KEYSPACE) & p.o.y==0){
		    p.o.vy = 0.2;
		}
		//if(checkkey(KEYLEFTCONTROL)) movePlayer(&p, 0, -0.2, 0);
		
		if(checkkey(KEYK)>0 & p.o.ox < M_PI/2) p.o.ox += 0.05;
		if(checkkey(KEYI)>0 & p.o.ox > -M_PI/2) p.o.ox -= 0.05;
		if(checkkey(KEYL)) p.o.oy += 0.05;
		if(checkkey(KEYJ)) p.o.oy -= 0.05;
		
		if(checkkey(KEYE)==1) wireframe = !wireframe;
		
		if(checkkey(KEYX)) resetPlayer(&p, defaultcam);
		
		
		    updatePlayer(&p);
		    
		    clear(0);
		
		    drawTriangles3D(tri, sizeof(tri)/sizeof(Triangle), p.c);
		    drawSphere(1,1,-1,0.2,0xFF00FF,p.c);
		    for(i = 0; i < sizeof(c)/sizeof(Cube); i++) drawCube(c[i], p.c);
		    drawLines3D(complex, sizeof(complex)/sizeof(Line), p.c);
		    
		    drawtext(0,0,"Wireframe test. Press q to exit.",0xFFFFFF);
		    drawline(screen.x/2-8,screen.y/2,screen.x/2+8,screen.y/2,0xFFFFFF);
		    drawline(screen.x/2,screen.y/2-8,screen.x/2,screen.y/2+8,0xFFFFFF);

		for(i = 0; i < 7; i++) hlt();
	}
	reset();
	return 0;
}
