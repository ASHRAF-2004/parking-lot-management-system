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

MAIN_CLASS ?= $(shell \
	if [ -f $(SRC_DIR)/main/java/app/Main.java ]; then \
		echo app.Main; \
	else \
		rg -l 'public[[:space:]]+static[[:space:]]+void[[:space:]]+main' $(SRC_DIR) --glob '*.java' | head -n 1 | sed -e 's#^$(SRC_DIR)/main/java/##' -e 's#^$(SRC_DIR)/##' -e 's#/#.#g' -e 's#\.java$$##'; \
	fi)

COMPILE_CP := $(BIN_DIR)$(CP_SEP)$(SQLITE_JAR)$(CP_SEP)$(SLF4J_API_JAR)$(CP_SEP)$(SLF4J_SIMPLE_JAR)

.PHONY: all compile run clean deps

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
	@if [ -z "$(MAIN_CLASS)" ]; then echo "Could not detect a Main class."; exit 1; fi
	$(JAVA) -cp "$(COMPILE_CP)" $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)
