package com.ryanlea.fix.chronicle.spec.generate.impl;

import com.ryanlea.fix.chronicle.*;
import com.ryanlea.fix.chronicle.lang.MutableDateTime;
import com.ryanlea.fix.chronicle.spec.*;
import com.ryanlea.fix.chronicle.spec.generate.SpecJavaGenerator;
import com.sun.codemodel.*;
import org.joda.time.ReadableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.lit;

public class CodeModelSpecJavaGenerator implements SpecJavaGenerator {

    private static final Logger log = LoggerFactory.getLogger(CodeModelSpecJavaGenerator.class);

    private static final Map<FieldType, TypeMapping> typeMappings = new HashMap<>();

    static {
        typeMappings.put(FieldType.STRING, new TypeMapping(CharSequence.class, "_string"));
        typeMappings.put(FieldType.LENGTH, new TypeMapping(int.class, "_int"));
        typeMappings.put(FieldType.UTCTIMESTAMP, new TypeMapping(MutableDateTime.class, "_dateTime"));
        typeMappings.put(FieldType.SEQNUM, new TypeMapping(long.class, "_long"));
        typeMappings.put(FieldType.CHAR, new TypeMapping(char.class, "_char"));
        typeMappings.put(FieldType.INT, new TypeMapping(int.class, "_int"));
        typeMappings.put(FieldType.QTY, new TypeMapping(Number.class, "_decimal"));
    }

    private File destDir;

    private String basePackage;

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void setBasePackage(String destPackage) {
        this.basePackage = destPackage;
    }

    public void generate(FixSpec fixSpec) {
        JCodeModel codeModel = new JCodeModel();
        try {
            final String versionPackage = basePackage + ".fix" + String.valueOf(fixSpec.getMajor()) +
                    String.valueOf(fixSpec.getMinor());

            generateFields(fixSpec, codeModel, versionPackage);
            generateHeader(fixSpec, codeModel, versionPackage);
            generateTrailer(fixSpec, codeModel, versionPackage);
            generateComponents(fixSpec, codeModel, versionPackage);
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

    private void generateComponents(FixSpec fixSpec, JCodeModel codeModel, String versionPackage)
            throws JClassAlreadyExistsException {
        final String componentPackage = versionPackage + ".component";
        for (ComponentDefinition componentDefinition : fixSpec.getComponentDefinitions()) {
            JDefinedClass componentClass = codeModel._class(componentPackage + '.' + componentDefinition.getName());
            componentClass._extends(Component.class);

            JMethod componentConstructor = componentClass.constructor(JMod.PUBLIC);
            JVar componentDefinitionVar = componentConstructor.param(ComponentDefinition.class, "componentDefinition");
            componentConstructor.body().invoke("super").arg(componentDefinitionVar);

            Iterable<? extends FieldReference> fieldReferences = componentDefinition.getFieldReferences();

            addFieldReferences(codeModel, versionPackage, fixSpec, componentClass, componentConstructor,
                    componentDefinitionVar, fieldReferences);
        }

        // The referencing of components to components needs to be done after all the components have been defined.
        // This is why it's in second loop
        for (ComponentDefinition componentDefinition : fixSpec.getComponentDefinitions()) {
            JDefinedClass componentClass = codeModel._getClass(componentPackage + '.' + componentDefinition.getName());
            JMethod componentConstructor = componentClass.getConstructor(new JType[]{codeModel.ref(ComponentDefinition.class)});
            JVar componentDefinitionVar = componentConstructor.listParams()[0];

            addComponents(codeModel, versionPackage, componentDefinition, componentClass, componentConstructor,
                    componentDefinitionVar);
        }
    }

    private void generateFields(FixSpec fixSpec, JCodeModel codeModel, String versionPackage) {
        final String fieldPackage = versionPackage + ".field";
        for (FieldDefinition fieldDefinition : fixSpec.getFieldDefinitions()) {

        }
    }

    private void generateMessages(FixSpec fixSpec, JCodeModel codeModel, String versionPackage)
            throws JClassAlreadyExistsException {
        for (MessageDefinition messageDefinition : fixSpec.messageDefinitions()) {
            JDefinedClass messageClass = codeModel._class(versionPackage + '.' + messageDefinition.getName());
            messageClass._extends(Message.class);

            JMethod messageConstructor = messageClass.constructor(JMod.PUBLIC);
            JVar messageDefinitionVar = messageConstructor.param(MessageDefinition.class, "messageDefinition");
            JVar headerVar = messageConstructor.param(Header.class, "header");
            JVar trailerVar = messageConstructor.param(Trailer.class, "trailer");
            messageConstructor.body().invoke("super").arg(messageDefinitionVar).arg(headerVar).arg(trailerVar);

            Iterable<? extends FieldReference> fieldReferences = messageDefinition.getFieldReferences();

            addFieldReferences(codeModel, versionPackage, fixSpec, messageClass, messageConstructor,
                    messageDefinitionVar, fieldReferences);

            addComponents(codeModel, versionPackage, messageDefinition, messageClass, messageConstructor,
                    messageDefinitionVar);
        }
    }

    private void addComponents(JCodeModel codeModel, String versionPackage, EntityDefinition entityDefinition,
                               JDefinedClass entityClass, JMethod entityConstructor, JVar entityDefinitionVar) {

        final String componentPackage = versionPackage + ".component";
        final ComponentDefinition[] componentDefinitions = entityDefinition.getComponentDefinitions();
        if (componentDefinitions != null) {
            for (ComponentDefinition componentDefinition : componentDefinitions) {
                JDefinedClass componentClass = codeModel._getClass(componentPackage + '.' + componentDefinition.getName());

                // private Parties parties;
                // parties = new Parties(entityDefinition.getComponentDefinition("Parties"));
                JFieldVar componentField = entityClass.field(JMod.PRIVATE, componentClass,
                        Introspector.decapitalize(componentDefinition.getName()));
                entityConstructor.body()
                        .assign(componentField,
                                _new(componentClass).arg(
                                        entityDefinitionVar.invoke("getComponentDefinition")
                                                .arg(lit(componentDefinition.getName()))));
                entityConstructor.body().add(_super().invoke("_component").arg(componentField));

                JMethod getter = entityClass.method(JMod.PUBLIC, componentClass, "get" + componentDefinition.getName());
                getter.body()._return(componentField);
            }
        }
    }

    private void addFieldReferences(JCodeModel codeModel, String versionPackage, FixSpec fixSpec,
                                    JDefinedClass entityClass, JMethod entityConstructor, JVar entityDefinitionVar,
                                    Iterable<? extends FieldReference> fieldReferences)
            throws JClassAlreadyExistsException {

        for (FieldReference messageFieldReference : fieldReferences) {
            if (messageFieldReference instanceof GroupDefinition) {
                generateGroup(codeModel, versionPackage, fixSpec, entityClass, entityConstructor, entityDefinitionVar,
                        (GroupDefinition) messageFieldReference);
            } else {
                addGetterForFieldReference(fixSpec, entityClass, messageFieldReference, false);
            }
        }
    }

    private void generateGroup(JCodeModel codeModel, String versionPackage, FixSpec fixSpec, JDefinedClass entityClass,
                               JMethod entityConstructor, JVar entityDefinitionVar, GroupDefinition groupDefinition)
            throws JClassAlreadyExistsException {

        JDefinedClass groupClass = entityClass._class(JMod.PUBLIC | JMod.STATIC, groupDefinition.getName());
        groupClass._extends(Group.class);

        JMethod groupConstructor = groupClass.constructor(JMod.PUBLIC);
        JVar groupDefinitionVar = groupConstructor.param(GroupDefinition.class, "groupDefinition");
        groupConstructor.body().invoke("super").arg(groupDefinitionVar);

        final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(groupDefinition);
        final String getterMethod = generateGetterMethodName(fieldDefinition);
        JMethod method = entityClass.method(JMod.PUBLIC, groupClass, getterMethod);
        method.body()._return(cast(groupClass, _super().invoke("_group").arg(lit(fieldDefinition.getNumber()))));

        entityConstructor.body().add(_super().invoke("_group")
                .arg(lit(fieldDefinition.getNumber()))
                .arg(_new(groupClass)
                        .arg(entityDefinitionVar.invoke("getGroupDefinition")
                                .arg(lit(fieldDefinition.getNumber())))));

        for (FieldReference groupFieldReference : groupDefinition.getFieldReferences()) {
            addGetterForFieldReference(fixSpec, groupClass, groupFieldReference, true);
        }

        final String componentPackage = versionPackage + ".component";
        final ComponentDefinition[] componentDefinitions = groupDefinition.getComponentDefinitions();
        JMethod createComponents = groupClass.method(JMod.PROTECTED, codeModel._ref(Component.class).array(),
                "createComponents");
        JArray components = JExpr.newArray(codeModel._ref(Component.class));
        if (componentDefinitions != null) {
            for (ComponentDefinition componentDefinition : componentDefinitions) {
                JDefinedClass componentClass =
                        codeModel._getClass(componentPackage + '.' + componentDefinition.getName());

                components.add(_new(componentClass).arg(
                        _super().invoke("getGroupDefinition").invoke("getComponentDefinition")
                                .arg(lit(componentDefinition.getName()))));

                JMethod getter = groupClass.method(JMod.PUBLIC, componentClass, "get" + componentDefinition.getName());
                JVar idx = getter.param(int.class, "idx");
                JInvocation invocation = _super().invoke("_component").arg(idx).arg(lit(componentDefinition.getName()));
                getter.body()._return(cast(componentClass, invocation));
            }
        }

        createComponents.body()._return(components);
    }

    private void generateHeader(FixSpec fixSpec, JCodeModel codeModel, String versionPackage)
            throws JClassAlreadyExistsException {
        JDefinedClass headerClass = codeModel._class(versionPackage + ".Header");
        headerClass._extends(Header.class);

        JMethod constructor = headerClass.constructor(JMod.PUBLIC);
        JVar headerDefinitionVar = constructor.param(HeaderDefinition.class, "headerDefinition");
        constructor.body().invoke("super").arg(headerDefinitionVar);

        for (FieldReference fieldReference : fixSpec.getHeaderDefinition().getFieldReferences()) {
            addGetterForFieldReference(fixSpec, headerClass, fieldReference, false);
        }
    }

    private void generateTrailer(FixSpec fixSpec, JCodeModel codeModel, String versionPackage)
            throws JClassAlreadyExistsException {
        JDefinedClass trailerClass = codeModel._class(versionPackage + ".Trailer");
        trailerClass._extends(Trailer.class);

        JMethod constructor = trailerClass.constructor(JMod.PUBLIC);
        JVar trailerDefinitionVar = constructor.param(TrailerDefinition.class, "trailerDefinition");
        constructor.body().invoke("super").arg(trailerDefinitionVar);

        for (FieldReference fieldReference : fixSpec.getHeaderDefinition().getFieldReferences()) {
            addGetterForFieldReference(fixSpec, trailerClass, fieldReference, false);
        }
    }

    private void addGetterForFieldReference(FixSpec fixSpec, JDefinedClass definedClass, FieldReference fieldReference,
                                            boolean indexMethod) {
        final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(fieldReference);
        final FieldType type = fieldDefinition.getType();
        final String getterMethod = generateGetterMethodName(fieldDefinition);
        final TypeMapping typeMapping = typeMappings.get(type);
        if (typeMapping != null) {
            JMethod method = definedClass.method(JMod.PUBLIC, typeMapping.returnType, getterMethod);
            JInvocation invocation = _super().invoke(typeMapping.methodCall);
            if (indexMethod) {
                JVar idx = method.param(int.class, "idx");
                invocation.arg(idx);
            }
            invocation.arg(lit(fieldDefinition.getNumber()));
            method.body()._return(invocation);
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
