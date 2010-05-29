typedef struct {
	int x;
	int y;
	int color;
} screeninfo;

void hlt();

unsigned char inb(int port);

void clear(int color);

void putpixel(int x, int y, int color);

void drawtext(int x, int y, int back, int fore, char *text);

void drawline(int x, int y, int x2, int y2, int color);

void drawcircle(int x, int y, int radius, int color);

void fillcircle(int x, int y, int radius, int color);

void drawrect(int x, int y, int x2, int y2, int color);

void fillrect(int x, int y, int x2, int y2, int color);

void getinfo(screeninfo * sc);

void reset();
