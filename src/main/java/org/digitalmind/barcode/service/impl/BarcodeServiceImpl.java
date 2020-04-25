package org.digitalmind.barcode.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.digitalmind.barcode.config.BarcodeModuleConfig;
import org.digitalmind.barcode.dto.BarcodeRequest;
import org.digitalmind.barcode.config.BarcodeConfig;
import org.digitalmind.barcode.dto.BarcodeResponse;
import org.digitalmind.barcode.exception.BarcodeException;
import org.digitalmind.barcode.service.BarcodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = BarcodeModuleConfig.ENABLED, havingValue = "true")
@Slf4j
public class BarcodeServiceImpl implements BarcodeService {

    private final ResourceLoader resourceLoader;
    private final BarcodeConfig config;

    @Autowired
    public BarcodeServiceImpl(ResourceLoader resourceLoader, BarcodeConfig config) {
        this.resourceLoader = resourceLoader;
        this.config = config;
    }

    @Override
    public BarcodeResponse generate(BarcodeRequest request) {
        BarcodeResponse barcodeResponse = null;

        BufferedImage qrImage = null;
        BufferedImage logoImageOriginal = null;
        BufferedImage logoImage = null;
        BufferedImage combined = null;

        try {
            BitMatrix bitMatrix;
            MatrixToImageConfig matrixToImageConfig;
            ByteArrayOutputStream barcodeImageStream;
            ByteArrayResource resource;
            barcodeImageStream = new ByteArrayOutputStream();

            Map<EncodeHintType, Object> hintTypes = getHintTypes(request);
            bitMatrix = new MultiFormatWriter().encode(
                    request.getContent(),
                    request.getFormat(),
                    request.getWidth(), request.getHeight(),
                    hintTypes
            );

            if (request.getOnColor() != null && request.getOffColor() != null) {
                matrixToImageConfig = new MatrixToImageConfig(request.getOnColor(), request.getOffColor());
            } else {
                matrixToImageConfig = new MatrixToImageConfig();
            }

            final String filename = (request.getName() == null ? UUID.randomUUID() : request.getName()) + "." + request.getImageType().toLowerCase();

            if (request.getLogo() == null) {
                MatrixToImageWriter.writeToStream(bitMatrix, request.getImageType(), barcodeImageStream, matrixToImageConfig);
            } else {
                if (!BarcodeFormat.QR_CODE.equals(request.getFormat())) {
                    throw new BarcodeException("The logo is supported only for QR_CODE barcode format.");
                }

                // Load QR image
                qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);

                // Load logo image and resize it
                logoImageOriginal = getLogo(request.getLogo());
                logoImage = resize(logoImageOriginal, request.getWidth() / config.getConfig().getLogoSizeFactor(), request.getHeight() / config.getConfig().getLogoSizeFactor());


                // Calculate the delta height and width between QR code and logo
                int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
                int deltaWidth = qrImage.getWidth() - logoImage.getWidth();

                // Initialize combined image
                //BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
                combined = qrImage;
                Graphics2D g = (Graphics2D) combined.getGraphics();

                // Write QR code to new image at position 0/0
                //g.drawImage(qrImage, 0, 0, null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                // Write logo into combine image at position (deltaWidth / 2) and
                // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
                // the same space for the logo to be centered
                g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

                // Write combined image as requested image type to OutputStream
                ImageIO.write(combined, request.getImageType(), barcodeImageStream);

            }

            resource = new ByteArrayResource(barcodeImageStream.toByteArray()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());
            barcodeResponse = BarcodeResponse.builder().resource(resource).contentType(contentType).build();

        } catch (WriterException e) {
            throw new BarcodeException(e);
        } catch (IOException e) {
            throw new BarcodeException(e);
        } finally {
            if (combined != null) {
                combined.flush();
                combined = null;
            }
            if (qrImage != null) {
                qrImage.flush();
                qrImage = null;
            }

            if (logoImageOriginal != null) {
                logoImageOriginal.flush();
                logoImageOriginal = null;
            }
            if (logoImage != null) {
                logoImage.flush();
                logoImage = null;
            }
        }

        return barcodeResponse;
    }

    private BufferedImage getLogo(String logo) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:digital/common/barcode/logo/" + logo + ".png");
        return ImageIO.read(resource.getInputStream());
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private Map<EncodeHintType, Object> getHintTypes(BarcodeRequest request) {
        Map<EncodeHintType, Object> hints = new HashMap<>();

        if (request.getHintTypes() != null) {
            for (Map.Entry<EncodeHintType, Object> hintEntry : request.getHintTypes().entrySet()) {
                switch (hintEntry.getKey()) {
                    case ERROR_CORRECTION:
                        break;
                    case MARGIN:
                        hints.put(hintEntry.getKey(), Integer.parseInt(String.valueOf(hintEntry.getValue())));
                    default:
                        hints.put(hintEntry.getKey(), hintEntry.getValue());
                }
                //return hints;
            }
        }

        //fix hint for logo on qrcode generation
        if (request.getLogo() != null && BarcodeFormat.QR_CODE.name().equals(request.getFormat())) {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }
        return hints;
    }

}
