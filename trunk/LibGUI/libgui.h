typedef struct {
	int x;
	int y;
	int color;
} screeninfo;

char keys[128];

void updatekeymap();

char checkkey(char key);

void hlt();

void clear(int color);

void putpixel(int x, int y, int color);

void drawtext(int x, int y, char *text, int color);

void drawline(int x, int y, int x2, int y2, int color);

void drawcircle(int x, int y, int radius, int color);

void fillcircle(int x, int y, int radius, int color);

void drawrect(int x, int y, int x2, int y2, int color);

void fillrect(int x, int y, int x2, int y2, int color);

void getinfo(screeninfo * sc);

void reset();

double R(double max);

enum KEYS{
    KEYESC=1,
    KEY1=2,
    KEY2=3,
    KEY3=4,
    KEY4=5,
    KEY5=6,
    KEY6=7,
    KEY7=8,
    KEY8=9,
    KEY9=0xA,
    KEY0=0xB,
    KEYMINUS=0xC,
    KEYEQUAL=0xD,
    KEYBACKSPACE=0xE,
    KEYTAB=0xF,
    KEYQ=0x10,
    KEYW=0x11,
    KEYE=0x12,
    KEYR=0x13,
    KEYT=0x14,
    KEYY=0x15,
    KEYU=0x16,
    KEYI=0x17,
    KEYO=0x18,
    KEYP=0x19,
    KEYLEFTBRACKET=0x1A,
    KEYRIGHTBRACKET=0x1B,
    KEYENTER=0x1C,
    KEYLEFTCONTROL=0x1D,
    KEYA=0x1E,
    KEYS=0x1F,
    KEYD=0x20,
    KEYF=0x21,
    KEYG=0x22,
    KEYH=0x23,
    KEYJ=0x24,
    KEYK=0x25,
    KEYL=0x26,
    KEYSEMICOLON=0x27,
    KEYQUOTE=0x28,
    KEYBACKQUOTE=0x29,
    KEYLEFTSHIFT=0x2A,
    KEYBACKSLASH=0x2B,
    KEYZ=0x2C,
    KEYX=0x2D,
    KEYC=0x2E,
    KEYV=0x2F,
    KEYB=0x30,
    KEYN=0x31,
    KEYM=0x32,
    KEYCOMMA=0x33,
    KEYPERIOD=0x34,
    KEYSLASH=0x35,
    KEYRIGHTSHIFT=0x36,
    KEYASTERISK=0x37,
    KEYLEFTALT=0x38,
    KEYSPACE=0x39,
    KEYCAPSLOCK=0x3A,
    KEYF1=0x3B,
    KEYF2=0x3C,
    KEYF3=0x3D,
    KEYF4=0x3E,
    KEYF5=0x3F,
    KEYF6=0x40,
    KEYF7=0x41,
    KEYF8=0x42,
    KEYF9=0x43,
    KEYF10=0x44,
    KEYNUMLOCK=0x45,
    KEYSCROLLLOCK=0x46,
    KEYHOME=0x47,
    KEYUP=0x48,
    KEYPAGEUP=0x49,
    KEYNUMMINUS=0x4A,
    KEYLEFT=0x4B,
    KEYNUM5=0x4C,
    KEYRIGHT=0x4D,
    KEYNUMPLUS=0x4E,
    KEYEND=0x4F,
    KEYDOWN=0x50,
    KEYPAGEDOWN=0x51,
    KEYDELETE=0x53,
    KEYF11=0x57,
    KEYF12=0x58,
};
