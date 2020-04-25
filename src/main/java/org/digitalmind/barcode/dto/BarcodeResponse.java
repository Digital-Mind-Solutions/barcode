package org.digitalmind.barcode.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.core.io.Resource;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@ApiModel(value = "BarcodeResponse", description = "The result object containing a barcode.")
@JsonPropertyOrder(
        {
                "contentType",
                "resource"
        }
)
public class BarcodeResponse {

    @ApiModelProperty(value = "The format of the barcode resource", required = false)
    private String contentType;

    @ApiModelProperty(value = "The barcode resource", required = false)
    private Resource resource;

}
