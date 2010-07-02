scc="/SollerOS/cross/bin/i586-pc-solleros-gcc"
$scc -s 3d.c -L SollerOS -lgui -lm -o SollerOS/3d.elf
$scc -s asteroids.c -L SollerOS -lgui -lm -o SollerOS/asteroids.elf
$scc -s gravity.c -L SollerOS -lgui -o SollerOS/gravity.elf
$scc -s raytrace.c -L SollerOS -lgui -o SollerOS/raytrace.elf
$scc -s screensaver.c -L SollerOS -lgui -o SollerOS/screensaver.elf
gcc 3d.c -LOSX -lgui -L/usr/X11/lib -lX11 -lm -o OSX/3d
gcc asteroids.c -LOSX -lgui -L/usr/X11/lib -lX11 -lm -o OSX/asteroids
gcc gravity.c -LOSX -lgui -L/usr/X11/lib -lX11 -o OSX/gravity
gcc raytrace.c -LOSX -lgui -L/usr/X11/lib -lX11 -o OSX/raytrace
gcc screensaver.c -LOSX -lgui -L/usr/X11/lib -lX11 -o OSX/screensaver
