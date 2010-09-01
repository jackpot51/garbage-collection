#include <math.h>
#include <stdio.h>
#include "libgui.h"
#include "physics/vector.h"
#include "physics/point.h"
#include "physics/object.h"
#include "physics/collision.h"
#include "physics/particle.h"
#include "physics/boson.h"
#include "physics/lepton.h"
#include "physics/quark.h"

#define HLTTIME 1
long double G = 6.67428E-11;

int main(int argc, char **argv){
	screeninfo screen;
	getinfo(&screen);
	object *b = NULL;
	object **zbuf = NULL;
	char showstats = 1;
	char showlines = 0;
	Vector cam = {0,0,-screen.y*4};//camera location
	Vector rot = {0,0,0}; //camera rotation
	while(1){
		int blen = 0;
		if(argc>1) blen = atoi(argv[1]);
		if(blen<=0) blen = R(198)+2;
		int i;
		if(b) free(b);
		if(zbuf) free(zbuf);
		b = (object *)calloc(blen,sizeof(object));
		zbuf = (object **)calloc(blen,sizeof(object *));
		zbuf[0] = &b[0];
		b[0].zbuf = 0;
		b[0].p.x = 0;
		b[0].p.y = 0;
		b[0].p.z = 0;
		b[0].v.x = 0;
		b[0].v.y = 0;
		b[0].v.z = 0;
		b[0].r = screen.y/2;
		b[0].m = 5.9742E16;
		b[0].c = 0x80;
		for(i=1; i<blen; i++){
			zbuf[i] = &b[i];
			b[i].zbuf = i;
			while(VectorMag(b[i].p)<screen.y/2){
				b[i].p.x = R(screen.x*2)-screen.y;
				b[i].p.y = R(screen.y*2)-screen.y;
				b[i].p.z = R(screen.y*2)-screen.y;
			}
			b[i].v.x = R(50)-25;
			b[i].v.y = R(50)-25;
			b[i].v.z = R(50)-25;
			b[i].r = R(20)+5;
			b[i].m = b[i].r*1E11;
			b[i].c = (int)R(0xFDFDFD)+0x10101;
		}
		long double ts = HLTTIME*2250.0/100000.0;
		while(1){
			Vector tm = {0,0,0};
			long double ke = 0;
			long double gpe = 0;
			for(i=0; i<blen; i++){
				b[i].pix = ConvertPoint(b[i].p, cam, rot, screen);
			}
			for(i=0; i<blen; i++){
				if((i+1)<blen){
					int j;
					for(j=i+1; j<blen; j++){
						if((b[i].pix.z > b[j].pix.z && b[i].zbuf < b[j].zbuf) 
						   || (b[i].pix.z < b[j].pix.z && b[i].zbuf > b[j].zbuf)){
							zbuf[b[i].zbuf] = &b[j];
							zbuf[b[j].zbuf] = &b[i];
							int swapzbuf = b[i].zbuf;
							b[i].zbuf = b[j].zbuf;
							b[j].zbuf = swapzbuf;
						}
						Collision r = ProcessCollision(&b[i], &b[j]);
						long double u = -G*b[j].m*b[i].m/r.d;
						if(showstats) gpe += u;
						Vector f = VectorMul(r.n, -u/r.d*ts);
						b[i].v = VectorAdd(b[i].v, VectorDiv(f, b[i].m));
						b[j].v = VectorSub(b[j].v, VectorDiv(f, b[j].m));
					}
				}
				b[i].p = VectorAdd(b[i].p, VectorMul(b[i].v, ts));
				if(showstats){
					tm = VectorAdd(tm, VectorMul(b[i].v, b[i].m));
					ke += VectorMag2(b[i].v)*b[i].m/2.0;
				}
			}
			Vector origin = {0,0,0};
			origin = ConvertPoint(origin, cam, rot, screen);
			clear(0);
			for(i=0; i<blen; i++){
				Vector p = zbuf[i]->pix;
				if(showlines){
					drawline(origin.x, origin.y, p.x, p.y, zbuf[i]->c);
				}
				if(p.x<screen.x && p.x>0 && p.y<screen.y && p.y>0){
					fillcircle(p.x, p.y, zbuf[i]->r*p.z, zbuf[i]->c);
				}
			}
			if(showstats){
				char stats[256];
				sprintf(stats, "#:%d P:<%LE,%LE,%LE> KE:%LE GPE:%LE E:%LE", blen, tm.x, tm.y, tm.z, ke, gpe, ke+gpe);
				drawtext(0,0,0,0xFFFFFF,stats);
			}
			for(i=0; i<HLTTIME; i++) hlt();
			if(checkkey(KEYA)) cam.x -= HLTTIME*1.6;
			if(checkkey(KEYD)) cam.x += HLTTIME*1.6;
			if(checkkey(KEYW)) cam.y -= HLTTIME*1.6;
			if(checkkey(KEYS)) cam.y += HLTTIME*1.6;
			if(checkkey(KEYZ)) cam.z *= (1+HLTTIME/160.0);
			if(checkkey(KEYX) && cam.z < -screen.y/16.0){
				cam.z /= (1+HLTTIME/160.0);
			}
			if(checkkey(KEYI)) rot.x += HLTTIME*0.004;
			if(checkkey(KEYK)) rot.x -= HLTTIME*0.004;
			if(checkkey(KEYJ)) rot.y += HLTTIME*0.004;
			if(checkkey(KEYL)) rot.y -= HLTTIME*0.004;
			if(checkkey(KEYC)){
				rot.x = 0;
				rot.y = 0;
				rot.z = 0;
				cam.x = 0;
				cam.y = 0;
				cam.z = -screen.y*4;
			}
			if(checkkey(KEYR)==1) showlines = !showlines;
			if(checkkey(KEYF)==1) showstats = !showstats;
			if(checkkey(KEYV)==1) break;
			if(checkkey(KEYQ)) return 0;
		}
	}
} 
