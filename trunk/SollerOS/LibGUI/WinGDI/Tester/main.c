#include "libgui.h"

int main(int argc, char *argv[]){    
    int i;
    clear(0xFF0000);
    StartPaint();
    drawline(1, 1, 200, 200, 0xFF00);
    drawtext(200, 600, 0,0xFF,"hello world");
    fillcircle(400, 400, 100, 0xFFFF);
    drawcircle(100, 100, 100, 0xFF00FF);
    for(i=0;i<100;i++)
    {
        putpixel(199 + i,599,0x404040);
    }
    StopPaint();
    for(i = 1; i != 0; i++);
    //I must do this because gcc will optimize out a simple sleep()
    return i;
}
