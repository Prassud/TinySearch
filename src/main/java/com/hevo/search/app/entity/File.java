package com.hevo.search.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {
    private String extension;
    private String contentType;
    private Date created;
    private Date lastModified;
    private Date lastAccessed;
    private Date indexingDate;
    private Long filesize;
    private String filename;
    private String url;
}
