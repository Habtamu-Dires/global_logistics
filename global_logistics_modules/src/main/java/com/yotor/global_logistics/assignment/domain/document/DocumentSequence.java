package com.yotor.global_logistics.assignment.domain.document;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("document_sequence")
@Getter
public class DocumentSequence {

    @Id
    private Long id;

    private String docType;
    private int year;

    private long nextValue;

    public long getAndIncrement() {
        long current = nextValue;
        this.nextValue++;
        return current;
    }

    public static DocumentSequence create(
            String docType, int year
    ) {
        DocumentSequence s = new DocumentSequence();
        s.docType = docType;
        s.year = year;
        s.nextValue = 1;
        return s;
    }
}

