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

public class BarcodeGenerator {

	public static BufferedImage generate2DBarcode(String barcodeMsg,
			Rectangle rect, int dpi) throws IOException,
			BarcodeCreationException {

		if (barcodeMsg == null || barcodeMsg.length() == 0)
			throw new BarcodeCreationException(
					"null or empty msg specified to 2D barcode generator");

		if (rect == null)
			throw new BarcodeCreationException(
					"null dimensions specified to 1D barcode generator");

		if (rect.width < 0 || rect.width > 100)
			throw new BarcodeCreationException("1d bacode width range is 0-100");

		if (rect.height < 0 || rect.height > 100)
			throw new BarcodeCreationException(
					"1d bacode height range is 0-100");

		if (dpi < 1 || dpi > 1000)
			throw new BarcodeCreationException("dpi range is 1-1000");

		DataMatrixBean barcodeGenDataMatrix = new DataMatrixBean();
		barcodeGenDataMatrix.setMaxSize(new Dimension(rect.height,rect.height));
		barcodeGenDataMatrix.setMinSize(new Dimension(rect.height,rect.height));
		barcodeGenDataMatrix.setModuleWidth(rect.width/10);

		BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi,
				BufferedImage.TYPE_BYTE_GRAY, true, 0);
		barcodeGenDataMatrix.generateBarcode(provider, barcodeMsg);

		provider.finish();
		return provider.getBufferedImage();
	}

	public static BufferedImage generate1DBarcode(String barcodeMsg, Font font,
			Rectangle rect, int dpi) throws BarcodeCreationException,
			IOException {

		if (font == null)
			throw new BarcodeCreationException(
					"null font specified to 1D barcode generator");

		if (barcodeMsg == null || barcodeMsg.length() == 0)
			throw new BarcodeCreationException(
					"null or empty msg specified to 1D barcode generator");

		if (rect == null)
			throw new BarcodeCreationException(
					"null dimensions specified to 1D barcode generator");

		if (rect.width < 0 || rect.width > 100)
			throw new BarcodeCreationException("1d bacode width range is 0-100");

		if (rect.height < 0 || rect.height > 100)
			throw new BarcodeCreationException(
					"1d bacode height range is 0-100");

		if (dpi < 1 || dpi > 1000)
			throw new BarcodeCreationException("dpi range is 1-1000");

		Code128Bean barcodeGenCode128 = new Code128Bean();
		barcodeGenCode128.setBarHeight(rect.height / 10.0);// 6
		barcodeGenCode128.setModuleWidth(rect.width / 100.0);// 0.2
		barcodeGenCode128.setFontName(font.getName());

		BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi,
				BufferedImage.TYPE_BYTE_GRAY, true, 0);
		barcodeGenCode128.generateBarcode(provider, barcodeMsg);

		provider.finish();
		return provider.getBufferedImage();
	}

}
