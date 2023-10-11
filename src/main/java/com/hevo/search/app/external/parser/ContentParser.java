package com.hevo.search.app.external.parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

public interface ContentParser {
    default String parse(ByteArrayOutputStream byteArrayOutputStream) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();
            getParser().parse(byteArrayInputStream, handler, metadata, pcontext);
            return handler.toString();
        } catch (IOException | SAXException | TikaException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(byteArrayInputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }

    Parser getParser();

    Set<String> getContentTypeSupported();
}
