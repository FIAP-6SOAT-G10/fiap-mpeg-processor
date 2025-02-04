build:
	mvn compile

unit-test:
	mvn test -P unit-test

production:
	mvn clean install -DskipTests -Pprd -q