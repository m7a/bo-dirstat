dir_stat_2:
	javac -cp .:mongo-java-driver-2.11.3.jar:org-netbeans-swing-outline.jar  $$(find ma/dirstat -name '*.java')

jar:
	mkdir /tmp/ma_dir_stat_2
	7z x -o/tmp/ma_dir_stat_2 mongo-java-driver-2.11.3.jar
	rm -r /tmp/ma_dir_stat_2/META-INF
	7z x -o/tmp/ma_dir_stat_2 org-netbeans-swing-outline.jar
	rm -r /tmp/ma_dir_stat_2/META-INF
	rm /tmp/ma_dir_stat_2/git-hash
	cp -R ma /tmp/ma_dir_stat_2
	cd /tmp/ma_dir_stat_2 && jar cfve /tmp/dirstat2.jar ma.dirstat.Main *
	mv /tmp/dirstat2.jar .
	rm -r /tmp/ma_dir_stat_2

test:
	./invoc.sh --eval --name=linux4

clean:
	find ma/dirstat -name '*.class' -delete
