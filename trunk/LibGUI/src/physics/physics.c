#define PRECISION 128

#if PRECISION == 32
	#warning "Floats are inaccurate!"
	#define FPN float
	#define M(function, args...) function ## l(args)
#endif

#if PRECISION == 64
	#define FPN double
	#define M(function, args...) function ## l(args)
#endif

#if PRECISION == 128
	#define FPN long double
	#define M(function, args...) function ## l(args)
#endif


#define PRINT_PRECISION(precision) \
printf("%s has a precision of %d bits.\n", #precision, (int)sizeof(precision)*8);

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <libgui.h>
#include "vector.h"
#include "object.h"
#include "view.h"
#include "collision.h"
//#include "region.h"

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
	Object *b = NULL;
	char showstats = 1;
	Vector cam = {0,0,-(FPN)screen.y*4.0};//camera location
	Vector rot = {0,0,0}; //camera rotation
	while(1){
		int blen = 0;
		if(argc>1) blen = atoi(argv[1]);
		if(blen<=0) blen = R(498)+2;
		int i;
		if(b) free(b);
		b = (Object *)calloc(blen,sizeof(Object));
	
		FPN density = 1;
		/*
		b[0].p.x = 0;
		b[0].p.y = 0;
		b[0].p.z = 0;
		b[0].v.x = 0;
		b[0].v.y = 0;
		b[0].v.z = 0;
		b[0].r = (FPN)screen.y/2.0;
		b[0].m = density*4.0/3.0*M_PI*M(pow, b[0].r, 3);
		b[0].c = 0x80;
		*/
		for(i=0; i<blen; i++){
			do{
				b[i].p.x = R(screen.x*2)-screen.x;
				b[i].p.y = R(screen.y*2)-screen.y;
				b[i].p.z = R(screen.y*2)-screen.y;
			}while(//VectorMag(b[i].p)<(FPN)screen.y/2.0 ||
				   VectorMag(b[i].p) > (FPN)screen.y);
			//b[i].v = ;//should force objects to orbit center
			b[i].r = R(25) + 5;
			b[i].m = density*4.0/3.0*M_PI*M(pow, b[i].r, 3);
			b[i].c = (int)R(0xFCFCFC)+0x20202;
		}
		FPN realtime = 0;
		FPN simtime = 0;
		FPN ts = 1.0/100.0;
		FPN sts = ts;
		int fps = 0;
		int frames = 0;
		while(1){
			FPN rts = -currentfpntime();
			Vector tm = {0,0,0};
			FPN ke = 0;
			FPN gpe = 0;
			
			for(i=0; i<blen; i++){
				int j;
				for(j=0; j<i; j++){
					Collision r = ProcessCollision(&b[i], &b[j]);
					if(showstats) gpe -= b[j].m*b[i].m/r.d;
					Vector f = VectorMul(r.n, ts/(r.d*r.d));
					b[i].v = VectorAdd(b[i].v, VectorMul(f, b[j].m));
					b[j].v = VectorSub(b[j].v, VectorMul(f, b[i].m));
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
				sprintf(stats, "#:%d t:%LE FPS:%d"
						" P:<%LE,%LE,%LE> KE:%LE GPE:%LE E:%LE"
						, blen, (long double)simtime, fps
						, (long double)tm.x, (long double)tm.y, (long double)tm.z, (long double)ke, (long double)gpe, (long double)(ke+gpe)
						);
			}
			
			View v = makeView(b, blen, cam, GetTransform(rot), screen);
			
			//drawing
			clear(0);
			drawView(&v);
			if(showstats){
				drawtext(0,0,stats,0xFFFFFF);
			}
			update();
			freeView(&v);
			//end drawing
			
			updatekeymap();
			rts += currentfpntime();
			if(sts>rts){
				int sleeptime = (int)((sts - rts)*1000000.0);
				usleep(sleeptime);
				rts = sts;
			}
			frames++;
			if((int)(realtime+rts)>(int)realtime){
				fps = frames;
				frames = 0;
			}
			realtime += rts;
			if(checkkey(KEYA)) cam.x -= rts*1000.0;
			if(checkkey(KEYD)) cam.x += rts*1000.0;
			if(checkkey(KEYW)) cam.y -= rts*1000.0;
			if(checkkey(KEYS)) cam.y += rts*1000.0;
			if(checkkey(KEYZ)) cam.z *= (1.0+rts);
			if(checkkey(KEYX) && cam.z < -(FPN)screen.y*1.25){
				cam.z /= (1.0+rts);
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
				cam.z = -(FPN)screen.y*4.0;
			}
			if(checkkey(KEYT)==1) ts = -ts;
			if(checkkey(KEYF)==1) showstats = !showstats;
			if(checkkey(KEYV)==1) break;
			if(checkkey(KEYQ)) return 0;
		}
	}
} 
