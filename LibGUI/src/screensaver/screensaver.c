#include <libgui.h>

screeninfo screen;
	
int main(int argc, char *argv[]){
	getinfo(&screen);
	while(1){
		int bg = R(0xFFFFFF);
		clear(bg);
		drawtext(R(screen.x),R(screen.y),"LibGUI",~bg);
		int i;
		for(i = 0; i < 1000; i++){
			hlt();
		}
	}
	return 0;
}
