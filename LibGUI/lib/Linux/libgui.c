#include "../common/X11.h"

void updatekeymap(){
    XEvent e;
    while(XCheckWindowEvent(_dpy, _win, KeyPressMask | KeyReleaseMask , &e)){
		char key = e.xkey.keycode - 8;
	    if(e.type==KeyRelease){
			keys[key] |= 0x80;
			keys[key] &= 0xBF;
	    }
	    else if(e.type==KeyPress){
			keys[key] |= 0x40;
			keys[key] &= 0x7F;
	    }
    }
    int i;
    for(i = 0; i < sizeof(keys); i++){
		if(keys[i] & 0x40){
			keys[i] &= 0xBF;
			if(keys[i]==0) keys[i] = 1;
		}
		if(keys[i] & 0x80){
			keys[i] = 0;
		}
    }
    XFlush(_dpy);
}
