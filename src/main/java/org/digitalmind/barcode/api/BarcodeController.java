package org.digitalmind.barcode.api;

import org.digitalmind.barcode.config.BarcodeModuleConfig;
import org.digitalmind.barcode.dto.BarcodeRequest;
import org.digitalmind.barcode.dto.BarcodeResponse;
import org.digitalmind.barcode.service.BarcodeService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@ConditionalOnProperty(name = BarcodeModuleConfig.API_ENABLED, havingValue = "true")
@RequestMapping("${" + BarcodeModuleConfig.PREFIX + ".api.docket.base-path}/")
@Api(value = "barcode", description = "This resource is exposing the services for barcode support", tags = {"barcode"})

public class BarcodeController {

    private final BarcodeService barcodeService;

    @Autowired
    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    //CREATE BARCODE
    @ApiOperation(
            value = "Create barcode",
            notes = "This API is used for creating a barcode image.",
            response = Resource.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Operation success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Error encountered when processing request")
    })
    @PostMapping(path = "/", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Resource> createBarcode(
            @ApiParam(name = "barcodeRequest", value = "The barcode request", required = true, allowMultiple = false) @Valid @RequestBody BarcodeRequest barcodeRequest) {

        BarcodeResponse barcodeResponse = barcodeService.generate(barcodeRequest);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(barcodeResponse.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + barcodeResponse.getResource().getFilename() + "\"")
                .body(barcodeResponse.getResource());
    }

}
