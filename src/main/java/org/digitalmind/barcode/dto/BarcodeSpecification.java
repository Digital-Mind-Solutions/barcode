package org.digitalmind.barcode.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@ApiModel(value = "BarcodeSpecification", description = "The specification of a barcode request .")
@JsonPropertyOrder(
        {
                "format",
                "width", "height",
                "onColor", "offColor",
                "imageType",
                "logo",
                "hintTypes"
        }
)
public class BarcodeSpecification {

    @ApiModelProperty(value = "The format of the barcode", required = true)
    private BarcodeFormat format;

    @ApiModelProperty(value = "The width of the generated code", required = false)
    private Integer width;

    @ApiModelProperty(value = "The height of the generated code", required = false)
    private Integer height;

    @ApiModelProperty(value = "The onColor of the generated code", required = false)
    private Integer onColor;

    @ApiModelProperty(value = "The offColor of the generated code", required = false)
    private Integer offColor;

    @ApiModelProperty(value = "The image type of the generated code", required = true, allowableValues = "JPG, GIF, PNG, BMP")
    private String imageType;

    @ApiModelProperty(value = "The logo image on top of the generated code", required = false, allowableValues = "JPG, GIF, PNG, BMP")
    private String logo;

    @ApiModelProperty(value = "The hint types of the generated code", required = false)
    private Map<EncodeHintType, Object> hintTypes;
}
