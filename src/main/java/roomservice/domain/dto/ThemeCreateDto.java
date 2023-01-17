package roomservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * ThemeCreateDto contains what to get from clients when creating theme.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeCreateDto {
    @NotNull
    private String name;
    private String desc;
    @PositiveOrZero
    private Integer price;
}
