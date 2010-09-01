@echo OFF
gcc.exe -msse3 -c ..\asteroids.c -o asteroids.o -I"C:/Dev-Cpp/include" -g3
gcc.exe asteroids.o  -o "asteroids.exe" -L"C:/Dev-Cpp/lib" -mwindows libgui.a -g3
del asteroids.o
gcc.exe -msse3 -c ..\gravity.c -o gravity.o -I"C:/Dev-Cpp/include" -g3
gcc.exe gravity.o  -o "gravity.exe" -L"C:/Dev-Cpp/lib" -mwindows libgui.a -g3
del gravity.o
gcc.exe -msse3 -c ..\raytrace.c -o raytrace.o -I"C:/Dev-Cpp/include" -g3
gcc.exe raytrace.o  -o "raytrace.exe" -L"C:/Dev-Cpp/lib" -mwindows libgui.a -g3
del raytrace.o
gcc.exe -msse3 -c ..\screensaver.c -o screensaver.o -I"C:/Dev-Cpp/include" -g3
gcc.exe screensaver.o  -o "screensaver.exe" -L"C:/Dev-Cpp/lib" -mwindows libgui.a -g3
del screensaver.o
gcc.exe -msse3 -c ..\3d.c -o 3d.o -I"C:/Dev-Cpp/include" -g3
gcc.exe 3d.o  -o "3d.exe" -L"C:/Dev-Cpp/lib" -mwindows libgui.a -g3
del 3d.o
gcc.exe -msse3 -c ..\physics.c -o physics.o -I"C:/Dev-Cpp/include" -g3
gcc.exe physics.o  -o "physics.exe" -L"C:/Dev-Cpp/lib" -mwindows libgui.a -g3
del physics.o
pause