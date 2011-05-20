package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.ElementCreationException;

public  class Text extends Element {
	
	Font font;
	
	public Text(Rectangle rect,String message,Font font) throws ElementCreationException{

		if (message == null || message.length() == 0)
			throw new ElementCreationException(
					"empty or null message specified to text element.");
		
		if (rect == null)
			throw new ElementCreationException(
					"null dimensions specified to text element.");
		
		if (font == null)
			throw new ElementCreationException(
					"null font specified to text element.");
		
		this.rect = rect;
		this.type =  Element.TYPE.Text;
		this.message = message;
		this.font = font;
	}
	
	
	public void render(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);
		g.drawString(message, rect.x, rect.y + font.getSize());
	}
	
}