JAVAC := javac
JAVA := java

SRC_DIR := src
BIN_DIR := bin
LIB_DIR := lib

SQLITE_JAR := $(LIB_DIR)/sqlite-jdbc-3.46.0.0.jar
SLF4J_API_JAR := $(LIB_DIR)/slf4j-api-2.0.13.jar
SLF4J_SIMPLE_JAR := $(LIB_DIR)/slf4j-simple-2.0.13.jar

ifeq ($(OS),Windows_NT)
CP_SEP := ;
else
CP_SEP := :
endif

JAVA_FILES := $(shell find $(SRC_DIR) -type f -name '*.java')

MAIN_CLASS ?= app.Main
SMOKE_CLASS ?= smoke.SmokeChecks

COMPILE_CP := $(BIN_DIR)$(CP_SEP)$(SQLITE_JAR)$(CP_SEP)$(SLF4J_API_JAR)$(CP_SEP)$(SLF4J_SIMPLE_JAR)

.PHONY: all compile run smoke clean deps

all: compile

compile:
	@if [ -z "$(JAVA_FILES)" ]; then echo "No Java files found in $(SRC_DIR)/"; exit 1; fi
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -cp "$(COMPILE_CP)" $(JAVA_FILES)

deps:
	@mkdir -p $(LIB_DIR)
	@test -f $(SQLITE_JAR) || (echo "Missing local dependency: $(SQLITE_JAR)"; exit 1)
	@test -f $(SLF4J_API_JAR) || (echo "Missing local dependency: $(SLF4J_API_JAR)"; exit 1)
	@test -f $(SLF4J_SIMPLE_JAR) || (echo "Missing local dependency: $(SLF4J_SIMPLE_JAR)"; exit 1)

run: deps compile
	$(JAVA) -cp "$(COMPILE_CP)" $(MAIN_CLASS)

smoke: deps compile
	$(JAVA) -cp "$(COMPILE_CP)" $(SMOKE_CLASS)

clean:
	rm -rf $(BIN_DIR)
