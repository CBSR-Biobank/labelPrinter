package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;

public  class Text extends Element {
	
	Font font;
	
	public Text(Rectangle rect,String message,Font font){
		this.rect = rect;
		this.type =  Element.TYPE.Text;
		this.message = message;

		if(font != null){
			this.font = font;
		}
		else{
			this.font = new Font("Times New Roman", Font.PLAIN, 20);
		}
		
	}
	
	public boolean verify(){
		return (message != null && font != null);
	}
	
	public void render(Graphics2D g) throws IOException{
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);
		g.drawString(message, rect.x, rect.y + font.getSize());
	}
	
}