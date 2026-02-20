package com.yotor.global_logistics.assignment.persistence;

import com.yotor.global_logistics.assignment.domain.document.Grn;
import org.springframework.data.repository.CrudRepository;

public interface GrnRepository extends CrudRepository<Grn,Long> {
}
