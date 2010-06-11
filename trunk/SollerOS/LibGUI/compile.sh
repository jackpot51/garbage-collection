scc="/SollerOS/cross/bin/i586-pc-solleros-gcc"
$scc -s gravity.c -L SollerOS -lgui -o SollerOS/gravity.elf
$scc -s raytrace.c -L SollerOS -lgui -o SollerOS/raytrace.elf
$scc -s screensaver.c -L SollerOS -lgui -o SollerOS/screensaver.elf
gcc -s gravity.c -L X11 -lgui -lX11 -o X11/gravity.elf
gcc -s raytrace.c -L X11 -lgui -lX11 -o X11/raytrace.elf
gcc -s screensaver.c -L SollerOS -lgui -o X11/screensaver.elf
