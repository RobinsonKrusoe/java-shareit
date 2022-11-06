package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long requestorId);

    Page<ItemRequest> findAllByRequestor_IdNotOrderByCreatedDesc(Long requestorId, Pageable pageable);
}
