package com.ryanlea.fix.chronicle.maven.plugin;

import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.chronicle.spec.generate.impl.CodeModelSpecJavaGenerator;
import com.ryanlea.fix.chronicle.spec.parser.FixSpecParser;
import com.ryanlea.fix.chronicle.spec.parser.impl.StaxFixSpecParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @goal generate-spec-code
 */
public class GenerateSpecCodeMojo extends AbstractMojo {

    /**
     *
     * @parameter
     *   default-value="${project.basedir}/src/main/generated-sources"
     */
    private String generatedSourcesFolder;

    /**
     *
     * @parameter
     * @required
     */
    private String fixSpecFile;

    /**
     * @parameter
     * @required
     */
    private String basePackage;

    public void execute() throws MojoExecutionException, MojoFailureException {
        FixSpecParser fixSpecParser = new StaxFixSpecParser();
        FixSpec fixSpec;
        try {
            fixSpec = fixSpecParser.parse(new FileInputStream(new File(fixSpecFile)));
        } catch (FileNotFoundException e) {
            getLog().error("Failed to load fix spec file: " + fixSpecFile, e);
            return;
        }

        CodeModelSpecJavaGenerator specJavaGenerator = new CodeModelSpecJavaGenerator();
        specJavaGenerator.setBasePackage(basePackage);
        File destDir = new File(generatedSourcesFolder);
        destDir.mkdirs();
        specJavaGenerator.setDestDir(destDir);
        specJavaGenerator.generate(fixSpec);
    }

}
