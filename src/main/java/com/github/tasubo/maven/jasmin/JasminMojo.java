package com.github.tasubo.maven.jasmin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import jas.StackMap;
import jasmin.Main;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Mojo(name = "compile-jasmin",
        defaultPhase = LifecyclePhase.COMPILE,
        threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM,
        requiresDependencyCollection = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class JasminMojo extends AbstractMojo {

    @Parameter(property = "project.build.outputDirectory", required = true, readonly = true)
    private File outputDirectory;

    @Parameter(property = "project.build.sourceDirectory", required = true, readonly = true)
    private File sourceDirectory;

    @Parameter(property = "project.build.testOutputDirectory", required = true, readonly = true)
    private File testOutputDirectory;

    @Parameter(property = "project.build.testSourceDirectory", required = true, readonly = true)
    private File testSourceDirectory;
    
    
    static class JasminFileVisitor implements FileVisitor<Path> {
        private File outputDirectory;
        
        JasminFileVisitor(File outputDirectory) {
            this.outputDirectory = outputDirectory;
        }
        
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String name = file.getFileName().toFile().getName();

            if (!name.endsWith(".j")) {
                return FileVisitResult.CONTINUE;
            }

            Main main = new Main();
            StackMap.reinit();
            String fname = file.toAbsolutePath().toString();
            System.out.println("Processing: " + fname);
            main.setDest_path(outputDirectory.getAbsolutePath());
            main.assemble(fname);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }    
    }
    
    public void execute() throws MojoExecutionException {
        try {
            Files.walkFileTree(sourceDirectory.toPath(), new JasminFileVisitor(outputDirectory));
            Files.walkFileTree(testSourceDirectory.toPath(), new JasminFileVisitor(testOutputDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
