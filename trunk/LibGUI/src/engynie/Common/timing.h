#include <time.h>

FPN fpntime(struct timeval ts){
	FPN ret = (FPN)ts.tv_sec;
	ret += (FPN)ts.tv_usec/1000000.0;
	return ret;
}

FPN currentfpntime(){
	struct timeval ts;
	gettimeofday(&ts, NULL);
	return fpntime(ts);
}