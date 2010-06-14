#include "libgui.h"
#include <stdlib.h>
#include <sys/time.h>
screeninfo screen;

int R(int max){
	double r = rand();
	r = r/RAND_MAX;
	return (int)(r*max);
}

void SR(){
	struct timeval seedt;
	gettimeofday(&seedt, NULL);
	srand((unsigned int)seedt.tv_usec);
}
	
int main(int argc, char *argv[]){
	getinfo(&screen);
	while(1){
		int bg = R(0xFFFF);
		clear(bg);
		drawtext(R(screen.x),R(screen.y),bg,~bg,"LibGUI");
		int i;
		for(i = 0; i < 1000; i++){
			hlt();
		}
	}
	return 0;
}
