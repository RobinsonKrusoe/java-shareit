package ru.practicum.shareit.booking.service;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Component
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    @Lazy
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemService itemService,
                              UserService userService){
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    /**
     * Добавление нового запроса на бронирование.
     * @param bookingInDto
     * @param userId
     * @return
     */
    @Override
    public BookingDto add(BookingInDto bookingInDto, long userId) {
        Item item = itemService.getItem(bookingInDto.getItemId());
        User booker = userService.getUser(userId);

        if(item.getOwner().getId() == userId){
            //throw new ValidationException("Нельзя брать вещи в аренду у самого себя!");
            throw new EntityNotFoundException("С какого-то перепугу, по тестам, здесь должно быть 404 а не 400!");
        }

        if(bookingInDto.getStart() == null ||
           bookingInDto.getEnd() == null ||
           bookingInDto.getStart().after(bookingInDto.getEnd()) ||
           bookingInDto.getStart().before(new Date())
        ){
            throw new ValidationException("Заданы некорректные даты бронирования!");
        }

        if(!item.isAvailable() ||
           bookingRepository.isBooked(bookingInDto.getItemId(), bookingInDto.getStart(), bookingInDto.getEnd()) > 0){
            throw new ValidationException("Вещь не доступна к бронированию!");
        }

        Booking booking = new Booking();
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.saveAndFlush(booking);

        return BookingMapper.toBookingDto(booking);
    }

    /**
     * Обновление статуса бронирования
     * @param bookingId
     * @param userId
     * @param approved
     * @return
     */

    @Override
    public BookingDto updateStatus(Long bookingId, Long userId, Boolean approved) {
        Optional<Booking> bookingInBase = bookingRepository.findById(bookingId);

        if(bookingInBase.isPresent() && bookingInBase.get().getItem().getOwner().getId() == userId){

            if((approved && bookingInBase.get().getStatus().equals(BookingStatus.APPROVED)) ||
                    (!approved && bookingInBase.get().getStatus().equals(BookingStatus.REJECTED))){
                throw new ValidationException("Некорректный статус для изменения!");
            }

            if (approved){
                bookingInBase.get().setStatus(BookingStatus.APPROVED);
            } else {
                bookingInBase.get().setStatus(BookingStatus.REJECTED);
            }

            return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(bookingInBase.get()));
        }else {
            throw new EntityNotFoundException("Бронирование с идентификатором" + bookingId + " не найдено!");
        }
    }

    /**
     * Получение отдельного бронирования
     *
     * @param bookingId
     * @param userId
     * @return
     */
    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Optional<Booking> bookingInBase = bookingRepository.findById(bookingId);

        if(bookingInBase.isPresent() &&
                (bookingInBase.get().getBooker().getId() == userId ||
                        bookingInBase.get().getItem().getOwner().getId() == userId)){
            return BookingMapper.toBookingDto(bookingInBase.get());
        }else {
            throw new EntityNotFoundException("Бронирование с идентификатором" + bookingId + " не найдено!");
        }
    }

    /**
     * Получение всех бронирований пользователя
     *
     * @param state
     * @param userId
     * @return
     */
    @Override
    public Collection<BookingDto> findUserBookings(String state, long userId) {
        User user = userService.getUser(userId);
        checkStatus(state);

        List<BookingDto> ret = new ArrayList<>();
        for (Booking booking :bookingRepository.findUserBookings(userId, state)) {
            ret.add(BookingMapper.toBookingDto(booking));
        }
        return ret;
    }

    /**
     * Получение бронирований вещей пользователя
     *
     * @param state
     * @param userId
     * @return
     */
    @Override
    public Collection<BookingDto> findOwnerBookings(String state, long userId) {
        User user = userService.getUser(userId);
        checkStatus(state);

        List<BookingDto> ret = new ArrayList<>();
        for (Booking booking : bookingRepository.findOwnerBookings(userId, state)){
            ret.add(BookingMapper.toBookingDto(booking));
        }
        return ret;
    }

    @Override
    public Boolean checkBooker(Long itemId, Long bookerId){
        return bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(itemId,
                bookerId,
                BookingStatus.APPROVED,
                new Date());
    }

    private void checkStatus(String state){
        try{
            BookingSearchStatus checkStatus = BookingSearchStatus.valueOf(state);
        } catch (IllegalArgumentException e){
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
