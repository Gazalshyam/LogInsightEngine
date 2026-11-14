package org.logInsightEngine.document.ocr;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.logInsightEngine.exception.OcrProcessingException;
import org.logInsightEngine.exception.UnsupportedFileTypeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


@Slf4j
@Component
public class OcrExtractor {
    private final Tesseract tesseract = new Tesseract();
    private final OCRProperties ocrProperties;

    public OcrExtractor(OCRProperties ocrProperties) {
        this.ocrProperties = ocrProperties;
        ImageIO.scanForPlugins();
    }

    public String extractText(MultipartFile multipartFile) throws IOException {
        //convert MultipartFile to File
        File file = null;
        try {
            String contentType = multipartFile.getContentType();
            String suffix = ".tmp";
            if (multipartFile.getOriginalFilename() != null && multipartFile.getOriginalFilename().contains(".")) {
                suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            }
            if ("image/webp".equalsIgnoreCase(contentType)) {
                throw new UnsupportedFileTypeException("Please upload a PNG or JPG file. The file type is not supported " + multipartFile.getOriginalFilename());
            }

            file = File.createTempFile("ocr-", suffix);
            multipartFile.transferTo(file);
            //extract text from the file using Tesseract OCR  library
            tesseract.setDatapath(ocrProperties.getTessdataPath());
            tesseract.setLanguage("eng");

            try {
                String fileData = tesseract.doOCR(file);
                return fileData;
            } catch (TesseractException e) {
                throw new OcrProcessingException("Error while extracting information from the file: " + multipartFile.getOriginalFilename(), e);

            }
        } finally {
            //delete the temp file
            if (file != null && file.exists()) {
                if (!file.delete()) {
                    log.warn("Failed to delete temporary file: {}", file.getAbsolutePath());
                }
            }
        }
    }
}
