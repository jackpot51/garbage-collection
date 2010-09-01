#include <libgui.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>
double sqroot(double m)
{
	double r;
	asm volatile("fsqrt"
		: "=t" (r)
		: "0" (m)
	);
	return r;
}
screeninfo *screen;
#include "Vector.h"
#include "Camera.h"
#include "Color.h"
#include "Sphere.h"
#include "Light.h"
#include "LightIntensity.h"
#include "View2D.h"

void nextFrame(View2D TheImage, Camera TheCamera, Sphere s, Color bcol, LightIntensity LIntensity){
	for(CameraStart(&TheCamera);TheCamera.from.y<1;CameraNext(&TheCamera)){
		Vector v = {0,0,0};
		Color col = {0,0,0};
		if(SphereDoesRayIntersect(s,TheCamera.from,TheCamera.to,&v)==1){
			LightIntensityDetermineIntensity(LIntensity,v,s.c,&col);
			View2DDrawPixel(TheImage,col,v);
		}else{
			View2DDrawPixel(TheImage,bcol,v);
		}
	}
}

int main(int argc, char *argv[]){
	screen = malloc(sizeof(screeninfo));
	getinfo(screen);
	if(!screen->x | !screen->y){
		_exit(1);
	}
	clear(0);
	
	Vector p = {0,0,0};
	Color c = {R(1),R(1),R(1)};
	Sphere s = {0.5,p,c};
	
	LightIntensity LIntensity = {newLight(R(2) - 1,R(2) - 1,-1),0.3};
	
	Vector from = {0,0,3};
	Vector to = {0,0,-1};
	Camera TheCamera = {from,to,screen->y,screen->y};

	View2D TheImage = {TheCamera.resX, TheCamera.resY};
	Color bcol = {0.5,0.5,0.5};
	
	nextFrame(TheImage,TheCamera,s,bcol,LIntensity);

	drawtext(0,0,"Press q to exit.",0xFFFFFF);	
	unsigned char key = 0;
	while(!checkkey(KEYQ)){
			hlt();
			if(checkkey(KEYUP) & LIntensity.light.y<1){
					LIntensity.light.y-=0.4;
					nextFrame(TheImage,TheCamera,s,bcol,LIntensity);
			}
			if(checkkey(KEYDOWN) & LIntensity.light.y>-1){
					LIntensity.light.y+=0.4;
					nextFrame(TheImage,TheCamera,s,bcol,LIntensity);
			}
			if(checkkey(KEYLEFT) & LIntensity.light.x>-1){
					LIntensity.light.x-=0.4;
					nextFrame(TheImage,TheCamera,s,bcol,LIntensity);
			}
			if(checkkey(KEYRIGHT) & LIntensity.light.x<1){
					LIntensity.light.x+=0.4;
					nextFrame(TheImage,TheCamera,s,bcol,LIntensity);
			}
	}
	reset();
	_exit(0);
}
