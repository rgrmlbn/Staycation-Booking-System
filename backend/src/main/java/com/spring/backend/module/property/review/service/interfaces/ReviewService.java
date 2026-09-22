
public interface ReviewService {

    Page<ReviewResponse> getAllReview(int page, int size);
    Page<ReviewResponse> getAllReviewByProperty(Long propertyId, int page, int size);
    Page<ReviewResponse> getReviewByBooking(Long bookingId, int page, int size);
    Page<ReviewResponse> getAllReviewByGuest(Long guestId, int page, int size);
    ReviewResponse createReview(ReviewCreateRequest request);
    ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest update);
    void deleteReviewById(Long reviewId);


}