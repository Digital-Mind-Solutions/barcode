package org.digitalmind.barcode.service;

import org.digitalmind.barcode.dto.BarcodeRequest;
import org.digitalmind.barcode.dto.BarcodeResponse;

public interface BarcodeService {

    BarcodeResponse generate(BarcodeRequest request);

}
