package de.test.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicResourceHandler extends ResourceHandlerWrapper {
	
	private static final Logger LOG = LoggerFactory.getLogger(DynamicResourceHandler.class.getName());

    private ResourceHandler wrapped;

    public DynamicResourceHandler(ResourceHandler wrapped) {
        LOG.info("[DynamicResourceHandler]");
    	this.wrapped = wrapped;
    }

    @Override
    public ViewResource createViewResource(FacesContext context, String resourceName) {
        LOG.info("createViewResource(): resourceName = '" + resourceName + "'");
    	if ("/dynamic.xhtml".equals(resourceName)) {
            try {
                File file = File.createTempFile("dynamic-", ".xhtml");

                try (Writer writer = new FileWriter(file)) {
                    writer
                        .append("<ui:composition")
                        .append(" xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"")
                        .append(" xmlns:h=\"http://xmlns.jcp.org/jsf/html\"")
                        .append(" template=\"/WEB-INF/resources/template/template.xhtml\"")
                        .append(">")
                        .append("<ui:define name=\"title\">")
                        .append("Title from dynamic")
                        .append("</ui:define>")
                        .append("<ui:define name=\"content\">")
                        .append("<p>Hello from a dynamic include!</p>")
                        .append("<p>The below should render as a real input field:</p>")
                        .append("<p><h:inputText /></p>")
                        .append("</ui:define>")
                        .append("</ui:composition>");
                }

                final URL url = file.toURI().toURL();
                LOG.info("createViewResource(): URL = " + url.toString());
                
                return new ViewResource() {
                    @Override
                    public URL getURL() {
                        return url;
                    }
                };
            }
            catch (IOException e) {
                throw new FacesException(e);
            }
        }

        return super.createViewResource(context, resourceName);
    }

    @Override
    public ResourceHandler getWrapped() {
    	return wrapped;
	}

}