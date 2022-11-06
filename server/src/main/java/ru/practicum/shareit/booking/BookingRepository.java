package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);

    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartBeforeAndStatusOrderByStartDesc(Long bookerId,
                                                                            LocalDateTime date,
                                                                            BookingStatus status,
                                                                            Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime sDate,
                                                                              LocalDateTime eDate, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime date, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(Long ownerId,
                                                                                LocalDateTime date,
                                                                                BookingStatus status,
                                                                                Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                                  LocalDateTime sDate,
                                                                                  LocalDateTime eDate,
                                                                                  Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    @Query(value = " select count(*) from bookings " +
                   " where item_id = ?1 " +
                   " and (start_date between ?2 and ?3 or end_date between ?2 and ?3) " +
                   " and status = 'APPROVED'",
           nativeQuery = true)
    Long isBooked(Long itemId, LocalDateTime startDate, LocalDateTime endDate);
}
