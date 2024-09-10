package com.parsingVAST.Model;

import lombok.Data;

@Data
public class MediaFile {

    private String type;      // MIME type of the media file (e.g., video/mp4)
    private int bitrate;      // Bitrate of the media file
    private int width;        // Width of the media file
    private int height;       // Height of the media file
    private String source;    // URL source of the media file
}
