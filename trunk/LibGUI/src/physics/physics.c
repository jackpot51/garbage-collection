#define PRECISION 64

#if PRECISION == 32
	#define FPN float
	#define SQRT(a) sqrtf(a)
	#define COS(a) cosf(a)
	#define SIN(a) sinf(a)
#endif

#if PRECISION == 64
	#define FPN double
	#define SQRT(a) sqrt(a)
	#define COS(a) cos(a)
	#define SIN(a) sin(a)
#endif

#if PRECISION == 128
	#define FPN long double
	#define SQRT(a) sqrtl(a)
	#define COS(a) cosl(a)
	#define SIN(a) sinl(a)
#endif

#define PRINT_PRECISION(precision) \
printf("Using precision with size %d.\n", (int)sizeof(precision)*8);

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <libgui.h>
#include "vector.h"
#include "point.h"
#include "object.h"
#include "collision.h"

FPN G = 6.67428E-11;

FPN fpntime(struct timeval ts){
	FPN ret = (FPN)ts.tv_sec;
	ret += (FPN)ts.tv_usec/1000000.0;
	return ret;
}

FPN currentfpntime(){
	struct timeval ts;
	gettimeofday(&ts, NULL);
	return fpntime(ts);
}

int main(int argc, char **argv){
	PRINT_PRECISION(FPN);
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
		b[0].m = 1E18;
		b[0].c = 0x80;
		for(i=1; i<blen; i++){
			zbuf[i] = &b[i];
			b[i].zbuf = i;
			while(VectorMag(b[i].p)<screen.y/2){
				b[i].p.x = R(screen.x*2)-screen.x;
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
		FPN realtime = 0;
		FPN simtime = 0;
		FPN ts = 2250.0/1000000.0;
		FPN sts = ts;
		int fps = 0;
		while(1){
			FPN rts = -currentfpntime();
			Vector tm = {0,0,0};
			FPN ke = 0;
			FPN gpe = 0;
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
						FPN u = -G*b[j].m*b[i].m/r.d;
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
			simtime += ts;
			char stats[256];
			if(showstats){
				sprintf(stats, "#:%d t:%LE FPS:%d P:<%LE,%LE,%LE> KE:%LE GPE:%LE E:%LE", blen, (long double)simtime, fps, (long double)tm.x, 
						(long double)tm.y, (long double)tm.z, (long double)ke, (long double)gpe, (long double)(ke+gpe));
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
				drawtext(0,0,stats,0xFFFFFF);
			}
			updatekeymap();
			rts += currentfpntime();
			if(sts>rts){
				int sleeptime = (int)((sts - rts)*1000000.0);
				usleep(sleeptime);
				rts = sts;
			}
			if((int)(realtime+rts)>(int)realtime){
				fps = (int)(1.0/rts);
			}
			realtime += rts;
			if(checkkey(KEYA)) cam.x -= rts*M_PI;
			if(checkkey(KEYD)) cam.x += rts*M_PI;
			if(checkkey(KEYW)) cam.y -= rts*M_PI;
			if(checkkey(KEYS)) cam.y += rts*M_PI;
			if(checkkey(KEYZ)) cam.z *= (1+rts);
			if(checkkey(KEYX) && cam.z < -(FPN)screen.y*1.25){
				cam.z /= (1+rts);
			}
			if(checkkey(KEYI)) rot.x += rts;
			if(checkkey(KEYK)) rot.x -= rts;
			if(checkkey(KEYJ)) rot.y += rts;
			if(checkkey(KEYL)) rot.y -= rts;
			if(checkkey(KEYC)){
				rot.x = 0;
				rot.y = 0;
				rot.z = 0;
				cam.x = 0;
				cam.y = 0;
				cam.z = -screen.y*4;
			}
			if(checkkey(KEYT)==1) ts = -ts;
			if(checkkey(KEYR)==1) showlines = !showlines;
			if(checkkey(KEYF)==1) showstats = !showstats;
			if(checkkey(KEYV)==1) break;
			if(checkkey(KEYQ)) return 0;
		}
	}
} 
