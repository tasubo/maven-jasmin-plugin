maven-jasmin-plugin
===================

Plugin to make easier to compile programs written in Jasmin Java bytecode assembly language


This plugin bundles Jasmin code in itself to ease distribution for such old piece of code. Main use should be for Java Bytecode learning :)

Add pom.xml config:
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.tasubo.maven</groupId>
                <artifactId>maven-jasmin-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile-jasmin</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

Jasmine files should end with .j

Write Jasmin code (HelloWorld.j):
```
.class public HelloWorld
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
  .limit stack 2
  .limit locals 1

  getstatic      java/lang/System/out Ljava/io/PrintStream;
  ldc            "Hello World."
  invokevirtual  java/io/PrintStream/println(Ljava/lang/String;)V
  return

.end method
```

Compiled files are put in target/classes folder


