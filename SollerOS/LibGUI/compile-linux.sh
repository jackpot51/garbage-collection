<<<<<<< .mine
#scc="/SollerOS/cross/bin/i586-pc-solleros-gcc -LSollerOS -lgui"
#$scc -s 3d.c -lm -o SollerOS/3d.elf
#$scc -s asteroids.c -lm -o SollerOS/asteroids.elf
#$scc -s gravity.c -o SollerOS/gravity.elf
#$scc -s raytrace.c -o SollerOS/raytrace.elf
#$scc -s screensaver.c -o SollerOS/screensaver.elf
flags="-LX11 -lgui -lX11 -s" 
gcc 3d.c -o X11/3d.elf $flags -lm
gcc asteroids.c -o X11/asteroids.elf $flags -lm
gcc gravity.c -o X11/gravity.elf $flags
gcc raytrace.c -o X11/raytrace.elf $flags
gcc screensaver.c -o X11/screensaver.elf $flags
=======
scc="/SollerOS/cross/bin/i586-pc-solleros-gcc -LSollerOS -lgui"
$scc -s 3d.c -lm -o SollerOS/3d.elf
$scc -s asteroids.c -lm -o SollerOS/asteroids.elf
$scc -s gravity.c -o SollerOS/gravity.elf
$scc -s raytrace.c -o SollerOS/raytrace.elf
$scc -s screensaver.c -o SollerOS/screensaver.elf
hcc="gcc -LX11 -lgui -lX11 -msse3"
$hcc -s physics.c -lm -o X11/physics.elf
$hcc -s 3d.c -lm -o X11/3d.elf
$hcc -s asteroids.c -lm -o X11/asteroids.elf
$hcc -s gravity.c -o X11/gravity.elf
$hcc -s raytrace.c -o X11/raytrace.elf
$hcc -s screensaver.c -o X11/screensaver.elf
>>>>>>> .r23
