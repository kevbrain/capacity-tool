package com.its4u.buildfactory.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.its4u.buildfactory.templateModel.MailAlertModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;

@Data
public class TemplateGenerator {

	private final Template mailOOMKillAlert;
	
	private final Template mailWarningCapacity;
	
	private final Template mailBlockCapacity;
	
	public TemplateGenerator(String pathTemplate ) throws IOException {    	
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        
        cfg.setDirectoryForTemplateLoading(new File(pathTemplate));
        mailOOMKillAlert = cfg.getTemplate("mailOOMKillAlert.html");
        mailWarningCapacity = cfg.getTemplate("mailWARNINGCapacity.html");
        mailBlockCapacity = cfg.getTemplate("mailBLOCKCapacity.html");
    }
	
	public String generateAlertEmail(MailAlertModel model,Template template) throws IOException, TemplateException {

        Writer out = new StringWriter();
        template.process(model, out);
        return out.toString();


    }
	
}
