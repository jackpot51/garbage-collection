#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <X11/Xlib.h> // Every Xlib program must include this

typedef struct {
	int x;
	int y;
	int color;
} screeninfo;

char _set = 0;
char _timeset = 0;
Display *_dpy;
Window _win;
int _blackColor;
int _whiteColor;
GC _gc;
struct timeval _timeoday;

void _setup(){
	// Open the display
	_dpy = XOpenDisplay((0));
	
	_blackColor = BlackPixel(_dpy, DefaultScreen(_dpy));
	_whiteColor = WhitePixel(_dpy, DefaultScreen(_dpy));

	_win = XCreateSimpleWindow(_dpy, DefaultRootWindow(_dpy), 0, 0, 
		 	1024, 768, 0, _blackColor, _blackColor);

	// We want to get MapNotify events and key events
	XSelectInput(_dpy, _win, StructureNotifyMask |
				KeyPressMask |
				KeyReleaseMask);

	// "Map" the _window (that is, make it appear on the screen)
	XMapWindow(_dpy, _win);

	// Create a "Graphics Context"
	_gc = XCreateGC(_dpy, _win, 0, (0));

	// Tell the GC we draw using the white color
	XSetForeground(_dpy, _gc, _whiteColor);

	// Wait for the MapNotify event
	while(1) {
		XEvent e;
		XNextEvent(_dpy, &e);
		if(e.type == MapNotify)
			break;
	}
	XAutoRepeatOn(_dpy);
	XGrabKeyboard(_dpy, _win, 1, GrabModeAsync, GrabModeAsync, CurrentTime);
	XFlush(_dpy);
	_set = 1;
}

char keys[128] = { 0 };
char OSXKEY[];

unsigned char inb(int port){
    return 0;
}

void updatekeymap(){
    XEvent e;
    while(XCheckWindowEvent(_dpy, _win, KeyPressMask | KeyReleaseMask , &e)){
		char key = OSXKEY[e.xkey.keycode-8];
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

char checkkey(char key){
    char ret = keys[key];
    if(keys[key]==1) keys[key]=2;
    return ret;
}

void hlt(){
	if(!_timeset){
		usleep(2250);
		gettimeofday(&_timeoday,NULL);
		_timeset = 1;
	}else{
		long next = _timeoday.tv_sec*1000000 + _timeoday.tv_usec;
		gettimeofday(&_timeoday,NULL);
		long cur = _timeoday.tv_sec*1000000 + _timeoday.tv_usec;
		while(next<cur)
			next += 2250;
		int mt = (int)(next - cur);
		usleep(mt);
		gettimeofday(&_timeoday,NULL);
	}
	updatekeymap();
}

void clear(int color){
	if(!_set) _setup();
	XSetWindowBackground(_dpy, _win, color);
	XClearWindow(_dpy, _win);
    XFlush(_dpy);
}

void putpixel(int x, int y, int color){
	if(!_set) _setup();
	XSetForeground(_dpy, _gc, color);
	XDrawPoint(_dpy, _win, _gc, x, y);
	XFlush(_dpy);
}

void drawtext(int x, int y, int back, int fore, char *text){
	if(!_set) _setup();
	XSetBackground(_dpy, _gc, back);
	XSetForeground(_dpy, _gc, fore);
	XDrawImageString(_dpy, _win, _gc, x, y + 10, text, strlen(text));
	XFlush(_dpy);
}

void drawline(int x, int y, int x2, int y2, int color){
	if(!_set) _setup();
   	XSetForeground(_dpy, _gc, color);
    XDrawLine(_dpy, _win, _gc, x, y, x2, y2);
    XFlush(_dpy);
}

void drawcircle(int x, int y, int radius, int color){
	if(!_set) _setup();
	XSetForeground(_dpy, _gc, color);
	x-=radius;
	y-=radius;
	radius+=radius;
	XDrawArc(_dpy, _win, _gc, x, y, radius, radius, 0, 360*65);
    XFlush(_dpy);
}

void fillcircle(int x, int y, int radius, int color){
	if(!_set) _setup();
	XSetForeground(_dpy, _gc, color);
	x-=radius;
	y-=radius;
	radius+=radius;
	XFillArc(_dpy, _win, _gc, x, y, radius, radius, 0, 360*65);
    XFlush(_dpy);
}

void drawrect(int x, int y, int x2, int y2, int color){
	if(!_set) _setup();
	XSetForeground(_dpy, _gc, color);
	int w;
	if(x>x2){
		w=x-x2;
		x=x2;
	}
	else w=x2-x;
	int h;
	if(y>y2){
		h=y-y2;
		y=y2;
	}
	else h=y2-y;
	XDrawRectangle(_dpy, _win, _gc, x, y, w, h);
	XFlush(_dpy);
}

void fillrect(int x, int y, int x2, int y2, int color){
	if(!_set) _setup();
	XSetForeground(_dpy, _gc, color);
	int w;
	if(x>x2){
		w=x-x2;
		x=x2;
	}
	else w=x2-x;
	int h;
	if(y>y2){
		h=y-y2;
		y=y2;
	}
	else h=y2-y;
	XFillRectangle(_dpy, _win, _gc, x, y, w, h);
	XFlush(_dpy);
}

void getinfo(screeninfo * sc){
	if(!_set) _setup();
	sc->x=1024;
	sc->y=768;
	sc->color=_blackColor;
}

void reset(){
	if(!_set) _setup();
	XSetForeground(_dpy, _gc, _whiteColor);
	XSetBackground(_dpy, _gc, _blackColor);
	clear(0);
}

char OSXKEY[128] = {	
	0x1E,//	0x8
	0x1F,//	0x9
	0x20,//	0xA
	0x21,//	0xB
	0x23,//	0xC
	0x22,//	0xD
	0x2C,//	0xE
	0x2D,//	0xF
	0x2E,//	0x10
	0x2F,//	0x11
	0,//	0x12
	0x30,//	0x13
	0x10,//	0x14
	0x11,//	0x15
	0x12,//	0x16
	0x13,//	0x17
	0x15,//	0x18
	0x14,//	0x19
	0x2,//	0x1A
	0x3,//	0x1B
	0x4,//	0x1C
	0x5,//	0x1D
	0x7,//	0x1E
	0x6,//	0x1F
	0xD,//	0x20
	0xA,//	0x21
	0x8,//	0x22
	0xC,//	0x23
	0x9,//	0x24
	0xB,//	0x25
	0x1B,//	0x26
	0x18,//	0x27
	0x16,//	0x28
	0x1A,//	0x29
	0x17,//	0x2A
	0x19,//	0x2B
	0x1C,//	0x2C
	0x26,//	0x2D
	0x24,//	0x2E
	0x28,//	0x2F
	0x25,//	0x30
	0x27,//	0x31
	0x2B,//	0x32
	0x33,//	0x33
	0x35,//	0x34
	0x31,//	0x35
	0x32,//	0x36
	0x34,//	0x37
	0xF,//	0x38
	0x39,//	0x39
	0x29,//	0x3A
	0xE,//	0x3B
	0x1,//	0x3D
	0,//	0x3E
	0,//	0x3F
	0x2A,//	0x40
	0x3A,//	0x41
	0,//	0x42
	0x1D,//	0x43
	0x36,//	0x44
	0x38,//	0x45
	0,//	0x46
	0,//	0x47
	0,//	0x48
	0,//	0x49
	0,//	0x4A
	0,//	0x4B
	0,//	0x4C
	0,//	0x4D
	0,//	0x4E
	0,//	0x4F
	0,//	0x50
	0,//	0x51
	0,//	0x52
	0,//	0x53
	0,//	0x54
	0,//	0x55
	0,//	0x56
	0,//	0x57
	0,//	0x58
	0,//	0x59
	0,//	0x5A
	0,//	0x5B
	0,//	0x5C
	0,//	0x5D
	0,//	0x5E
	0,//	0x5F
	0x3F,//	0x60
	0x40,//	0x61
	0x41,//	0x62
	0x3D,//	0x63
	0,//	0x64
	0,//	0x65
	0,//	0x66
	0,//	0x67
	0,//	0x68
	0,//	0x69
	0,//	0x6A
	0,//	0x6B
	0,//	0x6C
	0,//	0x6D
	0,//	0x6E
	0,//	0x6F
	0,//	0x70
	0,//	0x71
	0,//	0x72
	0x47,//	0x73
	0x49,//	0x74
	0x53,//	0x75
	0x3E,//	0x76
	0x4F,//	0x77
	0x3C,//	0x78
	0x51,//	0x79
	0x3B,//	0x7A
	0x4B,//	0x7B
	0x4D,//	0x7C
	0x50,//	0x7D
	0x48,//	0x7E
	0,//	0x7F
};