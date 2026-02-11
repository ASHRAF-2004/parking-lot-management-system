JAVAC := javac
JAVA := java

MAIN_CLASS := Main
MAIN_SRC := Main.java

LIB_DIR := lib
SQLITE_JAR := $(LIB_DIR)/sqlite-jdbc-3.46.0.0.jar
SLF4J_API_JAR := $(LIB_DIR)/slf4j-api-2.0.13.jar
SLF4J_SIMPLE_JAR := $(LIB_DIR)/slf4j-simple-2.0.13.jar

ifeq ($(OS),Windows_NT)
CP_SEP := ;
else
CP_SEP := :
endif

RUNTIME_CP := .$(CP_SEP)$(SQLITE_JAR)$(CP_SEP)$(SLF4J_API_JAR)$(CP_SEP)$(SLF4J_SIMPLE_JAR)

.PHONY: all deps compile run clean

all: compile

deps:
	@mkdir -p $(LIB_DIR)
	@test -f $(SQLITE_JAR) || curl -L -o $(SQLITE_JAR) https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.0.0/sqlite-jdbc-3.46.0.0.jar
	@test -f $(SLF4J_API_JAR) || curl -L -o $(SLF4J_API_JAR) https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar
	@test -f $(SLF4J_SIMPLE_JAR) || curl -L -o $(SLF4J_SIMPLE_JAR) https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.13/slf4j-simple-2.0.13.jar

compile:
	$(JAVAC) $(MAIN_SRC)

run: deps compile
	$(JAVA) -cp "$(RUNTIME_CP)" $(MAIN_CLASS)

clean:
	find . -name "*.class" -delete
