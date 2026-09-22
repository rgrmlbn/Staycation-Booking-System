

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReviewUpdateRequest {

    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(min = 5, max = 2000)
    private String comment;
}