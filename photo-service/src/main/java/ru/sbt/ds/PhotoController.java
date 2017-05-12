package ru.sbt.ds;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.Files.readAllBytes;

@RestController
public class PhotoController {
    @Value("${picture.path}")
    private String picturePath;

    @RequestMapping(value = "/img", produces = "image/jpeg")
    public byte[] getById(int version) throws IOException {
        return readAllBytes(new File(picturePath + getVersionStr(version)).toPath());
    }

    private String getVersionStr(int version) {
        return "" + (version == 1
                ? ""
                : ("." + version));
    }
}
