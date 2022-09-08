package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, Date end);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    @Query(value = " select * from bookings b " +
                   " where b.booker_id = ?1 " +
                   " and (?2 = 'ALL' or " +
                        " ?2 = b.status or" +
                        " ?2 = case " +
                                "when start_date > current_date then 'FUTURE' " +
                                "when end_date < current_date then 'PAST' " +
                                "when current_date between start_date and end_date then 'CURRENT' " +
                              "end) " +
                   " order by b.start_date desc",
           nativeQuery = true)
    List<Booking> findUserBookings(Long bookerId, String status);

    @Query(value = " select * from bookings b, items i " +
                   " where b.item_id = i.id " +
                   " and i.owner_id = ?1 " +
                   " and (?2 = 'ALL' or" +
                        " ?2 = b.status or" +
                        " ?2 = case " +
                                "when start_date > current_date then 'FUTURE' " +
                                "when end_date < current_date then 'PAST' " +
                                "when current_date between start_date and end_date then 'CURRENT' " +
                              "end) " +
                  " order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findOwnerBookings(Long ownerId, String status);

    @Query(value = " select count(*) from bookings " +
                   " where item_id = ?1 " +
                   " and (start_date between ?2 and ?3 or end_date between ?2 and ?3) " +
                   " and status = 'APPROVED'",
           nativeQuery = true)
    Long isBooked(Long itemId, Date start_date, Date end_date);
}
