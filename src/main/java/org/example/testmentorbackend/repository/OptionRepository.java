package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.Options;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Options, Long> {

}