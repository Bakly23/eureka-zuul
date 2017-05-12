package ru.sbt.ds;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhotoClientController {
    private static final Logger logger = Logger.getLogger(PhotoClientController.class);
    private final PhotoServiceClient photoServiceClient;
    @Value("${app.version}")
    private int version;

    @Autowired
    public PhotoClientController(PhotoServiceClient photoServiceClient) {
        this.photoServiceClient = photoServiceClient;
    }

    @RequestMapping(value = "/")
    public String ping() {
        return "200";
    }

    @RequestMapping(value = "/img", produces = "image/jpeg")
    public byte[] getKoala() {
        logger.info("Version: " + version);
        return photoServiceClient.getPhoto(version);
    }
}
