scc="/SollerOS/cross/bin/i586-pc-solleros-gcc -lgui -LSollerOS"
$scc -s 3d.c -lm -o SollerOS/3d.elf
$scc -s asteroids.c -lm -o SollerOS/asteroids.elf
$scc -s gravity.c  -o SollerOS/gravity.elf
$scc -s raytrace.c -o SollerOS/raytrace.elf
$scc -s screensaver.c -o SollerOS/screensaver.elf
hcc="gcc -LOSX -lgui -L/usr/X11/lib -lX11 -msse3"
$hcc physics.c -lm -o OSX/physics
$hcc 3d.c -lm -o OSX/3d
$hcc asteroids.c -lm -o OSX/asteroids
$hcc gravity.c -o OSX/gravity
$hcc raytrace.c -o OSX/raytrace
$hcc screensaver.c -o OSX/screensaver
