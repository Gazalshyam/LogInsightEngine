package org.logInsightEngine.processor.ocr;

import org.logInsightEngine.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.io.File;


@Component
public class OcrExtractor {

    public OcrExtractor() {
        ImageIO.scanForPlugins();
    }

    public String extractText(MultipartFile multipartFile) {
        //convert MultipartFile to File
        File file = null;
        try {
            String contentType = multipartFile.getContentType();
            String suffix = ".tmp";
            if (multipartFile.getOriginalFilename() != null &&
                    multipartFile.getOriginalFilename().contains(".")) {
                suffix = multipartFile.getOriginalFilename()
                        .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            }
            if ("image/webp".equalsIgnoreCase(contentType)) {
                throw new RuntimeException("WEBP images are not supported for OCR. Please upload PNG or JPG.");
            }

            file = File.createTempFile("ocr-", suffix);
            multipartFile.transferTo(file);
            //extract text from the file using Tesseract OCR  library
              Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(Constants.OCR_DATA_PATH);
            tesseract.setDatapath("/usr/share/tesseract/tessdata");
            tesseract.setLanguage("eng");

            try {
                String fileData = tesseract.doOCR(file);
                return fileData;
            } catch (TesseractException e) {
                e.printStackTrace();
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //delete the temp file
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }
}
