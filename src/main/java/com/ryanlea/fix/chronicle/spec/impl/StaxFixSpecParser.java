package com.ryanlea.fix.chronicle.spec.impl;

import com.ryanlea.fix.chronicle.spec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StaxFixSpecParser {

    private static final Logger log = LoggerFactory.getLogger(StaxFixSpecParser.class);

    private static final Map<String, ElementHandler> elementHandlers = new HashMap<String, ElementHandler>();

    static {
        elementHandlers.put("fix", new FixElementHandler());
        elementHandlers.put("header", new HeaderElementHandler());
        elementHandlers.put("trailer", new TrailerElementHandler());
        elementHandlers.put("field", new FieldElementHandler());
        elementHandlers.put("message", new MessageElementHandler());
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
                    case XMLEvent.START_ELEMENT:
                        final String elementName = reader.getLocalName();
                        ElementHandler elementHandler = elementHandlers.get(elementName);
                        if (elementHandler != null) {
                            elementHandler.handle(context);
                        } else {
                            // report an error
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        break;
                    case XMLStreamConstants.ATTRIBUTE:
                        break;
                }
            }
            fixSpec = context.fixSpec;
        } catch (XMLStreamException e) {
            log.error("Failed to created xml stream reader.", e);
        }
        return fixSpec;
    }

    private static interface ElementHandler {

        void handle(ParsingContext context);
    }

    private static class FixElementHandler implements ElementHandler {

        public void handle(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            int major = Integer.parseInt(reader.getAttributeValue(null, "major"));
            int minor = Integer.parseInt(reader.getAttributeValue(null, "minor"));

            final FixSpec fixSpec = new FixSpec();
            fixSpec.setMajor(major);
            fixSpec.setMinor(minor);
        }
    }

    private static class HeaderElementHandler implements ElementHandler {

        public void handle(ParsingContext context) {
            HeaderDefinition headerDefinition = new HeaderDefinition();
            context.fixSpec.setHeaderDefinition(headerDefinition);
        }
    }

    private static class TrailerElementHandler implements ElementHandler {

        public void handle(ParsingContext context) {
            TrailerDefinition trailerDefinition = new TrailerDefinition();
            context.fixSpec.setTrailerDefinition(trailerDefinition);
        }
    }

    private static class MessageElementHandler implements ElementHandler {

        public void handle(ParsingContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private static class FieldElementHandler implements ElementHandler {

        public void handle(ParsingContext context) {
            final XMLStreamReader reader = context.reader;
            String number = reader.getAttributeValue(null, "number");
            String name = reader.getAttributeValue(null, "name");
            String requiredValue = reader.getAttributeValue(null, "required");
            boolean required = requiredValue != null && "Y".equals(requiredValue);

            if (number != null && !number.trim().isEmpty()) {
                // handle as a FieldDefinition
                FieldDefinition fieldDefinition = new FieldDefinition(Integer.parseInt(number), name, required);
                context.fixSpec.addFieldDefinition(fieldDefinition);
            } else {
                // handle as a FieldReference
                FieldReference fieldReference = new FieldReference(name, required);

                EntityDefinition entityDefinition = context.peek(EntityDefinition.class);
                entityDefinition.addFieldReference(fieldReference);
            }
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

        private <T> T pop(Class<T> clazz) {
            return (T) stack.pop();
        }

        private <T> T peek(Class<T> clazz) {
            return (T) stack.peek();
        }

    }
}
