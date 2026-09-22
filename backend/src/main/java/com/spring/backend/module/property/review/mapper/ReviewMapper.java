@Component
public class ReviewMapper {

    public ReviewEntity toEntity(ReviewCreateRequest request,
                                  BookingEntity booking,
                                  PropertyEntity property,
                                  UserEntity guest) {

        return ReviewEntity.builder()
                .booking(booking)
                .property(property)
                .guest(guest)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    public ReviewResponse toResponse(ReviewEntity entity){

        return ReviewResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBooking().getId())
                .propertyId(entity.getPropertyId().getId())
                .guestId(entity.getGuestId().getId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .build();
    }
}