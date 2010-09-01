typedef struct{
	Camera c;
	Object o;
} Player;

//I ASSUME TZ DOES NOT CHANGE AT ALL
//AND THAT TX DOES NOT CHANGE THE DIRECTION OF TRAVEL!!!
//X, Y, and Z are relative to the player
void movePlayer(Player *p, double x, double y, double z){
    p->o.z += z*cos(p->o.oy)+x*sin(p->o.oy);
    p->o.x -= x*cos(p->o.oy)-z*sin(p->o.oy);
    
    p->o.y += y;
}

void resetPlayer(Player *p, Camera c){
    p->c = c;
    p->o.x = c.cx;
    p->o.y = c.cy;
    p->o.z = c.cz;
    p->o.ox = c.ox;
    p->o.oy = c.oy;
    p->o.oz = c.oz;
    
    p->o.vx = 0;
    p->o.vy = 0;
    p->o.vz = 0;
    
    p->o.fx = 0;
    p->o.fy = 0;
    p->o.fz = 0;
    
    p->o.wx = 0;
    p->o.wy = 0;
    p->o.wz = 0;
    
    p->o.tx = 0;
    p->o.ty = 0;
    p->o.tz = 0;
}

void updatePlayer(Player *p){
    if(p->o.y > 0) p->o.fy = -1;
    else if(p->o.y < 0){
	p->o.fy = 0;
	p->o.vy = 0;
	p->o.y = 0;
    }
    updateObject(&(p->o));
    p->c.ox=p->o.ox;
    p->c.oy=p->o.oy;
    p->c.oz=p->o.oz;
    p->c.cx=p->o.x;
    p->c.cy=p->o.y;
    p->c.cz=p->o.z;
}
