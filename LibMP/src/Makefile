FILES=node,task
LIBRARY=libmp
OUTPUT={${FILES}}.o
SOURCE={${FILES}}.c
HEADER={${FILES}}.h

all:
	echo "/*Automatically generated header for ${LIBRARY}*/" > ${LIBRARY}.h
	gcc -c ${SOURCE}
	for i in ${HEADER}; \
	do \
		echo "/*$$i*/" >> ${LIBRARY}.h ; \
		cat $$i >> ${LIBRARY}.h ; \
		echo "\n" >> ${LIBRARY}.h ; \
	done
	ar rcs ${LIBRARY}.a ${OUTPUT}

clean:
	rm -rf ${OUTPUT} ${LIBRARY}.a ${LIBRARY}.h
