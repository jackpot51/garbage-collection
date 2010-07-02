scc="/SollerOS/cross/bin/i586-pc-solleros-gcc"
$scc -s 3d.c -L SollerOS -lgui -lm -o SollerOS/3d.elf
$scc -s asteroids.c -L SollerOS -lgui -lm -o SollerOS/asteroids.elf
$scc -s gravity.c -L SollerOS -lgui -o SollerOS/gravity.elf
$scc -s raytrace.c -L SollerOS -lgui -o SollerOS/raytrace.elf
$scc -s screensaver.c -L SollerOS -lgui -o SollerOS/screensaver.elf
gcc -s 3d.c -L X11 -lgui -lX11 -lm -o X11/3d.elf
gcc -s asteroids.c -L X11 -lgui -lX11 -lm -o X11/asteroids.elf
gcc -s gravity.c -L X11 -lgui -lX11 -o X11/gravity.elf
gcc -s raytrace.c -L X11 -lgui -lX11 -o X11/raytrace.elf
gcc -s screensaver.c -L X11 -lgui -lX11 -o X11/screensaver.elf
