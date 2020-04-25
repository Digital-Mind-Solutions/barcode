package org.digitalmind.barcode.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "BarcodeRequest", description = "The request object for creating a barcode.")
@JsonPropertyOrder(
        {
                "name", "content",
                "format",
                "width", "height",
                "onColor", "offColor",
                "imageType",
                "logo",
                "hintTypes"
        }
)
public class BarcodeRequest extends BarcodeSpecification {

    @ApiModelProperty(value = "The name of the barcode image", required = false)
    private String name;

    @ApiModelProperty(value = "The text to be encoded as barcode", required = true)
    private String content;


}
