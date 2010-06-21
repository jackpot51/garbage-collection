#include "libgui.h"
screeninfo screen;
#include <math.h>
#include "3D/Camera.h"
#include "3D/Object.h"
#include "3D/Player.h"
#include "3D/Line.h"
#include "3D/Cube.h"
//#include <stdio.h>

int main(int argc, char *argv[]){
	getinfo(&screen);
	clear(0);
	
	Line complex[] = {
	    {2,2,2,3,2,2},
	    {2,2,2,2,2,3},
	    {3,2,2,3,2,3},
	    {2,2,3,3,2,3},

	    {2,2,2,2.5,1,2.5},
	    {2,2,3,2.5,1,2.5},
	    {3,2,2,2.5,1,2.5},
	    {3,2,3,2.5,1,2.5}
	};
	
	Camera defaultcam = {0, 0, -1, 0, 0, 0, 0, 0, 1};
	Player p = {defaultcam, {100,2,0.5,0.25, //size
				0,0,-1, //position
				0,0,0, //velocity
				0,0,0, //force
				0,0,0, //orientation
				0,0,0, //angular speed
				0,0,0 //torque
				}};
	int i;
	Cube c[] = {newCube(0.25, 0, 0, 0, 0xFF),
		    newCube(0.5, 1, 1, 1, 0xFF00),
		    newCube(1, -2, 2, 2, 0xFF0000)};
	int clen = sizeof(c)/sizeof(Cube);
	int complexsize = sizeof(complex)/sizeof(Line);
	while(!checkkey(KEYQ)){
		for(i = 0; i < clen; i++) clearCube(c[i], p.c);
		drawLines(complex, complexsize, 0, p.c);
		
		if(checkkey(KEYW)) movePlayer(&p, 0, 0, 0.2);
		if(checkkey(KEYA)) movePlayer(&p, 0.2, 0, 0);
		if(checkkey(KEYS)) movePlayer(&p, 0, 0, -0.2);
		if(checkkey(KEYD)) movePlayer(&p, -0.2, 0, 0);
		if(checkkey(KEYSPACE)) movePlayer(&p, 0, -0.2, 0);
		if(checkkey(KEYLEFTCONTROL)) movePlayer(&p, 0, 0.2, 0);
		
		if(checkkey(KEYI)>0 & p.o.ox < M_PI/2) p.o.ox += 0.05;
		if(checkkey(KEYK)>0 & p.o.ox > -M_PI/2) p.o.ox -= 0.05;
		if(checkkey(KEYL)) p.o.oy += 0.05;
		if(checkkey(KEYJ)) p.o.oy -= 0.05;
		
		if(checkkey(KEYX)){
		    resetPlayer(&p, defaultcam);
		}
		
		updatePlayer(&p);
		
		for(i = 0; i < clen; i++) drawCube(c[i], p.c);
		drawLines(complex, complexsize, 0xFFFFFF, p.c);
		
		drawtext(0,0,0,0xFFFFFF,"Wireframe test. Press q to exit.");
		drawline(screen.x/2-8,screen.y/2,screen.x/2+8,screen.y/2,0xFFFFFF);
		drawline(screen.x/2,screen.y/2-8,screen.x/2,screen.y/2+8,0xFFFFFF);
		for(i = 0; i < 10; i++) hlt();
	}
	reset();
	return 0;
}
