

@Repository
public class ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Page<ReviewEntity> findAllByPropertyId(Long propertyId, Pageable pageable);
}
