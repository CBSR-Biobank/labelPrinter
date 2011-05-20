package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.ElementCreationException;



public class BarcodeGenerator {
	
	// datamatrix TODO implement width,height
	public static BufferedImage generate2DBarcode(String barcodeMsg) throws IOException, BarcodeCreationException{
		
		if(barcodeMsg == null || barcodeMsg.length() == 0)
			throw new BarcodeCreationException("null or empty msg specified to 2D barcode generator");
		
		DataMatrixBean barcodeGenDataMatrix = new DataMatrixBean();
		barcodeGenDataMatrix.setMaxSize(new Dimension(18,18));
		barcodeGenDataMatrix.setMinSize(new Dimension(18,18));
		barcodeGenDataMatrix.setModuleWidth(1);
		
		BitmapCanvasProvider provider = new BitmapCanvasProvider(100,
				BufferedImage.TYPE_BYTE_GRAY, true, 0);
		barcodeGenDataMatrix.generateBarcode(provider, barcodeMsg);
		
		provider.finish();
		return provider.getBufferedImage();
	}
	
	// Code 128 TODO implement width,height
	public static BufferedImage generate1DBarcode(String barcodeMsg, Font  font, Rectangle rect, int dpi) throws BarcodeCreationException, IOException{
		
		if(font == null)
			throw new BarcodeCreationException("null font specified to 1D barcode generator");
		
		if(barcodeMsg == null || barcodeMsg.length() == 0)
			throw new BarcodeCreationException("null or empty msg specified to 1D barcode generator");
		
		if(rect == null)
			throw new BarcodeCreationException("null dimensions specified to 1D barcode generator");
		
		if(dpi < 1 || dpi > 1000)
			throw new BarcodeCreationException("dpi range is 1-1000");
		
		
		Code128Bean barcodeGenCode128 = new Code128Bean();
		barcodeGenCode128.setBarHeight(6);
		barcodeGenCode128.setModuleWidth(0.20);//(size.getWidth()/100)
		barcodeGenCode128.setFontName(font.getName());
		

		BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi,
				BufferedImage.TYPE_BYTE_GRAY, true, 0);
		barcodeGenCode128.generateBarcode(provider, barcodeMsg);
		

		provider.finish();
		return provider.getBufferedImage();
	}
	
}
