package com.ryanlea.fix.chronicle.spec.parser.impl;

import com.ryanlea.fix.chronicle.spec.parser.FixSpecParser;
import com.ryanlea.fix.chronicle.spec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StaxFixSpecParser implements FixSpecParser {

    private static final Logger log = LoggerFactory.getLogger(StaxFixSpecParser.class);

    private static final Map<String, ElementHandler> elementHandlers = new HashMap<String, ElementHandler>();

    static {
        elementHandlers.put("fix", new FixElementHandler());
        elementHandlers.put("header", new HeaderElementHandler());
        elementHandlers.put("trailer", new TrailerElementHandler());
        elementHandlers.put("field", new FieldElementHandler());
        elementHandlers.put("message", new MessageElementHandler());
        elementHandlers.put("group", new GroupElementHandler());
        elementHandlers.put("value", new ValueElementHandler());
    }

    public FixSpec parse(InputStream inputStream) {
        FixSpec fixSpec = null;
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        final XMLReporter reporter = factory.getXMLReporter();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
            final ParsingContext context = new ParsingContext(reporter, reader);

            while (reader.hasNext()) {
                final int event = reader.next();
                switch (event) {
                    case XMLEvent.START_ELEMENT: {
                        final String elementName = reader.getLocalName();
                        ElementHandler elementHandler = elementHandlers.get(elementName);
                        if (elementHandler != null) {
                            elementHandler.handleStart(context);
                        } else {
                            // report an error
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        final String elementName = reader.getLocalName();
                        ElementHandler elementHandler = elementHandlers.get(elementName);
                        if (elementHandler != null) {
                            elementHandler.handleEnd(context);
                        } else {
                            // report an error
                        }
                        break;
                    }
                    case XMLStreamConstants.ATTRIBUTE:
                        break;
                }
            }
            fixSpec = context.fixSpec;
            fixSpec.init();
        } catch (XMLStreamException e) {
            log.error("Failed to created xml stream reader.", e);
        }
        return fixSpec;
    }

    private static interface ElementHandler {

        void handleStart(ParsingContext context);

        void handleEnd(ParsingContext context);
    }

    private static class FixElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            int major = Integer.parseInt(reader.getAttributeValue(null, "major"));
            int minor = Integer.parseInt(reader.getAttributeValue(null, "minor"));

            final FixSpec fixSpec = new FixSpec();
            fixSpec.setMajor(major);
            fixSpec.setMinor(minor);
            context.fixSpec = fixSpec;
        }

        public void handleEnd(ParsingContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private static class HeaderElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            HeaderDefinition headerDefinition = new HeaderDefinition();
            context.fixSpec.setHeaderDefinition(headerDefinition);
            context.push(headerDefinition);
        }

        public void handleEnd(ParsingContext context) {
            context.pop();
        }
    }

    private static class TrailerElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            TrailerDefinition trailerDefinition = new TrailerDefinition();
            context.fixSpec.setTrailerDefinition(trailerDefinition);
            context.push(trailerDefinition);
        }

        public void handleEnd(ParsingContext context) {
            context.pop();
        }
    }

    private static class MessageElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            String name = reader.getAttributeValue(null, "name");
            String type = reader.getAttributeValue(null, "msgtype");
            String category = reader.getAttributeValue(null, "msgcat");
            MessageDefinition messageDefinition = new MessageDefinition(name, type, category);
            context.fixSpec.addMessageDefinition(messageDefinition);
            context.push(messageDefinition);
        }

        public void handleEnd(ParsingContext context) {
            context.pop();
        }
    }

    private static class FieldElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            String number = reader.getAttributeValue(null, "number");
            String name = reader.getAttributeValue(null, "name");
            String requiredValue = reader.getAttributeValue(null, "required");
            boolean required = requiredValue != null && "Y".equals(requiredValue);

            if (number != null && !number.trim().isEmpty()) {
                // handle as a FieldDefinition
                String type = reader.getAttributeValue(null, "type");
                FieldDefinition fieldDefinition = new FieldDefinition(Integer.parseInt(number), name, FieldType.fromString(type));
                context.fixSpec.addFieldDefinition(fieldDefinition);
                context.push(fieldDefinition);
            } else {
                // handle as a FieldReference
                FieldReference fieldReference = new FieldReference(name, required);

                EntityDefinition entityDefinition = context.peek(EntityDefinition.class);
                entityDefinition.addFieldReference(fieldReference);
            }
        }

        public void handleEnd(ParsingContext context) {
            context.popIf(FieldDefinition.class);
        }
    }

    private static class GroupElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            String name = reader.getAttributeValue(null, "name");
            String requiredValue = reader.getAttributeValue(null, "required");
            boolean required = requiredValue != null && "Y".equals(requiredValue);

            final GroupDefinition groupDefinition = new GroupDefinition(name, required);
            final EntityDefinition owner = context.peek(EntityDefinition.class);
            owner.addFieldReference(groupDefinition);
            context.push(groupDefinition);
        }

        public void handleEnd(ParsingContext context) {
            context.popIf(GroupDefinition.class);
        }
    }

    private static class ValueElementHandler implements ElementHandler {

        public void handleStart(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            String enumValue = reader.getAttributeValue(null, "enum");
            String description = reader.getAttributeValue(null, "description");

            final ValueDefinition valueDefinition = new ValueDefinition(enumValue, description);

            FieldDefinition fieldDefinition = context.peek(FieldDefinition.class);
            fieldDefinition.addValue(valueDefinition);
        }

        public void handleEnd(ParsingContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private static class ParsingContext {

        private final XMLReporter reporter;

        private final XMLStreamReader reader;

        private final Stack stack = new Stack();

        private FixSpec fixSpec;

        private ParsingContext(XMLReporter reporter, XMLStreamReader reader) {
            this.reporter = reporter;
            this.reader = reader;
        }

        private void push(Object obj) {
            stack.push(obj);
        }

        private void pop() {
            if (stack.isEmpty()) {
                return;
            }
            stack.pop();
        }

        private <T> T peek(Class<T> clazz) {
            return (T) stack.peek();
        }

        public <T> T popIf(Class<T> aClass) {
            final Object item = stack.peek();
            if (aClass.isAssignableFrom(item.getClass())) {
                return (T) stack.pop();
            }
            return null;
        }
    }
}
