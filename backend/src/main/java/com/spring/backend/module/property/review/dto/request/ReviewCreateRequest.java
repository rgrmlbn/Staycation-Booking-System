

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(min = 5, max = 2000)
    private String comment;
}