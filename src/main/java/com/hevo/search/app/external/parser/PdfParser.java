package com.hevo.search.app.external.parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class PdfParser implements ContentParser {

    private static  final Set<String> SUPPORTED_EXTENSIONS = new HashSet<String>(){{
        add("pdf");
    }};

    @Override
    public Set<String> getContentTypeSupported() {
        return SUPPORTED_EXTENSIONS;
    }
    private final PDFParser pdfparser = new PDFParser();

    @Override
    public Parser getParser() {
        return pdfparser;
    }
}
