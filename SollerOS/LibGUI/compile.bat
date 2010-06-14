@echo OFF
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
gcc.exe -c asteroids.c -o asteroids.o -I"C:/Dev-Cpp/include"    -g3
gcc.exe asteroids.o  -o "WinGDI/asteroids.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a  -g3
rm asteroids.o
gcc.exe -c gravity.c -o gravity.o -I"C:/Dev-Cpp/include"    -g3
gcc.exe gravity.o  -o "WinGDI/gravity.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a  -g3
rm gravity.o
gcc.exe -c raytrace.c -o raytrace.o -I"C:/Dev-Cpp/include"    -g3
gcc.exe raytrace.o  -o "WinGDI/raytrace.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a  -g3
rm raytrace.o
gcc.exe -c screensaver.c -o screensaver.o -I"C:/Dev-Cpp/include"    -g3
gcc.exe screensaver.o  -o "WinGDI/screensaver.exe" -L"C:/Dev-Cpp/lib" -mwindows WinGDI/libgui.a  -g3
rm screensaver.o