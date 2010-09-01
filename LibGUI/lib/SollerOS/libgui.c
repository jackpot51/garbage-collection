typedef struct {
	int x;
	int y;
	int color;
} screeninfo;

char keys[128] = { 0 };

unsigned char inb(int port){
	unsigned char r;
	asm("xor %%eax, %%eax\n\t"
		"in %%dx, %%al"
		: "=a" (r)
		: "d" (port)
	);
	return r;
}

void updatekeymap(){
 	unsigned char key = inb(0x64);
	if(!(key&0x20)){
		key = inb(0x60);
		if(key>=0x80) keys[key - 0x80] = 0;
		else if(keys[key]==0) keys[key] = 1;
	}
}

char checkkey(char key){
    char ret = keys[key];
    if(keys[key]==1) keys[key]=2;
    return ret;
}

void hlt(){
	asm("hlt");
	updatekeymap();
}

int Convert(int color){
	int c = (color&0xF80000)>>8;
	c += (color&0xFC00)>>5;
	c += (color&0xF8)>>3;
	return c;
}

void clear(int color){
	asm volatile("movb $17, %%ah\n\t"
		"xorb %%al, %%al\n\t"
		"int $0x30"
	:
	: "b" (Convert(color))
	: "%eax", "%ecx", "%edx", "%edi", "%esi"
	);
}

void putpixel(int x, int y, int color){
	asm volatile("movb $17, %%ah\n\t"
		"movb $1, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "b" (Convert(color))
	: "%eax", "%edi", "%esi"
	);
}

void drawtext(int x, int y, int back, int fore, char *text){
	asm volatile("movb $17, %%ah\n\t"
		"movb $2, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "D" (Convert(back)), "b" (Convert(fore)), "S" (text)
	: "%eax"
	);
}

void drawline(int x, int y, int x2, int y2, int color){
	asm volatile("movb $17, %%ah\n\t"
		"movb $3, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "D" (x2), "S" (y2), "b" (Convert(color))
	: "%eax"
	);
}

void drawcircle(int x, int y, int radius, int color){
	asm volatile("movb $17, %%ah\n\t"
		"movb $4, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "S" (radius), "b" (Convert(color))
	: "%eax", "%edi"
	);
}

void fillcircle(int x, int y, int radius, int color){
	asm volatile("movb $17, %%ah\n\t"
		"movb $5, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "S" (radius), "b" (Convert(color))
	: "%eax", "%edi"
	);
}

void drawrect(int x, int y, int x2, int y2, int color){
	asm volatile("movb $17, %%ah\n\t"
		"movb $6, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "D" (x2), "S" (y2), "b" (Convert(color))
	: "%eax"
	);
}

void fillrect(int x, int y, int x2, int y2, int color){
	asm volatile("movb $17, %%ah\n\t"
		"movb $7, %%al\n\t"
		"int $0x30"
	:
	: "d" (x), "c" (y), "D" (x2), "S" (y2), "b" (Convert(color))
	: "%eax"
	);
}

void getinfo(screeninfo * sc){
	int color;
	asm volatile("movb $17, %%ah\n\t"
		"movb $253, %%al\n\t"
		"int $0x30"
	: "=d" (sc->x), "=c" (sc->y), "=b" (color)
	:
	);
	int c = (color&0xF800)<<8;
	c += (color&0x7E0)<<5;
	c += (color&0x1F)<<3;
	sc->color = c;
}

void reset(){
	asm volatile("movb $17, %%ah\n\t"
		"movb $255, %%al\n\t"
		"int $0x30"
	:
	:
	: "%eax", "%ebx", "%ecx", "%edx", "%esi", "%edi"
	);
}
