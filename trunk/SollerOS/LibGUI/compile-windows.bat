@echo OFF
gcc.exe -msse3 -c asteroids.c -o asteroids.o -I"C:/Dev-Cpp/include" -g3
gcc.exe asteroids.o  -o "WinGDI/asteroids.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a -g3
del asteroids.o
gcc.exe -msse3 -c gravity.c -o gravity.o -I"C:/Dev-Cpp/include" -g3
gcc.exe gravity.o  -o "WinGDI/gravity.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a -g3
del gravity.o
gcc.exe -msse3 -c raytrace.c -o raytrace.o -I"C:/Dev-Cpp/include" -g3
gcc.exe raytrace.o  -o "WinGDI/raytrace.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a -g3
del raytrace.o
gcc.exe -msse3 -c screensaver.c -o screensaver.o -I"C:/Dev-Cpp/include" -g3
gcc.exe screensaver.o  -o "WinGDI/screensaver.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a -g3
del screensaver.o
gcc.exe -msse3 -c 3d.c -o 3d.o -I"C:/Dev-Cpp/include" -g3
gcc.exe 3d.o  -o "WinGDI/3d.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a -g3
del 3d.o


REM FOR CYGWIN:
REM set PATH=%PATH%;\cygwin\bin
REM set scc=\cygwin\SollerOS\cross\bin\i586-pc-solleros-gcc
REM %scc% -s asteroids.c -L SollerOS -lgui -lm -o SollerOS/asteroids.elf
REM %scc% -s gravity.c -L SollerOS -lgui -o SollerOS/gravity.elf
REM %scc% -s raytrace.c -L SollerOS -lgui -o SollerOS/raytrace.elf
REM %scc% -s screensaver.c -L SollerOS -lgui -o SollerOS/screensaver.elf
REM gcc-4 -s asteroids.c -L cygwin -lgui -lX11 -lm -o cygwin/asteroids.exe
REM gcc-4 -s gravity.c -L cygwin -lgui -lX11 -o cygwin/gravity.exe
REM gcc-4 -s raytrace.c -L cygwin -lgui -lX11 -o cygwin/raytrace.exe
REM gcc-4 -s screensaver.c -L cygwin -lgui -lX11 -o cygwin/screensaver.exe

pause