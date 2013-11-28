package com.ryanlea.fix.chronicle.spec.generate.impl;

import com.ryanlea.fix.chronicle.Group;
import com.ryanlea.fix.chronicle.Header;
import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.spec.*;
import com.ryanlea.fix.chronicle.spec.generate.SpecJavaGenerator;
import com.sun.codemodel.*;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.lit;

public class CodeModelSpecJavaGenerator implements SpecJavaGenerator {

    private static final Logger log = LoggerFactory.getLogger(CodeModelSpecJavaGenerator.class);

    private static final Map<String, TypeMapping> typeMappings = new HashMap<String, TypeMapping>();

    static {
        typeMappings.put("String", new TypeMapping(CharSequence.class, "_string"));
        typeMappings.put("Length", new TypeMapping(int.class, "_int"));
        typeMappings.put("UTCTimestamp", new TypeMapping(ReadableInstant.class, "_dateTime"));
        typeMappings.put("SeqNum", new TypeMapping(long.class, "_long"));
        typeMappings.put("char", new TypeMapping(char.class, "_char"));
        typeMappings.put("int", new TypeMapping(int.class, "_int"));
        typeMappings.put("Qty", new TypeMapping(Number.class, "_decimal"));
    }

    private File destDir;

    private String destPackage;

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void setDestPackage(String destPackage) {
        this.destPackage = destPackage;
    }

    public void generate(FixSpec fixSpec) {
        JCodeModel codeModel = new JCodeModel();
        try {
            final String versionPackage = destPackage + ".fix" + String.valueOf(fixSpec.getMajor()) + String.valueOf(fixSpec.getMinor());

            generateFields(fixSpec, codeModel, versionPackage);
            generateHeader(fixSpec, codeModel, versionPackage);

            generateMessages(fixSpec, codeModel, versionPackage);
        } catch (JClassAlreadyExistsException e) {
            log.error("Cannot create spec class, it already exists.", e);
        }
        try {
            codeModel.build(destDir);
        } catch (IOException e) {
            log.error("Failed to generate source files to [" + destDir.getAbsolutePath() + "].", e);
        }
    }

    private void generateFields(FixSpec fixSpec, JCodeModel codeModel, String versionPackage) {
        final String fieldPackage = versionPackage + ".field";
        for (FieldDefinition fieldDefinition : fixSpec.getFieldDefinitions()) {

        }
    }

    private void generateMessages(FixSpec fixSpec, JCodeModel codeModel, String versionPackage) throws JClassAlreadyExistsException {
        for (MessageDefinition messageDefinition : fixSpec.messageDefinitions()) {
            JDefinedClass messageClass = codeModel._class(versionPackage + '.' +  messageDefinition.getName());
            messageClass._extends(Message.class);

            Iterable<? extends FieldReference> fieldReferences = messageDefinition.getFieldReferences();

            for (FieldReference messageFieldReference : fieldReferences) {
                if (messageFieldReference instanceof GroupDefinition) {
                    GroupDefinition groupDefinition = (GroupDefinition) messageFieldReference;
                    JDefinedClass groupClass = messageClass._class(JMod.PUBLIC | JMod.STATIC, messageFieldReference.getName());
                    groupClass._extends(Group.class);

                    final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(messageFieldReference);
                    final String getterMethod = generateGetterMethodName(fieldDefinition);
                    JMethod method = messageClass.method(JMod.PUBLIC, groupClass, getterMethod);
                    method.body()._return(_super().invoke("_group").arg(lit(fieldDefinition.getNumber())));

                    for (FieldReference groupFieldReference : groupDefinition.getFieldReferences()) {
                        addGetterForFieldReference(fixSpec, groupClass, groupFieldReference);
                    }
                } else {
                    addGetterForFieldReference(fixSpec, messageClass, messageFieldReference);
                }
            }
        }
    }

    private void generateHeader(FixSpec fixSpec, JCodeModel codeModel, String versionPackage) throws JClassAlreadyExistsException {
        JDefinedClass headerClass = codeModel._class(versionPackage + ".Header");
        headerClass._extends(Header.class);

        for (FieldReference fieldReference : fixSpec.getHeaderDefinition().getFieldReferences()) {
            addGetterForFieldReference(fixSpec, headerClass, fieldReference);
        }
    }

    private void addGetterForFieldReference(FixSpec fixSpec, JDefinedClass definedClass, FieldReference fieldReference) {
        final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(fieldReference);
        final String type = fieldDefinition.getType();
        final String getterMethod = generateGetterMethodName(fieldDefinition);
        final TypeMapping typeMapping = typeMappings.get(type);
        if (typeMapping != null) {
            JMethod method = definedClass.method(JMod.PUBLIC, typeMapping.returnType, getterMethod);
            method.body()._return(_super().invoke(typeMapping.methodCall).arg(lit(fieldDefinition.getNumber())));
        } else {
            log.warn("Failed to find TypeMapping for type: {}", type);
        }
    }

    private String generateGetterMethodName(FieldDefinition definition) {
        return "get" + definition.getName();
    }

    private static class TypeMapping {
        private final Class<?> returnType;
        private final String methodCall;

        private TypeMapping(Class<?> returnType, String methodCall) {
            this.returnType = returnType;
            this.methodCall = methodCall;
        }

    }
}
