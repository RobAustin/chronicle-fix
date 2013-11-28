package com.ryanlea.fix.chronicle.spec.generate.impl;

import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.chronicle.spec.parser.FixSpecParser;
import com.ryanlea.fix.chronicle.spec.parser.impl.StaxFixSpecParser;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class CodeModelSpecJavaGeneratorTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private FixSpecParser fixSpecParser;

    private FixSpec fixSpec;

    @Before
    public void init() {
        fixSpecParser = new StaxFixSpecParser();
        fixSpec = fixSpecParser.parse(CodeModelSpecJavaGeneratorTest.class.getResourceAsStream("/fx.fix.spec.xml"));
    }

    @Test
    public void generate() throws IOException {
        CodeModelSpecJavaGenerator generator = new CodeModelSpecJavaGenerator();
        generator.setDestDir(temporaryFolder.getRoot());
        generator.setBasePackage("com.ryanlea.fix.chronicle.message");
        generator.generate(fixSpec);

        Files.walkFileTree(temporaryFolder.getRoot().toPath(), new FileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                FileInputStream input = new FileInputStream(file.toFile());
                PrintWriter output = new PrintWriter(System.out);
                IOUtils.copy(input, output);
                input.close();
                output.flush();
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
