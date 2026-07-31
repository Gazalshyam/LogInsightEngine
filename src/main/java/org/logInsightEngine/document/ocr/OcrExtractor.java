package org.logInsightEngine.document.ocr;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;


@Slf4j
@Component
public class OcrExtractor {
    private final Tesseract tesseract = new Tesseract();
    private final Logger logger = LoggerFactory.getLogger(OcrExtractor.class);
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
            tesseract.setDatapath(OCRProperties.OCR_DATA_PATH);
            tesseract.setLanguage("eng");

            try {
                String fileData = tesseract.doOCR(file);
                return fileData;
            } catch (TesseractException e) {
                logger.error("Error while performing OCR on the file: {}", multipartFile.getOriginalFilename(), e);
                return null;

            }
        } catch (Exception e) {
            logger.error("Unexpected error occurred while processing the file: {}", multipartFile.getOriginalFilename(), e);
            return null;
        } finally {
            //delete the temp file
            if (file != null && file.exists()) {
                if (!file.delete()) {
                    logger.warn("Failed to delete temporary file: {}", file.getAbsolutePath());
                }
            }
        }
    }
}
