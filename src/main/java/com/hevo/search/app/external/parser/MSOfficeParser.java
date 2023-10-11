package com.hevo.search.app.external.parser;

import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MSOfficeParser implements ContentParser {
    private final OOXMLParser msofficeparser = new OOXMLParser();
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<String>() {{
        add("docx");
        add("doc");
        add("xls");
    }};

    @Override
    public Set<String> getContentTypeSupported() {
        return SUPPORTED_EXTENSIONS;
    }

    @Override
    public Parser getParser() {
        return msofficeparser;
    }
}
