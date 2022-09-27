package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, Date end);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);
    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, Date date);
    List<Booking> findAllByBooker_IdAndStartBeforeOrderByStartDesc(Long bookerId, Date date);
    List<Booking> findAllByBooker_IdAndStartOrderByStartDesc(Long bookerId, Date date);
    List<Booking> findAllByBooker_IdAndStatus_OrderByStartDesc(Long bookerId, String status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);
    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, Date date);
    List<Booking> findAllByItem_Owner_IdAndStartBeforeOrderByStartDesc(Long ownerId, Date date);
    List<Booking> findAllByItem_Owner_IdAndStartOrderByStartDesc(Long ownerId, Date date);
    List<Booking> findAllByItem_Owner_IdAndStatus_OrderByStartDesc(Long ownerId, String status);

    @Query(value = " select count(*) from bookings " +
                   " where item_id = ?1 " +
                   " and (start_date between ?2 and ?3 or end_date between ?2 and ?3) " +
                   " and status = 'APPROVED'",
           nativeQuery = true)
    Long isBooked(Long itemId, Date start_date, Date end_date);
}
