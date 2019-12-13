MA_OUTLINE    = org-netbeans-swing-outline.jar
MA_POSTGRESQL = postgresql-9.3-1100.jdbc41.jar
MA_TMP        = /tmp/ma_dir_stat_2
MA_INVOC      = java -ea -cp .:$(MA_OUTLINE):$(MA_POSTGRESQL) ma.dirstat.Main \
					--db=linux-fan:testwort@localhost 

ifneq ($(TEST),)
.DEFAULT_GOAL = testg
endif

dir_stat_2:
	javac -Xlint:unchecked -cp .:$(MA_OUTLINE) $$(find ma/dirstat -name \
								'*.java')

jar:
	mkdir $(MA_TMP)
	7z x -o$(MA_TMP) $(MA_OUTLINE)
	rm -r $(MA_TMP)/META-INF
	7z x -o$(MA_TMP) $(MA_POSTGRESQL)
	rm $(MA_TMP)/META-INF/MANIFEST.MF
	cp -R ma $(MA_TMP)
	cp drop.sql $(MA_TMP)
	cp README.txt $(MA_TMP)
	cd $(MA_TMP) && jar cfve /tmp/dirstat2.jar ma.dirstat.Main *
	mv /tmp/dirstat2.jar .
	rm -r $(MA_TMP)

release: jar
	mkdir dirstat2
	mv dirstat2.jar dirstat2
	# cp -R ma dirstat2 # not necessary
	cp drop.sql README.txt Makefile dirstat2
	tar -cf dirstat2.tar dirstat2
	gzip -9 dirstat2.tar
	rm -r dirstat2

test:
	$(MA_INVOC) --ping

testg:
	time $(MA_INVOC) $(TEST)

test-scan:
	$(MA_INVOC) --scan --name=linux1 --src=/

test-eval:
	$(MA_INVOC) --eval --name=linux1

clean:
	find ma/dirstat -name '*.class' -delete

dist-clean: clean
	-rm dirstat2.jar
