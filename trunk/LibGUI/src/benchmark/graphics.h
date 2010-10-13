#include <libgui.h>

void guiClear(){
	int i;
	for(i=0; i<0xFFFFFF; i++){
		clear(i);
	}
}