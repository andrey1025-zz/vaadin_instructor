package com.petrz.instructors.util;

import java.io.*;

import org.apache.commons.io.FileUtils;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;
import org.springframework.core.io.ClassPathResource;

public class DownloadLink extends Anchor {

    private static final long serialVersionUID = 1L;

    public DownloadLink(String filePath, String downloadAsFilename, String label) {
        //Anchor anchor = new Anchor(getStreamResource(file.getName(), file), file.getName());
        Anchor anchor = new Anchor(getStreamResource(filePath, downloadAsFilename), label);
        anchor.getElement().setAttribute("download", true);
        anchor.setHref(getStreamResource(filePath, downloadAsFilename));
        add(anchor);
    }

    public StreamResource getStreamResource(String filePath, String downloadAsFilename) {
        return new StreamResource(downloadAsFilename, () -> {
            try {
                return new BufferedInputStream(new ClassPathResource(filePath).getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
