help:
	cat Makefile

clean:
	./gradlew clean

fmt:
	./gradlew spotlessApply

jib:
	./gradlew jibDockerBuild

pull:
	docker-compose pull

up:
	docker-compose up

down:
	docker-compose down -v --remove-orphans

start:
	./gradlew run --args="start"
