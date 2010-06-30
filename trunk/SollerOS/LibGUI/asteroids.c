#include "libgui.h"
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>

screeninfo screen;

enum Status{
    THRUST=0x1,
    CLOCKWISE=0x2,
    COUNTERCLOCKWISE=0x4,
};

typedef struct{
    double x; //x position
    double y; //y position
    double vx; //velocity x
    double vy; //velocity y
    double r; //radius
    double h; //health
    int c; //color
} asteroid;

void displayAsteroid(asteroid a){
    double health = (a.h)*20/100-10;
    drawrect(a.x-10,a.y-2,a.x+10,a.y+2,a.c);
    fillrect(a.x-10,a.y-2,a.x+health,a.y+2,a.c);
    drawcircle(a.x,a.y,a.r,a.c);
}

void clearAsteroid(asteroid a){
    a.c = 0;
    displayAsteroid(a);
}

asteroid newAsteroid(){
    asteroid a;
    a.x = R(screen.x);
    a.y = R(screen.y); //y position
    a.vx = R(2)-1; //velocity x
    a.vy = R(2)-1; //velocity y
    a.r  = R(16)+16; //radius
    a.h = 100; //health
    a.c = 0xFFFFFF; //color
    return a;
}

void updateAsteroid(asteroid *a){
    clearAsteroid(*a);
    if(a->h < 0) *a = newAsteroid();
    a->x += a->vx;
    a->y -= a->vy;
    if(a->x > screen.x - a->r) a->x = a->r;
    if(a->x < a->r) a->x = screen.x - a->r;
    if(a->y > screen.y - a->r) a->y = a->r;
    if(a->y < a->r) a->y = screen.y - a->r;
    displayAsteroid(*a);
}

typedef struct{
    double x; //x of center
    double y; //y of center
    double vx; //velocity x
    double vy; //velocity y
    double d; //damage
    int c; //color
} projectile;

typedef struct{
    double x; //x position of center
    double y; //y position of center
    double o; //orientation
    double vx; //velocity x
    double vy; //velocity y
    double vo; //angular velocity
    double f; //force of thrusters
    double t; //torque
    double m; //mass
    double i; //moment of inertia
    double w; //width
    double l; //length
    double h; //health
    char s; //status
    int c; //color
} ship;

void displayShip(ship s){
    double theta = s.o*M_PI/180;
    double si = sin(theta);
    double co = cos(theta);
    double w = s.w/2;
    double l = s.l/2;
    
    double fx = l*si;
    double fy = -l*co;
    
    double lx = -w*co - l*si;
    double ly = -w*si + l*co;
    
    double rx = w*co - l*si;
    double ry = w*si + l*co;
    double health = (s.h)*20/100-10;
    drawrect(s.x-10,s.y-2,s.x+10,s.y+2,s.c);
    fillrect(s.x-10,s.y-2,s.x+health,s.y+2,s.c);
    drawline(fx+s.x,fy+s.y,lx+s.x,ly+s.y,s.c);
    drawline(fx+s.x,fy+s.y,rx+s.x,ry+s.y,s.c);
}

void clearShip(ship s){
    s.c = 0;
    displayShip(s);
}

void updateShip(ship *s){
    clearShip(*s);
    s->x += s->vx;
    s->y -= s->vy;
    s->o += s->vo;
    if(s->x > screen.x - s->w/2) s->x = s->w/2;
    if(s->x < s->w/2) s->x = screen.x - s->w/2;
    if(s->y > screen.y - s->l/2) s->y = s->l/2;
    if(s->y < s->l/2) s->y = screen.y - s->l/2;
    if(s->s&THRUST){
	double theta = s->o*M_PI/180;
	double a = s->f/s->m;
	s->vx += a*sin(theta);
	s->vy += a*cos(theta);
    }
    if(s->s&CLOCKWISE) s->vo += s->t/s->i;
    if(s->s&COUNTERCLOCKWISE) s->vo -= s->t/s->i;
    displayShip(*s);
}

int main(int argc, char *argv[]){
    getinfo(&screen);
    ship ships[] = {{screen.x/3,screen.y/3,0,0,0,0,0.01,0.1,1,1,
			48,64,80,0,0xFF0000},
		    {screen.x/2,screen.y/2,0,0,0,0,0.01,0.1,1,1,
			48,64,100,0,0xFF},
		    {screen.x*2/3,screen.y*2/3,0,0,0,0,0.01,0.1,1,1,
			64,128,60,0,0xFF00}
		    };
    int shiplen = sizeof(ships)/sizeof(ship);
    
    asteroid asteroids[(int)R(20) + 2];
    char astlen = sizeof(asteroids)/sizeof(asteroid);
    int i;
    for(i=0;i<astlen;i++){
	asteroids[i] = newAsteroid();
    }
    clear(0);
    while(!checkkey(KEYQ)){
		if(checkkey(KEYW)) ships[0].s |= THRUST;
		else ships[0].s &= ~THRUST;
		if(checkkey(KEYA)) ships[0].s |= COUNTERCLOCKWISE;
		else ships[0].s &= ~COUNTERCLOCKWISE;
		if(checkkey(KEYD)) ships[0].s |= CLOCKWISE;
		else ships[0].s &= ~CLOCKWISE;
	
		if(checkkey(KEYT)) ships[1].s |= THRUST;
		else ships[1].s &= ~THRUST;
		if(checkkey(KEYF)) ships[1].s |= COUNTERCLOCKWISE;
		else ships[1].s &= ~COUNTERCLOCKWISE;
		if(checkkey(KEYH)) ships[1].s |= CLOCKWISE;
		else ships[1].s &= ~CLOCKWISE;
	
		if(checkkey(KEYI)) ships[2].s |= THRUST;
		else ships[2].s &= ~THRUST;
		if(checkkey(KEYJ)) ships[2].s |= COUNTERCLOCKWISE;
		else ships[2].s &= ~COUNTERCLOCKWISE;
		if(checkkey(KEYL)) ships[2].s |= CLOCKWISE;
		else ships[2].s &= ~CLOCKWISE;
		
		for(i = 0; i < astlen; i++) updateAsteroid(&asteroids[i]);
		for(i = 0; i < shiplen; i++) updateShip(&ships[i]);
		for(i = 0; i < 7; i++) hlt(); //~63 Hz
    }
	reset();
	return 0;
}
