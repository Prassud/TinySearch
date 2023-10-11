package com.hevo.search.app.external.parser;

import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.txt.TXTParser;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TextParser implements ContentParser {

    private static  final Set<String> SUPPORTED_EXTENSIONS = new HashSet<String>(){{
        add("txt");
        add("csv");
        add("json");
    }};

    @Override
    public Set<String> getContentTypeSupported() {
        return SUPPORTED_EXTENSIONS;
    }

    private final TXTParser textPraser = new TXTParser();
    @Override
    public Parser getParser() {
        return textPraser;
    }
}