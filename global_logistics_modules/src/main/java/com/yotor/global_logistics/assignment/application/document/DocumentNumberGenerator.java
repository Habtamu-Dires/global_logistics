package com.yotor.global_logistics.assignment.application.document;

import com.yotor.global_logistics.assignment.application.document.dto.DocumentType;
import com.yotor.global_logistics.assignment.domain.document.DocumentSequence;
import com.yotor.global_logistics.assignment.persistence.DocumentSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DocumentNumberGenerator {

    private final DocumentSequenceRepository repository;

    @Transactional
    public String next(DocumentType type) {

        LocalDate today = LocalDate.now();

        DocumentSequence seq =
                repository.findForUpdate(
                                type.name(),
                                today.getYear()
                        )
                        .orElseGet(() ->
                                repository.save(
                                        DocumentSequence.create(
                                                type.name(),
                                                today.getYear()
                                        )
                                )
                        );

        long value = seq.getAndIncrement();

        repository.save(seq);

        return format(type, today, value);
    }

    private String format(DocumentType type, LocalDate date, long value) {
        return "%s-%d-%06d".formatted(
                type.name(),
                date.getYear(),
                value
        );
    }
}

