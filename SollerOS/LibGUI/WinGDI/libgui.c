#include <windows.h>

int gettimeofday(struct timeval *tv, void* tz){
    union {
        long long ns100; /*time since 1 Jan 1601 in 100ns units */
        FILETIME ft;
    } now;
    GetSystemTimeAsFileTime (&now.ft);
    tv->tv_usec = (long) ((now.ns100 / 10LL) % 1000000LL);
    tv->tv_sec = (long) ((now.ns100 - 116444736000000000LL) / 10000000LL);
    return 0;
}

typedef struct {
	int x;
	int y;
	int color;
} screeninfo;

char keys[128];
HDC _hdc;
HWND _win;
char _winset = 0;
char _timeset = 0;
struct timeval _timeoday;
char _set = 0;
char _painting = 0;

int _winX = 1024;
int _winY = 768;

RECT _r = {0, 0, 1024, 768};

static TCHAR szClassName[] = TEXT("LibGUI");

LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam){
        switch (message){
               case WM_PAINT:
                    _painting = 1;
                    break;
               case WM_DESTROY:
                    PostQuitMessage(0);
                    _exit(0);
                    break;
               default:
                    return DefWindowProc(hWnd, message, wParam, lParam);
        }
        return 0;      
}

int APIENTRY WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow){
     WNDCLASS wndclass;
     wndclass.style = CS_HREDRAW | CS_VREDRAW;
     wndclass.lpfnWndProc = WndProc;
     wndclass.cbClsExtra = 0;
     wndclass.cbWndExtra = 0;
     wndclass.hInstance = hInstance;
     wndclass.hIcon = LoadIcon(NULL,IDI_APPLICATION);
     wndclass.hCursor = LoadCursor(NULL,IDC_ARROW);
     wndclass.hbrBackground = (HBRUSH) GetStockObject(WHITE_BRUSH);
     wndclass.lpszMenuName = NULL;
     wndclass.lpszClassName = szClassName;

     RegisterClass(&wndclass);
     
     _win = CreateWindow(szClassName, "LibGUI", WS_OVERLAPPED, CW_USEDEFAULT, CW_USEDEFAULT, _winX, _winY, NULL, NULL, hInstance, NULL);
     if(_win == NULL){
             int err = GetLastError();
             char errcode[256];
             sprintf(errcode, "WINDOW CREATION FAILED WITH ERROR %d", err);
             MessageBox(NULL, errcode, "FAIL", 0);
     }else{
           //_hdc = GetDC(_win);
           ShowWindow(_win, nCmdShow);
           UpdateWindow(_win);
           _winset = 1;
           MSG msg;
           while (GetMessage(&msg, NULL, 0, 0))
           {
               TranslateMessage(&msg);
               DispatchMessage(&msg);
           }
           return (int)msg.wParam;
     }
     return 0;
}

DWORD WINAPI WindowThread(LPVOID lpParam){
     return WinMain(GetModuleHandle(NULL), GetModuleHandle(NULL), NULL, SW_SHOW);          
}

void _setup(){
     int nodata;
     _beginthread(WindowThread, 0, &nodata);
     while(!_winset) Sleep(100);
//     CreateThread(NULL,0,WindowThread,&nodata,0,NULL);
//     if(_win != NULL) _hdc = GetDC(_win);
//     else _hdc = CreateDC("DISPLAY", NULL, NULL, NULL);
     _set = 1;
}

unsigned char inb(int port){return 0;}

void updatekeymap(){}

char checkkey(char key){return 0;}

void hlt(){
	if(!_timeset){
		Sleep(2);
		gettimeofday(&_timeoday,NULL);
		_timeset = 1;
	}else{
		long next = _timeoday.tv_sec*1000000 + _timeoday.tv_usec;
		gettimeofday(&_timeoday,NULL);
		long cur = _timeoday.tv_sec*1000000 + _timeoday.tv_usec;
		while(next<cur)
			next += 2250;
		int mt = (int)(next - cur);
		Sleep(mt/1000);
		gettimeofday(&_timeoday,NULL);
	}
	updatekeymap();
}

COLORREF Convert(int color){
    int ret = ((color&0xFF)<<16);
    ret += color&0xFF00;
    ret += ((color&0xFF0000)>>16);
    return (COLORREF) ret;
}

PAINTSTRUCT ps;

void StartPaint(){
     InvalidateRect(_win, NULL, FALSE);
     while(!_painting);
     _hdc = BeginPaint(_win, &ps);
}

void StopPaint(){
     EndPaint(_win, &ps);
     _painting = 0;
}

void putpixel(int x, int y, int color){
     if(!_set) _setup();
     StartPaint();
     SetPixel(_hdc, x, y, Convert(color));
     StopPaint();
}

void drawtext(int x, int y, int back, int fore, char *text){
     if(!_set) _setup();
     StartPaint();
     SetTextColor(_hdc, Convert(fore));
     SetBkColor(_hdc, Convert(back));
     TextOut(_hdc, x, y, text, strlen(text));
     StopPaint();
}

void drawline(int x, int y, int x2, int y2, int color){
     if(!_set) _setup();
     StartPaint();
     SelectObject(_hdc, CreatePen(PS_SOLID, 1, Convert(color)));
     MoveToEx(_hdc, x, y, NULL);
     LineTo(_hdc, x2, y2);
     StopPaint();
}

void drawcircle(int x, int y, int radius, int color){
     if(!_set) _setup();
     StartPaint();
     SelectObject(_hdc, CreatePen(PS_SOLID, 1, Convert(color)));
     Arc(_hdc, x-radius, y-radius, x+radius, y+radius,
               x-radius, y, x-radius, y);
     StopPaint();
}

void fillcircle(int x, int y, int radius, int color){
     if(!_set) _setup();
     StartPaint();
     SelectObject(_hdc, CreatePen(PS_SOLID, 1, Convert(color)));
     SelectObject(_hdc, CreateSolidBrush(Convert(color)));
     Ellipse(_hdc, x-radius, y-radius, x+radius, y+radius);     
     StopPaint();
}

void drawrect(int x, int y, int x2, int y2, int color){}

void fillrect(int x, int y, int x2, int y2, int color){
     if(!_set) _setup();
     StartPaint();
     SelectObject(_hdc, CreatePen(PS_SOLID, 1, Convert(color)));
     SelectObject(_hdc, CreateSolidBrush(Convert(color)));
     RECT r = {x, y, x2, y2};
     FillRect(_hdc, &r, CreateSolidBrush(Convert(color)));
     StopPaint();
}

void clear(int color){
     if(!_set) _setup();
     StartPaint();
     fillrect(0, 0, _winX, _winY, color);
     StopPaint();
}

void getinfo(screeninfo * sc){
     sc->x = _winX;
     sc->y = _winY;
     sc->color = 0;
}

void reset(){}
