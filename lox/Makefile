JC = javac	
JFLAGS = -g
.SUFFIXES: .java .class
.java.class: 
	$(JC) $(JFLAGS) $*.java

CLASSES = Lox.java Scanner.java Token.java TokenType.java

default: classes

classes: $(CLASSES:.java=.class)

.PHONY: clean
clean:
	$(RM) *.class