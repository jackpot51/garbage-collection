#include <stdio.h>
#include <errno.h>
#include <sys/time.h>
struct timeval begin, end;

int main(int argc, char *argv[]){
	int err = 0;
	if(argc==2){
		int number = atoi(argv[1]);
		int i;
		if(number==0){
			errno=EINVAL;
			perror("The number must be larger than 0.");
			err=1;
		}else{
			gettimeofday(&begin, NULL);
			for(i=1;i<=number;i++){
				printf("Line #%d\n",i);
			}
			gettimeofday(&end, NULL);
			int uduration = (end.tv_sec - begin.tv_sec)*1000000 + end.tv_usec - begin.tv_usec;
			float duration = (float)(uduration)/1000000;
			printf("Printed %d lines in %f seconds.\n", number, duration);
			if(duration==0){
				err=3;
				printf("Cannot divide by zero.\n");
			}else{
				float speed = (float)(number)/duration;
				printf("This is at a rate of %f lines per second.\n",speed);
			}
		}
	}else{
		errno=EINVAL;
		perror("You must supply a single number as the command argument.");
		err=2;
	}
	return err;
}