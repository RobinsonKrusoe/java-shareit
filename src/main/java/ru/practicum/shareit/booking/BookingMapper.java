package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking){
        if(booking != null) {
            return BookingDto.builder()
                    .id(booking.getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .item(ItemMapper.toItemDto(booking.getItem()))
                    .booker(UserMapper.toUserDto(booking.getBooker()))
                    .status(booking.getStatus().name())
                    .build();
        } else {
            return null;
        }
    }

    public static Booking toBooking(BookingDto bookingDto){
        Booking booking = new Booking();
        if(bookingDto.getId() != null){
            booking.setId(bookingDto.getId());
        }

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(ItemMapper.toItem(bookingDto.getItem()));
        booking.setBooker(UserMapper.toUser(bookingDto.getBooker()));

        if(bookingDto.getStatus() == null){
            booking.setStatus(BookingStatus.WAITING);
        } else {
            booking.setStatus(BookingStatus.valueOf(bookingDto.getStatus()));
        }

        return booking;
    }
}
